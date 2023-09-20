package pl.pinecone.suplements;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button settings_btn;
    private FloatingActionButton addSupplementBtn;
    private LinearLayout supplement_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.settings_btn = findViewById(R.id.settings_btn);
        this.addSupplementBtn = findViewById(R.id.add_supplement_btn);
        this.supplement_list = findViewById(R.id.activity_main_layout);
        this.inicjalizationOperations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.supplement_list.removeAllViewsInLayout();
        this.inicjalizationOperations();
    }

    private void inicjalizationOperations()
    {
        DbAccess BaseAccess = new DbAccess(getApplicationContext(),"tracker.db",null,1);
        final List<String[]> allData = BaseAccess.getSupplements();

        for(int i = 0; i < allData.size(); i++)
        {
            LinearLayout supplements = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400, Gravity.CENTER);
            if(i == allData.size()-1)
            {
                parameters.setMargins(0,0,0,0);
            }else
            {
                parameters.setMargins(0,0,0,70);
            }
            supplements.setBackground(getDrawable(R.drawable.rounded_white));
            supplements.setLayoutParams(parameters);
            supplements.setPaddingRelative(30, 30, 30, 30);
            supplements.setOrientation(LinearLayout.VERTICAL);

            TextView supplementName = new TextView(getApplicationContext());
            String gettedSuplementName = allData.get(i)[0];
            supplementName.setText(gettedSuplementName);
            supplementName.setTextSize(20);
            supplementName.setTextColor(getColor(R.color.black));
            Typeface bold_suppplementName = ResourcesCompat.getFont(getApplicationContext(), R.font.googlesansbold);
            supplementName.setTypeface(bold_suppplementName);
            supplementName.setGravity(Gravity.CENTER_HORIZONTAL);
            supplements.addView(supplementName);

            TextView lastTakenInfo = new TextView(getApplicationContext());
            String gettedLastTaken = allData.get(i)[2];
            lastTakenInfo.setText(getString(R.string.lastTaken) +": " + gettedLastTaken);
            lastTakenInfo.setTextSize(18);
            lastTakenInfo.setTextColor(getColor(R.color.dark_background));
            Typeface regular_lasttakeninfo = ResourcesCompat.getFont(getApplicationContext(),R.font.googlesansregular);
            lastTakenInfo.setTypeface(regular_lasttakeninfo);
            lastTakenInfo.setGravity(Gravity.CENTER_HORIZONTAL);
            supplements.addView(lastTakenInfo);

            LinearLayout supplementDetails = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            detailParams.setMargins(0,20,0,0);
            supplementDetails.setPaddingRelative(0,10,0,0);
            supplementDetails.setLayoutParams(detailParams);
            supplementDetails.setBackground(getDrawable(R.drawable.rounded_up));
            supplementDetails.setOrientation(LinearLayout.HORIZONTAL);
            supplementDetails.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView dose = new TextView(getApplicationContext());
            String gettedDose = allData.get(i)[1];
            dose.setText(getString(R.string.dose) + "\r\n" + gettedDose);
            dose.setTextSize(18);
            dose.setTextColor(getColor(R.color.white));
            Typeface medium_detailsrow = ResourcesCompat.getFont(getApplicationContext(),R.font.googlesansmedium);
            dose.setTypeface(medium_detailsrow);
            dose.setGravity(Gravity.CENTER);
            FrameLayout doseLayout = new FrameLayout(getApplicationContext());
            FrameLayout.LayoutParams doseLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            doseLayoutParams.setMargins(0,0,40,0);
            doseLayout.setLayoutParams(doseLayoutParams);
            doseLayout.addView(dose);
            supplementDetails.addView(doseLayout);

            TextView nextintake = new TextView(getApplicationContext());
            int lastIntakeTimeYear = Integer.parseInt(gettedLastTaken.substring(6,10));
            int lastIntakeTimeMonth = Integer.parseInt(gettedLastTaken.substring(3,5));
            int lastIntakeTimeDay = Integer.parseInt(gettedLastTaken.substring(0,2));
            Date lastIntakeTime = new Date(lastIntakeTimeYear,lastIntakeTimeMonth,lastIntakeTimeDay);
            long nextIntakeDateMiliseconds = lastIntakeTime.getTime() + Integer.parseInt(allData.get(i)[3])*24*60*60*1000;
            Date nextIntakeTime = new Date(nextIntakeDateMiliseconds);
            String nextIntakeDayString;
            String nextIntakeMonthString;
            String nextIntakeYearString = Integer.toString(nextIntakeTime.getYear());

            if(nextIntakeTime.getDate() < 10)
                nextIntakeDayString = "0" + Integer.toString(nextIntakeTime.getDate());
            else nextIntakeDayString = Integer.toString(nextIntakeTime.getDate());

            if(nextIntakeTime.getMonth() < 10)
                nextIntakeMonthString = "0" + Integer.toString(nextIntakeTime.getMonth());
            else nextIntakeMonthString = Integer.toString(nextIntakeTime.getMonth());
            if(nextIntakeTime.getMonth() == 0)
                nextIntakeMonthString = "12";

            String nextIntakeDateString = nextIntakeDayString + "/" + nextIntakeMonthString + "/" + nextIntakeYearString;
            nextintake.setText(getString(R.string.nextIntake) + "\r\n" + nextIntakeDateString);
            nextintake.setTextSize(18);
            nextintake.setTextColor(getColor(R.color.white));
            nextintake.setTypeface(medium_detailsrow);
            nextintake.setGravity(Gravity.CENTER_HORIZONTAL);
            FrameLayout nextIntakeLayout = new FrameLayout(getApplicationContext());
            FrameLayout.LayoutParams nextIntakeLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            nextIntakeLayoutParams.setMargins(0,0,30,0);
            nextIntakeLayout.setLayoutParams(nextIntakeLayoutParams);
            nextIntakeLayout.addView(nextintake);
            supplementDetails.addView(nextIntakeLayout);

            TextView daysleft = new TextView(getApplicationContext());
            Date currentTime = new Date();
            long timeDiffrence = nextIntakeTime.getTime()-currentTime.getTime();

            short cursedMonth = 0;
            if(currentTime.getMonth()+1 == 6 || currentTime.getMonth()+1 == 9 || currentTime.getMonth()+1 == 11 || currentTime.getMonth()+1 == 4)
                cursedMonth = 1;
            else if(currentTime.getMonth()+1 == 2)
                cursedMonth = 2;

            long daysLeftNumber = ((timeDiffrence/ (1000 * 60 * 60 * 24))%365)-(125-cursedMonth);
            if(daysLeftNumber < 0)
                daysLeftNumber = 0;
            if(daysLeftNumber == 0)
            {
                nextIntakeDateString = getString(R.string.today);
                nextintake.setText(getString(R.string.nextIntake) + "\r\n" + nextIntakeDateString);
            }

            daysleft.setText(getString(R.string.daysLeft) + "\r\n" + daysLeftNumber);
            daysleft.setTextSize(18);
            daysleft.setTextColor(getColor(R.color.white));
            daysleft.setTypeface(medium_detailsrow);
            daysleft.setGravity(Gravity.CENTER_HORIZONTAL);
            FrameLayout daysLeftLayout = new FrameLayout(getApplicationContext());
            FrameLayout.LayoutParams daysleftLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            daysleftLayoutParams.setMargins(0,0,0,0);
            daysLeftLayout.setLayoutParams(daysleftLayoutParams);
            daysLeftLayout.addView(daysleft);
            supplementDetails.addView(daysLeftLayout);

            if(dose.getText().toString().length() > 10 && nextintake.getText().toString().length() < 6)
                supplementDetails.setPaddingRelative(0,10,20,0);
            else if(dose.getText().toString().length() < 10)
                supplementDetails.setPaddingRelative(10,10,0,0);

            Intent SupplementDetailsActivityIntent = new Intent(getApplicationContext(),SupplementDetailsActivity.class);
            SupplementDetailsActivityIntent.putExtra("supplementname",gettedSuplementName);
            SupplementDetailsActivityIntent.putExtra("lasttaken", gettedLastTaken);
            SupplementDetailsActivityIntent.putExtra("dose",gettedDose);
            SupplementDetailsActivityIntent.putExtra("nextintake",nextIntakeDateString);
            SupplementDetailsActivityIntent.putExtra("daysleft",Long.toString(daysLeftNumber));
            supplementName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(SupplementDetailsActivityIntent);
                }
            });

            supplements.addView(supplementDetails);
            this.supplement_list.addView(supplements);
        }

        BaseAccess.close();

        final Drawable addSupplementButtonSrc = getDrawable(android.R.drawable.ic_input_add).getConstantState().newDrawable();
        addSupplementButtonSrc.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        this.addSupplementBtn.setImageDrawable(addSupplementButtonSrc);

        this.addSupplementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent addSupplementActivityIntent = new Intent(getApplicationContext(),AddActivity.class);
                startActivity(addSupplementActivityIntent);
            }
        });

        this.settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent settingsActivityIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(settingsActivityIntent);
            }
        });
    }
}