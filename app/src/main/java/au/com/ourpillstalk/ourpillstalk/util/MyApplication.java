package au.com.ourpillstalk.ourpillstalk.util;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.model.TranslatedText;

public class MyApplication extends Application implements WebKeys,WebMessages
{
    public static String firstName = "", lastName = "", gender = "Male", emailAddress = "",
            yearOfBirth = "", postCode = "", countryOfOrigin = "", preferedLang = "",
            allergies = "", emergencyContactNumber = "", lang = "en", UniqueID = "0";
    public static int spnrIndex = 21;
    public int countrySpnrIndex = 8;
    static final String PRESCRIPTION_TAG = "p";
    boolean saveInfo, saveSpinnerIndex, saveCountrySpnrIndex;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    Context ctx, myCtx;
    public Language country = Language.ENGLISH;

    String[] myTexts;
    ArrayList<String> myTextsAL;
    Object[] myViews;
    String[] translatedText;
    WebServerUtils mWebServerUtils;



    @Override
    public void onCreate()
    {
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        edt = sp.edit();

        ctx = getApplicationContext();
        mWebServerUtils=WebServerUtils.getInstance(ctx);

        saveInfo = sp.getBoolean("SaveInfo", false);
        saveSpinnerIndex = sp.getBoolean("saveSpinnerIndex", false);
        saveCountrySpnrIndex = sp.getBoolean("saveCountrySpinnerIndex", false);

        if (saveInfo==true)
        {
            firstName = getDefaults("firstName", ctx);
            lastName = getDefaults("lastName", ctx);
            gender = getDefaults("gender", ctx);
            emailAddress = getDefaults("email", ctx);
            yearOfBirth = getDefaults("yob", ctx);
            postCode = getDefaults("postcode", ctx);
            countryOfOrigin = getDefaults("country", ctx);
            preferedLang = getDefaults("lang", ctx);
            allergies = getDefaults("allergies", ctx);
            emergencyContactNumber = getDefaults("number", ctx);
            UniqueID = getDefaults("userID", ctx);
        }

        if (saveSpinnerIndex==true)
        {
            spnrIndex = Integer.parseInt(getDefaults("Index", ctx));
            country = Language.fromString(getDefaults("Lang", ctx));
            lang = getDefaults("myLang", ctx);
        }

        if (saveCountrySpnrIndex == true)
        {
            countrySpnrIndex = Integer.parseInt(getDefaults("CountrySpnrIndex", ctx));
        }
    }

    public void myUnivTranslator(String[] texts, Object[] views, Context myCtx)
    {
        translatedText = new String[texts.length];
        myTexts = texts;
        myViews = views;
        this.myCtx = myCtx;
        UnivTranslator univTranslator = new UnivTranslator();
        univTranslator.execute();
    }

    public void myUnivTranslatorForScanOnly(ArrayList<String> texts, Object[] views, Context myCtx)
    {
        myTextsAL = new ArrayList<>();
        String breakText = texts.get(1);
        String[] newBreakText = breakText.split("\n");
        myTextsAL.add(texts.get(0));

        for (int i = 0; i<newBreakText.length; i++)
        {
            myTextsAL.add(newBreakText[i]);
        }

        myViews = views;
        this.myCtx = myCtx;
        translatedText = new String[myTextsAL.size()];

        UnivTranslatorForScanOnly univTranslatorForScanOnly = new UnivTranslatorForScanOnly();
        univTranslatorForScanOnly.execute();
    }

    /*class UnivTranslator extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = new ProgressDialog(myCtx);
            dialog.setMessage("Loading Language...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Translate.setClientId("com_steve_OurPillsTalk007");
                Translate.setClientSecret("QR2+jqRWpURZXpfEOMD7h6+9Nqy83hYBw3UuKcYaQIw=");

                for (int i = 0; i<myTexts.length; i++)
                {
                    translatedText[i] = Translate.execute(myTexts[i], country);
                }
                Log.v("Inside Try ", translatedText+"");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void res)
        {
            dialog.dismiss();
            for (int i = 0; i<myViews.length; i++)
            {
                if (myViews[i] instanceof TextView)
                {
                    ((TextView)myViews[i]).setText(translatedText[i]);
                }
                else if (myViews[i] instanceof Button)
                {
                    ((Button)myViews[i]).setText(translatedText[i]);
                }
                else if (myViews[i] instanceof Switch)
                {
                    ((Switch)myViews[i]).setText(translatedText[i]);
                }
            }
        }
    }*/


    class UnivTranslator extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = new ProgressDialog(myCtx);
            dialog.setMessage("Loading Language...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {

                for (int i = 0; i<myTexts.length; i++)
                {

                    MultipartUtility lMultipartUtility=new MultipartUtility(KEY_TRANSLATE_URL,"utf-8");
                    lMultipartUtility.addFormField(KEY_Q,myTexts[i]);
                    lMultipartUtility.addFormField(KEY_TARGET,lang);
                    lMultipartUtility.addFormField(KEY_KEY,KEY_GOOGLE_API_KEY);

                    String lResponse=lMultipartUtility.finish();

                    TranslatedText lTranlatesText=mWebServerUtils.parseTranslatedText(lResponse);

                    if(lTranlatesText.getStatusCode()==null){
                     translatedText[i]=lTranlatesText.getTranslatedText();
                    }
                    else {
                        translatedText[i] = "";
                    }
                }
                Log.v("Inside Try ", translatedText+"");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void res)
        {
            dialog.dismiss();
            for (int i = 0; i<myViews.length; i++)
            {
                if (myViews[i] instanceof TextView)
                {
                    ((TextView)myViews[i]).setText(translatedText[i]);
                }
                else if (myViews[i] instanceof Button)
                {
                    ((Button)myViews[i]).setText(translatedText[i]);
                }
                else if (myViews[i] instanceof Switch)
                {
                    ((Switch)myViews[i]).setText(translatedText[i]);
                }
            }
        }
    }

    class UnivTranslatorForScanOnly extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = new ProgressDialog(myCtx);
            dialog.setMessage("Loading Language...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Translate.setClientId("com_steve_OurPillsTalk007");
                Translate.setClientSecret("QR2+jqRWpURZXpfEOMD7h6+9Nqy83hYBw3UuKcYaQIw=");

                for (int i = 0; i<myTextsAL.size(); i++)
                {
                    MultipartUtility lMultipartUtility=new MultipartUtility(KEY_TRANSLATE_URL,"utf-8");
                    lMultipartUtility.addFormField(KEY_Q,myTextsAL.get(i));
                    lMultipartUtility.addFormField(KEY_TARGET,lang);
                    lMultipartUtility.addFormField(KEY_KEY,KEY_GOOGLE_API_KEY);

                    String lResponse=lMultipartUtility.finish();

                    TranslatedText lTranlatesText=mWebServerUtils.parseTranslatedText(lResponse);

                    if(lTranlatesText.getStatusCode()==null){
                        translatedText[i]=lTranlatesText.getTranslatedText();
                    }
                    else {
                        translatedText[i] = "";
                    }
                }
                Log.v("Inside Try ", translatedText+"");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void res)
        {
            dialog.dismiss();
            for (int i = 0; i<myViews.length; i++)
            {
                if (((TextView)myViews[i]).getId()== R.id.lastScanTextView)
                {
                    ((TextView)myViews[i]).setText(translatedText[0]);
                }
                else if (((TextView)myViews[i]).getId()== R.id.txt_pillsInfo)
                {
                    ((TextView)myViews[i]).setText("");
                    for (int x = 1; x<translatedText.length; x++)
                    {
                        ((TextView)myViews[i]).setText(((TextView)myViews[i]).getText()+translatedText[x]+"\n");
                    }
                }
            }
        }
    }

    public static void setDefaults(String key, String value, Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static String abbreviations(String scanText)
    {
        String splitter[] = scanText.split("</in|in>");
        scanText=" "+scanText+" ";
        Log.e("Abbrevation text", scanText);
        scanText = " " + splitter[1]+ " ";
        scanText = scanText.replaceAll(" % ", " percent ");
        scanText = scanText.replaceAll(" (?i)1acn ", " one hour before meals and at bedtime ");
        scanText = scanText.replaceAll(" (?i)aa ", " to the affected area ");
        scanText = scanText.replaceAll("ac ", "before meals");
        scanText = scanText.replaceAll("a\\.c", "before meals");
        scanText = scanText.replaceAll(" (?i)a\\.c", "before meals");
        scanText = scanText.replaceAll("auto-inh", "auto inhaler ");

        if(scanText.toLowerCase().contains(" alt d ")) {
            scanText = scanText.replaceAll(" (?i)alt d ", " on alternate days ");
        }
        else {
            scanText = scanText.replaceAll(" (?i)alt ", " on alternate ");
        }
        if(scanText.toLowerCase().contains(" ex aq ")) {
            scanText = scanText.replaceAll(" (?i)ex aq ", " in water ");
            //  return scanText;
        }
        else
        {
            scanText = scanText.replaceAll(" (?i)ex " , " external use only ");
        }
        scanText = scanText.replaceAll(" (?i)aq ", " with water ");
        scanText = scanText.replaceAll(" (?i)bd ", " twice daily ");
        scanText = scanText.replaceAll(" (?i)bid ", " twice daily ");
        scanText = scanText.replaceAll(" (?i)cap " , " capsule ");
        scanText = scanText.replaceAll(" (?i)caps " , " capsules ");
        scanText = scanText.replaceAll(" (?i)cc " , " with food ");
        scanText = scanText.replaceAll(" (?i)cr " , " cream ");
        scanText = scanText.replaceAll(" (?i)crem " , " cream ");
        scanText = scanText.replaceAll(" (?i)crm " , " cream ");
        scanText = scanText.replaceAll(" (?i)d " , " daily ");
        scanText = scanText.replaceAll(" (?i)disp " , " dispensed ");
        scanText = scanText.replaceAll(" (?i)fini " , " until finished ");
//        scanText = scanText.replaceAll(" (?i)g " , " gram ");
        scanText = scanText.replaceAll(" (?i)gutt " , " drops ");
        scanText = scanText.replaceAll(" (?i)h " , " hours ");
        scanText = scanText.replaceAll(" (?i)hs " , " at bedtime ");
        scanText = scanText.replaceAll(" (?i)ex " , " external use only ");
        scanText = scanText.replaceAll(" IM "  ,  " intramuscular " );
        scanText = scanText.replaceAll(" IN "  ,  " intra nasal " );
        scanText = scanText.replaceAll(" (?i)inh "  ,  " inhaler " );
        scanText = scanText.replaceAll(" (?i)-inh "  ,  " -inhaler " );
        scanText = scanText.replaceAll(" (?i)inj "  ,  " injection " );
        scanText = scanText.replaceAll(" IU "  ,  " units " );
        scanText = scanText.replaceAll(" (?i)l "  ,  " left " );
        scanText = scanText.replaceAll(" (?i)linc "  ,  " linctus " );
        scanText = scanText.replaceAll(" (?i)linct "  ,  " linctus " );
        scanText = scanText.replaceAll(" (?i)m "  ,  " in the morning " );
        scanText = scanText.replaceAll(" MDI "  ,  " metered dose inhaler " );
        scanText = scanText.replaceAll(" (?i)mdu "  ,  " as directed by your doctor " );
//        scanText = scanText.replaceAll(" (?i)mg "  ,  " milligram " );
//        scanText = scanText.replaceAll(" (?i) mcg", " micrograms");
        scanText = scanText.replaceAll(" (?i)mist "  ,  " mixture " );
//        scanText = scanText.replaceAll(" (?i)mmol "  ,  " millimoles " );
        scanText = scanText.replaceAll(" (?i)n "  ,  " at night " );
        scanText = scanText.replaceAll(" (?i)oc "  ,  " eye ointment " );
        scanText = scanText.replaceAll(" (?i)occ "  ,  " eye ointment " );
        scanText = scanText.replaceAll(" (?i)od "  ,  " once daily " );
        scanText = scanText.replaceAll(" (?i)oint "  ,  " ointment " );
        scanText = scanText.replaceAll(" (?i)ot "  ,  " ear " );
        scanText = scanText.replaceAll(" (?i)pc "  ,  " after meals " );
        scanText = scanText.replaceAll(" (?i)pess "  ,  " pessary " );
        scanText = scanText.replaceAll(" (?i)pn "  ,  " for pain " );
        scanText = scanText.replaceAll(" (?i)po "  ,  " by mouth " );
        scanText = scanText.replaceAll(" (?i)pr "  ,  " into the rectum ");
        scanText = scanText.replaceAll(" (?i)prn "  ,  " when required ");
        scanText = scanText.replaceAll(" (?i)Ptch "  ,  " patch " );
        scanText = scanText.replaceAll(" (?i)pulv "  ,  " powder " );
        scanText = scanText.replaceAll(" (?i)pv "  ,  " into the vagina " );
        scanText = scanText.replaceAll(" (?i)q12h "  ,  " every 12 hours " );
        scanText = scanText.replaceAll(" (?i)q4h "  ,  " every 4 hours " );
        scanText = scanText.replaceAll(" (?i)q6h "  ,  " every 6 hours " );
        scanText = scanText.replaceAll(" (?i)q8h "  ,  " every 8 hours " );
        scanText = scanText.replaceAll(" (?i)qds "  ,  " four times a day " );
        scanText = scanText.replaceAll(" (?i)qh "  ,  " every hour " );
        scanText = scanText.replaceAll(" (?i)qhs "  ,  " nightly at bedtime " );
        scanText = scanText.replaceAll(" (?i)qid "  ,  " four times a day " );
        scanText = scanText.replaceAll(" (?i)qqh "  ,  " every 4 hours " );
        scanText = scanText.replaceAll(" (?i)r "  ,  " right " );
        scanText = scanText.replaceAll(" (?i)rect "  ,  " rectum " );
        scanText = scanText.replaceAll(" (?i)sl "  ,  " under the tongue " );
        scanText = scanText.replaceAll(" (?i)sos "  ,  " if necessary " );
        scanText = scanText.replaceAll(" (?i)stat "  ,  " immediately " );
        scanText = scanText.replaceAll(" (?i)supp "  ,  " suppository " );
        scanText = scanText.replaceAll(" (?i)syr "  ,  " syrup " );
        if(scanText.toLowerCase().contains(" tab-ec "))
        {
            scanText = scanText.replaceAll(" (?i)tab-ec "  ,  " enteric coated " );
        }
        else
        {
            scanText = scanText.replaceAll(" (?i)tab "  ,  " tablet " );
        }
        scanText = scanText.replaceAll(" (?i)tabs "  ,  " tablets " );
        scanText = scanText.replaceAll(" (?i)tds "  ,  " three times a day " );
        scanText = scanText.replaceAll(" (?i)tid "  ,  " three times a day " );
        scanText = scanText.replaceAll(" (?i)top "  ,  " on the skin " );
        scanText = scanText.replaceAll(" (?i)uat "  ,  " until all taken " );
        scanText = scanText.replaceAll(" (?i)ung "  ,  " ointment " );
        scanText = scanText.replaceAll(" (?i)utd "  ,  " as directed " );
        scanText = scanText.replaceAll(" (?i)w "  ,  " weekly " );
        scanText = scanText.replaceAll(" (?i)xaq ", " with water ");
        scanText = scanText.replaceAll(" (?i)vag "  ,  " vaginal " );

        System.out.println(scanText);
        splitter[1] = scanText;
        String finalz = "";
        finalz+=splitter[0];
        finalz+= "in>";
        finalz+=splitter[1];
        finalz+= "</in";
        finalz+=splitter[2];

        return finalz;
    }

    public static String abbreviationsTwo(String scanText){
        scanText=" "+scanText+" ";

        scanText = scanText.replaceAll("<", " <");
        scanText = scanText.replaceAll(">", " >");

        scanText = scanText.replaceAll(" % ", " percent ");
        scanText=scanText.replaceAll(" auto-inh "," auto inhaler ");
        scanText = scanText.replaceAll(" (?i)1acn ", " one hour before meals and at bedtime ");
        scanText = scanText.replaceAll("aa ", "to the affected area ");
        scanText = scanText.replaceAll("Auto-Inh ", "Auto Inhaler ");
        scanText = scanText.replaceAll("ac ", " before meals");
        scanText = scanText.replaceAll("a\\.c", "before meals");
        scanText = scanText.replaceAll(" (?i)a\\.c", "before meals");
        if(scanText.toLowerCase().contains(" alt d ")) {
            scanText = scanText.replaceAll(" (?i)alt d ", " on alternate days ");
        }
        else {
            scanText = scanText.replaceAll(" (?i)alt ", " on alternate ");
        }
        if(scanText.toLowerCase().contains(" ex aq ")) {
            scanText = scanText.replaceAll(" (?i)ex aq ", " in water ");
            //  return scanText;
        }
        else
        {
            scanText = scanText.replaceAll(" (?i)ex " , " external use only ");
        }
        scanText = scanText.replaceAll(" (?i)aq ", " with water ");
        scanText = scanText.replaceAll(" (?i)bd ", " twice daily ");
        scanText = scanText.replaceAll(" (?i)bid ", " twice daily ");
        scanText = scanText.replaceAll(" (?i)cap " , " capsule ");
        scanText = scanText.replaceAll(" (?i)caps " , " capsules ");
        scanText = scanText.replaceAll(" (?i)cc " , " with food ");
        scanText = scanText.replaceAll(" (?i)cr " , " cream ");
        scanText = scanText.replaceAll(" (?i)crem " , " cream ");
        scanText = scanText.replaceAll(" (?i)crm " , " cream ");
        scanText = scanText.replaceAll(" (?i)d " , " daily ");
        scanText = scanText.replaceAll(" (?i)disp " , " dispensed ");
        scanText=scanText.replaceAll(" eye-drp "," eye drops ");
        scanText = scanText.replaceAll(" (?i)fini " , " until finished ");
        scanText = scanText.replaceAll("(?i)amp\\;" , "");
//        scanText = scanText.replaceAll(" (?i)g " , " gram ");
        scanText = scanText.replaceAll(" (?i)gutt " , " drops ");
        scanText = scanText.replaceAll(" (?i)h " , " hours ");
        scanText = scanText.replaceAll(" (?i)hr " , " hours ");
        scanText = scanText.replaceAll("(?i)hr " , " hours ");
        scanText = scanText.replaceAll(" (?i)hs " , " at bedtime ");
        scanText = scanText.replaceAll(" (?i)ex " , " external use only ");
        scanText = scanText.replaceAll(" IM "  ,  " intramuscular " );
        scanText = scanText.replaceAll(" IN "  ,  " intra nasal " );
        scanText = scanText.replaceAll(" (?i)inh "  ,  " inhaler " );
        scanText = scanText.replaceAll(" (?i)inj "  ,  " injection " );
        scanText = scanText.replaceAll(" IU "  ,  " units " );
        scanText = scanText.replaceAll(" (?i)l "  ,  " left " );
        scanText = scanText.replaceAll(" (?i)linc "  ,  " linctus " );
        scanText = scanText.replaceAll(" (?i)linct "  ,  " linctus " );
        scanText = scanText.replaceAll(" (?i)m "  ,  " in the morning " );
        scanText = scanText.replaceAll(" met-aero "," metered aerosol ");
        scanText = scanText.replaceAll(" MDI "  ,  " metered dose inhaler " );
        scanText = scanText.replaceAll(" (?i)mdu "  ,  " as directed by your doctor " );
//        scanText = scanText.replaceAll(" (?i)mg "  ,  " milligram " );
        scanText = scanText.replaceAll(" (?i)mist "  ,  " mixture " );
//        scanText = scanText.replaceAll(" (?i)mmol "  ,  " millimoles " );
        scanText = scanText.replaceAll(" (?i)n "  ,  " at night " );
        scanText = scanText.replaceAll(" (?i)oc "  ,  " eye ointment " );
        scanText = scanText.replaceAll("occ "  ,  "eye ointment ");
        scanText = scanText.replaceAll(" (?i)od "  ,  " once daily " );
        scanText = scanText.replaceAll(" (?i)oint "  ,  " ointment " );
        scanText = scanText.replaceAll(" (?i)ot "  ,  " ear " );
        scanText = scanText.replaceAll(" (?i)pc "  ,  " after meals " );
        scanText = scanText.replaceAll(" (?i)pess "  ,  " pessary " );
        scanText = scanText.replaceAll(" (?i)pn "  ,  " for pain " );
        scanText = scanText.replaceAll(" (?i)po "  ,  " by mouth " );
        scanText = scanText.replaceAll(" (?i)pr "  ,  " into the rectum ");
        scanText = scanText.replaceAll(" (?i)prn "  ,  " when required ");
        scanText = scanText.replaceAll(" (?i)Ptch "  ,  " patch " );
        scanText = scanText.replaceAll(" (?i)pulv "  ,  " powder " );
        scanText = scanText.replaceAll(" (?i)pv "  ,  " into the vagina " );
        scanText = scanText.replaceAll(" (?i)q12h "  ,  " every 12 hours " );
        scanText = scanText.replaceAll(" (?i)q4h "  ,  " every 4 hours " );
        scanText = scanText.replaceAll(" (?i)q6h "  ,  " every 6 hours " );
        scanText = scanText.replaceAll(" (?i)q8h "  ,  " every 8 hours " );
        scanText = scanText.replaceAll(" (?i)qds "  ,  " four times a day " );
        scanText = scanText.replaceAll(" (?i)qh "  ,  " every hour " );
        scanText = scanText.replaceAll(" (?i)qhs "  ,  " nightly at bedtime " );
        scanText = scanText.replaceAll(" (?i)qid "  ,  " four times a day " );
        scanText = scanText.replaceAll(" (?i)qqh "  ,  " every 4 hours " );
        scanText = scanText.replaceAll(" (?i)r "  ,  " right " );
        scanText = scanText.replaceAll(" (?i)rect "  ,  " rectum " );
        scanText = scanText.replaceAll(" (?i)sl "  ,  " under the tongue " );
        scanText = scanText.replaceAll(" (?i)sos "  ,  " if necessary " );
        scanText = scanText.replaceAll(" (?i)stat "  ,  " immediately " );
        scanText = scanText.replaceAll(" (?i)supp "  ,  " suppository " );
        scanText = scanText.replaceAll(" (?i)syr "  ,  " syrup " );
        if(scanText.toLowerCase().contains(" tab-ec "))
        {
            scanText = scanText.replaceAll(" (?i)tab-ec "  ,  " enteric coated " );
        }
        else
        {
            scanText = scanText.replaceAll(" (?i)tab "  ,  " tablet " );
        }
        scanText = scanText.replaceAll(" (?i)tabs "  ,  " tablets " );
        scanText = scanText.replaceAll(" (?i)tds "  ,  " three times a day " );
        scanText = scanText.replaceAll(" (?i)tid "  ,  " three times a day " );
        scanText = scanText.replaceAll(" (?i)top "  ,  " on the skin " );
        scanText = scanText.replaceAll(" (?i)uat "  ,  " until all taken " );
        scanText = scanText.replaceAll(" (?i)ung "  ,  " ointment " );
        scanText = scanText.replaceAll(" (?i)utd "  ,  " as directed " );
        scanText = scanText.replaceAll(" (?i)w "  ,  " weekly " );
        scanText = scanText.replaceAll(" (?i)xaq "  ,  " with water " );
        scanText = scanText.replaceAll(" (?i)vag "  ,  " vaginal " );
        scanText = scanText.replaceAll("(?i)null"  ,  "  " );
        scanText = scanText.replaceAll(" (?i)\\d+u", " $0uunits").replace("uuunits", " units");
        scanText=scanText.replaceAll(" (?i)x1d", " for 1 day");
        scanText=scanText.replaceAll(" (?i)x2d", " for 2 days");
        scanText=scanText.replaceAll(" (?i)x3d", " for 3 days");
        scanText=scanText.replaceAll(" (?i)x4d", " for 4 days");
        scanText=scanText.replaceAll(" (?i)x5d", " for 5 days");
        scanText=scanText.replaceAll(" (?i)x6d", " for 6 days");
        scanText=scanText.replaceAll(" (?i)x7d"," for 7 days");
        scanText=scanText.replaceAll(" (?i)x14d"," for 14 days");
        return scanText;
    }

    public static String abbreviationsUnitsToSpeak(String scanText)
    {
        /*scanText = scanText.replaceAll("mg ", "milligram ");
        scanText = scanText.replaceAll("mcg ", "microgram ");
        scanText = scanText.replaceAll("ml ", "millilitre ");
        scanText = scanText.replaceAll("mcl ", "microlitre ");*/
        scanText = scanText.replaceAll("(?i) mcg", " micrograms");
        scanText=scanText.replaceAll("(?i)\\d+mcg", " $0mmicrograms").replace("mcgmmicrograms", " micrograms");
        scanText = scanText.replaceAll("(?i) mmol", " millimoles");
        scanText = scanText.replaceAll("(?i)\\d+mmol", " $0millimoles").replace("mmolmillimoles", " millimoles");
        scanText = scanText.replaceAll("(?i) g", " grams");
        scanText = scanText.replaceAll("(?i)\\d+g", " $0grams").replace("ggrams", " grams");
        scanText = scanText.replaceAll("(?i) mcl", " microlitres");
        scanText = scanText.replaceAll("(?i)\\d+mcl", " $0mmicrolitres").replace("mclmmicrolitres", " microlitres");
        scanText=scanText.replaceAll("(?i) mg", "milligrams");
        scanText=scanText.replaceAll("(?i)\\d+mg"," $0mmilligrams").replace("mgmmilligrams", " milligrams");;
        scanText=scanText.replaceAll("(?i) ml", "millilitres");
        scanText=scanText.replaceAll("(?i)\\d+ml"," $0mmillilitres").replace("mlmmillilitres", " millilitres");

        return scanText;
    }

    public static boolean isPrescriptionScanXML(String fileBody)
    {
        return fileBody.startsWith("<"+PRESCRIPTION_TAG);
    }

    public static String getDateString()
    {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        int hourInt = c.get(Calendar.HOUR);
        int forAmPm = c.get(Calendar.HOUR_OF_DAY);
        String hour = String.valueOf(hourInt);
        if(hourInt < 10)
        {
            hour = "0" + String.valueOf(hourInt);
        }

        int minuteInt = c.get(Calendar.MINUTE);
        String minute = String.valueOf(minuteInt);
        if(minuteInt < 10)
        {
            minute = "0" + String.valueOf(minuteInt);
        }

        int dayMonthInt = c.get(Calendar.DAY_OF_MONTH);
        String dayMonth = String.valueOf(dayMonthInt);
        if(dayMonthInt < 10) {
            dayMonth = "0" + String.valueOf(dayMonthInt);
        }

        int monthInt = c.get(Calendar.MONTH);
        monthInt++;
        String month = String.valueOf(monthInt);
        if(monthInt < 10) {
            month = "0" + String.valueOf(monthInt);
        }
        int yearInt = c.get(Calendar.YEAR);
        String year = String.valueOf(yearInt);
        Log.e("MOnths",""+monthInt);
        switch (monthInt)
        {
            case 1:
                month="January";
                break;
            case 2:
                month="February";
                break;
            case 3:
                month="March";
                break;
            case 4:
                month="April";
                break;
            case 5:
                month="May";
                break;
            case 6:
                month="June";
                break;
            case 7:
                month="July";
                break;
            case 8:
                month="August";
                break;
            case 9:
                month="September";
                break;
            case 10:
                month="October";
                break;
            case 11:
                month="November";
                break;
            case 12:
                month="December";
                break;
        }
        String dateString="";
        if (forAmPm>=12)
        {
            dateString = hour + ":" + minute + " PM, " + dayMonth + " " + month + " " + year;
        }
        else
        {
            dateString = hour + ":" + minute + " AM, " + dayMonth + " " + month + " " + year;
        }


        return dateString;
    }

    public static int getYearString()
    {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());

        int yearInt = c.get(Calendar.YEAR);
        return yearInt;
    }

    public boolean hasConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected())
        {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected())
        {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
        {
            return true;
        }

        return false;
    }
}