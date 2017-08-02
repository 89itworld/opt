package au.com.ourpillstalk.ourpillstalk.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.memetix.mst.language.Language;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.util.CustomRequest;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class UserInfoActivity extends AppCompatActivity
{
    EditText edt_firstName, edt_lastName, edt_email, edt_yearOfBirth, edt_postCode, edt_allergies,
            edt_emergencyContactNumber;
    Button btn_privatePolicy, btn_saveDetails;
    Spinner spnr_mCountryOfOrigin, spnr_preferedLang;
    private String URL_INSERT_USER = "http://www.ourpillstalk.com.au/submission/insertuser.php";
    TextInputLayout til_yearOfBirth, til_postCode, til_email;
    RadioGroup rg;
    RadioButton rb_male, rb_female;
    MyApplication myApplication;
    SharedPreferences.Editor edt;
    SharedPreferences sp;
    Context ctx;
    boolean saveInfo, FromSetting, FromScan = false, ForBack;
    int flag = 0;
    int index1, index2;
    String fName, lName, gender, email, yob, postcode, country, lang, allergies, number;
    String UniqueID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        myApplication = (MyApplication) getApplicationContext();
        ctx = getApplicationContext();

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edt = sp.edit();

        Bundle b = getIntent().getExtras();
        {
            if (b!=null)
            {
                FromScan = b.getBoolean("FromScan", false);
                ForBack = b.getBoolean("ForBack", false);
            }
        }

        saveInfo = sp.getBoolean("SaveInfo", false);

        FromSetting = sp.getBoolean("FromSetting", false);

        if (FromSetting==true || ForBack==true)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            flag = 1;
        }

        if (saveInfo == true)
        {
            if (FromSetting==false)
            {
                if (FromScan == true)
                {

                }
                else
                {
                    startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                    finish();
                }
            }
        }

        edt.putBoolean("FromSetting", false);
        edt.commit();

        edt_firstName = (EditText) findViewById(R.id.edt_firstName);
        edt_lastName = (EditText) findViewById(R.id.edt_lastName);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_yearOfBirth = (EditText) findViewById(R.id.edt_yearOfBirth);
        edt_postCode = (EditText) findViewById(R.id.edt_postCode);
        edt_allergies = (EditText) findViewById(R.id.edt_allergies);
        edt_emergencyContactNumber = (EditText) findViewById(R.id.edt_emergencyContactNumber);
        btn_privatePolicy = (Button) findViewById(R.id.btn_privatePolicy);
        btn_saveDetails = (Button) findViewById(R.id.btn_saveDetails);
        rg = (RadioGroup) findViewById(R.id.rg);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        spnr_mCountryOfOrigin = (Spinner) findViewById(R.id.spnr_mCountryOfOrigin);
        spnr_preferedLang = (Spinner) findViewById(R.id.spnr_preferedLang);
        til_yearOfBirth = (TextInputLayout) findViewById(R.id.til_yearOfBirth);
        til_postCode = (TextInputLayout) findViewById(R.id.til_postCode);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        rb_male.setChecked(true);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.country_name_array, android.R.layout.simple_spinner_dropdown_item);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnr_mCountryOfOrigin.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.google_language_array, android.R.layout.simple_spinner_dropdown_item);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnr_preferedLang.setAdapter(adapter2);

        edt_firstName.setText(myApplication.firstName);
        edt_lastName.setText(myApplication.lastName);
        edt_email.setText(myApplication.emailAddress);
        edt_yearOfBirth.setText(myApplication.yearOfBirth);
        edt_postCode.setText(myApplication.postCode);

        spnr_mCountryOfOrigin.setSelection(myApplication.countrySpnrIndex);
        spnr_preferedLang.setSelection(myApplication.spnrIndex);

        edt_allergies.setText(myApplication.allergies);
        edt_emergencyContactNumber.setText(myApplication.emergencyContactNumber);

        if (rb_female.getText().toString().equals(myApplication.gender))
        {
            rb_female.setChecked(true);
        }
        else
        {
            rb_male.setChecked(true);
        }

        spnr_mCountryOfOrigin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                index1 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        spnr_preferedLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                index2 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        btn_privatePolicy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(UserInfoActivity.this).
                        setTitle("Privacy Policy").
                        setMessage("When you register for use of the Our Pills Talk App we will collect the data and information outlined below in Schedule 1. \n"+
                        "Your actual identity, namely your name, will not be collected. \n"+
                        "The data and personal information we collect allows us to keep you posted on software updates and our latest product announcements. We also use your personal information to help us create, develop, operate, deliver and improve our product, services, content and advertising and for loss prevention and anti-fraud purposes, and to assist with identification of users and to determine the appropriate services. We may also use personal information for internal purposes such as auditing, data analysis and research to improve our product, services and customer communications. Your email address will only be used in an emergency situation.\n"+
                        "We take the security of your personal information very seriously. The information and data will be stored on a secure database. The information and data supplied by you other than the data referred to in Schedule 2, may be shared with third party entities, such as marketing or pharmaceutical firms. \n"+
                        "Should an arrangement take place with third party entities, direct access will not be provided to the stored data, only exported excerpts closely vetted and screened for any personal information. \n"+
                        "You may at any time request that the data or information supplied is inaccurate or that we delete the data. We may decline to process requests that are frivolous/vexatious, jeopardise the privacy of others, are extremely impractical or for which access is not otherwise required by law. \n"+
                        "We shall use all reasonable efforts to protect information submitted by you in connection with the service provided, but you agree that your submission of such information is at your sole risk and we hereby disclaim any and all liability for any loss or liability relating to such information in any way. \n"+
                        "We also disclaim all liability for any failure of the Our Pills Talk App arising out of any one or more of the following:-(a) The preferred language not being available. (b) The preferred language not translating accurately, due to translations being provided by an external third party. (c) Failure of Pharmacists to administer the QR code. (d) Incompatibility with the operating system used by you or the Pharmacist. (e) Failure by you to upgrade to the latest version of the App. (f) Failure by you to consent to the sending of the relevant data. \n"+
                        "\nSchedule 1.\n"+
                        "Stored Data Attributes: \n- Email Address \n- Year of Birth \n- Post Code \n- Gender \n- Country of Origin \n- Preferred Language \n- Medical History \n- Allergies. \n"+
                        "Data Captured at each Scan: \n- Time of Scan \n- Date of Scan \n- Date Pharmaceuticals Dispensed \n- Pharmacy ID \n- Dosage. \n"+
                        "\nSchedule 2.\n"+
                        "Data not shared to Third Party entities: - Email Address - First Name - Last Name\n").
                        setPositiveButton("Okay", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        }).show();
            }
        });

        btn_saveDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (edt_firstName.getText().toString().isEmpty())
                {
                    fName = "";
                }
                else
                {
                    fName = edt_firstName.getText().toString();
                }

                UniqueID = MyApplication.UniqueID;

                if (edt_lastName.getText().toString().isEmpty())
                {
                    lName = "";
                }
                else
                {
                    lName = edt_lastName.getText().toString();
                }

                if (rb_male.isChecked())
                {
                    gender = (rb_male.getText().toString());
                }
                else
                {
                    gender = (rb_female.getText().toString());
                }

                if (edt_email.getText().toString().isEmpty())
                {
                    email = "";
                }
                else
                {
                    email = edt_email.getText().toString();
                }

                if (edt_yearOfBirth.getText().toString().isEmpty())
                {
                    til_yearOfBirth.setError("Please Enter your Year Of Birth");
                }
                else
                {
                    yob = edt_yearOfBirth.getText().toString();
                }

                if (edt_postCode.getText().toString().isEmpty())
                {
                    til_postCode.setError("Please Enter your Postcode");
                }
                else
                {
                    til_postCode.setErrorEnabled(false);
                    postcode = edt_postCode.getText().toString();
                }

                country = spnr_mCountryOfOrigin.getSelectedItem().toString();
                lang = spnr_preferedLang.getSelectedItem().toString();

                if (edt_allergies.getText().toString().isEmpty())
                {
                    allergies = "";
                }
                else
                {
                    allergies = edt_allergies.getText().toString();
                }

                if (edt_emergencyContactNumber.getText().toString().isEmpty())
                {
                    number = "";
                }
                else
                {
                    number = edt_emergencyContactNumber.getText().toString();
                }

                if (!edt_yearOfBirth.getText().toString().isEmpty() && !edt_postCode.getText().toString().isEmpty())
                {
                    if (edt_yearOfBirth.getText().toString().equalsIgnoreCase(" "))
                    {

                    }
                    else if (((Integer.parseInt(edt_yearOfBirth.getText().toString().trim()))<1910
                            || (Integer.parseInt(edt_yearOfBirth.getText().toString().trim())) > MyApplication.getYearString())) {
                        til_yearOfBirth.setError("You must enter a valid year greater than 1910 and less than " + MyApplication.getYearString());
                    }
                    else if (edt_email.getText().toString().equalsIgnoreCase(""))
                    {
                        done();
                    }
                    else
                    {
                        if (isEmailValid(edt_email.getText().toString().trim()))
                        {
                            done();
                        }
                        else
                        {
                            til_yearOfBirth.setErrorEnabled(false);
                            til_email.setError("Please enter valid email ID");
                            edt_email.requestFocus();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                startActivity(new Intent(UserInfoActivity.this, SettingsActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (flag == 1)
        {
            startActivity(new Intent(UserInfoActivity.this, SettingsActivity.class));
        }
        else
        {
            MyApplication.setDefaults("firstName", " ", ctx);
            MyApplication.setDefaults("lastName", " ", ctx);
            MyApplication.setDefaults("gender", " ", ctx);
            MyApplication.setDefaults("email", " ", ctx);
            MyApplication.setDefaults("yob", " ", ctx);
            MyApplication.setDefaults("postcode", " ", ctx);
            MyApplication.setDefaults("country", " ", ctx);
            MyApplication.setDefaults("country", " ", ctx);
            MyApplication.setDefaults("lang", " ", ctx);
            MyApplication.setDefaults("allergies", " ", ctx);
            MyApplication.setDefaults("number", " ", ctx);
            MyApplication.setDefaults("userID", "0", ctx);
            edt.putBoolean("SaveInfo", true);
            edt.commit();
            startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
        }
    }

    /*@Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }*/

    public static boolean isEmailValid(String email)
    {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void done()
    {
        myApplication.setDefaults("CountrySpnrIndex", index1 + "", getApplicationContext());
        myApplication.setDefaults("Index", index2 + "", getApplicationContext());
        myApplication.setDefaults("Lang", getResources().getStringArray(R.array.google_language_code_array)[index2], getApplicationContext());
        myApplication.setDefaults("myLang", getResources().getStringArray(R.array.google_language_code_array)[index2], getApplicationContext());
        myApplication.setDefaults("firstName", fName, ctx);
        myApplication.setDefaults("lastName", lName, ctx);
        myApplication.setDefaults("gender", gender, ctx);
        myApplication.setDefaults("email", email, ctx);
        myApplication.setDefaults("yob", yob, ctx);
        myApplication.setDefaults("postcode", postcode, ctx);
        myApplication.setDefaults("country", country, ctx);
        myApplication.setDefaults("lang", lang, ctx);
        myApplication.setDefaults("allergies", allergies, ctx);
        myApplication.setDefaults("number", number, ctx);

        myApplication.countrySpnrIndex = index1;
        myApplication.spnrIndex = index2;
        myApplication.lang = getResources().getStringArray(R.array.google_language_code_array)[index2];
        myApplication.country = Language.fromString(getResources().getStringArray(R.array.google_language_code_array)[index2]);
        myApplication.firstName = fName;
        myApplication.lastName = lName;
        myApplication.gender = gender;
        myApplication.emailAddress = email;
        myApplication.yearOfBirth = yob;
        myApplication.postCode = postcode;
        myApplication.countryOfOrigin = country;
        myApplication.preferedLang = lang;
        myApplication.allergies = allergies;
        myApplication.emergencyContactNumber = number;

        Map<String, String> mDetails = new HashMap<String, String>();
        mDetails.put("email", email);
        mDetails.put("year_of_birth", yob);
        mDetails.put("postcode", postcode);
        mDetails.put("gender", gender);
        mDetails.put("country_of_origin", country);
        mDetails.put("language", lang);
        mDetails.put("allergies", allergies);
        mDetails.put("userID", UniqueID);

        if (myApplication.hasConnection())
        {
            webServicePart(URL_INSERT_USER, mDetails);
            Log.e("Inside Webservice", mDetails.toString());
        }
        else
        {
            edt.putBoolean("SaveInfo", true);
            edt.putBoolean("saveSpinnerIndex", true);
            edt.putBoolean("saveCountrySpinnerIndex", true);
            edt.commit();
        }

        Toast.makeText(UserInfoActivity.this, "Your Details Saved Successfully", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
        finish();

        //Toast.makeText(UserInfoActivity.this, ""+myApplication.lang, Toast.LENGTH_SHORT).show();
    }

    void webServicePart(String URL_FEED, Map<String,String> dataParam)
    {
        CustomRequest jsonReq = new CustomRequest(Request.Method.POST, URL_FEED, dataParam, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    Log.d("Response: ", response.toString());
                    if (response !=null)
                    {
                        String id=response.getString("userid");
                        Log.e("Inside Response", id + "exist " + UniqueID);

                        myApplication.setDefaults("userID", id, ctx);

                        edt.putBoolean("SaveInfo", true);
                        edt.putBoolean("saveSpinnerIndex", true);
                        edt.putBoolean("saveCountrySpinnerIndex", true);
                        edt.commit();

                        UniqueID = id;
                        myApplication.UniqueID = UniqueID;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError response)
            {
                Log.d("Response: ", response.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonReq);
    }
}