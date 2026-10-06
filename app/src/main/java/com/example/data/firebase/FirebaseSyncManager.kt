package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.ProductEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

data class FirebaseBackendStatus(
    val isInitialized: Boolean,
    val projectId: String,
    val appName: String,
    val isAuthAvailable: Boolean,
    val isFirestoreAvailable: Boolean,
    val currentUserId: String?,
    val currentUserPhone: String?
)

/**
 * FirebaseSyncManager
 *
 * Handles real-time cloud synchronization between the embedded Room SQLite database
 * and Firebase Cloud Firestore & Firebase Auth.
 * Operates gracefully whether Firebase is active with google-services.json or in offline local-first mode.
 */
class FirebaseSyncManager(private val context: Context) {

    private val TAG = "FirebaseSyncManager"

    val isFirebaseInitialized: Boolean
        get() {
            return try {
                FirebaseApp.getApps(context).isNotEmpty()
            } catch (e: Exception) {
                Log.w(TAG, "Error checking FirebaseApp: ${e.message}")
                false
            }
        }

    val firestore: FirebaseFirestore?
        get() {
            return if (isFirebaseInitialized) {
                try {
                    FirebaseFirestore.getInstance()
                } catch (e: Exception) {
                    Log.w(TAG, "FirebaseFirestore instance unavailable: ${e.message}")
                    null
                }
            } else null
        }

    val auth: FirebaseAuth?
        get() {
            return if (isFirebaseInitialized) {
                try {
                    FirebaseAuth.getInstance()
                } catch (e: Exception) {
                    Log.w(TAG, "FirebaseAuth instance unavailable: ${e.message}")
                    null
                }
            } else null
        }

    fun getStatus(): FirebaseBackendStatus {
        val initialized = isFirebaseInitialized
        var projectId = "Not configured (Add google-services.json)"
        var appName = "None"
        var currentUid: String? = null
        var currentPhone: String? = null

        if (initialized) {
            try {
                val app = FirebaseApp.getInstance()
                appName = app.name
                projectId = app.options.projectId ?: "Active (Default Project)"
                val user = auth?.currentUser
                currentUid = user?.uid
                currentPhone = user?.phoneNumber
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading Firebase options: ${e.message}")
            }
        }

        return FirebaseBackendStatus(
            isInitialized = initialized,
            projectId = projectId,
            appName = appName,
            isAuthAvailable = auth != null,
            isFirestoreAvailable = firestore != null,
            currentUserId = currentUid,
            currentUserPhone = currentPhone
        )
    }

    /**
     * Programmatically re-configure or connect to a custom Firebase project.
     */
    fun configureFirebaseProject(projectId: String, apiKey: String, appId: String): Boolean {
        return try {
            val options = com.google.firebase.FirebaseOptions.Builder()
                .setProjectId(projectId.trim())
                .setApiKey(apiKey.trim())
                .setApplicationId(appId.trim())
                .build()

            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                val defaultApp = apps.first()
                defaultApp.delete()
            }
            FirebaseApp.initializeApp(context, options)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Configure Firebase failed: ${e.message}", e)
            false
        }
    }

    /**
     * Backup and sync local marketplace products to Firestore 'products' collection.
     */
    fun syncProductsToCloud(
        products: List<ProductEntity>,
        onComplete: (Boolean, String) -> Unit
    ) {
        val db = firestore
        if (db == null) {
            onComplete(false, "Firebase not connected. Place google-services.json in the /app directory to connect.")
            return
        }

        try {
            val batch = db.batch()
            products.take(50).forEach { p ->
                val docRef = db.collection("products").document(p.id.toString())
                val map = hashMapOf(
                    "id" to p.id,
                    "nameEn" to p.nameEn,
                    "nameHi" to p.nameHi,
                    "nameAs" to p.nameAs,
                    "subCategory" to p.subCategory,
                    "originalSellerPrice" to p.originalSellerPrice,
                    "unit" to p.unit,
                    "quality" to p.quality,
                    "verificationStatus" to p.verificationStatus,
                    "sellerMandiLocation" to p.sellerMandiLocation,
                    "harvestDate" to p.harvestDate,
                    "updatedAt" to System.currentTimeMillis()
                )
                batch.set(docRef, map, SetOptions.merge())
            }

            batch.commit()
                .addOnSuccessListener {
                    Log.i(TAG, "Successfully synced ${products.size} products to Firebase Firestore")
                    onComplete(true, "Synced ${products.size} products to Cloud Firestore!")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Firestore batch commit failed: ${e.message}", e)
                    onComplete(false, "Firestore sync failed: ${e.localizedMessage}")
                }
        } catch (e: Exception) {
            onComplete(false, "Sync error: ${e.localizedMessage}")
        }
    }

    /**
     * Sync local orders to Firestore 'orders' collection.
     */
    fun syncOrdersToCloud(
        orders: List<OrderEntity>,
        onComplete: (Boolean, String) -> Unit
    ) {
        val db = firestore
        if (db == null) {
            onComplete(false, "Firebase not connected. Place google-services.json in the /app directory to connect.")
            return
        }

        try {
            val batch = db.batch()
            orders.take(50).forEach { order ->
                val docRef = db.collection("orders").document(order.id.toString())
                val map = hashMapOf(
                    "id" to order.id,
                    "orderNumber" to order.orderNumber,
                    "finalTotalAmount" to order.finalTotalAmount,
                    "buyerAddress" to order.buyerAddress,
                    "deliverySlot" to order.deliverySlot,
                    "orderStatus" to order.orderStatus,
                    "paymentMethod" to order.paymentMethod,
                    "createdAt" to order.createdAt,
                    "deliveryOtp" to order.deliveryOtp
                )
                batch.set(docRef, map, SetOptions.merge())
            }

            batch.commit()
                .addOnSuccessListener {
                    onComplete(true, "Synced ${orders.size} orders to Cloud Firestore!")
                }
                .addOnFailureListener { e ->
                    onComplete(false, "Order sync failed: ${e.localizedMessage}")
                }
        } catch (e: Exception) {
            onComplete(false, "Order sync error: ${e.localizedMessage}")
        }
    }
}
