package au.com.ourpillstalk.ourpillstalk.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class HelpActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    private TextToSpeech tts;
    TextView txt_helpText;
    String myLang;
    LinearLayout linearLayout1;
    MyApplication myApplication;
    int myttsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        myApplication = (MyApplication) getApplicationContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_helpText = (TextView) findViewById(R.id.txt_helpText);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        myLang = myApplication.lang;

        if (!myLang.equals("en"))
        {
            if (myApplication.hasConnection())
            {
                String[] texts = new String[1];
                texts[0] = txt_helpText.getText().toString();

                Object[] views = new Object[1];
                views[0] = txt_helpText;

                myApplication.myUnivTranslator(texts, views, HelpActivity.this);
            }
            else
            {
                myLang = "en";
            }
        }

        tts = new TextToSpeech(this, this);
    }

    class ForSpeak extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            if (myttsStatus == TextToSpeech.SUCCESS)
            {
                int result = tts.setLanguage(toLocale(myLang));

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                {
                    Handler handler =  new Handler(HelpActivity.this.getMainLooper());
                    handler.post( new Runnable(){
                        public void run(){
                            Toast.makeText(HelpActivity.this, "Not Supported Speech",Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.e("TTS", "This Language is not supported");
                }
                else
                {
                    speakOut(txt_helpText.getText().toString());
                }
            }
            else
            {
                Handler handler =  new Handler(HelpActivity.this.getMainLooper());
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(HelpActivity.this, "Not Supported Speech1", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e("TTS1", "This Language is not supported");
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status)
    {
        myttsStatus = status;
        ForSpeak fs = new ForSpeak();
        fs.execute();
    }

    public static Locale toLocale(String str)
    {
        return new Locale(str);
    }

    private void speakOut(String text)
    {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        tts.stop();
        tts.shutdown();
        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        tts.stop();
        tts.shutdown();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!myLang.equals("en"))
        {
            if (myApplication.hasConnection())
            {
                String[] texts = new String[1];
                texts[0] = txt_helpText.getText().toString();

                Object[] views = new Object[1];
                views[0] = txt_helpText;

                myApplication.myUnivTranslator(texts, views, HelpActivity.this);
            }
            else
            {
                myLang = "en";
            }
        }
    }
}
