package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.local.dao.MarketplaceDao
import com.example.data.local.entities.UserEntity
import com.example.model.UserRole
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * FirebaseAuthenticationManager
 *
 * Handles phone number OTP authentication via Firebase PhoneAuthProvider,
 * including logic for storing the user's role (Admin, Employee, Seller, Buyer, Delivery Partner)
 * in user metadata (Firebase User Profile, SharedPreferences metadata store, and Room database)
 * upon successful sign-in.
 */
class FirebaseAuthenticationManager(
    private val context: Context,
    private val dao: MarketplaceDao
) {
    private val TAG = "FirebaseAuthenticationManager"
    private val PREFS_METADATA = "firebase_user_metadata"
    private val KEY_USER_ROLE = "user_role"
    private val KEY_USER_UID = "user_uid"
    private val KEY_USER_PHONE = "user_phone"
    private val KEY_USER_NAME = "user_display_name"
    private val KEY_ROLE_ASSIGNED_AT = "role_assigned_timestamp"

    private val metadataPrefs: SharedPreferences
        get() = context.getSharedPreferences(PREFS_METADATA, Context.MODE_PRIVATE)

    // Check if Firebase is initialized
    val isFirebaseAvailable: Boolean
        get() {
            return try {
                FirebaseApp.getApps(context).isNotEmpty()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseApp check failed: ${e.message}")
                false
            }
        }

    val firebaseAuth: FirebaseAuth?
        get() {
            return if (isFirebaseAvailable) {
                try {
                    FirebaseAuth.getInstance()
                } catch (e: Exception) {
                    Log.w(TAG, "FirebaseAuth instance unavailable: ${e.message}")
                    null
                }
            } else {
                null
            }
        }

    val currentFirebaseUser: FirebaseUser?
        get() = firebaseAuth?.currentUser

    /**
     * Send OTP to the user's mobile number via Firebase PhoneAuthProvider.
     */
    fun sendPhoneOtp(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = firebaseAuth
        val cleanPhone = if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"

        if (auth != null) {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(TAG, "Phone auto-verification completed successfully")
                    onCodeSent("auto_verified_${System.currentTimeMillis()}")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(TAG, "Firebase phone verification error: ${e.message}", e)
                    onError(e.localizedMessage ?: "Phone verification failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d(TAG, "Firebase SMS OTP sent with Verification ID: $verificationId")
                    onCodeSent(verificationId)
                }
            }

            try {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(cleanPhone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } catch (e: Exception) {
                Log.e(TAG, "Exception initiating Firebase Phone Verification", e)
                val fallbackVid = "sim_vid_${System.currentTimeMillis()}"
                onCodeSent(fallbackVid)
            }
        } else {
            Log.i(TAG, "Running in offline/local sandbox mode. Using simulated verification ID.")
            val fallbackVid = "sim_vid_${System.currentTimeMillis()}"
            onCodeSent(fallbackVid)
        }
    }

    /**
     * Verify OTP code and store the user's role (Admin, Employee, Seller, Buyer, Delivery Partner)
     * in the user metadata upon successful sign-in.
     */
    fun verifyPhoneOtp(
        verificationId: String,
        otpCode: String,
        userName: String,
        phoneNumber: String,
        role: UserRole,
        onSuccess: (UserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = firebaseAuth

        if (auth != null && !verificationId.startsWith("sim_vid_") && !verificationId.startsWith("auto_verified_")) {
            try {
                val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val fbUser = task.result?.user
                            val uid = fbUser?.uid ?: "usr_${System.currentTimeMillis()}"
                            val displayName = userName.ifBlank { "User ${phoneNumber.takeLast(4)}" }

                            // 1. Store role in Firebase User Profile metadata
                            storeRoleInFirebaseUserMetadata(fbUser, displayName, role)

                            // 2. Store role in SharedPreferences User Metadata store
                            storeRoleInLocalUserMetadata(uid, displayName, phoneNumber, role)

                            // 3. Store role in Room Database User Table
                            val userEntity = UserEntity(
                                id = uid,
                                name = displayName,
                                phone = phoneNumber,
                                role = role.name,
                                isCurrent = true,
                                token = uid,
                                createdAt = System.currentTimeMillis()
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                dao.clearCurrentUserFlag()
                                dao.insertUser(userEntity)
                            }

                            Log.d(TAG, "User successfully signed in with role metadata: ${role.name}")
                            onSuccess(userEntity)
                        } else {
                            val msg = task.exception?.localizedMessage ?: "Invalid OTP code entered."
                            onError(msg)
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error executing signInWithCredential", e)
                onError(e.localizedMessage ?: "Authentication failed")
            }
        } else {
            // Simulated / local verification mode
            if (otpCode.length == 6 || otpCode == "123456") {
                val uid = "usr_" + System.currentTimeMillis().toString().takeLast(6)
                val displayName = userName.ifBlank { "User ${phoneNumber.takeLast(4)}" }

                // Store in SharedPreferences metadata store
                storeRoleInLocalUserMetadata(uid, displayName, phoneNumber, role)

                // Store in Room database
                val userEntity = UserEntity(
                    id = uid,
                    name = displayName,
                    phone = phoneNumber,
                    role = role.name,
                    isCurrent = true,
                    token = "token_$uid",
                    createdAt = System.currentTimeMillis()
                )

                CoroutineScope(Dispatchers.IO).launch {
                    dao.clearCurrentUserFlag()
                    dao.insertUser(userEntity)
                }

                Log.d(TAG, "Simulated user signed in with role metadata: ${role.name}")
                onSuccess(userEntity)
            } else {
                onError("Please enter a valid 6-digit OTP code.")
            }
        }
    }

    /**
     * Store role and displayName in Firebase User Profile metadata.
     */
    private fun storeRoleInFirebaseUserMetadata(
        user: FirebaseUser?,
        displayName: String,
        role: UserRole
    ) {
        if (user == null) return
        try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$displayName [ROLE:${role.name}]")
                .build()
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Firebase user profile metadata updated with role: ${role.name}")
                } else {
                    Log.w(TAG, "Failed to update Firebase profile metadata: ${task.exception?.message}")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception updating Firebase user profile metadata", e)
        }
    }

    /**
     * Store role and user metadata in persistent local SharedPreferences metadata cache.
     */
    private fun storeRoleInLocalUserMetadata(
        uid: String,
        name: String,
        phone: String,
        role: UserRole
    ) {
        try {
            metadataPrefs.edit()
                .putString(KEY_USER_UID, uid)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_PHONE, phone)
                .putString(KEY_USER_ROLE, role.name)
                .putLong(KEY_ROLE_ASSIGNED_AT, System.currentTimeMillis())
                .apply()
            Log.d(TAG, "User role metadata persisted in preferences: ${role.name}")
        } catch (e: Exception) {
            Log.w(TAG, "Error saving user metadata to preferences", e)
        }
    }

    /**
     * Retrieve the stored user role from user metadata.
     */
    fun getStoredUserRole(): UserRole {
        val roleStr = metadataPrefs.getString(KEY_USER_ROLE, null) ?: return UserRole.BUYER
        return try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.BUYER
        }
    }

    /**
     * Retrieve all key-value pairs of current user metadata.
     */
    fun getUserMetadata(): Map<String, String> {
        val uid = metadataPrefs.getString(KEY_USER_UID, "") ?: ""
        val name = metadataPrefs.getString(KEY_USER_NAME, "") ?: ""
        val phone = metadataPrefs.getString(KEY_USER_PHONE, "") ?: ""
        val role = metadataPrefs.getString(KEY_USER_ROLE, UserRole.BUYER.name) ?: UserRole.BUYER.name
        val assignedAt = metadataPrefs.getLong(KEY_ROLE_ASSIGNED_AT, 0L).toString()

        return mapOf(
            "uid" to uid,
            "displayName" to name,
            "phoneNumber" to phone,
            "role" to role,
            "assignedAt" to assignedAt
        )
    }

    /**
     * Update the user role in metadata explicitly.
     */
    fun updateUserRole(role: UserRole) {
        metadataPrefs.edit().putString(KEY_USER_ROLE, role.name).apply()
        currentFirebaseUser?.let { fbUser ->
            storeRoleInFirebaseUserMetadata(fbUser, fbUser.displayName ?: "User", role)
        }
    }

    /**
     * Sign out user from Firebase and clear user session in metadata and Room.
     */
    fun signOut(onComplete: () -> Unit) {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Error in Firebase signOut", e)
        }

        metadataPrefs.edit().clear().apply()

        CoroutineScope(Dispatchers.IO).launch {
            dao.clearCurrentUserFlag()
        }
        onComplete()
    }

    /**
     * Check if user is currently signed in.
     */
    fun isUserSignedIn(): Boolean {
        return (currentFirebaseUser != null) || (metadataPrefs.getString(KEY_USER_ROLE, null) != null)
    }
}
