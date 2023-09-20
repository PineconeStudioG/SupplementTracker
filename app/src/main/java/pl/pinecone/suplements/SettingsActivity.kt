package pl.pinecone.suplements

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val close_settings_btn: ImageButton = findViewById(R.id.close_settings_btn)
        val delete_all_supplements_btn: Button = findViewById(R.id.deleteSupplements_settings_btn)
        val share_btn: Button = findViewById(R.id.share_settings_btn)
        val about_btn: Button = findViewById(R.id.about_settings_btn)
        val notifiactions_switch: Switch = findViewById(R.id.notifications_switch)
        val notification_system_info: TextView = findViewById(R.id.notification_system_info)

        val closeSettingsButtonSrc = getDrawable(androidx.appcompat.R.drawable.abc_ic_ab_back_material)?.constantState?.newDrawable()
        closeSettingsButtonSrc!!.mutate().setColorFilter(getColor(R.color.dark_background), PorterDuff.Mode.SRC_IN)
        close_settings_btn.setImageDrawable(closeSettingsButtonSrc)

        close_settings_btn.setOnClickListener{ finish() }

        val notificationsEnabled = NotificationManagerCompat.from(baseContext).areNotificationsEnabled()
        val notifications_preferences: SharedPreferences = applicationContext.getSharedPreferences("pl.pinecone.suplements",Context.MODE_PRIVATE)
        val notifications_preferencesModify: SharedPreferences.Editor = notifications_preferences.edit()
        if(!notificationsEnabled)
        {
            notifiactions_switch.isChecked = false
            notifiactions_switch.isEnabled = false
            notification_system_info.text = getString(R.string.notificationSystemDisabledInfo)
            notifications_preferencesModify.putBoolean("enabled",false).commit()
        }else
        {
            notification_system_info.text = getString(R.string.notificationSettingsInfo)
            notifiactions_switch.isEnabled = true
            notifiactions_switch.isChecked = notifications_preferences.getBoolean("enabled", true)
            notifications_preferencesModify.putBoolean("enabled",notifiactions_switch.isChecked).commit()
            notifiactions_switch.setOnCheckedChangeListener { _, isChecked ->
                notifications_preferencesModify.putBoolean("enabled",isChecked).commit()
            }
        }

        delete_all_supplements_btn.setOnClickListener {
            AlertDialog.Builder(this).setTitle(R.string.deleteSupplements).setMessage(R.string.deletingAllSupplementsText).setPositiveButton(R.string.yes,DialogInterface.OnClickListener { dialog, which ->
                val BaseAccess: DbAccess = DbAccess(applicationContext,"tracker.db", null, 1)
                BaseAccess.deleteAllRecords()
                BaseAccess.close()
                Toast.makeText(applicationContext,R.string.deletingAllSupplementsConfirmation, Toast.LENGTH_SHORT).show()
            }).setNegativeButton(R.string.no, null).show()
        }

        share_btn.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareTitle))
            intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.shareContent) + "\r\n\r\nhttps://play.google.com/store/apps/details?id=pl.pinecone.suplements")
            startActivity(Intent.createChooser(intent,getString(R.string.shareVia)))
        }

        about_btn.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8599976374104206031"))
            startActivity(intent)
        }
    }
}