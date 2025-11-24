package week11.st269142.RunTrack.model

import com.google.firebase.Timestamp

data class Run(
    val id: String = "",
    val uid: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp? = null,
    val durationSeconds: Long = 0,
    val isActive: Boolean = false
) {
    // Helper function to get duration in formatted string
    fun getFormattedDuration(): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    // Convert to map for Firestore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "startTime" to startTime,
            "endTime" to endTime,
            "durationSeconds" to durationSeconds,
            "isActive" to isActive
        )
    }
    
    companion object {
        // Create Run from Firestore document
        fun fromMap(id: String, map: Map<String, Any>): Run {
            return Run(
                id = id,
                uid = map["uid"] as? String ?: "",
                startTime = map["startTime"] as? Timestamp ?: Timestamp.now(),
                endTime = map["endTime"] as? Timestamp,
                durationSeconds = (map["durationSeconds"] as? Long) ?: 0L,
                isActive = map["isActive"] as? Boolean ?: false
            )
        }
    }
}
