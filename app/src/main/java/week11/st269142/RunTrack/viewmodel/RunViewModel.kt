package week11.st269142.RunTrack.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import week11.st269142.RunTrack.data.RunRepository
import week11.st269142.RunTrack.data.RunResult
import week11.st269142.RunTrack.model.Run
import week11.st269142.RunTrack.model.UiState

data class RunUiState(
    val currentRun: Run? = null,
    val isRunning: Boolean = false,
    val elapsedSeconds: Long = 0,
    val runs: List<Run> = emptyList(),
    val runsState: UiState<List<Run>> = UiState.idle(),
    val error: String? = null
)

class RunViewModel(
    private val runRepository: RunRepository = RunRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RunUiState())
    val uiState: StateFlow<RunUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    private var startTimeMillis: Long = 0
    
    init {
        checkForActiveRun()
        observeRuns()
    }
    
    /**
     * Check if there's an active run when ViewModel is created
     */
    private fun checkForActiveRun() {
        viewModelScope.launch {
            when (val result = runRepository.getActiveRun()) {
                is RunResult.Success -> {
                    result.data?.let { activeRun ->
                        _uiState.value = _uiState.value.copy(
                            currentRun = activeRun,
                            isRunning = true
                        )
                        // Calculate elapsed time from start time
                        val now = System.currentTimeMillis()
                        val startMillis = activeRun.startTime.toDate().time
                        val elapsedSeconds = (now - startMillis) / 1000
                        _uiState.value = _uiState.value.copy(elapsedSeconds = elapsedSeconds)
                        startTimer()
                    }
                }
                is RunResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
            }
        }
    }
    
    /**
     * Observe all runs with real-time updates
     */
    private fun observeRuns() {
        viewModelScope.launch {
            runRepository.getUserRuns().collect { result ->
                when (result) {
                    is RunResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            runs = result.data,
                            runsState = UiState.success(result.data)
                        )
                    }
                    is RunResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            runsState = UiState.error(result.message)
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Start a new run
     */
    fun startRun() {
        Log.d("RunViewModel", "startRun() called")
        
        // Check if already running
        if (_uiState.value.isRunning) {
            Log.w("RunViewModel", "Run already in progress, ignoring start request")
            return
        }
        
        viewModelScope.launch {
            Log.d("RunViewModel", "Calling runRepository.createRun()")
            when (val result = runRepository.createRun()) {
                is RunResult.Success -> {
                    Log.d("RunViewModel", "Run created successfully: ${result.data.id}")
                    _uiState.value = _uiState.value.copy(
                        currentRun = result.data,
                        isRunning = true,
                        elapsedSeconds = 0,
                        error = null
                    )
                    startTimeMillis = System.currentTimeMillis()
                    startTimer()
                    Log.d("RunViewModel", "UI state updated, timer started")
                }
                is RunResult.Error -> {
                    Log.e("RunViewModel", "Error starting run: ${result.message}")
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
            }
        }
    }
    
    /**
     * Stop the current run
     */
    fun stopRun() {
        val currentRun = _uiState.value.currentRun ?: return
        val duration = _uiState.value.elapsedSeconds
        
        viewModelScope.launch {
            stopTimer()
            
            when (val result = runRepository.stopRun(currentRun.id, duration)) {
                is RunResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        currentRun = null,
                        isRunning = false,
                        elapsedSeconds = 0,
                        error = null
                    )
                }
                is RunResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
            }
        }
    }
    
    /**
     * Delete a run
     */
    fun deleteRun(runId: String) {
        viewModelScope.launch {
            when (val result = runRepository.deleteRun(runId)) {
                is RunResult.Success -> {
                    // Success - the Flow will automatically update the list
                }
                is RunResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
            }
        }
    }
    
    /**
     * Start the timer to track elapsed time
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Update every second
                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = _uiState.value.elapsedSeconds + 1
                )
            }
        }
    }
    
    /**
     * Stop the timer
     */
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Format elapsed seconds to HH:MM:SS or MM:SS
     */
    fun getFormattedTime(): String {
        val seconds = _uiState.value.elapsedSeconds
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
