package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.QuickCartViewModel
import com.example.ui.theme.QuickCartAmber
import com.example.ui.theme.QuickCartGreen
import com.example.ui.theme.QuickCartGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    orderId: String,
    viewModel: QuickCartViewModel,
    onNavigateHome: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val riderProgress by viewModel.riderProgress.collectAsState()
    val currentOrder = orders.find { it.orderId == orderId } ?: orders.firstOrNull()

    val animatedProgress by animateFloatAsState(
        targetValue = riderProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "rider_anim"
    )

    val etaDisplay = when {
        animatedProgress >= 1.0f -> "Delivered!"
        animatedProgress >= 0.8f -> "Arriving in 2 Mins"
        animatedProgress >= 0.5f -> "Arriving in 6 Mins"
        animatedProgress >= 0.25f -> "Arriving in 9 Mins"
        else -> "Packing at Dark Store (11 Mins)"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Live Order #${currentOrder?.orderId ?: orderId}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = etaDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = QuickCartGreen
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateHome,
                        modifier = Modifier.testTag("tracking_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Call Support */ }) {
                        Icon(Icons.Default.SupportAgent, contentDescription = "Support", tint = QuickCartGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // ETA Banner Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = QuickCartGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("eta_banner_card")
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = etaDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Status: ${currentOrder?.status ?: "Out for Delivery"}",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBike,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = QuickCartAmber,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // Canvas Map Route Simulation
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Live Route Map",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE8ECEF))
                        ) {
                            val mapBgColor = Color(0xFFD3DCE3)
                            val roadColor = Color.White
                            val primaryGreen = QuickCartGreen

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val startX = size.width * 0.15f
                                val startY = size.height * 0.5f
                                val endX = size.width * 0.85f
                                val endY = size.height * 0.5f

                                // Draw road path
                                drawLine(
                                    color = roadColor,
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = 24.dp.toPx()
                                )

                                // Draw dashed route line
                                drawLine(
                                    color = primaryGreen,
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = 6.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                )
                            }

                            // Dark Store Icon Pin (Start)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 16.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(QuickCartGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = "Dark Store", tint = Color.White, modifier = Modifier.size(20.dp))
                            }

                            // User House Pin (End)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(QuickCartAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Home, contentDescription = "Destination", tint = Color.Black, modifier = Modifier.size(20.dp))
                            }

                            // Moving Rider Icon
                            val startPad = 16.dp
                            val endPad = 16.dp
                            val startXFrac = 0.15f
                            val endXFrac = 0.85f
                            val currentXFrac = startXFrac + (endXFrac - startXFrac) * animatedProgress

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = (20 + (230 * animatedProgress)).dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(2.dp, QuickCartGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsBike, contentDescription = "Rider", tint = QuickCartGreen, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            // Rider Contact Details Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(QuickCartGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = QuickCartGreen)
                            }
                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Ramesh Kumar (Rider)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Scooter: DL 8S 4921 • 4.9 ★",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Button(
                            onClick = { /* Call Rider */ },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = QuickCartGreenLight),
                            modifier = Modifier.size(44.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call Rider", tint = QuickCartGreen)
                        }
                    }
                }
            }

            // Order Pipeline Status
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Delivery Pipeline",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        PipelineStep("Order Placed", "Confirmed by Dark Store", isDone = animatedProgress >= 0.05f)
                        PipelineStep("Items Packed", "Checked & sealed in express bag", isDone = animatedProgress >= 0.25f)
                        PipelineStep("Out for Delivery", "Ramesh on his electric scooter", isDone = animatedProgress >= 0.5f)
                        PipelineStep("Order Delivered", "Handed over at doorstep", isDone = animatedProgress >= 1.0f)
                    }
                }
            }

            // Order Items Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Items Ordered",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentOrder?.itemsSummary ?: "Groceries & Food items",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Paid:", fontWeight = FontWeight.Bold)
                            Text(text = "₹${currentOrder?.totalAmount?.toInt() ?: 0}", fontWeight = FontWeight.ExtraBold, color = QuickCartGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PipelineStep(title: String, subtitle: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isDone) QuickCartGreen else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (isDone) MaterialTheme.colorScheme.onSurface else Color.Gray
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
