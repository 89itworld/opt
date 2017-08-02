package au.com.ourpillstalk.ourpillstalk.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.adapter.CustomListItemAdapter;
import au.com.ourpillstalk.ourpillstalk.database.DBHelper;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class HistoryActivity extends AppCompatActivity
{
    ListView list_totalScans;
    DBHelper dbHelper;
    Cursor c1;
    ArrayList<String> arrayList, arrayList1, arrayList2, dateArray, dateArray1, dateArray2, S_no, S_no1, S_no2;
    ArrayList<String> info1, info2, info3, info4;
    ArrayList<String> myinfo1, myinfo2, getInfo1, getInfo2;
    EditText edt_searchAtToolbar, edt_editMail;
    String myLang;
    ImageButton imgbtn_sendMail, imgbtn_done;
//    ImageButton imgbtn_retypeMail;
    LinearLayout ll_mail, linearLayout2;
    TextView txt_email, txt_translatedhideDupicateScan;
    SharedPreferences.Editor edt;
    SharedPreferences sp;
    ImageView search_image, cross_image;
    MyApplication myApplication;
    CustomListItemAdapter adapter;
    Switch switch_hideDupicateScan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myApplication = (MyApplication) getApplicationContext();

        dbHelper = new DBHelper(this);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edt = sp.edit();

        arrayList = new ArrayList<String>();
        arrayList1 = new ArrayList<String>();
        arrayList2 = new ArrayList<String>();
        dateArray = new ArrayList<String>();
        dateArray1 = new ArrayList<String>();
        dateArray2 = new ArrayList<String>();
        info1 = new ArrayList<String>();
        info2 = new ArrayList<String>();
        info3 = new ArrayList<String>();
        info4 = new ArrayList<String>();
        myinfo1 = new ArrayList<String>();
        myinfo2 = new ArrayList<String>();
        getInfo1 = new ArrayList<String>();
        getInfo2 = new ArrayList<String>();
        S_no = new ArrayList<String>();
        S_no1 = new ArrayList<String>();
        S_no2 = new ArrayList<String>();

        c1 = dbHelper.getDataFromScannedPills();
        if (c1!=null && c1.getCount()>0)
        {
            c1.moveToFirst();
            do
            {
                S_no1.add(c1.getString(0));
                info1.add(c1.getString(1));
                arrayList1.add(c1.getString(2));
                dateArray1.add(c1.getString(3));
                if (!arrayList2.contains(c1.getString(2)))
                {
                    S_no2.add(c1.getString(0));
                    info2.add(c1.getString(1));
                    arrayList2.add(c1.getString(2));
                    dateArray2.add(c1.getString(3));
                }
            }
            while(c1.moveToNext());
        }

        edt_searchAtToolbar = (EditText) findViewById(R.id.edt_searchAtToolbar);
        edt_editMail = (EditText) findViewById(R.id.edt_editMail);
        list_totalScans = (ListView) findViewById(R.id.list_totalScans);
        switch_hideDupicateScan = (Switch) findViewById(R.id.switch_hideDupicateScan);
        search_image = (ImageView) findViewById(R.id.search_image);
        cross_image = (ImageView) findViewById(R.id.cross_image);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_translatedhideDupicateScan = (TextView) findViewById(R.id.txt_translatedhideDupicateScan);
        imgbtn_sendMail = (ImageButton) findViewById(R.id.imgbtn_sendMail);
//        imgbtn_retypeMail = (ImageButton) findViewById(R.id.imgbtn_retypeMail);
        imgbtn_done = (ImageButton) findViewById(R.id.imgbtn_done);
        ll_mail = (LinearLayout) findViewById(R.id.ll_mail);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        edt_editMail.setText(MyApplication.emailAddress.toString());

      //  edt_editMail.setVisibility(View.GONE);
        imgbtn_done.setVisibility(View.GONE);

        myLang = myApplication.lang;

        if (!myLang.equals("en"))
        {
            if (myApplication.hasConnection())
            {
                txt_translatedhideDupicateScan.setVisibility(View.VISIBLE);

                String[] texts = new String[1];
                texts[0] = switch_hideDupicateScan.getText().toString();

                Object[] views = new Object[1];
                views[0] = switch_hideDupicateScan;

                myApplication.myUnivTranslator(texts, views, HistoryActivity.this);
            }
            else
            {
                myLang = "en";
                txt_translatedhideDupicateScan.setVisibility(View.GONE);
            }
        }
        else
        {
            txt_translatedhideDupicateScan.setVisibility(View.GONE);
        }

        for (int i=info1.size()-1; i>=0; i--)
        {
            info3.add(info1.get(i));
            getInfo1.add(arrayList1.get(i));
        }

        for (int i = info2.size()-1; i>=0; i--)
        {
            info4.add(info2.get(i));
            getInfo2.add(arrayList2.get(i));
        }

        for (int i = 0; i< info3.size(); i++)
        {
            myinfo1.add(getInfo1.get(i)+"\n"+info3.get(i)+"\n--------------- \n");
        }

        for (int i = 0; i< info4.size(); i++)
        {
            myinfo2.add(getInfo2.get(i)+"\n"+info4.get(i)+"\n--------------- \n");
        }

        /*imgbtn_retypeMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_email.setVisibility(View.GONE);
                imgbtn_sendMail.setVisibility(View.GONE);
                imgbtn_retypeMail.setVisibility(View.GONE);
                edt_editMail.setVisibility(View.VISIBLE);
                imgbtn_done.setVisibility(View.VISIBLE);
                edt_editMail.setText(txt_email.getText().toString().substring(9).trim());
            }
        });*/

        imgbtn_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (UserInfoActivity.isEmailValid(edt_editMail.getText().toString().trim()))
                {
                    imgbtn_sendMail.setVisibility(View.VISIBLE);
//                    imgbtn_retypeMail.setVisibility(View.VISIBLE);
                    txt_email.setVisibility(View.VISIBLE);
                    imgbtn_done.setVisibility(View.GONE);
                    edt_editMail.setVisibility(View.GONE);
                    txt_email.setText("mail to: " + edt_editMail.getText().toString());
                    myApplication.setDefaults("email", edt_editMail.getText().toString(), HistoryActivity.this);
                    edt.putBoolean("SaveInfo", true);
                    edt.commit();

                    myApplication.emailAddress = edt_editMail.getText().toString();
                } else {
                    Toast.makeText(HistoryActivity.this, "Email Id is not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edt_searchAtToolbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                ArrayList<String> searchList = new ArrayList<String>();
                ArrayList<String> searchDateList = new ArrayList<String>();
                ArrayList<String> searchS_No_List = new ArrayList<String>();
                String temp = edt_searchAtToolbar.getText().toString();

                if (switch_hideDupicateScan.isChecked()) {
                    for (int i = 0; i < arrayList2.size(); i++)
                    {
                        if (arrayList2.get(i).contains(temp)) {
                            searchList.add(arrayList2.get(i));
                            searchDateList.add(dateArray2.get(i));
                            searchS_No_List.add(S_no2.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < arrayList1.size(); i++) {
                        if (arrayList1.get(i).contains(temp)) {
                            searchList.add(arrayList1.get(i));
                            searchDateList.add(dateArray1.get(i));
                            searchS_No_List.add(S_no1.get(i));
                        }
                    }
                }

                adapter = new CustomListItemAdapter(searchList, searchDateList, searchS_No_List, getApplicationContext());
                list_totalScans.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (MyApplication.emailAddress.equals(""))
        {
            txt_email.setText("Tab here to enter Email Id");
        }
        else
        {
            txt_email.setText("mail to: " + MyApplication.emailAddress);
        }

        txt_email.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                txt_email.setVisibility(View.GONE);
                imgbtn_sendMail.setVisibility(View.GONE);
                edt_editMail.setVisibility(View.VISIBLE);
                imgbtn_done.setVisibility(View.VISIBLE);
                if (MyApplication.emailAddress.equals(""))
                {

                }
                else
                {
                    edt_editMail.setText(MyApplication.emailAddress+"");
                }
            }
        });

        adapter = new CustomListItemAdapter(arrayList1, dateArray1, S_no1, getApplicationContext());
        list_totalScans.setAdapter(adapter);

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_image.setVisibility(View.GONE);
                cross_image.setVisibility(View.VISIBLE);
                edt_searchAtToolbar.setVisibility(View.VISIBLE);
            }
        });

        cross_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_searchAtToolbar.setText("");
                search_image.setVisibility(View.VISIBLE);
                cross_image.setVisibility(View.GONE);
                edt_searchAtToolbar.setVisibility(View.GONE);
            }
        });

        switch_hideDupicateScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String temp = edt_searchAtToolbar.getText().toString();
                ArrayList<String> searchList = new ArrayList<String>();
                ArrayList<String> searchDateList = new ArrayList<String>();
                ArrayList<String> searchS_no_List = new ArrayList<String>();
                if (isChecked)
                {
                    if (!temp.isEmpty()) {
                        for (int i = 0; i < arrayList2.size(); i++) {
                            if (arrayList2.get(i).contains(temp)) {
                                searchList.add(arrayList2.get(i));
                                searchDateList.add(dateArray2.get(i));
                                searchS_no_List.add(S_no2.get(i));
                            }
                        }
                        adapter = new CustomListItemAdapter(searchList, searchDateList, searchS_no_List, getApplicationContext());
                        list_totalScans.setAdapter(adapter);
                    } else {
                        adapter = new CustomListItemAdapter(arrayList2, dateArray2, S_no2, getApplicationContext());
                        list_totalScans.setAdapter(adapter);
                    }
                } else {
                    if (!temp.isEmpty()) {
                        for (int i = 0; i < arrayList1.size(); i++)
                        {
                            if (arrayList1.get(i).contains(temp))
                            {
                                searchList.add(arrayList1.get(i));
                                searchDateList.add(dateArray1.get(i));
                                searchS_no_List.add(S_no1.get(i));
                            }
                        }
                        adapter = new CustomListItemAdapter(searchList, searchDateList, searchS_no_List, getApplicationContext());
                        list_totalScans.setAdapter(adapter);
                    } else {
                        adapter = new CustomListItemAdapter(arrayList1, dateArray1, S_no1, getApplicationContext());
                        list_totalScans.setAdapter(adapter);
                    }
                }
            }
        });

        imgbtn_sendMail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (UserInfoActivity.isEmailValid(edt_editMail.getText().toString().trim()))
                {
                    //imgbtn_sendMail.setVisibility(View.VISIBLE);
                    //imgbtn_retypeMail.setVisibility(View.VISIBLE);
                    txt_email.setVisibility(View.VISIBLE);
                    imgbtn_sendMail.setVisibility(View.VISIBLE);
                    //imgbtn_done.setVisibility(View.GONE);
                    //edt_editMail.setVisibility(View.GONE);
                    txt_email.setText("mail to: " + edt_editMail.getText().toString());
                    myApplication.setDefaults("email", edt_editMail.getText().toString(), HistoryActivity.this);
                    edt.putBoolean("SaveInfo", true);
                    edt.commit();

                    myApplication.emailAddress = edt_editMail.getText().toString();
                }
                else
                {
                    Toast.makeText(HistoryActivity.this, "Email Id is not valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                String recipient = txt_email.getText().toString().substring(9).trim();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
                i.putExtra(Intent.EXTRA_SUBJECT, "Our Pills Talk");
                if (switch_hideDupicateScan.isChecked())
                {
                    i.putExtra(Intent.EXTRA_TEXT, myinfo2 + "");
                }
                else
                {
                    i.putExtra(Intent.EXTRA_TEXT, myinfo1 + "");
                }
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                    Toast.makeText(HistoryActivity.this, "Send mail...", Toast.LENGTH_SHORT).show();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HistoryActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgbtn_sendMail.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Toast.makeText(HistoryActivity.this, "Send History", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (cross_image.getVisibility()==View.VISIBLE)
        {
            search_image.setVisibility(View.VISIBLE);
            edt_searchAtToolbar.setText("");
            edt_searchAtToolbar.setVisibility(View.GONE);
            cross_image.setVisibility(View.GONE);
            return;
        }
        else if (imgbtn_done.getVisibility()==View.VISIBLE)
        {
            imgbtn_sendMail.setVisibility(View.VISIBLE);
//            imgbtn_retypeMail.setVisibility(View.VISIBLE);
            txt_email.setVisibility(View.VISIBLE);
            imgbtn_done.setVisibility(View.GONE);
            edt_editMail.setVisibility(View.GONE);
        }
        else
        {
            super.onBackPressed();
            finish();
        }

    }
}
