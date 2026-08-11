package com.example.data

import com.example.model.Category
import com.example.model.Coupon
import com.example.model.Product

object MockCatalog {

    val categories = listOf(
        Category("cat_veg", "Vegetables & Fruits", listOf("Fresh Vegetables", "Organic Fruits", "Exotic Produce"), "veg"),
        Category("cat_dairy", "Dairy, Bread & Eggs", listOf("Milk & Butter", "Eggs & Bread", "Paneer & Cheese"), "dairy"),
        Category("cat_instant", "10-Min Fast Food & Meals", listOf("Ramen & Noodles", "Ready Meals", "Snack Bowls"), "instant"),
        Category("cat_snacks", "Snacks & Munchies", listOf("Chips & Crisps", "Biscuits", "Nuts & Seeds"), "snacks"),
        Category("cat_drinks", "Cold Drinks & Juices", listOf("Soft Drinks", "Energy Drinks", "Fresh Juices"), "drinks"),
        Category("cat_staples", "Atta, Rice & Dals", listOf("Wheat Atta", "Basmati Rice", "Pulses & Lentils"), "staples"),
        Category("cat_sweets", "Chocolates & Ice Cream", listOf("Dark Chocolates", "Tub Ice Creams", "Indian Sweets"), "sweets"),
        Category("cat_home", "Cleaning & Essentials", listOf("Detergents", "Paper Towels", "Personal Care"), "home")
    )

    val products = listOf(
        // Vegetables & Fruits
        Product("p101", "Fresh Organic Onions", "cat_veg", "Vegetables & Fruits", 38.0, 50.0, "1 kg", isVeg = true, rating = 4.8f, tag = "10 Min", description = "Farm fresh crispy red onions, essential for everyday cooking."),
        Product("p102", "Hybrid Tomatoes", "cat_veg", "Vegetables & Fruits", 28.0, 40.0, "500 g", isVeg = true, rating = 4.7f, tag = "Farm Fresh", description = "Plump, juicy tomatoes rich in lycopene and vitamin C."),
        Product("p103", "Fresh Green Capsicum", "cat_veg", "Vegetables & Fruits", 24.0, 35.0, "250 g", isVeg = true, rating = 4.6f, tag = "10 Min"),
        Product("p104", "Robusta Bananas", "cat_veg", "Vegetables & Fruits", 45.0, 60.0, "1 Dozen (12 pcs)", isVeg = true, rating = 4.9f, tag = "Bestseller"),
        Product("p105", "Fresh Coriander Leaves", "cat_veg", "Vegetables & Fruits", 12.0, 20.0, "100 g", isVeg = true, rating = 4.5f),
        Product("p106", "Shimla Apples (Crisp)", "cat_veg", "Vegetables & Fruits", 140.0, 180.0, "4 pcs (approx 600g)", isVeg = true, rating = 4.8f, tag = "Premium"),

        // Dairy, Bread & Eggs
        Product("p201", "Amul Taaza Toned Milk", "cat_dairy", "Dairy, Bread & Eggs", 28.0, 28.0, "500 ml", isVeg = true, rating = 4.9f, tag = "Bestseller", description = "Pasteurised toned milk with 3% fat and 8.5% SNF."),
        Product("p202", "Amul Pasteurised Butter", "cat_dairy", "Dairy, Bread & Eggs", 58.0, 60.0, "100 g", isVeg = true, rating = 4.9f, tag = "10 Min"),
        Product("p203", "Fresh Farm White Eggs", "cat_dairy", "Dairy, Bread & Eggs", 48.0, 65.0, "Pack of 6", isVeg = false, rating = 4.8f, tag = "High Protein"),
        Product("p204", "Mother Dairy Malai Paneer", "cat_dairy", "Dairy, Bread & Eggs", 92.0, 105.0, "200 g", isVeg = true, rating = 4.7f, tag = "Fresh Today"),
        Product("p205", "Modern 100% Whole Wheat Bread", "cat_dairy", "Dairy, Bread & Eggs", 45.0, 50.0, "400 g", isVeg = true, rating = 4.6f),

        // 10-Min Fast Food & Meals
        Product("p301", "Maggi 2-Minute Masala Noodles", "cat_instant", "10-Min Fast Food & Meals", 56.0, 60.0, "Pack of 4 (280g)", isVeg = true, rating = 4.9f, tag = "Classic Favorite"),
        Product("p302", "Ready-to-Eat Butter Chicken with Rice", "cat_instant", "10-Min Fast Food & Meals", 180.0, 220.0, "350 g Bowl", isVeg = false, rating = 4.7f, tag = "Hot Meal"),
        Product("p303", "Paneer Tikka Instant Bowl", "cat_instant", "10-Min Fast Food & Meals", 150.0, 180.0, "300 g Bowl", isVeg = true, rating = 4.8f, tag = "Hot Meal"),
        Product("p304", "Knorr Classic Tomato Soup", "cat_instant", "10-Min Fast Food & Meals", 55.0, 65.0, "4 Servings (53g)", isVeg = true, rating = 4.5f),

        // Snacks & Munchies
        Product("p401", "Lay's India's Magic Masala Chips", "cat_snacks", "Snacks & Munchies", 20.0, 20.0, "50 g", isVeg = true, rating = 4.8f, tag = "Bestseller"),
        Product("p402", "Doritos Nacho Cheese Tortilla Chips", "cat_snacks", "Snacks & Munchies", 50.0, 50.0, "82.5 g", isVeg = true, rating = 4.7f, tag = "Crunchy"),
        Product("p403", "Haldiram's Nagpur Aloo Bhujia", "cat_snacks", "Snacks & Munchies", 52.0, 60.0, "200 g", isVeg = true, rating = 4.9f),
        Product("p404", "Roasted Salted Almonds", "cat_snacks", "Snacks & Munchies", 199.0, 250.0, "200 g", isVeg = true, rating = 4.8f, tag = "Healthy Snack"),

        // Cold Drinks & Juices
        Product("p501", "Coca-Cola Original Taste", "cat_drinks", "Cold Drinks & Juices", 40.0, 40.0, "750 ml Pet Bottle", isVeg = true, rating = 4.8f, tag = "Chilled"),
        Product("p502", "Real Fruit Power 100% Mixed Fruit Juice", "cat_drinks", "Cold Drinks & Juices", 110.0, 130.0, "1 Litre Tetra Pack", isVeg = true, rating = 4.7f, tag = "No Added Sugar"),
        Product("p503", "Red Bull Energy Drink", "cat_drinks", "Cold Drinks & Juices", 125.0, 125.0, "250 ml Can", isVeg = true, rating = 4.9f, tag = "Energy Boost"),

        // Atta, Rice & Dals
        Product("p601", "Aashirvaad Shudh Chakki Atta", "cat_staples", "Atta, Rice & Dals", 260.0, 310.0, "5 kg Bag", isVeg = true, rating = 4.9f, tag = "Super Saver"),
        Product("p602", "Fortune Everyday Basmati Rice", "cat_staples", "Atta, Rice & Dals", 145.0, 190.0, "1 kg Pack", isVeg = true, rating = 4.8f),
        Product("p603", "Tata Sampann Unpolished Toor Dal", "cat_staples", "Atta, Rice & Dals", 165.0, 195.0, "1 kg Pack", isVeg = true, rating = 4.8f),

        // Sweets & Ice Cream
        Product("p701", "Amul Real Milk Vanilla Gold Ice Cream", "cat_sweets", "Chocolates & Ice Cream", 180.0, 210.0, "1 Litre Tub", isVeg = true, rating = 4.9f, tag = "Frozen Cold"),
        Product("p702", "Cadbury Dairy Milk Silk Chocolate", "cat_sweets", "Chocolates & Ice Cream", 80.0, 90.0, "60 g Bar", isVeg = true, rating = 4.9f, tag = "Indulgence"),

        // Cleaning & Essentials
        Product("p801", "Surf Excel Easy Wash Liquid Detergent", "cat_home", "Cleaning & Essentials", 215.0, 250.0, "1 Litre Bottle", isVeg = true, rating = 4.8f),
        Product("p802", "Dettol Liquid Handwash Refill", "cat_home", "Cleaning & Essentials", 99.0, 120.0, "750 ml Pouch", isVeg = true, rating = 4.9f)
    )

    val coupons = listOf(
        Coupon("QUICK50", discountFlat = 50.0, minOrderValue = 199.0, title = "₹50 Instant Discount", description = "Get ₹50 flat off on orders above ₹199"),
        Coupon("WELCOME100", discountPercent = 20, minOrderValue = 299.0, title = "20% OFF First Order", description = "Save 20% up to ₹100 on orders above ₹299"),
        Coupon("FREESHIP", discountFlat = 15.0, minOrderValue = 149.0, title = "Free Delivery", description = "Waive ₹15 delivery fee on orders above ₹149")
    )
}
