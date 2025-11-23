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
import week11.st269142.RunTrack.model.User

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val authState: UiState<User> = UiState.idle()
)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun login() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                authState = UiState.loading()
            )

            when (val result = authRepository.signIn(_uiState.value.email, _uiState.value.password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        authState = UiState.success(result.data)
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        authState = UiState.error(result.message)
                    )
                }
                else -> {}
            }
        }
    }

    private fun validateInputs(): Boolean {
        val email = _uiState.value.email
        val password = _uiState.value.password

        var isValid = true
        var emailError: String? = null
        var passwordError: String? = null

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email format"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }

        _uiState.value = _uiState.value.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        return isValid
    }

    fun resetAuthState() {
        _uiState.value = _uiState.value.copy(
            authState = UiState.idle()
        )
    }
}
