package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockCatalog
import com.example.model.Product
import com.example.ui.QuickCartViewModel
import com.example.ui.components.ProductCard
import com.example.ui.theme.HighDensityBackground
import com.example.ui.theme.HighDensityBadgeRed
import com.example.ui.theme.HighDensityContainer
import com.example.ui.theme.HighDensityOnContainer
import com.example.ui.theme.HighDensityOutline
import com.example.ui.theme.HighDensityPurple
import com.example.ui.theme.HighDensityPurpleLight
import com.example.ui.theme.HighDensitySecondaryBg
import com.example.ui.theme.HighDensitySurfaceVariant
import com.example.ui.theme.HighDensityTextPrimary
import com.example.ui.theme.HighDensityTextSecondary
import com.example.ui.theme.QuickCartAmber
import com.example.ui.theme.QuickCartGreen
import com.example.ui.theme.QuickCartGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: QuickCartViewModel,
    onOpenCart: () -> Unit,
    onOpenOrdersHistory: () -> Unit,
    onOpenPartnerPortal: () -> Unit,
    onOpenAiRecipe: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryId.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var activeBottomTab by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            Column {
                // Sticky Floating Cart Bar (if items in cart)
                if (cartCount > 0) {
                    Surface(
                        color = HighDensityPurple,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenCart() }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                .testTag("floating_cart_bar"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "$cartCount ITEMS • ₹${cartSubtotal.toInt()}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Extra discounts applied at checkout",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "View Cart →",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // High Density Bottom Navigation Bar
                Surface(
                    color = HighDensitySurfaceVariant,
                    tonalElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HighDensityOutline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { activeBottomTab = "home" }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (activeBottomTab == "home") HighDensitySecondaryBg else Color.Transparent)
                                    .padding(horizontal = 18.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home",
                                    tint = HighDensityOnContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Home",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityOnContainer
                            )
                        }

                        // Categories Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    activeBottomTab = "categories"
                                    viewModel.selectCategory(null)
                                }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (activeBottomTab == "categories") HighDensitySecondaryBg else Color.Transparent)
                                    .padding(horizontal = 18.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = "Categories",
                                    tint = HighDensityTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Categories",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = HighDensityTextSecondary
                            )
                        }

                        // Offers Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onOpenAiRecipe() }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Transparent)
                                    .padding(horizontal = 18.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Recipes",
                                    tint = HighDensityPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "AI Meals",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = HighDensityTextSecondary
                            )
                        }

                        // Cart / Orders Tab
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onOpenCart() }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (activeBottomTab == "cart") HighDensitySecondaryBg else Color.Transparent)
                                    .padding(horizontal = 18.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Cart",
                                        tint = HighDensityTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (cartCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(HighDensityBadgeRed),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$cartCount",
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = if (cartCount > 0) "₹${cartSubtotal.toInt()}" else "Cart",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityTextPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(HighDensityBackground)
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // High Density Header Section
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Top Bar: Delivery Time Banner + Address + Profile Avatar + Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DELIVERY IN 8 MINS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HighDensityPurple,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { /* Address selector */ }
                            ) {
                                Text(
                                    text = selectedAddress?.let { "${it.title}, ${it.addressLine}" } ?: "Home, Sector 62",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Address",
                                    tint = HighDensityTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = onOpenOrdersHistory,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("orders_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Orders",
                                    tint = HighDensityTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = onOpenPartnerPortal,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("partner_portal_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBike,
                                    contentDescription = "Partner Mode",
                                    tint = HighDensityPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            // High Density Avatar Badge
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(HighDensityContainer)
                                    .border(1.dp, HighDensityPurple, CircleShape)
                                    .clickable { onOpenOrdersHistory() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "JD",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityOnContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // High Density Search Bar (Pill with voice/AI trigger)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(CircleShape)
                            .border(1.dp, HighDensityOutline, CircleShape)
                            .testTag("search_input"),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = HighDensityTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        text = "Search 'milk', 'egg' or 'bread'",
                                        fontSize = 13.sp,
                                        color = HighDensityTextSecondary
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )

                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(HighDensityOutline)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // AI Meal / Voice Search Button Trigger
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { onOpenAiRecipe() }
                                    .padding(4.dp)
                                    .testTag("ai_assistant_trigger"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Meal Builder",
                                    tint = HighDensityPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // High Density Banner Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("hero_banner_card")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(HighDensityPurple, HighDensityPurpleLight)
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Up to 50% OFF",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "On your first 3 orders",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .padding(horizontal = 10.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "USE CODE: FIRST50",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = HighDensityPurple
                                        )
                                    }
                                }

                                // Right Decorative Graphic Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // High Density Categories Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Shop by Category",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = HighDensityTextPrimary
                        )
                        Text(
                            text = "See all",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighDensityPurple,
                            modifier = Modifier.clickable { viewModel.selectCategory(null) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // High Density Category Grid Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val categoryEmojis = mapOf(
                            "cat_veg" to "🥦",
                            "cat_dairy" to "🥛",
                            "cat_instant" to "🍎",
                            "cat_snacks" to "🥤"
                        )

                        MockCatalog.categories.take(4).forEach { cat ->
                            val isSelected = selectedCategory == cat.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (isSelected) viewModel.selectCategory(null)
                                        else viewModel.selectCategory(cat.id)
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) HighDensityContainer else HighDensitySecondaryBg)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) HighDensityPurple else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = categoryEmojis[cat.id] ?: "🧺",
                                        fontSize = 24.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cat.name.split(",")[0].split("&")[0].trim(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = HighDensityTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Chips Bar (Secondary Filter)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryChip(
                                title = "All Items",
                                isSelected = selectedCategory == null,
                                onClick = { viewModel.selectCategory(null) }
                            )
                        }
                        items(MockCatalog.categories) { cat ->
                            CategoryChip(
                                title = cat.name,
                                isSelected = selectedCategory == cat.id,
                                onClick = { viewModel.selectCategory(cat.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (selectedCategory == null) "Bestsellers" else "Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = HighDensityTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // High Density Product Cards Grid
            items(products) { product ->
                val quantityInCart = cartItems.find { it.productId == product.id }?.quantity ?: 0
                val isFav = favoriteIds.contains(product.id)

                ProductCard(
                    product = product,
                    cartQuantity = quantityInCart,
                    isFavorite = isFav,
                    onAddToCart = { viewModel.addToCart(product, 1) },
                    onRemoveFromCart = { viewModel.addToCart(product, -1) },
                    onToggleFavorite = { viewModel.toggleFavorite(product.id) },
                    onClick = { onProductClick(product) }
                )
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) HighDensityPurple else HighDensitySecondaryBg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else HighDensityOnContainer
        )
    }
}
