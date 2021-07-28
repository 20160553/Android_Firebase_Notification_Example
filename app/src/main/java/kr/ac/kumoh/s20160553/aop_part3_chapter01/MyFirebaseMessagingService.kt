package kr.ac.kumoh.s20160553.aop_part3_chapter01

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //android 8 이상부터는 알림 생성 시 채널 필요. 채널생성 함수
        createNotificationChannel()

        val type = remoteMessage.data["type"]
            ?.let { NotificationType.valueOf(it) }
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        //null일 경우 리턴
        type ?: return

        NotificationManagerCompat.from(this)
            .notify(type.id, createNotification(type, title, message))
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            //기존 화면 갱신
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        //인텐트를 다룰 권한을 주는 코드
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when(type) {
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "😀 😃 😄 😁 😆 😅 😂 🤣 🥲 ☺" +
                                    "️ 😊 😇 🙂 🙃 😉 😌 😍" +
                                    " 🥰 😘 😗 😙 😚 😋 😛 😝" +
                                    " 😜 🤪 🤨 🧐 🤓 😎 🥸 🤩" +
                                    " 🥳 😏 😒 😞 😔 😟 😕 🙁 ☹" +
                                    "️ 😣 😖 😫 😩 🥺 😢 😭 😤 😠 😡" +
                                    " 🤬 🤯 😳 🥵 🥶 😱 😨 😰 😥 😓 " +
                                    "🤗 🤔 🤭 🤫 🤥 😶 😐 😑 😬 🙄 😯 😦" +
                                    " 😧 😮 😲 🥱 😴 🤤 😪 😵 🤐 🥴 🤢 🤮 " +
                                    "🤧 😷 🤒 🤕 🤑 🤠 😈 👿 👹 👺 🤡 💩 👻 💀" +
                                    " ☠️ 👽 👾 🤖 🎃 😺 😸 😹 😻 😼 😽 🙀 😿 😾"
                        )
                )
            }
            //커스텀 권장 되진 않음
            NotificationType.CUSTOM -> {
                notificationBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ).apply {
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )
            }
        }

        return notificationBuilder.build()
    }

    companion object{
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = "Emoji Party를 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }
}