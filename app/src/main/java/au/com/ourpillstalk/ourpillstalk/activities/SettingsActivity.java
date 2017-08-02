package au.com.ourpillstalk.ourpillstalk.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.memetix.mst.language.Language;
import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.database.DBHelper;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class SettingsActivity extends AppCompatActivity
{
    Spinner spnr_mLanguageChooser;
    TextView txt_selectLanguage, txt_eraseAllScans, txt_updateUserInfo;
    TextView txt_translatedupdateUserInfo, txt_translatederaseAllScans, txt_translatedselectLanguage;
    LinearLayout ll_updateUserInfo, ll_eraseAllScans, linearLayout4;
    MyApplication myApplication;
    String myLang;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myApplication = (MyApplication) getApplicationContext();

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edt = sp.edit();

        dbHelper = new DBHelper(this);

        spnr_mLanguageChooser = (Spinner) findViewById(R.id.spnr_mLanguageChooser);
        ll_updateUserInfo = (LinearLayout) findViewById(R.id.ll_updateUserInfo);
        ll_eraseAllScans = (LinearLayout) findViewById(R.id.ll_eraseAllScans);
        linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
        txt_selectLanguage = (TextView) findViewById(R.id.txt_selectLanguage);
        txt_eraseAllScans = (TextView) findViewById(R.id.txt_eraseAllScans);
        txt_updateUserInfo = (TextView) findViewById(R.id.txt_updateUserInfo);
        txt_translatedupdateUserInfo = (TextView) findViewById(R.id.txt_translatedupdateUserInfo);
        txt_translatederaseAllScans = (TextView) findViewById(R.id.txt_translatederaseAllScans);
        txt_translatedselectLanguage = (TextView) findViewById(R.id.txt_translatedselectLanguage);
        myLang = myApplication.lang;

        if (!myLang.equals("en"))
        {
            if (myApplication.hasConnection())
            {
                txt_translatedupdateUserInfo.setVisibility(View.VISIBLE);
                txt_translatederaseAllScans.setVisibility(View.VISIBLE);
                txt_translatedselectLanguage.setVisibility(View.VISIBLE);

                String[] texts = new String[3];
                texts[0] = txt_selectLanguage.getText().toString();
                texts[1] = txt_eraseAllScans.getText().toString();
                texts[2] = txt_updateUserInfo.getText().toString();

                Object[] views = new Object[3];
                views[0] = txt_selectLanguage;
                views[1] = txt_eraseAllScans;
                views[2] = txt_updateUserInfo;

                myApplication.myUnivTranslator(texts, views, SettingsActivity.this);
            }
            else
            {
                myLang = "en";
                txt_translatedupdateUserInfo.setVisibility(View.GONE);
                txt_translatederaseAllScans.setVisibility(View.GONE);
                txt_translatedselectLanguage.setVisibility(View.GONE);
            }
        }
        else
        {
            txt_translatedupdateUserInfo.setVisibility(View.GONE);
            txt_translatederaseAllScans.setVisibility(View.GONE);
            txt_translatedselectLanguage.setVisibility(View.GONE);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.google_language_array, android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnr_mLanguageChooser.setAdapter(adapter);
        spnr_mLanguageChooser.setSelection(myApplication.spnrIndex);
        spnr_mLanguageChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                myApplication.setDefaults("Index", position + "", getApplicationContext());
                myApplication.setDefaults("Lang", getResources().getStringArray(R.array.google_language_code_array)[position], getApplicationContext());
                myApplication.setDefaults("myLang", getResources().getStringArray(R.array.google_language_code_array)[position], getApplicationContext());
                edt.putBoolean("saveSpinnerIndex", true);
                edt.commit();
                myApplication.spnrIndex = position;
                myApplication.lang = getResources().getStringArray(R.array.google_language_code_array)[position];
                myApplication.country = Language.fromString(getResources().getStringArray(R.array.google_language_code_array)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        ll_updateUserInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SettingsActivity.this, UserInfoActivity.class);
                edt.putBoolean("FromSetting", true);
                edt.commit();
                startActivity(i);
                finish();
            }
        });

        ll_eraseAllScans.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder.setTitle("Confirm Erase");
                alertDialogBuilder.setMessage("Are You sure you wish to erase scan history?");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dbHelper.deleteAllDataFromScannedPills();
                        Toast.makeText(SettingsActivity.this, "Your Scans Has Been Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });

                alertDialogBuilder.create().show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }
 }