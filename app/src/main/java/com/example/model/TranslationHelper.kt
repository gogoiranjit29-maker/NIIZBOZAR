package com.example.model

object TranslationHelper {

    // Central dictionary for vegetable and daily essentials product names
    private val productDict: Map<String, Triple<String, String, String>> = mapOf(
        "Potato" to Triple("Potato", "आलू", "আলু"),
        "Tomato" to Triple("Tomato", "टमाटर", "বিলাহী"),
        "Onion" to Triple("Onion", "प्याज", "পিয়াঁজ"),
        "Garlic" to Triple("Garlic", "लहसुन", "নহৰু"),
        "Ginger" to Triple("Ginger", "अदरक", "আদা"),
        "Green Chili" to Triple("Green Chili", "हरी मिर्च", "কেঁচা জলকীয়া"),
        "Spinach" to Triple("Spinach", "पालक", "পালেং শাক"),
        "Coriander" to Triple("Coriander", "धनिया", "ধনিয়া"),
        "Brinjal / Eggplant" to Triple("Brinjal / Eggplant", "बैंगन", "বেঙেনা"),
        "Brinjal" to Triple("Brinjal", "बैंगन", "বেঙেনা"),
        "Cauliflower" to Triple("Cauliflower", "फूलगोभी", "ফুলকবি"),
        "Cabbage" to Triple("Cabbage", "पत्तागोभी", "বন্ধাকবি"),
        "Mustard Greens" to Triple("Mustard Greens", "सरसों का साग", "লাই শাক"),
        "Radish" to Triple("Radish", "मूली", "মূলা"),
        "Carrot" to Triple("Carrot", "गाजर", "গাজৰ"),
        "Pumpkin" to Triple("Pumpkin", "कद्दू", "ৰঙালাউ"),
        "Bottle Gourd" to Triple("Bottle Gourd", "लौकी", "জাতি লাউ"),
        "Bitter Gourd" to Triple("Bitter Gourd", "करेला", "তিতা কেৰেলা"),
        "Ridge Gourd" to Triple("Ridge Gourd", "तोरी", "ঝিকা"),
        "Cucumber" to Triple("Cucumber", "खीरा", "তিয়ঁহ"),
        "Green Peas" to Triple("Green Peas", "हरी मटर", "মটৰ"),
        "French Beans" to Triple("French Beans", "बीन्स", "বিন"),
        "Button Mushroom" to Triple("Button Mushroom", "बटन मशरूम", "কাঠফুলা"),
        "Mint Leaves" to Triple("Mint Leaves", "पुदीना", "পদিনা"),
        "Curry Leaves" to Triple("Curry Leaves", "कढ़ी पत्ता", "নৰসিংহ পাত"),
        "Turmeric" to Triple("Turmeric", "हल्दी", "হালধি"),
        "Mustard Oil" to Triple("Mustard Oil", "सरसों का तेल", "সৰিয়হৰ তেল"),
        "Joha Rice" to Triple("Joha Rice", "जोहा चावल", "জোহা চাউল"),
        "Basmati Rice" to Triple("Basmati Rice", "बासमती चावल", "বাচমতী চাউল"),
        "Masoor Dal" to Triple("Masoor Dal", "मसूर दाल", "মচুৰ দাইল"),
        "Fresh Cow Milk" to Triple("Fresh Cow Milk", "ताजा गाय का दूध", "গৰুৰ গাখীৰ"),
        "Farm Brown Eggs" to Triple("Farm Brown Eggs", "देसी अंडे", "কণী"),
        "Apple" to Triple("Apple", "सेब", "আপেল"),
        "Banana" to Triple("Banana", "केला", "কল"),
        "Mango" to Triple("Mango", "आम", "আম")
    )

    fun getProductName(baseNameEn: String, lang: Language): String {
        val entry = productDict.entries.firstOrNull { it.key.equals(baseNameEn.trim(), ignoreCase = true) }
        return if (entry != null) {
            when (lang) {
                Language.EN -> entry.value.first
                Language.HI -> entry.value.second
                Language.AS -> entry.value.third
            }
        } else {
            baseNameEn
        }
    }

    private val strings: Map<String, Triple<String, String, String>> = mapOf(
        // General & Header
        "app_title" to Triple("NIIZ BOZAR", "निज बजार", "নিজ বজাৰ"),
        "tagline" to Triple("EMPOWERING FARMERS", "किसानों का सशक्तिकरण", "কৃষকৰ সৱলীকৰণ"),
        "search_hint" to Triple("Search fresh vegetables, fruits, essentials...", "ताज़ी सब्जियाँ, फल, सामग्री खोजें...", "সতেজ শাক-পাচলি, ফল-মূল বিচাৰক..."),
        "filter" to Triple("Filter", "फ़िल्टर", "ফিল্টাৰ"),
        "sort_by" to Triple("Sort By", "क्रमबद्ध करें", "ক্ৰম সজাওক"),
        "nearest" to Triple("Nearest Distance", "निकटतम दूरी", "আটাইতকৈ ওচৰৰ"),
        "lowest_price" to Triple("Lowest Price", "कम कीमत", "কম দাম"),
        "highest_freshness" to Triple("Freshness First", "ताजगी पहले", "প্ৰথমে সতেজতা"),
        "best_rating" to Triple("Top Rated", "सर्वश्रेष्ठ रेटिंग", "উচ্চ ৰেটিং"),

        // Roles
        "role_buyer" to Triple("Buyer", "खरीदार", "ক্ৰেতা"),
        "role_seller" to Triple("Seller / Farmer", "विक्रेता / किसान", "বিক্ৰেতা / কৃষক"),
        "role_employee" to Triple("Employee / Staff", "कर्मचारी", "কৰ্মচাৰী"),
        "role_admin" to Triple("Admin", "व्यवस्थापक", "এডমিন"),
        "role_delivery" to Triple("Delivery Partner", "डिलीवरी पार्टनर", "ডেলিভাৰী অংশীদাৰ"),

        // Freshness
        "freshness_freshly_harvested" to Triple("Freshly Harvested", "ताजा तोड़ा हुआ", "সতেজ চপোৱা"),
        "freshness_fresh" to Triple("Fresh", "ताजा", "সতেজ"),
        "freshness_good_quality" to Triple("Good Quality", "अच्छी गुणवत्ता", "ভাল মান"),
        "freshness_average_quality" to Triple("Average Quality", "सामान्य गुणवत्ता", "মধ্যম মান"),
        "freshness_premium_quality" to Triple("Premium Grade", "प्रीमियम गुणवत्ता", "প্ৰিমিয়াম মান"),

        // Quality
        "quality_grade_a" to Triple("Grade A (Export / Select)", "ग्रेड ए (उत्कृष्ट)", "গ্ৰেড এ (উন্নত)"),
        "quality_grade_b" to Triple("Grade B (Standard)", "ग्रेड बी (मानक)", "গ্ৰেড বি (সাধাৰণ)"),
        "quality_organic_desi" to Triple("100% Organic Desi", "देसी जैविक", "সম্পূৰ্ণ জৈৱিক দেশী"),

        // Units
        "unit_kg" to Triple("kg", "किग्रा", "কেজি"),
        "unit_gram" to Triple("gram", "ग्राम", "গ্ৰাম"),
        "unit_piece" to Triple("piece", "नग", "টা"),
        "unit_bundle" to Triple("bundle", "गड्डी", "মুঠা"),
        "unit_packet" to Triple("packet", "पैकेट", "পেকেট"),
        "unit_sack" to Triple("sack", "बोरी", "বস্তা"),
        "unit_dozen" to Triple("dozen", "दर्जन", "ডজন"),
        "unit_crate" to Triple("crate", "क्रेट", "ক্ৰেট"),

        // Verification Statuses
        "verification_pending" to Triple("Verification Pending", "सत्यापन लंबित", "যাচাই বাকী আছে"),
        "verification_approved" to Triple("Verified & Live", "सत्यापित और सक्रिय", "যাচাইকৃত আৰু সক্ৰিয়"),
        "verification_rejected" to Triple("Rejected", "अस्वीकृत", "প্ৰত্যাখ্যান কৰা হৈছে"),
        "verification_info_required" to Triple("Info Required", "अतिरिक्त जानकारी चाहिए", "অতিৰিক্ত তথ্য প্ৰয়োজন"),

        // Order Statuses
        "status_order_placed" to Triple("Order Placed", "ऑर्डर दिया गया", "অৰ্ডাৰ দিয়া হ’ল"),
        "status_paid" to Triple("Payment Confirmed", "भुगतान सफल", "পেমেন্ট সফল"),
        "status_processing" to Triple("Processing Order", "ऑर्डर प्रक्रिया में", "প্ৰক্ৰিয়া অব্যাহত"),
        "status_collected" to Triple("Products Collected from Mandi", "मंडी से एकत्र किया गया", "মাণ্ডিৰ পৰা সংগ্ৰহ কৰা হ’ল"),
        "status_assigned" to Triple("Delivery Partner Assigned", "डिलीवरी पार्टनर नियुक्त", "ডেলিভাৰী পাৰ্টনাৰ নিৰ্ধাৰিত"),
        "status_out_for_delivery" to Triple("Out for Delivery", "डिलीवरी के लिए रवाना", "বিতৰণৰ বাবে ওলাইছে"),
        "status_delivered" to Triple("Delivered Successfully", "सफलतापूर्वक डिलीवर", "সফলভাৱে বিতৰণ কৰা হ’ল"),
        "status_cancelled" to Triple("Cancelled", "रद्द किया गया", "বাতিল কৰা হ’ল"),

        // Delivery Slots
        "slot_morning_early" to Triple("Early Morning (6 AM - 9 AM)", "सुबह जल्दी (6 - 9 बजे)", "পুৱতি পুৱা (৬ - ৯ বজা)"),
        "slot_morning_late" to Triple("Mid Morning (9 AM - 12 PM)", "सुबह (9 - 12 बजे)", "পুৱা (৯ - ১২ বজা)"),
        "slot_afternoon" to Triple("Afternoon (12 PM - 3 PM)", "दोपहर (12 - 3 बजे)", "দুপৰীয়া (১২ - ৩ বজা)"),
        "slot_evening_early" to Triple("Early Evening (3 PM - 6 PM)", "शाम (3 - 6 बजे)", "গধূলি (৩ - ৬ বজা)"),
        "slot_evening_late" to Triple("Evening (6 PM - 9 PM)", "देर शाम (6 - 9 बजे)", "নিশা (৬ - ৯ বজা)"),

        // Pricing Breakdown
        "seller_price" to Triple("Original Seller Price", "मूल विक्रेता मूल्य", "মূল বিক্ৰেতাৰ মূল্য"),
        "delivery_charge" to Triple("Distance Delivery Fee", "दूरी वितरण शुल्क", "দূৰত্ব বিতৰণ মাচুল"),
        "service_charge" to Triple("Mandi Service & Handling", "मंडी सेवा व रखरखाव शुल्क", "মাণ্ডি সেৱা মাচুল"),
        "packaging_charge" to Triple("Eco-Friendly Packaging", "पर्यावरण-अनुकूल पैकेजिंग", "পেকেজিং মাচুল"),
        "demand_adjustment" to Triple("Market Demand Factor", "बाजार मांग समायोजन", "বজাৰৰ চাহিদা বৃদ্ধি"),
        "final_price" to Triple("Final Transparent Price", "अंतिम पारदर्शी मूल्य", "চূড়ান্ত স্বচ্ছ মূল্য"),
        "transparent_pricing_note" to Triple("Transparent Pricing Policy: All adjustments are itemized with no hidden fees.", "पारदर्शी मूल्य नीति: कोई छिपा हुआ शुल्क नहीं।", "স্বচ্ছ মূল্য নীতি: কোনো লুকাই থকা মাচুল নাই।"),

        // Network Mode
        "network_online" to Triple("Online (Full Speed)", "ऑनलाइन (तेज गति)", "অনলাইন (দ্ৰুত গতি)"),
        "network_low" to Triple("Low Network Mode (Compressed Images & Cached Data)", "धीमा इंटरनेट मोड (डेटा बचत)", "কম নেটৱৰ্ক ম’ড (ডাটা সংৰক্ষণ)"),
        "network_offline" to Triple("Offline Mode (Viewing Cached Mandi Data)", "ऑफ़लाइन मोड (कैश किया गया डेटा)", "অফলাইন ম’ড (কেশ্ব ডাটা)"),

        // Buttons & Actions
        "add_to_cart" to Triple("Add to Basket", "टोकरी में जोड़ें", "টোপোলাত যোগ কৰক"),
        "buy_now" to Triple("Direct Buy", "तुरंत खरीदें", "পোনপটীয়া ক্ৰয়"),
        "view_basket" to Triple("View Basket", "टोकरी देखें", "টোপোলা চাওক"),
        "checkout" to Triple("Proceed to Checkout", "भुगतान के लिए आगे बढ़ें", "পেমেন্টলৈ আগবাঢ়ক"),
        "post_requirement" to Triple("Post Custom Requirement", "अपनी आवश्यकता पोस्ट करें", "আপোনাৰ প্ৰয়োজন প’ষ্ট কৰক"),
        "upload_product" to Triple("Upload New Product", "नया उत्पाद अपलोड करें", "নতুন সামগ্ৰী আপল’ড কৰক"),
        "call_seller" to Triple("Call Seller to Verify", "सत्यापन के लिए विक्रेता को कॉल करें", "বিক্ৰেতাক কল কৰক"),
        "approve_product" to Triple("Approve & Publish", "मंजूरी दें और प्रकाशित करें", "অনুমোদন কৰক"),
        "reject_product" to Triple("Reject Product", "उत्पाद अस्वीकार करें", "প্ৰত্যাখ্যান কৰক"),
        "request_info" to Triple("Request Clarification", "जानकारी मांगें", "স্পষ্টীকৰণ বিচাৰক"),
        "no_cod_notice" to Triple("NO CASH ON DELIVERY: Mandatory secure prepaid online payment for fresh perishable items.", "कैश ऑन डिलीवरी उपलब्ध नहीं है। केवल सुरक्षित ऑनलाइन भुगतान स्वीकार्य है।", "কেশ্ব অন ডেলিভাৰী নাই। কেৱল অনলাইন পেমেন্ট গ্ৰহণযোগ্য।")
    )

    fun getString(key: String, lang: Language): String {
        val entry = strings[key] ?: return key
        return when (lang) {
            Language.EN -> entry.first
            Language.HI -> entry.second
            Language.AS -> entry.third
        }
    }
}
