package com.wolttracker.app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.wolttracker.app.util.WoltScreenshotParser
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.wolttracker.app.R
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.wolttracker.app.data.model.DailySummary
import com.wolttracker.app.data.model.RecentEntry
import com.wolttracker.app.data.model.ShiftEntity
import com.wolttracker.app.ui.theme.TipsGreen
import com.wolttracker.app.ui.theme.WoltCardBg
import com.wolttracker.app.ui.theme.WoltCardStroke
import com.wolttracker.app.ui.theme.WoltCyan
import com.wolttracker.app.ui.theme.WoltDarkBg
import com.wolttracker.app.ui.theme.WoltTrackerTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainDashboardContent(
        uiState = uiState,
        onNewEntryClick = viewModel::onNewEntryClicked,
        onDeleteShift = viewModel::deleteShift,
        modifier = modifier
    )

    if (uiState.showAddDialog) {
        AddEntryDialog(
            onDismiss = viewModel::onDismissAddDialog,
            onAddShift = viewModel::addShiftWithTimes
        )
    }
}

@Composable
fun MainDashboardContent(
    uiState: MainDashboardUiState,
    onNewEntryClick: () -> Unit,
    onDeleteShift: (ShiftEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WoltDarkBg,
        topBar = {
            TopAppBarContent(
                selectedTab = selectedTab,
                onNewEntryClick = onNewEntryClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeDashboardTab(uiState = uiState)
                1 -> ShiftsTab(shifts = uiState.allShifts, onDeleteShift = onDeleteShift)
                2 -> ReportsTab(shifts = uiState.allShifts)
            }
        }
    }
}

@Composable
private fun TopAppBarContent(
    selectedTab: Int,
    onNewEntryClick: () -> Unit
) {
    val screenTitle = when (selectedTab) {
        0 -> "Wolt Partner Stats"
        1 -> "Βάρδιες"
        2 -> "Αναφορές"
        else -> "Wolt Partner Stats"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (selectedTab == 0) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(TipsGreen, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = screenTitle,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF151D2A))
                .border(1.dp, WoltCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .clickable { onNewEntryClick() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "+ Νέα",
                color = WoltCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 0: ΑΡΧΙΚΗ (HOME DASHBOARD)
// -------------------------------------------------------------
@Composable
private fun HomeDashboardTab(uiState: MainDashboardUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DateHeader(formattedDate = uiState.formattedDate)
        }
        item {
            HeroProfitCard(summary = uiState.dailySummary)
        }
        item {
            Text(
                text = "Στατιστικά Βάρδιας",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            MetricsGrid(summary = uiState.dailySummary)
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Τελευταίες Καταχωρήσεις",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Σήμερα",
                    color = WoltCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (uiState.recentEntries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WoltCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WoltCardStroke))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Καμία βάρδια για σήμερα.\nΠατήστε '+ Νέα' για καταχώρηση ή screenshot.",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.recentEntries) { entry ->
                RecentEntryItem(entry = entry)
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 1: ΒΑΡΔΙΕΣ (SHIFTS LIST)
// -------------------------------------------------------------
@Composable
private fun ShiftsTab(
    shifts: List<ShiftEntity>,
    onDeleteShift: (ShiftEntity) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ιστορικό Βαρδιών (${shifts.size})",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                val totalShiftEarnings = shifts.sumOf { it.grossIncome + it.tips }
                Text(
                    text = "Σύνολο: " + String.format(Locale.GERMANY, "%.2f kr.", totalShiftEarnings),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (shifts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Δεν υπάρχουν καταχωρημένες βάρδιες ακόμα.",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(shifts, key = { it.id }) { shift ->
                val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
                val startFormatted = timeFormatter.format(Date(shift.startTimeMillis))
                val endFormatted = timeFormatter.format(Date(shift.endTimeMillis))
                val totalShiftMoney = shift.grossIncome + shift.tips

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WoltCardBg)
                        .border(1.dp, WoltCardStroke, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$startFormatted - $endFormatted",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = String.format(Locale.GERMANY, "%.2f kr.", totalShiftMoney),
                                color = WoltCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${shift.durationHours} ώρες • ${shift.ordersCount} παραγγελίες",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Tips: " + String.format(Locale.GERMANY, "%.2f kr.", shift.tips),
                                color = TipsGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateFormatter.format(Date(shift.startTimeMillis)),
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Διαγραφή",
                                color = Color(0xFFEF4444).copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable { onDeleteShift(shift) }
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 2: ΑΝΑΦΟΡΕΣ (REPORTS / STATS)
// -------------------------------------------------------------
private enum class ReportPeriod {
    MONTHLY,
    ALL_TIME
}

@Composable
private fun ReportsTab(
    shifts: List<ShiftEntity>
) {
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.MONTHLY) }
    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    val targetYear = selectedCalendar.get(Calendar.YEAR)
    val targetMonth = selectedCalendar.get(Calendar.MONTH)

    val filteredShifts = remember(shifts, selectedPeriod, targetYear, targetMonth) {
        if (selectedPeriod == ReportPeriod.ALL_TIME) {
            shifts
        } else {
            shifts.filter { shift ->
                val cal = Calendar.getInstance().apply { timeInMillis = shift.dateMillis }
                cal.get(Calendar.YEAR) == targetYear && cal.get(Calendar.MONTH) == targetMonth
            }
        }
    }

    val totalGross = filteredShifts.sumOf { it.grossIncome }
    val totalTips = filteredShifts.sumOf { it.tips }
    val totalHours = filteredShifts.sumOf { it.durationHours }
    val totalOrders = filteredShifts.sumOf { it.ordersCount }
    val totalEarnings = totalGross + totalTips

    val hourlyRate = if (totalHours > 0) totalEarnings / totalHours else 0.0
    val perOrderRate = if (totalOrders > 0) totalEarnings / totalOrders else 0.0
    val avgPerShift = if (filteredShifts.isNotEmpty()) totalEarnings / filteredShifts.size else 0.0
    val avgHoursPerShift = if (filteredShifts.isNotEmpty()) totalHours / filteredShifts.size else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Period selector toggle: Μηνιαία / Γενικά
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WoltCardBg)
                    .border(1.dp, WoltCardStroke, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isMonthly = selectedPeriod == ReportPeriod.MONTHLY
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isMonthly) WoltCyan.copy(alpha = 0.18f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isMonthly) WoltCyan else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedPeriod = ReportPeriod.MONTHLY }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Μηνιαία",
                        color = if (isMonthly) WoltCyan else Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        fontWeight = if (isMonthly) FontWeight.Bold else FontWeight.Medium
                    )
                }

                val isAllTime = selectedPeriod == ReportPeriod.ALL_TIME
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAllTime) WoltCyan.copy(alpha = 0.18f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isAllTime) WoltCyan else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedPeriod = ReportPeriod.ALL_TIME }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Γενικά (Όλα)",
                        color = if (isAllTime) WoltCyan else Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        fontWeight = if (isAllTime) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // If MONTHLY is selected, show month navigation controls
        if (selectedPeriod == ReportPeriod.MONTHLY) {
            item {
                val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("el-GR")) }
                val monthTitle = monthFormatter.format(selectedCalendar.time).replaceFirstChar { it.uppercase() }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WoltCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WoltCardStroke))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newCal = (selectedCalendar.clone() as Calendar).apply {
                                add(Calendar.MONTH, -1)
                            }
                            selectedCalendar = newCal
                        }) {
                            Text(
                                text = "‹",
                                color = WoltCyan,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = monthTitle,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (filteredShifts.isEmpty()) "Καμία βάρδια" else "${filteredShifts.size} βάρδιες",
                                color = if (filteredShifts.isEmpty()) Color(0xFFEF4444) else Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(onClick = {
                            val newCal = (selectedCalendar.clone() as Calendar).apply {
                                add(Calendar.MONTH, 1)
                            }
                            selectedCalendar = newCal
                        }) {
                            Text(
                                text = "›",
                                color = WoltCyan,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // ALL_TIME subtitle badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ιστορικό Όλων των Ετών",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${filteredShifts.size} βάρδιες συνολικά",
                        color = WoltCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Hero card: Συνολικά Έσοδα
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WoltCardBg),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WoltCardStroke))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedPeriod == ReportPeriod.MONTHLY) "ΕΣΟΔΑ ΜΗΝΑ" else "ΣΥΝΟΛΙΚΑ ΕΣΟΔΑ",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(Locale.GERMANY, "%.2f kr.", totalEarnings),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Βασική: " + String.format(Locale.GERMANY, "%.2f kr.", totalGross),
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "•",
                            color = Color(0xFF475569),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Tips: " + String.format(Locale.GERMANY, "%.2f kr.", totalTips),
                            color = TipsGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Αποδοτικότητα",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Έσοδα / Ώρα",
                    value = String.format(Locale.GERMANY, "%.2f kr./h", hourlyRate),
                    valueColor = WoltCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Έσοδα / Παραγγελία",
                    value = String.format(Locale.GERMANY, "%.2f kr.", perOrderRate),
                    valueColor = WoltCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Σύνολο Ωρών",
                    value = "$totalHours ώρες",
                    valueColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Σύνολο Παραγγελιών",
                    value = "$totalOrders",
                    valueColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Μέσος Όρος / Βάρδια",
                    value = String.format(Locale.GERMANY, "%.2f kr.", avgPerShift),
                    valueColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Μ.Ο. Ώρες / Βάρδια",
                    value = String.format(Locale.GERMANY, "%.1f ώρες", avgHoursPerShift),
                    valueColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DateHeader(formattedDate: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedDate,
            color = Color(0xFF94A3B8),
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun HeroProfitCard(summary: DailySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WoltCardBg),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WoltCardStroke))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ΣΥΝΟΛΙΚΑ ΕΣΟΔΑ ΣΗΜΕΡΑ",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = String.format(Locale.GERMANY, "%.2f kr.", summary.totalEarnings),
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = Color(0xFF1E293B),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Βασική Αμοιβή",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                    Text(
                        text = String.format(Locale.GERMANY, "%.2f kr.", summary.grossIncome),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tips",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                    Text(
                        text = String.format(Locale.GERMANY, "+%.2f kr.", summary.totalTips),
                        color = TipsGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricsGrid(summary: DailySummary) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Ώρες Εργασίας",
                value = "${summary.totalHours} ώρες",
                valueColor = Color.White,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Παραγγελίες",
                value = "${summary.totalOrders}",
                valueColor = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val hourlyRate = if (summary.totalHours > 0) summary.totalEarnings / summary.totalHours else 0.0
            MetricCard(
                title = "Έσοδα / Ώρα",
                value = String.format(Locale.GERMANY, "%.2f kr./h", hourlyRate),
                valueColor = WoltCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Tips",
                value = String.format(Locale.GERMANY, "%.2f kr.", summary.totalTips),
                valueColor = TipsGreen,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(WoltCardBg)
            .border(1.dp, WoltCardStroke, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = title,
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecentEntryItem(entry: RecentEntry) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WoltCardBg)
            .border(1.dp, WoltCardStroke, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        when (entry) {
            is RecentEntry.Shift -> {
                val startFormatted = timeFormatter.format(Date(entry.shift.startTimeMillis))
                val endFormatted = timeFormatter.format(Date(entry.shift.endTimeMillis))
                val totalShiftMoney = entry.shift.grossIncome + entry.shift.tips

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$startFormatted - $endFormatted",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${entry.shift.ordersCount} παρ.",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${entry.shift.durationHours} ώρες • Tips: " + String.format(Locale.GERMANY, "%.2f kr.", entry.shift.tips),
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = String.format(Locale.GERMANY, "+%.2f kr.", totalShiftMoney),
                        color = WoltCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF0B0F17),
        tonalElevation = 0.dp
    ) {
        val navItems = listOf(
            R.drawable.ic_nav_home to "Αρχική",
            R.drawable.ic_nav_shifts to "Βάρδιες",
            R.drawable.ic_nav_reports to "Αναφορές"
        )

        navItems.forEachIndexed { index, (iconRes, label) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = label,
                            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 4.dp else 0.dp)
                                .clip(CircleShape)
                                .background(WoltCyan)
                        )
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryDialog(
    onDismiss: () -> Unit,
    onAddShift: (startHour: Int, startMinute: Int, endHour: Int, endMinute: Int, orders: Int, income: Double, tips: Double) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var startTimeStr by remember { mutableStateOf("10:00") }
    var endTimeStr by remember { mutableStateOf("15:30") }
    var shiftOrders by remember { mutableStateOf("") }
    var shiftIncome by remember { mutableStateOf("") }
    var shiftTips by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            isScanning = true
            coroutineScope.launch {
                val parsed = WoltScreenshotParser.parseFromUri(context, uri)
                startTimeStr = String.format(Locale.getDefault(), "%02d:%02d", parsed.startHour, parsed.startMinute)
                endTimeStr = String.format(Locale.getDefault(), "%02d:%02d", parsed.endHour, parsed.endMinute)
                if (parsed.orders > 0) {
                    shiftOrders = parsed.orders.toString()
                }
                if (parsed.income > 0) {
                    shiftIncome = String.format(Locale.US, "%.2f", parsed.income)
                }
                if (parsed.tips > 0) {
                    shiftTips = String.format(Locale.US, "%.2f", parsed.tips)
                }
                isScanning = false
            }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = WoltCyan,
        unfocusedBorderColor = Color(0xFF334155),
        focusedLabelColor = WoltCyan,
        unfocusedLabelColor = Color(0xFF94A3B8),
        cursorColor = WoltCyan
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WoltCardBg,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Νέα Βάρδια",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = WoltCyan,
                        strokeWidth = 2.dp
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Κουμπί Σκαναρίσματος Screenshot
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF151D2A),
                        contentColor = WoltCyan
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WoltCyan.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (isScanning) "Ανάγνωση εικόνας..." else "Σκανάρισμα από Screenshot",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startTimeStr,
                        onValueChange = { startTimeStr = it },
                        label = { Text("Έναρξη (π.χ. 10:00)") },
                        colors = textFieldColors,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTimeStr,
                        onValueChange = { endTimeStr = it },
                        label = { Text("Λήξη (π.χ. 15:30)") },
                        colors = textFieldColors,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = shiftOrders,
                    onValueChange = { shiftOrders = it },
                    label = { Text("Αριθμός Παραγγελιών") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = shiftIncome,
                        onValueChange = { shiftIncome = it },
                        label = { Text("Έσοδα kr.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = textFieldColors,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = shiftTips,
                        onValueChange = { shiftTips = it },
                        label = { Text("Tips kr.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = textFieldColors,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startParts = startTimeStr.split(":")
                    val sHour = startParts.getOrNull(0)?.trim()?.toIntOrNull() ?: 10
                    val sMin = startParts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0

                    val endParts = endTimeStr.split(":")
                    val eHour = endParts.getOrNull(0)?.trim()?.toIntOrNull() ?: 15
                    val eMin = endParts.getOrNull(1)?.trim()?.toIntOrNull() ?: 30

                    val orders = shiftOrders.toIntOrNull() ?: 0
                    val income = shiftIncome.toDoubleOrNull() ?: 0.0
                    val tips = shiftTips.toDoubleOrNull() ?: 0.0

                    onAddShift(sHour, sMin, eHour, eMin, orders, income, tips)
                },
                colors = ButtonDefaults.buttonColors(containerColor = WoltCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Αποθήκευση", color = Color(0xFF0B0F17), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ακύρωση", color = Color(0xFF94A3B8))
            }
        }
    )
}

