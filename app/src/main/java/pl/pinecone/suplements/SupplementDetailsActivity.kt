package pl.pinecone.suplements

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class SupplementDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplementdetails)

        val previousIntent: Intent = intent
        val supplement_name_textview: TextView = findViewById(R.id.supplement_name_textview_details)
        val supplement_lasttaken_textview: TextView = findViewById(R.id.textView5)
        val supplement_dose_textview: TextView = findViewById(R.id.textView7)
        val supplement_nextintake_textview: TextView = findViewById(R.id.textView9)
        val supplement_daysleft_textview: TextView = findViewById(R.id.textView11)
        val close_btn: Button = findViewById(R.id.close_btn_detailsactivity)
        val take_btn: Button = findViewById(R.id.take_btn_details)
        val modify_btn: Button = findViewById(R.id.modify_btn_details)
        val del_btn: Button = findViewById(R.id.delete_btn_details)

        val supplement_name: String? = previousIntent.getStringExtra("supplementname")
        val supplement_lastatken: String? = previousIntent.getStringExtra("lasttaken")
        val supplement_dose: String? = previousIntent.getStringExtra("dose")
        val supplement_nextintake: String? = previousIntent.getStringExtra("nextintake")
        val supplement_daysleft: String? = previousIntent.getStringExtra("daysleft")


        supplement_name_textview.text = supplement_name
        supplement_lasttaken_textview.text = supplement_lastatken
        supplement_dose_textview.text = supplement_dose
        supplement_nextintake_textview.text = supplement_nextintake
        supplement_daysleft_textview.text = supplement_daysleft

        close_btn.setOnClickListener { finish() }
        modify_btn.setOnClickListener {
            val modify_activity: Intent = Intent(applicationContext, ModifyActivity::class.java)
            modify_activity.putExtra("supplementname",supplement_name)
            modify_activity.putExtra("lasttaken",supplement_lastatken)
            modify_activity.putExtra("dose",supplement_dose)
            startActivity(modify_activity)
        }
        take_btn.setOnClickListener{
            val BaseAccess: DbAccess = DbAccess(applicationContext,"tracker.db",null,1)
            BaseAccess.updateRecordDate(supplement_name_textview.text as String?)
            BaseAccess.close()
            Toast.makeText(applicationContext,supplement_name_textview.text.toString() + " " + getString(R.string.taken), Toast.LENGTH_LONG).show()
            finish()
        }

        del_btn.setOnClickListener {
            AlertDialog.Builder(this).setTitle(R.string.delete_supplement).setMessage(getString(R.string.confirmationDeletionOne) + " " + supplement_name_textview.text.toString() + "? " + getString(R.string.confirmationDeletionSecond)).setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                val BaseAccess: DbAccess = DbAccess(applicationContext,"tracker.db", null, 1)
                BaseAccess.deleteOneRecord(supplement_name_textview.text as String?)
                BaseAccess.close()
                Toast.makeText(applicationContext,supplement_name_textview.text.toString() + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                finish()
            }).setNegativeButton(R.string.no, null).show()
        }
    }
}