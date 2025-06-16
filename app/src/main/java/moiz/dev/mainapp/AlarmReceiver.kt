package moiz.dev.mainapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm went off!", Toast.LENGTH_SHORT).show()

        // Play alarm sound
        val mediaPlayer = MediaPlayer.create(context, R.raw.new_ringtone)
        mediaPlayer.start()

        // Optionally stop after 10 seconds
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }
            },10000
        )
    }
}
