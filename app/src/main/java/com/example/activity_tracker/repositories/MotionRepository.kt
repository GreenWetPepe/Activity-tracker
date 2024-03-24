import android.content.Context
import android.content.SharedPreferences
import com.example.activity_tracker.Motion
import com.google.firebase.database.DatabaseReference
import org.koin.core.KoinComponent

class MotionRepository(context: Context) : KoinComponent {
    private val prefs: SharedPreferences = context.getSharedPreferences("com.activity.motions", 0)

    fun defaultMotions() {
        val fireBase: DatabaseReference
        addMotion("Run", "ac_run")
        addMotion("Jump", "ac_mountain_peak")
        addMotion("Walk", "ac_walk")
    }

    fun addMotion(activityName: String, imageSrc: String) {
        prefs.edit().putString(activityName, imageSrc).apply()
    }

    fun getMotions(): ArrayList<Motion> {
        val motions = ArrayList<Motion>()
        for ((key, value) in prefs.all) {
            val motion = Motion(key, value)
            motions.add(motion)
        }
        return motions
    }

    fun removeMotion(key: String) {
        prefs.edit().remove(key).apply()
    }
}
