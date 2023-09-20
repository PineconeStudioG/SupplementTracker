package pl.pinecone.suplements

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth
import java.util.Date


class ModifyActivity : AppCompatActivity() {

    private lateinit var SupplementLastTaken: EditText
    private lateinit var SupplementDose: EditText
    private lateinit var SupplementFrequency:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        val BaseAccess = DbAccess(applicationContext,"tracker.db", null, 1)

        this.SupplementLastTaken = findViewById(R.id.LastTaken)
        this.SupplementDose = findViewById(R.id.Dose)
        this.SupplementFrequency = findViewById(R.id.FrequencyIndays)
        val modified_name: TextView = findViewById(R.id.modifiedName)
        val close_btn: Button = findViewById(R.id.close_btn_modifyactivity)
        val modify_btn: Button = findViewById(R.id.button_modifySupplement)

        val previousIntent: Intent = intent
        val supplement_name: String? = previousIntent.getStringExtra("supplementname")
        val supplement_lasttaken: String? = previousIntent.getStringExtra("lasttaken")
        val supplement_dose: String? = previousIntent.getStringExtra("dose")
        var supplement_frequency: String? = ""

        val allData = BaseAccess.supplements
        BaseAccess.close()
        for(i in 0..allData.size)
        {
            if(allData[i][0] == supplement_name)
            {
                supplement_frequency = allData[i][3]
                break
            }
        }

        modified_name.setText(supplement_name)
        this.SupplementLastTaken.setText(supplement_lasttaken)
        this.SupplementDose.setText(supplement_dose)
        this.SupplementFrequency.setText(supplement_frequency)

        modify_btn.setTextColor(getColor(R.color.white))
        this.SupplementLastTaken.setHintTextColor(1627389952)
        this.SupplementDose.setHintTextColor(1627389952)
        this.SupplementFrequency.setHintTextColor(1627389952)

        this.SupplementLastTaken.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                this.datePick()
            }
        }
        this.SupplementLastTaken.setOnClickListener {
            this.datePick()
        }
        close_btn.setOnClickListener { finish() }
        modify_btn.setOnClickListener{

            var verificationStatus = true

            if(!this.supplementLastTakenVerification())
            {
                changeFieldColorError(this.SupplementLastTaken)
                this.SupplementLastTaken.addTextChangedListener {
                    changeFieldColorDefault(this.SupplementLastTaken)
                }
                verificationStatus = false
            }

            if(!this.supplementDoseVerification())
            {
                changeFieldColorError(this.SupplementDose)
                this.SupplementDose.addTextChangedListener {
                    changeFieldColorDefault(this.SupplementDose)
                }
                verificationStatus = false
            }

            if(!this.supplementFrequencyVerification())
            {
                changeFieldColorError(this.SupplementFrequency)
                this.SupplementFrequency.addTextChangedListener {
                    changeFieldColorDefault(this.SupplementFrequency)
                }
                verificationStatus = false
            }

            if(!verificationStatus)
                return@setOnClickListener

            val BaseAccess = DbAccess(applicationContext,"tracker.db",null,1)
            BaseAccess.updateRecord(supplement_name,this.SupplementDose.text.toString(), this.SupplementLastTaken.text.toString(),this.SupplementFrequency.text.toString().toInt())
            BaseAccess.close()
            Toast.makeText(applicationContext,R.string.modified, Toast.LENGTH_SHORT).show()
            val returnIntent: Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(returnIntent)
            finish()
        }
    }

    private fun changeFieldColorError(a: EditText)
    {
        a.setBackgroundDrawable(getDrawable(R.drawable.rounded_add_error))
    }
    private fun changeFieldColorDefault(a: EditText)
    {
        a.setBackgroundDrawable(getDrawable(R.drawable.rounded_add))
    }

    private fun datePick()
    {
        val defaultDate = Date()
        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            val selectedDay: String
            val selectedMonth: String
            val selectedYear: String = year.toString()

            if(monthOfYear < 10)
                selectedMonth = "0" + (monthOfYear+1).toString()
            else selectedMonth = (monthOfYear+1).toString()

            if(dayOfMonth < 10)
                selectedDay = "0$dayOfMonth"
            else selectedDay = dayOfMonth.toString()

            val fullDate = "$selectedDay/$selectedMonth/$selectedYear"
            this.SupplementLastTaken.setText(fullDate)
        }, defaultDate.year+1900, defaultDate.month, defaultDate.date)
        datePicker.show()
    }

    private fun supplementLastTakenVerification(): Boolean
    {
        val dateLength: Int = this.SupplementLastTaken.text.toString().length
        if(dateLength != 10)
        {
            Toast.makeText(applicationContext,R.string.dateFormatError, Toast.LENGTH_SHORT).show()
            return false
        }
        val daySeparator: Char = this.SupplementLastTaken.text.toString()[2]
        val monthSeparator: Char = this.SupplementLastTaken.text.toString()[5]
        if(daySeparator != '/' || monthSeparator != '/')
        {
            Toast.makeText(applicationContext,R.string.dateFormatError, Toast.LENGTH_SHORT).show()
            return false
        }
        val dayLastTaken: String = this.SupplementLastTaken.text.toString().split('/')[0]
        val monthLastTaken: String = this.SupplementLastTaken.text.toString().split('/')[1]
        val yearLastTaken: String = this.SupplementLastTaken.text.toString().split('/')[2]
        if(yearLastTaken.toInt() > Year.now().value)
        {
            Toast.makeText(applicationContext,R.string.dateFutureError, Toast.LENGTH_SHORT).show()
            return false
        }else
        {
            if(monthLastTaken.toInt() > YearMonth.now().monthValue && yearLastTaken.toInt() == Year.now().value)
            {
                Toast.makeText(applicationContext,R.string.dateFutureError, Toast.LENGTH_SHORT).show()
                return false
            }else
            {
                if(dayLastTaken.toInt() > MonthDay.now().dayOfMonth && monthLastTaken.toInt() == YearMonth.now().monthValue && yearLastTaken.toInt() == Year.now().value)
                {
                    Toast.makeText(applicationContext,R.string.dateFutureError, Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }

    private fun supplementDoseVerification(): Boolean
    {
        if(this.SupplementDose.text.toString().isEmpty() || this.SupplementDose.text.toString() == "0")
        {
            Toast.makeText(applicationContext,R.string.dosageErrorZero, Toast.LENGTH_SHORT).show()
            return false
        }
        if(this.SupplementDose.text.toString().length > 10)
        {
            Toast.makeText(applicationContext,R.string.dosageErrorLength, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun supplementFrequencyVerification(): Boolean
    {
        try {
            if (this.SupplementFrequency.text.toString().toInt() == 0)
            {
                Toast.makeText(applicationContext,R.string.frequencyErrorZero, Toast.LENGTH_SHORT).show()
                return false
            }
            if (this.SupplementFrequency.text.toString().toInt() >= 25)
            {
                Toast.makeText(applicationContext,R.string.frequencyErrorDistance, Toast.LENGTH_SHORT).show()
                return false
            }
        }catch (e: java.lang.NumberFormatException)
        {
            Toast.makeText(applicationContext,R.string.frequencyErrorNull, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}