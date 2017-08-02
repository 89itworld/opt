package au.com.ourpillstalk.ourpillstalk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class MainActivity extends AppCompatActivity
{
    LinearLayout btn_mScanner, btn_mHistory, btn_mSettings, btn_mEmergency, btn_mHelp;
    TextView txt_scanText, txt_historyText, txt_settingText, txt_emergencyText, txt_helpText;
    TextView txt_translatedhistoryText, txt_translatedemergencyText, txt_translatedsettingText,
            txt_translatedscanText, txt_translatedhelpText;
    String myLang;
    MyApplication myApplication;
    int x = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApplication = (MyApplication) getApplicationContext();

        btn_mScanner = (LinearLayout) findViewById(R.id.btn_mScanner);
        btn_mHistory = (LinearLayout) findViewById(R.id.btn_mHistory);
        btn_mSettings = (LinearLayout) findViewById(R.id.btn_mSettings);
        btn_mEmergency = (LinearLayout) findViewById(R.id.btn_mEmergency);
        btn_mHelp = (LinearLayout) findViewById(R.id.btn_mHelp);
        txt_scanText = (TextView) findViewById(R.id.txt_scanText);
        txt_historyText = (TextView) findViewById(R.id.txt_historyText);
        txt_settingText = (TextView) findViewById(R.id.txt_settingText);
        txt_emergencyText = (TextView) findViewById(R.id.txt_emergencyText);
        txt_helpText = (TextView) findViewById(R.id.txt_helpText);
        txt_translatedhistoryText = (TextView) findViewById(R.id.txt_translatedhistoryText);
        txt_translatedemergencyText = (TextView) findViewById(R.id.txt_translatedemergencyText);
        txt_translatedsettingText = (TextView) findViewById(R.id.txt_translatedsettingText);
        txt_translatedscanText = (TextView) findViewById(R.id.txt_translatedscanText);
        txt_translatedhelpText = (TextView) findViewById(R.id.txt_translatedhelpText);
        myLang = myApplication.lang;

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);
        getSupportActionBar().setTitle("   Our Pills Talk");
        getSupportActionBar().setSubtitle("    v 7.42");
      //getSupportActionBar().setSubtitle("    v 2.39");

        btn_mScanner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (MyApplication.UniqueID.equalsIgnoreCase("0"))
                {
                    Toast.makeText(MainActivity.this, "Please Update the info first", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, UserInfoActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("FromScan", true);
                    b.putBoolean("ForBack", true);
                    i.putExtras(b);
                    startActivity(i);
                }
                else
                {
                    startActivity(new Intent(MainActivity.this, ScanActivity.class));
                }
            }
        });

        btn_mHistory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });

        btn_mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                finish();
            }
        });

        btn_mEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EmergencyActivity.class));
            }
        });

        btn_mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!myLang.equals("en"))
        {
            if (myApplication.hasConnection())
            {
                String[] texts = new String[5];
                texts[0] = txt_scanText.getText().toString();
                texts[1] = txt_historyText.getText().toString();
                texts[2] = txt_settingText.getText().toString();
                texts[3] = txt_emergencyText.getText().toString();
                texts[4] = txt_helpText.getText().toString();

                Object[] views = new Object[5];
                views[0] = txt_scanText;
                views[1] = txt_historyText;
                views[2] = txt_settingText;
                views[3] = txt_emergencyText;
                views[4] = txt_helpText;

                myApplication.myUnivTranslator(texts, views, MainActivity.this);

                txt_translatedhistoryText.setVisibility(View.VISIBLE);
                txt_translatedemergencyText.setVisibility(View.VISIBLE);
                txt_translatedsettingText.setVisibility(View.VISIBLE);
                txt_translatedscanText.setVisibility(View.VISIBLE);
                txt_translatedhelpText.setVisibility(View.VISIBLE);
            }
            else
            {
                myLang = "en";
                txt_translatedhistoryText.setVisibility(View.GONE);
                txt_translatedemergencyText.setVisibility(View.GONE);
                txt_translatedsettingText.setVisibility(View.GONE);
                txt_translatedscanText.setVisibility(View.GONE);
                txt_translatedhelpText.setVisibility(View.GONE);
            }
        }
        else
        {
            txt_translatedhistoryText.setVisibility(View.GONE);
            txt_translatedemergencyText.setVisibility(View.GONE);
            txt_translatedsettingText.setVisibility(View.GONE);
            txt_translatedscanText.setVisibility(View.GONE);
            txt_translatedhelpText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (!myLang.equals("en"))
        {
            if (myApplication.hasConnection())
            {
                String[] texts = new String[5];
                texts[0] = txt_scanText.getText().toString();
                texts[1] = txt_historyText.getText().toString();
                texts[2] = txt_settingText.getText().toString();
                texts[3] = txt_emergencyText.getText().toString();
                texts[4] = txt_helpText.getText().toString();

                Object[] views = new Object[5];
                views[0] = txt_scanText;
                views[1] = txt_historyText;
                views[2] = txt_settingText;
                views[3] = txt_emergencyText;
                views[4] = txt_helpText;

                myApplication.myUnivTranslator(texts, views, MainActivity.this);

                txt_translatedhistoryText.setVisibility(View.VISIBLE);
                txt_translatedemergencyText.setVisibility(View.VISIBLE);
                txt_translatedsettingText.setVisibility(View.VISIBLE);
                txt_translatedscanText.setVisibility(View.VISIBLE);
                txt_translatedhelpText.setVisibility(View.VISIBLE);
            }
            else
            {
                myLang = "en";
                txt_translatedhistoryText.setVisibility(View.GONE);
                txt_translatedemergencyText.setVisibility(View.GONE);
                txt_translatedsettingText.setVisibility(View.GONE);
                txt_translatedscanText.setVisibility(View.GONE);
                txt_translatedhelpText.setVisibility(View.GONE);
            }
        }
        else
        {
            txt_translatedhistoryText.setVisibility(View.GONE);
            txt_translatedemergencyText.setVisibility(View.GONE);
            txt_translatedsettingText.setVisibility(View.GONE);
            txt_translatedscanText.setVisibility(View.GONE);
            txt_translatedhelpText.setVisibility(View.GONE);
        }
    }
}