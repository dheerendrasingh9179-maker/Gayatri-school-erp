package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ClassStrengthItem(val name: String, val count: Int, val color: Color, val presentPct: Int = 95)

data class TrendPoint(
    val label: String,
    val feeAmount: Float,      // in thousands e.g. 45.0 = ₹45,000
    val attendancePct: Float,  // percentage e.g. 94.5f
    val presentCount: Int,
    val totalCount: Int = 582
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTrendChartsCard(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Fee Collection, 1: Attendance Trends
    var selectedTimeframe by remember { mutableStateOf("July 2026") }
    var timeframeMenuExpanded by remember { mutableStateOf(false) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(5) } // Default highlight on latest point

    val samplePoints = remember {
        listOf(
            TrendPoint("01 Jul", 28.5f, 92.0f, 535),
            TrendPoint("05 Jul", 35.0f, 94.2f, 548),
            TrendPoint("10 Jul", 52.0f, 91.5f, 532),
            TrendPoint("15 Jul", 48.0f, 95.8f, 557),
            TrendPoint("20 Jul", 72.5f, 93.0f, 541),
            TrendPoint("25 Jul", 84.0f, 96.2f, 560),
            TrendPoint("31 Jul", 65.8f, 94.5f, 550)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_trend_charts_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row with Tabs and Filter Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab Selection Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; selectedPointIndex = 5 },
                        label = { Text("Fee Collection Trend", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0243B6),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; selectedPointIndex = 5 },
                        label = { Text("Attendance Trend", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00A86B),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                // Timeframe Selector Dropdown
                Box {
                    Surface(
                        onClick = { timeframeMenuExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedTimeframe, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = timeframeMenuExpanded,
                        onDismissRequest = { timeframeMenuExpanded = false }
                    ) {
                        listOf("July 2026", "June 2026", "May 2026", "Academic Year 2026-27").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, fontSize = 13.sp) },
                                onClick = {
                                    selectedTimeframe = option
                                    timeframeMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlighted Active Tooltip Box
            selectedPointIndex?.let { idx ->
                val pt = samplePoints.getOrNull(idx)
                if (pt != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == 0) Color(0xFFEFF6FF) else Color(0xFFE6F4EA),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedTab == 0) Color(0xFFBFDBFE) else Color(0xFFA8E6CF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (selectedTab == 0) Icons.Default.TrendingUp else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (selectedTab == 0) Color(0xFF0243B6) else Color(0xFF00A86B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Date: ${pt.label}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            if (selectedTab == 0) {
                                Text(
                                    text = "Collection: ₹ ${String.format("%.1f", pt.feeAmount)}k (Total ₹ 4,25,860)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0243B6)
                                )
                            } else {
                                Text(
                                    text = "Attendance: ${pt.attendancePct}% (${pt.presentCount} / ${pt.totalCount} Present)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00A86B)
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Canvas Area
            val primaryColor = if (selectedTab == 0) Color(0xFF0243B6) else Color(0xFF00A86B)
            val secondaryColor = if (selectedTab == 0) Color(0xFF7367F0) else Color(0xFF28C76F)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Y-Axis Labels
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 8.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        val yLabels = if (selectedTab == 0) {
                            listOf("₹100k", "₹75k", "₹50k", "₹25k", "₹0k")
                        } else {
                            listOf("100%", "95%", "90%", "85%", "80%")
                        }
                        yLabels.forEach { lbl ->
                            Text(text = lbl, fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    // Main Canvas Chart
                    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(selectedTab) {
                                        detectTapGestures { offset ->
                                            val stepX = size.width / (samplePoints.size - 1)
                                            val clickedIdx = (offset.x / stepX).coerceIn(0f, (samplePoints.size - 1).toFloat()).toInt()
                                            selectedPointIndex = clickedIdx
                                        }
                                    }
                            ) {
                                val width = size.width
                                val height = size.height

                                // Draw Grid lines
                                val gridRows = 5
                                for (i in 0 until gridRows) {
                                    val y = height * (i.toFloat() / (gridRows - 1))
                                    drawLine(
                                        color = Color(0xFFF1F5F9),
                                        start = Offset(0f, y),
                                        end = Offset(width, y),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }

                                if (selectedTab == 0) {
                                    // Fee Collection Line Chart
                                    val maxVal = 100f
                                    val stepX = width / (samplePoints.size - 1)
                                    val points = mutableListOf<Offset>()

                                    val linePath = Path()
                                    val areaPath = Path()

                                    samplePoints.forEachIndexed { index, pt ->
                                        val x = index * stepX
                                        val normalized = (pt.feeAmount / maxVal).coerceIn(0f, 1f)
                                        val y = height - (normalized * height)
                                        points.add(Offset(x, y))

                                        if (index == 0) {
                                            linePath.moveTo(x, y)
                                            areaPath.moveTo(x, height)
                                            areaPath.lineTo(x, y)
                                        } else {
                                            linePath.lineTo(x, y)
                                            areaPath.lineTo(x, y)
                                        }
                                    }
                                    areaPath.lineTo(width, height)
                                    areaPath.close()

                                    // Area fill
                                    drawPath(
                                        path = areaPath,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent)
                                        )
                                    )

                                    // Smooth line
                                    drawPath(
                                        path = linePath,
                                        color = primaryColor,
                                        style = Stroke(width = 3.dp.toPx())
                                    )

                                    // Dots and highlight selection
                                    points.forEachIndexed { index, point ->
                                        val isSelected = selectedPointIndex == index
                                        val radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx()

                                        if (isSelected) {
                                            drawCircle(
                                                color = primaryColor.copy(alpha = 0.2f),
                                                radius = 12.dp.toPx(),
                                                center = point
                                            )
                                        }

                                        drawCircle(
                                            color = Color.White,
                                            radius = radius,
                                            center = point
                                        )
                                        drawCircle(
                                            color = if (isSelected) Color(0xFFEA5455) else primaryColor,
                                            radius = radius * 0.65f,
                                            center = point
                                        )
                                    }
                                } else {
                                    // Attendance Bars & Trend Line
                                    val minPct = 80f
                                    val maxPct = 100f
                                    val barWidth = 24.dp.toPx()
                                    val count = samplePoints.size
                                    val spacing = width / count

                                    samplePoints.forEachIndexed { index, pt ->
                                        val xCenter = (index * spacing) + (spacing / 2)
                                        val pctNormalized = ((pt.attendancePct - minPct) / (maxPct - minPct)).coerceIn(0.1f, 1f)
                                        val barHeight = height * pctNormalized
                                        val topY = height - barHeight

                                        val isSelected = selectedPointIndex == index
                                        val barColor = if (isSelected) Color(0xFF00A86B) else Color(0xFF28C76F).copy(alpha = 0.65f)

                                        // Draw Attendance Rounded Bar
                                        drawRoundRect(
                                            color = barColor,
                                            topLeft = Offset(xCenter - (barWidth / 2), topY),
                                            size = Size(barWidth, barHeight),
                                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                        )

                                        if (isSelected) {
                                            drawCircle(
                                                color = Color(0xFF00A86B),
                                                radius = 4.dp.toPx(),
                                                center = Offset(xCenter, topY - 8.dp.toPx())
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // X-Axis Labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            samplePoints.forEachIndexed { idx, pt ->
                                val isSelected = selectedPointIndex == idx
                                Text(
                                    text = pt.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) primaryColor else Color(0xFF94A3B8),
                                    modifier = Modifier.clickable { selectedPointIndex = idx }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Metrics Bar
            if (selectedTab == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricColumn(title = "Total Collection", amount = "₹ 4,25,860", color = Color(0xFF0243B6))
                    MetricColumn(title = "This Month Expenses", amount = "₹ 1,85,420", color = Color(0xFFEA5455))
                    MetricColumn(title = "This Month Income", amount = "₹ 6,11,280", color = Color(0xFF28C76F))
                    MetricColumn(title = "Net Balance", amount = "₹ 4,25,860", color = Color(0xFF0243B6))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricColumn(title = "Avg Attendance Rate", amount = "94.2%", color = Color(0xFF00A86B))
                    MetricColumn(title = "Present Today", amount = "526 Students", color = Color(0xFF0243B6))
                    MetricColumn(title = "Absent Today", amount = "42 Students", color = Color(0xFFEA5455))
                    MetricColumn(title = "Staff Attendance", amount = "98.5%", color = Color(0xFF7367F0))
                }
            }
        }
    }
}

// Backward compatibility alias
@Composable
fun MonthlyFeeCollectionChart(
    totalCollection: String = "₹ 4,25,860",
    thisMonthExpense: String = "₹ 1,85,420",
    thisMonthIncome: String = "₹ 6,11,280",
    netBalance: String = "₹ 4,25,860",
    modifier: Modifier = Modifier
) {
    DashboardTrendChartsCard(modifier = modifier)
}

@Composable
private fun MetricColumn(title: String, amount: String, color: Color) {
    Column {
        Text(text = title, fontSize = 11.sp, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun ClassWiseStrengthDonutChart(
    items: List<ClassStrengthItem>,
    totalCount: Int = 582,
    modifier: Modifier = Modifier
) {
    var selectedItemIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("class_wise_strength_chart"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Class Wise Strength & Attendance",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "582 Enrolled",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0243B6),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Donut Chart Canvas
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(items) {
                                detectTapGestures { offset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val dx = offset.x - center.x
                                    val dy = offset.y - center.y
                                    var angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    if (angle < 0) angle += 360f

                                    // Offset by start angle (-90 -> 270)
                                    var normAngle = (angle + 90f) % 360f
                                    val total = items.sumOf { it.count }.toFloat().coerceAtLeast(1f)
                                    var current = 0f

                                    items.forEachIndexed { index, item ->
                                        val sweep = (item.count / total) * 360f
                                        if (normAngle >= current && normAngle < current + sweep) {
                                            selectedItemIndex = if (selectedItemIndex == index) null else index
                                            return@detectTapGestures
                                        }
                                        current += sweep
                                    }
                                }
                            }
                    ) {
                        val strokeWidth = 22.dp.toPx()
                        val total = items.sumOf { it.count }.toFloat().coerceAtLeast(1f)
                        var startAngle = -90f

                        items.forEachIndexed { index, item ->
                            val sweepAngle = (item.count / total) * 360f
                            val isSelected = selectedItemIndex == index
                            val effectiveStroke = if (isSelected) strokeWidth + 6.dp.toPx() else strokeWidth

                            drawArc(
                                color = if (isSelected) item.color.copy(alpha = 1f) else item.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 2f,
                                useCenter = false,
                                style = Stroke(width = effectiveStroke)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val activeItem = selectedItemIndex?.let { items.getOrNull(it) }
                        if (activeItem != null) {
                            Text(
                                text = "${activeItem.count}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = activeItem.color
                            )
                            Text(
                                text = activeItem.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        } else {
                            Text(
                                text = "$totalCount",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Total Students",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Legend Grid List
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            pair.forEach { item ->
                                val itemIdx = items.indexOf(item)
                                val isSelected = selectedItemIndex == itemIdx

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSelected) item.color.copy(alpha = 0.12f) else Color.Transparent)
                                        .clickable { selectedItemIndex = if (isSelected) null else itemIdx }
                                        .padding(vertical = 2.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(item.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${item.count}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
