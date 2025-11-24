package week11.st269142.RunTrack.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import week11.st269142.RunTrack.data.AuthRepository
import week11.st269142.RunTrack.viewmodel.RunViewModel

@Composable
fun HomeScreen(
    onNavigateToFeature: () -> Unit,
    onSignOut: () -> Unit,
    runViewModel: RunViewModel = viewModel()
) {
    val authRepository = AuthRepository()
    val currentUser = authRepository.getCurrentUser()
    val uiState by runViewModel.uiState.collectAsState()

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

        Spacer(modifier = Modifier.height(48.dp))

        // Run Timer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isRunning) "Running..." else "Ready to Run",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Timer Display
                Text(
                    text = runViewModel.getFormattedTime(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Start/Stop Button
                Button(
                    onClick = {
                        Log.d("HomeScreen", "Button clicked! isRunning: ${uiState.isRunning}")
                        if (uiState.isRunning) {
                            Log.d("HomeScreen", "Stopping run...")
                            runViewModel.stopRun()
                        } else {
                            Log.d("HomeScreen", "Starting run...")
                            runViewModel.startRun()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isRunning) 
                            Color(0xFFD32F2F) else Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isRunning) "Stop Run" else "Start Run",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (uiState.isRunning) "Stop Run" else "Start Run",
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // View Run History Button
        OutlinedButton(
            onClick = onNavigateToFeature,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Run History")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        OutlinedButton(
            onClick = {
                authRepository.signOut()
                onSignOut()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out")
        }

        // Error Snackbar
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = error)
            }
        }
    }
}
