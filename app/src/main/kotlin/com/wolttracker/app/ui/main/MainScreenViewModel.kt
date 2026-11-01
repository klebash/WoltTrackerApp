package com.wolttracker.app.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wolttracker.app.data.WoltRepository
import com.wolttracker.app.data.db.WoltTrackerDatabase
import com.wolttracker.app.data.model.DailySummary
import com.wolttracker.app.data.model.ExpenseCategory
import com.wolttracker.app.data.model.ExpenseEntity
import com.wolttracker.app.data.model.RecentEntry
import com.wolttracker.app.data.model.ShiftEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MainDashboardUiState(
    val formattedDate: String = "",
    val dailySummary: DailySummary = DailySummary(),
    val recentEntries: List<RecentEntry> = emptyList(),
    val allShifts: List<ShiftEntity> = emptyList(),
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false
)

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WoltTrackerDatabase.getDatabase(application)
    private val repository = WoltRepository(database.shiftDao(), database.expenseDao())

    private val _showAddDialog = MutableStateFlow(false)

    // Current date range (start & end of today)
    private val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private val startOfDay = calendar.timeInMillis
    private val endOfDay = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    private val greekDateFormatter = SimpleDateFormat("EEEE, d MMMM", Locale("el", "GR"))
    private val formattedDateText = "Σήμερα: " + greekDateFormatter.format(Date()).replaceFirstChar { it.uppercase() }

    val uiState: StateFlow<MainDashboardUiState> = combine(
        repository.getDailySummary(startOfDay, endOfDay),
        repository.getRecentEntries(startOfDay, endOfDay),
        repository.getAllShifts(),
        _showAddDialog
    ) { summary, entries, shifts, showDialog ->
        MainDashboardUiState(
            formattedDate = formattedDateText,
            dailySummary = summary,
            recentEntries = entries,
            allShifts = shifts,
            isLoading = false,
            showAddDialog = showDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainDashboardUiState(formattedDate = formattedDateText, isLoading = true)
    )

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllShifts()
        }
    }

    fun onNewEntryClicked() {
        _showAddDialog.value = true
    }

    fun onDismissAddDialog() {
        _showAddDialog.value = false
    }

    fun addShiftWithTimes(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        orders: Int,
        income: Double,
        tips: Double
    ) {
        viewModelScope.launch {
            val calStart = Calendar.getInstance().apply {
                timeInMillis = startOfDay
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val calEnd = Calendar.getInstance().apply {
                timeInMillis = startOfDay
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var startTime = calStart.timeInMillis
            var endTime = calEnd.timeInMillis
            // Αν η ώρα λήξης είναι μικρότερη (π.χ. πέρασε τα μεσάνυχτα), προσθέτουμε 1 ημέρα στο endTime
            if (endTime <= startTime) {
                endTime += 24 * 3600 * 1000
            }

            val diffMillis = endTime - startTime
            val hours = Math.round((diffMillis / (1000.0 * 3600.0)) * 10.0) / 10.0
            val formattedTitle = String.format(Locale.getDefault(), "%02d:%02d - %02d:%02d", startHour, startMinute, endHour, endMinute)

            repository.insertShift(
                ShiftEntity(
                    title = formattedTitle,
                    dateMillis = startOfDay,
                    startTimeMillis = startTime,
                    endTimeMillis = endTime,
                    durationHours = hours,
                    ordersCount = orders,
                    grossIncome = income,
                    tips = tips
                )
            )
            _showAddDialog.value = false
        }
    }

    fun deleteShift(shift: ShiftEntity) {
        viewModelScope.launch {
            repository.deleteShift(shift)
        }
    }
}
