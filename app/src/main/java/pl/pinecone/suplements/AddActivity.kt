package pl.pinecone.suplements

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth
import java.util.Date

class AddActivity : AppCompatActivity() {

    private lateinit var close_btn: Button
    private lateinit var add_btn: Button
    private lateinit var SupplementName: EditText
    private lateinit var SupplementLastTaken: EditText
    private lateinit var SupplementDose: EditText
    private lateinit var SupplementFrequency: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        this.SupplementName = findViewById(R.id.SupplementName)
        this.SupplementLastTaken = findViewById(R.id.LastTaken)
        this.SupplementDose = findViewById(R.id.Dose)
        this.SupplementFrequency = findViewById(R.id.FrequencyIndays)
        this.close_btn = findViewById(R.id.close_btn_addactivity)
        this.add_btn = findViewById(R.id.button_addSupplement)

        this.add_btn.setTextColor(getColor(R.color.white))
        this.SupplementName.setHintTextColor(1627389952)
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
        this.close_btn.setOnClickListener { finish() }
        this.add_btn.setOnClickListener{

            var verificationStatus = true
            if(!this.supplementNameVerification())
            {
                changeFieldColorError(this.SupplementName)
                this.SupplementName.addTextChangedListener {
                    changeFieldColorDefault(this.SupplementName)
                }
                verificationStatus = false
            }

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
            if(BaseAccess.addRecord(this.SupplementName.text.toString(),this.SupplementDose.text.toString(), this.SupplementLastTaken.text.toString(),this.SupplementFrequency.text.toString().toInt()))
            {
                Toast.makeText(applicationContext,R.string.supplementAdded, Toast.LENGTH_SHORT).show()
                finish()
            }else
            {
                Toast.makeText(applicationContext,getString(R.string.supplementExistsErrorFirst) + " " + this.SupplementName.text.toString() + " " + getString(R.string.supplementExistsErrorSecond), Toast.LENGTH_SHORT).show()
            }
            BaseAccess.close()
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

    private fun supplementNameVerification(): Boolean
    {
        if (this.SupplementName.text.toString().isEmpty())
        {
            Toast.makeText(applicationContext,R.string.supplementNameErrorNull, Toast.LENGTH_SHORT).show()
            return false
        }
        if (this.SupplementName.text.toString().length > 17)
        {
            Toast.makeText(applicationContext,R.string.supplementNameErrorLength, Toast.LENGTH_SHORT).show()
            return false
        }
        if(this.SupplementName.text.toString().contains("'"))
        {
            Toast.makeText(applicationContext,R.string.supplementNameErrorSign, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun datePick()
    {
        val defaultDate = Date()
        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            val selectedDay: String
            val selectedMonth: String
            val selectedYear: String = year.toString()

            if(monthOfYear+1 < 10)
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