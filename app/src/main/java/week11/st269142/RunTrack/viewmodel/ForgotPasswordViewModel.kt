package week11.st269142.RunTrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import week11.st269142.RunTrack.data.AuthRepository
import week11.st269142.RunTrack.model.AuthResult
import week11.st269142.RunTrack.model.UiState

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val resetState: UiState<Unit> = UiState.idle()
)

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun resetPassword() {
        if (!validateEmail()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                resetState = UiState.loading()
            )

            when (val result = authRepository.resetPassword(_uiState.value.email)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        resetState = UiState.success(Unit)
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        resetState = UiState.error(result.message)
                    )
                }
                else -> {}
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = _uiState.value.email

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                emailError = "Email is required"
            )
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(
                emailError = "Invalid email format"
            )
            return false
        }

        return true
    }

    fun resetState() {
        _uiState.value = _uiState.value.copy(
            resetState = UiState.idle()
        )
    }
}
