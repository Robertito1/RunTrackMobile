package week11.st269142.RunTrack.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st269142.RunTrack.data.AuthRepository

@Composable
fun HomeScreen(
    onNavigateToFeature: () -> Unit,
    onSignOut: () -> Unit
) {
    val authRepository = AuthRepository()
    val currentUser = authRepository.getCurrentUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to RunTrack!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hello, ${currentUser?.displayName ?: currentUser?.email ?: "User"}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToFeature
        ) {
            Text("Go to Feature Screen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authRepository.signOut()
                onSignOut()
            }
        ) {
            Text("Sign Out")
        }
    }
}
