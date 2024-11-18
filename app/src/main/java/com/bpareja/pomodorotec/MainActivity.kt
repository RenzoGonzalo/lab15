package com.bpareja.pomodorotec

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.media.RingtoneManager
import android.widget.Toast
import androidx.activity.viewModels
import com.bpareja.pomodorotec.pomodoro.PomodoroScreen
import com.bpareja.pomodorotec.pomodoro.PomodoroViewModel


class MainActivity : ComponentActivity() {

    private val viewModel: PomodoroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroScreen(viewModel)
        }

        // Crear el canal de notificaciones
        createNotificationChannel()
        // Solicitar permiso para notificaciones en Android 13+
        requestNotificationPermission()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Canal Pomodoro"
            val descriptionText = "Notificaciones para el temporizador Pomodoro"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        // Verificar si el permiso ha sido concedido antes de mostrar la notificación
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permiso de notificación si no ha sido concedido
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE
            )
        } else {
            // Si ya tienes el permiso, mostrar la notificación
            showPomodoroNotification()
        }
    }

    private fun showPomodoroNotification() {
        // Ruta del archivo de audio personalizado en los recursos
        val audioUri: Uri = Uri.parse("android.resource://${packageName}/raw/hello_audio")

        // Crear el PendingIntent con FLAG_IMMUTABLE o FLAG_MUTABLE
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent("SKIP_BREAK"),
            PendingIntent.FLAG_IMMUTABLE // Usar FLAG_IMMUTABLE si no necesitas cambiar el PendingIntent después
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pomodoro)  // Asegúrate de que el archivo esté en res/drawable
            .setContentTitle("¡Hola listo para trabajar con el mejor metodo?!")
            .setContentText("Toma en cuenta que este metodo esa desarrollado para mejorar y mantener un buen rendiemineto academido que tengas una buena experiencia suerte!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)  // La notificación se cancela cuando el usuario la toca
            .setSound(audioUri)   // Usar el sonido personalizado
            .addAction(
                R.drawable.ic_skip, "Saltar Descanso",
                pendingIntent // Usamos el pendingIntent con el flag adecuado
            )
            .build()

        // Muestra la notificación
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    // Manejo del resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, mostrar la notificación
                showPomodoroNotification()
            } else {
                // Permiso denegado, mostrar un mensaje o manejar el caso
                Toast.makeText(this, "Permiso de notificaciones necesario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "pomodoro_channel"
        private const val REQUEST_CODE = 1
        const val NOTIFICATION_ID = 1
    }
}
