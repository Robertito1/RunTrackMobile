package week11.st269142.RunTrack.model

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
) {
    companion object {
        fun <T> loading(): UiState<T> = UiState(isLoading = true)
        fun <T> success(data: T): UiState<T> = UiState(data = data, isSuccess = true)
        fun <T> error(message: String): UiState<T> = UiState(error = message)
        fun <T> idle(): UiState<T> = UiState()
    }
}
