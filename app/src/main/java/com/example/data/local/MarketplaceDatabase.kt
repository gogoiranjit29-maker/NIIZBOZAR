package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.MarketplaceDao
import com.example.data.local.entities.CartItemEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.DeliveryPartnerEntity
import com.example.data.local.entities.EmployeeEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.PricingConfigEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RequirementOrderEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        RequirementOrderEntity::class,
        PricingConfigEntity::class,
        DeliveryPartnerEntity::class,
        ReviewEntity::class,
        NotificationEntity::class,
        UserEntity::class,
        EmployeeEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MarketplaceDatabase : RoomDatabase() {

    abstract fun marketplaceDao(): MarketplaceDao

    companion object {
        @Volatile
        private var INSTANCE: MarketplaceDatabase? = null

        fun getInstance(context: Context): MarketplaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarketplaceDatabase::class.java,
                    "niiz_bozar_marketplace.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).populateInitialData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateInitialData() {
        val dao = marketplaceDao()

        // 1. Initial Pricing Configuration
        dao.insertOrUpdatePricingConfig(
            PricingConfigEntity(
                id = 1,
                baseServiceFee = 5.0,
                perKmDeliveryRate = 2.5,
                packagingFee = 3.0,
                demandMultiplier = 1.0,
                bulkDiscountThresholdQty = 20.0,
                bulkDiscountPercent = 10.0
            )
        )

        // 2. Categories
        val initialCategories = listOf(
            CategoryEntity("leafy_veg", "Leafy Vegetables", "पत्तेदार सब्जियां", "শাক-পাচলি", "Vegetables", false, true, "spa"),
            CategoryEntity("root_veg", "Root Vegetables", "कंदमूल सब्जियां", "শিপাৰ পাচলি", "Vegetables", false, true, "nutrition"),
            CategoryEntity("bulb_veg", "Bulb Vegetables", "कंद सब्जियां", "পিয়াঁজ-নহৰু জাতীয়", "Vegetables", false, true, "lens"),
            CategoryEntity("fruit_veg", "Fruit Vegetables", "फलदार सब्जियां", "ফল জাতীয় পাচলি", "Vegetables", false, true, "yard"),
            CategoryEntity("cruciferous", "Cruciferous Vegetables", "गोभी वर्ग की सब्जियां", "কবি জাতীয়", "Vegetables", false, true, "psychiatry"),
            CategoryEntity("legumes", "Legume Vegetables", "फलीदार सब्जियां", "মাহ জাতীয় পাচলি", "Vegetables", false, true, "grain"),
            CategoryEntity("mushrooms", "Mushrooms", "मशरूम / खुंब", "কাঠফুলা", "Vegetables", false, true, "nature"),
            CategoryEntity("herbs", "Fresh Herbs & Spices", "ताजी जड़ी-बूटियाँ", "সুগন্ধি শাক-বন", "Vegetables", false, true, "local_florist"),
            CategoryEntity("exotic", "Exotic Vegetables", "विदेशी सब्जियां", "বিদেশী শাক-পাচলি", "Vegetables", false, true, "eco"),
            CategoryEntity("local_veg", "Regional & Local", "स्थानीय / क्षेत्रीय सब्जियां", "স্থানীয় থলুৱা পাচলি", "Vegetables", false, true, "agriculture"),
            CategoryEntity("fruits", "Fresh Fruits", "ताजे फल", "সতেজ ফল-মূল", "Daily Cooking Products", false, true, "apple"),
            CategoryEntity("rice_grains", "Rice & Grains", "चावल एवं अनाज", "চাউল আৰু শস্য", "Daily Cooking Products", false, true, "grass"),
            CategoryEntity("pulses", "Pulses & Dal", "दालें", "দাইল", "Daily Cooking Products", false, true, "scatter_plot"),
            CategoryEntity("cooking_essentials", "Cooking Essentials & Oils", "तेल एवं खाद्य सामग्री", "তেল আৰু মচলা", "Daily Cooking Products", false, true, "oil_barrel"),
            CategoryEntity("dairy_eggs", "Dairy & Fresh Eggs", "दूध एवं देसी अंडे", "গাখীৰ আৰু কণী", "Daily Cooking Products", false, true, "egg")
        )
        dao.insertAllCategories(initialCategories)

        // 3. Initial Products (both approved and pending for verification queue testing!)
        val initialProducts = listOf(
            ProductEntity(
                nameEn = "Tomato",
                nameHi = "टमाटर",
                nameAs = "বিলাহী",
                categoryId = "fruit_veg",
                subCategory = "Red Vine Ripe",
                description = "Naturally sun-ripened, farm-fresh juicy red tomatoes. Plucked early morning, rich in lycopene and perfect for curries and salads.",
                originalSellerPrice = 28.0,
                unit = "kg",
                availableQty = 180.0,
                freshnessStatus = "Freshly harvested",
                quality = "Grade A (Export / Select)",
                harvestDate = "Today 5:30 AM",
                availabilityStatus = "In Stock",
                sellerId = "seller_biren_01",
                sellerName = "Biren Das (Pragati Krishi Farm)",
                sellerPhone = "+91 98540 12345",
                sellerMandiLocation = "Bonda Mandi Yard, Gate 2",
                sellerDistanceKm = 1.8,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Stock inspected in Mandi Shed 1. Farm harvest confirmed, high freshness score.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.05
            ),
            ProductEntity(
                nameEn = "Potato",
                nameHi = "आलू",
                nameAs = "আলু",
                categoryId = "root_veg",
                subCategory = "Pahari Desi Potato",
                description = "Starchy, firm, earthy mountain potatoes. Unwashed for longer shelf life, free from synthetic chemicals.",
                originalSellerPrice = 22.0,
                unit = "kg",
                availableQty = 450.0,
                freshnessStatus = "Good quality",
                quality = "Grade A (Export / Select)",
                harvestDate = "Yesterday Evening",
                availabilityStatus = "In Stock",
                sellerId = "seller_ramesh_02",
                sellerName = "Ramesh Gogoi",
                sellerPhone = "+91 94350 56789",
                sellerMandiLocation = "Pamohi Wholesale Depot",
                sellerDistanceKm = 3.2,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Wholesale lot verified, moisture level acceptable.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.0
            ),
            ProductEntity(
                nameEn = "Onion",
                nameHi = "प्याज",
                nameAs = "পিয়াঁজ",
                categoryId = "bulb_veg",
                subCategory = "Nasik Red Onion",
                description = "Crisp, pungent red onions with tight skins. Dried naturally to preserve freshness and strong flavor.",
                originalSellerPrice = 32.0,
                unit = "kg",
                availableQty = 320.0,
                freshnessStatus = "Fresh",
                quality = "Grade A (Export / Select)",
                harvestDate = "2 days ago",
                availabilityStatus = "In Stock",
                sellerId = "seller_ramesh_02",
                sellerName = "Ramesh Gogoi",
                sellerPhone = "+91 94350 56789",
                sellerMandiLocation = "Pamohi Wholesale Depot",
                sellerDistanceKm = 3.2,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Inspected sacks, premium dry skin, no sprouting.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.1
            ),
            ProductEntity(
                nameEn = "Spinach",
                nameHi = "पालक",
                nameAs = "পালেং শাক",
                categoryId = "leafy_veg",
                subCategory = "Tender Green Leaves",
                description = "Crisp tender baby spinach bundles, freshly cut from riverbank alluvial soil. Washed in clean spring water.",
                originalSellerPrice = 18.0,
                unit = "bundle",
                availableQty = 75.0,
                freshnessStatus = "Freshly harvested",
                quality = "100% Organic Desi",
                harvestDate = "Today 6:00 AM",
                availabilityStatus = "In Stock",
                sellerId = "seller_pranab_03",
                sellerName = "Pranab Saikia (Organic Valley)",
                sellerPhone = "+91 97060 98765",
                sellerMandiLocation = "Chandrapur Riverbank",
                sellerDistanceKm = 2.4,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Organic soil certificate checked, spotless tender leaves.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.0
            ),
            ProductEntity(
                nameEn = "Green Chili",
                nameHi = "हरी मिर्च",
                nameAs = "কেঁচা জলকীয়া",
                categoryId = "fruit_veg",
                subCategory = "Hot Bird Eye & Desi",
                description = "Crisp and intensely aromatic spicy green chillies. Hand-picked with fresh green stems attached.",
                originalSellerPrice = 60.0,
                unit = "kg",
                availableQty = 40.0,
                freshnessStatus = "Freshly harvested",
                quality = "Grade A (Export / Select)",
                harvestDate = "Today 5:00 AM",
                availabilityStatus = "In Stock",
                sellerId = "seller_biren_01",
                sellerName = "Biren Das (Pragati Krishi Farm)",
                sellerPhone = "+91 98540 12345",
                sellerMandiLocation = "Bonda Mandi Yard, Gate 2",
                sellerDistanceKm = 1.8,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Fresh stems, high pungent aroma.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.0
            ),
            ProductEntity(
                nameEn = "Cauliflower",
                nameHi = "फूलगोभी",
                nameAs = "ফুলকবি",
                categoryId = "cruciferous",
                subCategory = "Snowball White",
                description = "Dense, bright white snowy curds surrounded by protective crisp green outer foliage. Completely worm-free.",
                originalSellerPrice = 35.0,
                unit = "piece",
                availableQty = 90.0,
                freshnessStatus = "Fresh",
                quality = "Grade A (Export / Select)",
                harvestDate = "Yesterday Night",
                availabilityStatus = "In Stock",
                sellerId = "seller_pranab_03",
                sellerName = "Pranab Saikia (Organic Valley)",
                sellerPhone = "+91 97060 98765",
                sellerMandiLocation = "Chandrapur Riverbank",
                sellerDistanceKm = 2.4,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Tight curd structure, zero yellowing.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.0
            ),
            ProductEntity(
                nameEn = "Mustard Oil",
                nameHi = "सरसों का तेल",
                nameAs = "সৰিয়হৰ তেল",
                categoryId = "cooking_essentials",
                subCategory = "Cold Pressed Kachi Ghani",
                description = "100% pure cold pressed yellow mustard seed oil. Traditional wood-press extraction with strong pungent tear-inducing aroma.",
                originalSellerPrice = 145.0,
                unit = "packet",
                availableQty = 60.0,
                freshnessStatus = "Premium Grade",
                quality = "100% Organic Desi",
                harvestDate = "Batch: Oct 2026",
                availabilityStatus = "In Stock",
                sellerId = "seller_oil_mill_04",
                sellerName = "Kamrup Desi Ghani Works",
                sellerPhone = "+91 99541 33221",
                sellerMandiLocation = "Beltola Daily Market",
                sellerDistanceKm = 4.1,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "FSSAI registered, batch purity certificate verified.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.0
            ),
            ProductEntity(
                nameEn = "Joha Rice",
                nameHi = "जोहा चावल",
                nameAs = "জোহা চাউল",
                categoryId = "rice_grains",
                subCategory = "Aromatic Indigenous Grain",
                description = "Prized sweet-scented heritage Joha aromatic rice from Assam valleys. Cleaned, destoned and packaged in breathable burlap.",
                originalSellerPrice = 85.0,
                unit = "kg",
                availableQty = 250.0,
                freshnessStatus = "Premium Grade",
                quality = "100% Organic Desi",
                harvestDate = "New Season Crop",
                availabilityStatus = "In Stock",
                sellerId = "seller_biren_01",
                sellerName = "Biren Das (Pragati Krishi Farm)",
                sellerPhone = "+91 98540 12345",
                sellerMandiLocation = "Bonda Mandi Yard, Gate 2",
                sellerDistanceKm = 1.8,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Geographical Indication (GI) heritage verified, intense natural aroma.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.15
            ),
            ProductEntity(
                nameEn = "Farm Brown Eggs",
                nameHi = "देसी अंडे",
                nameAs = "কণী",
                categoryId = "dairy_eggs",
                subCategory = "Free Range Desi",
                description = "Free-range pasture raised country chicken eggs. High protein, rich golden yolks, collected daily.",
                originalSellerPrice = 110.0,
                unit = "dozen",
                availableQty = 45.0,
                freshnessStatus = "Freshly harvested",
                quality = "100% Organic Desi",
                harvestDate = "Today Morning",
                availabilityStatus = "In Stock",
                sellerId = "seller_pranab_03",
                sellerName = "Pranab Saikia (Organic Valley)",
                sellerPhone = "+91 97060 98765",
                sellerMandiLocation = "Chandrapur Riverbank",
                sellerDistanceKm = 2.4,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "APPROVED",
                verificationNotes = "Candling test passed, no cracks.",
                verifiedByEmployeeId = "emp_rahul_01",
                demandFactor = 1.0
            ),
            // PENDING VERIFICATION PRODUCTS (for Employee / Admin verification queue!)
            ProductEntity(
                nameEn = "Bottle Gourd",
                nameHi = "लौकी",
                nameAs = "জাতি লাউ",
                categoryId = "fruit_veg",
                subCategory = "Tender Green Lau",
                description = "Straight tender bottle gourds with smooth pale green skin. Extremely low calorie, picked at dawn.",
                originalSellerPrice = 30.0,
                unit = "piece",
                availableQty = 65.0,
                freshnessStatus = "Freshly harvested",
                quality = "Grade A (Export / Select)",
                harvestDate = "Today 6:15 AM",
                availabilityStatus = "In Stock",
                sellerId = "seller_manoj_05",
                sellerName = "Manoj Talukdar (Khetri Greens)",
                sellerPhone = "+91 91234 56780",
                sellerMandiLocation = "Khetri Agricultural Yard",
                sellerDistanceKm = 5.6,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "PENDING",
                verificationNotes = "",
                verifiedByEmployeeId = "",
                demandFactor = 1.0
            ),
            ProductEntity(
                nameEn = "French Beans",
                nameHi = "बीन्स",
                nameAs = "বিন",
                categoryId = "legumes",
                subCategory = "Tender Stringless",
                description = "Crunchy stringless slender green beans, plucked young for sweet tender taste.",
                originalSellerPrice = 55.0,
                unit = "kg",
                availableQty = 50.0,
                freshnessStatus = "Freshly harvested",
                quality = "Grade A (Export / Select)",
                harvestDate = "Today 5:45 AM",
                availabilityStatus = "In Stock",
                sellerId = "seller_manoj_05",
                sellerName = "Manoj Talukdar (Khetri Greens)",
                sellerPhone = "+91 91234 56780",
                sellerMandiLocation = "Khetri Agricultural Yard",
                sellerDistanceKm = 5.6,
                imageUrl = "drawable:fresh_market_banner",
                verificationStatus = "PENDING",
                verificationNotes = "",
                verifiedByEmployeeId = "",
                demandFactor = 1.0
            )
        )
        dao.insertAllProducts(initialProducts)

        // 4. Delivery Partners
        val initialDeliveryPartners = listOf(
            DeliveryPartnerEntity("dp_arun_01", "Arun Kalita", "+91 98640 11223", "EV Cargo Scooter", null, 26.1520, 91.7450, true),
            DeliveryPartnerEntity("dp_deepak_02", "Deepak Sharma", "+91 97061 44556", "3-Wheeler Auto Van", null, 26.1380, 91.7300, true),
            DeliveryPartnerEntity("dp_manash_03", "Manash Barman", "+91 94352 77889", "Insulated Delivery Bike", null, 26.1600, 91.7600, true)
        )
        dao.insertAllDeliveryPartners(initialDeliveryPartners)

        // 5. Initial Notifications
        dao.insertNotification(
            NotificationEntity(
                targetRole = "ALL",
                title = "Welcome to NIIZ BOZAR - Empowering Farmers",
                message = "Connecting local farmers directly to your kitchen with transparent pricing, strict employee verification, and fast doorstep delivery."
            )
        )
        dao.insertNotification(
            NotificationEntity(
                targetRole = "EMPLOYEE",
                title = "Pending Product Verification",
                message = "New batch of Bottle Gourd and French Beans submitted by Manoj Talukdar requires verification call and price check."
            )
        )
        dao.insertNotification(
            NotificationEntity(
                targetRole = "SELLER",
                title = "Marketplace Rules Reminder",
                message = "All products undergo employee review and call verification prior to publishing to guarantee customer trust."
            )
        )

        // 6. Initial Staff Employees
        val initialEmployees = listOf(
            EmployeeEntity(
                employeeId = "EMP-NB-2026-1024",
                name = "Rahul Baruah",
                phone = "+91 94350 11223",
                email = "staff.rahul@niizbozar.in",
                department = "Quality & Verification",
                assignedMandi = "Pamohi Wholesale Mandi",
                isActive = true
            ),
            EmployeeEntity(
                employeeId = "EMP-NB-2026-2048",
                name = "Nilakshi Kalita",
                phone = "+91 98642 33445",
                email = "nilakshi.k@niizbozar.in",
                department = "Mandi Field Operations",
                assignedMandi = "Beltola Daily Market",
                isActive = true
            ),
            EmployeeEntity(
                employeeId = "EMP-NB-2026-3096",
                name = "Bikram Barman",
                phone = "+91 97063 55667",
                email = "bikram.b@niizbozar.in",
                department = "Farmer Onboarding & Logistics",
                assignedMandi = "Uzan Bazar Produce Hub",
                isActive = true
            )
        )
        dao.insertAllEmployees(initialEmployees)
    }
}
