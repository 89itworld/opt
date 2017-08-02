package au.com.ourpillstalk.ourpillstalk.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import au.com.ourpillstalk.ourpillstalk.model.TranslatedText;

/**
 * Created by dev on 16/5/17.
 */

public class WebServerUtils implements WebKeys,WebMessages{

    private static WebServerUtils mWebUtils=new WebServerUtils();
    private static Context mContext;


    public static WebServerUtils getInstance(Context ctx){
        mContext=ctx;
        return mWebUtils;
    }

    public TranslatedText parseTranslatedText(String response){
        TranslatedText translatedText=new TranslatedText();

        try {
            Log.i("Trans Response-",response);

            JSONObject lJsonObject=new JSONObject(response);

            if(lJsonObject.has(KEY_DATA)){

                JSONObject lLocalJsobObject=lJsonObject.getJSONObject(KEY_DATA);

                JSONArray lJsonArray=lLocalJsobObject.getJSONArray(KEY_TRANSLATIONS);

                for(int count=0;count<lJsonArray.length();count++){

                    JSONObject lJsonLocalObject=lJsonArray.getJSONObject(count);

                    if(lJsonLocalObject.has(KEY_TRANSLATEDTEXT)){
                        translatedText.setTranslatedText(lJsonLocalObject.get(KEY_TRANSLATEDTEXT).toString());
                    }

                    translatedText.setStatusCode(null);
                }
            }

            if(lJsonObject.has(KEY_ERROR)){

                if(lJsonObject.has(KEY_CODE)) {
                    String lCode = lJsonObject.getString(KEY_CODE);
                    translatedText.setStatusCode(lCode);
                }

                if(lJsonObject.has(KEY_MESSAGE)){
                    String lMessage=lJsonObject.getString(KEY_MESSAGE);
                    translatedText.setStatusMessage(lMessage);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return translatedText;
    }


}
