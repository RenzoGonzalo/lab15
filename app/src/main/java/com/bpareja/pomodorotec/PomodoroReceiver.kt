package com.bpareja.pomodorotec

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bpareja.pomodorotec.pomodoro.PomodoroViewModel

class PomodoroReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "SKIP_BREAK") {
            // Llama a la función para saltar el descanso
            PomodoroViewModel.skipBreak()
            Toast.makeText(context, "Descanso saltado", Toast.LENGTH_SHORT).show()
        }
    }
}
