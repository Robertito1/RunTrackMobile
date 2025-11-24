package week11.st269142.RunTrack.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st269142.RunTrack.model.Run

sealed class RunResult<out T> {
    data class Success<T>(val data: T) : RunResult<T>()
    data class Error(val message: String) : RunResult<Nothing>()
}

class RunRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val runsCollection = firestore.collection("runs")
    
    private val currentUserId: String?
        get() = auth.currentUser?.uid
    
    companion object {
        private const val TAG = "RunRepository"
    }
    
    /**
     * Create a new run (Start Run)
     */
    suspend fun createRun(): RunResult<Run> {
        return try {
            val userId = currentUserId
            Log.d(TAG, "createRun() called for userId: $userId")
            
            if (userId == null) {
                Log.e(TAG, "User not authenticated")
                return RunResult.Error("User not authenticated")
            }
            
            // Check if there's already an active run
            val activeRunResult = getActiveRun()
            if (activeRunResult is RunResult.Success && activeRunResult.data != null) {
                Log.w(TAG, "Active run already exists: ${activeRunResult.data.id}")
                return RunResult.Error("You already have an active run")
            }
            
            val run = Run(
                uid = userId,
                startTime = Timestamp.now(),
                isActive = true
            )
            
            Log.d(TAG, "Adding run to Firestore...")
            val docRef = runsCollection.add(run.toMap()).await()
            val createdRun = run.copy(id = docRef.id)
            
            Log.d(TAG, "Run created successfully: ${createdRun.id}")
            RunResult.Success(createdRun)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating run: ${e.message}", e)
            RunResult.Error(e.message ?: "Failed to create run")
        }
    }
    
    /**
     * Stop a run and save the duration
     */
    suspend fun stopRun(runId: String, durationSeconds: Long): RunResult<Run> {
        return try {
            val userId = currentUserId ?: return RunResult.Error("User not authenticated")
            
            val updates = mapOf(
                "endTime" to Timestamp.now(),
                "durationSeconds" to durationSeconds,
                "isActive" to false
            )
            
            runsCollection.document(runId).update(updates).await()
            
            // Fetch the updated run
            val snapshot = runsCollection.document(runId).get().await()
            if (snapshot.exists()) {
                val run = Run.fromMap(snapshot.id, snapshot.data!!)
                Log.d(TAG, "Run stopped successfully: $runId")
                RunResult.Success(run)
            } else {
                RunResult.Error("Run not found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping run", e)
            RunResult.Error(e.message ?: "Failed to stop run")
        }
    }
    
    /**
     * Get all runs for the current user (Real-time updates with Flow)
     */
    fun getUserRuns(): Flow<RunResult<List<Run>>> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            trySend(RunResult.Error("User not authenticated"))
            close()
            return@callbackFlow
        }
        
        val listener = runsCollection
            .whereEqualTo("uid", userId)
            // Note: orderBy requires a composite index in Firestore
            // For now, we'll sort in-memory to avoid index requirement
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to runs", error)
                    trySend(RunResult.Error(error.message ?: "Failed to fetch runs"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val runs = snapshot.documents.mapNotNull { doc ->
                        try {
                            Run.fromMap(doc.id, doc.data ?: emptyMap())
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing run document", e)
                            null
                        }
                    }.sortedByDescending { it.startTime.toDate() }
                    trySend(RunResult.Success(runs))
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Get a single run by ID
     */
    suspend fun getRun(runId: String): RunResult<Run> {
        return try {
            val snapshot = runsCollection.document(runId).get().await()
            if (snapshot.exists()) {
                val run = Run.fromMap(snapshot.id, snapshot.data!!)
                RunResult.Success(run)
            } else {
                RunResult.Error("Run not found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching run", e)
            RunResult.Error(e.message ?: "Failed to fetch run")
        }
    }
    
    /**
     * Delete a run
     */
    suspend fun deleteRun(runId: String): RunResult<Unit> {
        return try {
            val userId = currentUserId ?: return RunResult.Error("User not authenticated")
            
            // First verify the run belongs to the current user
            val snapshot = runsCollection.document(runId).get().await()
            if (!snapshot.exists()) {
                return RunResult.Error("Run not found")
            }
            
            val runUserId = snapshot.getString("uid")
            if (runUserId != userId) {
                return RunResult.Error("Unauthorized to delete this run")
            }
            
            runsCollection.document(runId).delete().await()
            Log.d(TAG, "Run deleted successfully: $runId")
            RunResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting run", e)
            RunResult.Error(e.message ?: "Failed to delete run")
        }
    }
    
    /**
     * Get the active run for the current user (if any)
     */
    suspend fun getActiveRun(): RunResult<Run?> {
        return try {
            val userId = currentUserId ?: return RunResult.Error("User not authenticated")
            
            val snapshot = runsCollection
                .whereEqualTo("uid", userId)
                .whereEqualTo("isActive", true)
                .limit(1)
                .get()
                .await()
            
            if (snapshot.documents.isNotEmpty()) {
                val doc = snapshot.documents[0]
                val run = Run.fromMap(doc.id, doc.data!!)
                RunResult.Success(run)
            } else {
                RunResult.Success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching active run", e)
            RunResult.Error(e.message ?: "Failed to fetch active run")
        }
    }
}
