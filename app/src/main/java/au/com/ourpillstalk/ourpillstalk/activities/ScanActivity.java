package au.com.ourpillstalk.ourpillstalk.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.memetix.mst.translate.Translate;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.database.DBHelper;
import au.com.ourpillstalk.ourpillstalk.util.CustomRequest;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class ScanActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
	DBHelper dbHelper;
	private TextToSpeech tts;
	TextView txt_pillsInfo, txt_pillsInfo1, lastScanTextView, txt_despenseDate;//, txt_pillsInfo3;
    static boolean myFlag = false;
	Button fab_replay, fab_stop, fab_cmi;
	ImageView fab_more;
	MyApplication myApplication;
	LinearLayout ll_forHome;
	TextView txt_home;
	String drugName;
	String originalText;
	String translatedText;
	String xml1, xml2;
	String myLang;
	int x=0;
	String s_no;
	int myttsStatus;
	Bundle b;
	Boolean flag = true;
	static String nameOfPill, nameOfPharmacy, dispensedDate, formatDate;
	static final String URL_INSERT_SCAN = "http://www.ourpillstalk.com.au/submission/insertscan.php";
	static final String SCAN_NUM_TAG = "scan_num";
	static final String DATE_TAG = "date";
	static final String PAT_NAME_TAG = "n";
	static final String DRUG_NAME_TAG = "dg";
	static final String EXP_INSTRUC_TAG = "in";
	static final String SCRIPT_ID_TAG = "id";
	static final String PHARM_NAME_TAG = "pm";
	static final String DATE_DISP_TAG = "dt";
	static final String CMI_NUM_TAG = "cn";
	static final String DATE_FRMT_TAG="df";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		myApplication = (MyApplication) getApplicationContext();

		dbHelper = new DBHelper(this);

		txt_pillsInfo = (TextView) findViewById(R.id.txt_pillsInfo);
		txt_pillsInfo1 = (TextView) findViewById(R.id.txt_pillsInfo1);
		txt_despenseDate = (TextView) findViewById(R.id.txt_despenseDate);
//		txt_pillsInfo3 = (TextView) findViewById(R.id.txt_pillsInfo3);
		lastScanTextView = (TextView) findViewById(R.id.lastScanTextView);
		fab_more = (ImageView) findViewById(R.id.fab_more);
		fab_replay = (Button) findViewById(R.id.fab_replay);
		fab_stop = (Button) findViewById(R.id.fab_stop);
		fab_cmi = (Button) findViewById(R.id.fab_cmi);
		ll_forHome = (LinearLayout) findViewById(R.id.ll_forHome);
		txt_home = (TextView) findViewById(R.id.txt_home);
		myLang = myApplication.lang;

		tts = new TextToSpeech(this, this);

		b = getIntent().getExtras();
		if (b!=null)
		{
			xml1 = b.getString("XML");
			x = b.getInt("Key", 0);
			s_no = b.getString("SNo");
			drugName = b.getString("DrugName");
			drugName = drugName.substring(0, drugName.indexOf(" "));
			nameOfPharmacy = b.getString("PharmacyName");
			dispensedDate = b.getString("DDate");
			lastScanTextView.setVisibility(View.GONE);

			xml2 = xml1;

			if (true)
			{
				String[] newxml1 = xml2.split("Dispensed:");
				String[] newxml2 = newxml1[1].split("\n");
				String date = newxml2[0];
				String a[] = date.trim().split("/");
				date = a[1] + "/" + a[0] + "/" + a[2];
				xml2 = xml2.replace(newxml2[0], " "+date);
			}

			if (MyApplication.spnrIndex == 11)
			{
				String[] newxml1 = xml1.split("Dispensed:");
				String[] newxml2 = newxml1[1].split("\n");
				String date = newxml2[0];
				String a[] = date.trim().split("/");
				date = a[1] + "/" + a[0] + "/" + a[2];
				xml1 = xml1.replace(newxml2[0], " "+date);
			}

			String text1 = xml1.substring(0, xml1.indexOf("\n"));
			String text2 = xml1.substring(xml1.indexOf("\n")+2);
			txt_pillsInfo1.setText(text1);
			SpannableString ss1 = new SpannableString(text2);
			int index=text2.lastIndexOf("\n");
			ss1.setSpan(new RelativeSizeSpan(.4f), index, index+1, 0);
			txt_pillsInfo.setText(ss1, TextView.BufferType.SPANNABLE);

			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					originalText = myApplication.abbreviationsUnitsToSpeak(xml2);

					if (!myLang.equalsIgnoreCase("en"))
					{
						tts.setLanguage(toLocale("en"));
						speakOut(txt_pillsInfo1.getText().toString());
						isTTSSpeaking();

						/*ForSpeak fs = new ForSpeak();
						fs.execute();*/
					}
					else
					{
						tts.setLanguage(toLocale(myLang));
						speakOut(originalText);
					}
				}
			},3000);
		}
		else
		{
			new IntentIntegrator(this).initiateScan();
		}

		txt_home.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		fab_more.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dbHelper.deleteDataFromScannedPills(s_no);
				Toast.makeText(ScanActivity.this, "Delete Perform", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(ScanActivity.this, HistoryActivity.class));
			}
		});

		fab_replay.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (x==1 || x==2)
				{
					if (!myLang.equalsIgnoreCase("en"))
					{
						tts.setLanguage(toLocale("en"));
						speakOut(txt_pillsInfo1.getText().toString());
						flag=true;
						isTTSSpeaking();
					}
					else {
						tts.setLanguage(toLocale(myLang));
//						speakOut(txt_pillsInfo3.getText().toString());
						speakOut(originalText);
					}
				}
				else
				{
					if (!myLang.equalsIgnoreCase("en"))
					{
						tts.setLanguage(toLocale("en"));
						speakOut(txt_pillsInfo1.getText().toString());
						flag=true;
						isTTSSpeaking();
					}
					else {
						tts.setLanguage(toLocale(myLang));
//						speakOut(txt_pillsInfo3.getText().toString());
						speakOut(originalText);
					}
				}
			}
		});

		fab_stop.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tts.stop();
			}
		});

		fab_cmi.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new AlertDialog.Builder(ScanActivity.this)
						.setTitle("Leaving Our Pills Talk")
						.setMessage("CMI is an external website and is not affiliated with Our Pills Talk.\n\nLeave Our Pills Talk to view CMI for " + drugName + "?")
						.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								Intent i = new Intent(Intent.ACTION_VIEW,
										Uri.parse("http://www.medicines.org.au/mob/consumer-results.cfm?searchtext=" + drugName));
								startActivity(i);
							}
						}).
						setNegativeButton("Cancel", new DialogInterface.OnClickListener()
						{
                            @Override
                            public void onClick(DialogInterface dialog, int which)
							{
                                dialog.dismiss();
                            }
                        }).
						show();
			}
		});

		if (!myLang.equals("en"))
		{
			if (myApplication.hasConnection())
			{
				ArrayList<String> t1 = new ArrayList<>();
				t1.add(0, lastScanTextView.getText().toString());
				t1.add(1, txt_pillsInfo.getText().toString());

				Object[] views = new Object[2];
				views[0] = lastScanTextView;
				views[1] = txt_pillsInfo;

				myApplication.myUnivTranslatorForScanOnly(t1, views, ScanActivity.this);
			}
			else
			{
				myLang = "en";
			}
		}

		if (x == 2)
		{
			ll_forHome.setVisibility(View.VISIBLE);
		}
	}

	/*public void doneWithTranslation()
	{
		txt_pillsInfo3.setText(txt_pillsInfo1.getText()+"\n"+txt_pillsInfo.getText());
		tts.setLanguage(toLocale(myLang));
		speakOut(txt_pillsInfo3.getText().toString());
	}

	public void doneWithTranslation1()
	{
		txt_pillsInfo3.setText(txt_pillsInfo1.getText() + "\n" + txt_pillsInfo.getText());
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				if (b!=null)
				{

				}
				else
				{
					startActivity(new Intent(ScanActivity.this, MainActivity.class));
				}
				tts.stop();
				tts.shutdown();
				finish();
		}
		return super.onOptionsItemSelected(item);
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
					Handler handler =  new Handler(ScanActivity.this.getMainLooper());
					handler.post( new Runnable(){
						public void run(){
							Toast.makeText(ScanActivity.this, "Speech Not Supported",Toast.LENGTH_LONG).show();
						}
					});
					Log.e("TTS", "This Language is not supported");
				}
				else
				{
//					doneWithTranslation1();
//					textToSpeech(txt_pillsInfo3.getText().toString());
					speakOut(originalText);
				}
			}
			else
			{
				Handler handler =  new Handler(ScanActivity.this.getMainLooper());
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(ScanActivity.this, "Hello Not Supported Speech1", Toast.LENGTH_LONG).show();
					}
				});
				Log.e("TTS1", "This Language is not supported");
			}
			return null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if(result != null)
		{
			if(result.getContents() == null)
			{
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
				startActivity(new Intent(ScanActivity.this, MainActivity.class));
				finish();
			}
			else
			{
				String scanResult1 = result.getContents();
				String scanResult = scanResult1.replace("&", "and");

				/*ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
				tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400);*/

				Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);

				if (myApplication.isPrescriptionScanXML(scanResult))
				{
					String text1 = myApplication.abbreviations(scanResult);
					String text2 = myApplication.abbreviationsTwo(text1);

					HashMap<String, String> parseXml = getXMLQRScanMap(text2);
					String processedBody = getScanInfoToDisplay(parseXml);
					String processedBody1 = getScanInfoToSpeek(parseXml);

					String process1 = processedBody.substring(0, processedBody.indexOf("\n"));
					String process2 = processedBody.substring(processedBody.indexOf("\n") + 2);

					nameOfPill = parseXml.get(DRUG_NAME_TAG);
					drugName = nameOfPill.substring(0, nameOfPill.indexOf(" "));
					nameOfPharmacy = parseXml.get(PHARM_NAME_TAG);
					dispensedDate = parseXml.get(DATE_DISP_TAG);
					formatDate = parseXml.get(DATE_FRMT_TAG);

					String current_date = myApplication.getDateString();
					dbHelper.insertDataToScannedPills(processedBody, nameOfPill, current_date, nameOfPharmacy, dispensedDate);

					if (!myFlag)
					{
						String[] newxml1 = processedBody1.split("Dispensed:");
						String[] newxml2 = newxml1[1].split("\n");
						String date = newxml2[0];
						String a[] = date.trim().split("/");
						date = a[1] + "/" + a[0] + "/" + a[2];
						processedBody1 = processedBody1.replace(newxml2[0], " "+date);
					}

					if (MyApplication.spnrIndex==11)
					{
						String[] newxml1 = processedBody.split("Dispensed:");
						String[] newxml2 = newxml1[1].split("\n");
						String date = newxml2[0];
						String a[] = date.trim().split("/");
						date = a[1] + "/" + a[0] + "/" + a[2];
						processedBody = processedBody.replace(newxml2[0], " "+date);
					}

					Map<String, String> mDetails = new HashMap<String, String>();
					mDetails.put("userID", MyApplication.UniqueID);
					mDetails.put("date_format", TextUtils.isEmpty(formatDate)?"":formatDate);
					mDetails.put("dispense_date", dispensedDate);
					mDetails.put("pharmaceutical", nameOfPill);
					mDetails.put("pharmacy_number", nameOfPharmacy);

					if (myApplication.hasConnection())
					{
						webServicePart(URL_INSERT_SCAN, mDetails);
					}

					SpannableString ss1 = new SpannableString(process2);
					int index=process2.lastIndexOf("\n");
					ss1.setSpan(new RelativeSizeSpan(.4f), index, index + 1, 0);


					txt_pillsInfo1.setText(process1);
					txt_pillsInfo.setText(ss1, TextView.BufferType.SPANNABLE);

					originalText = myApplication.abbreviationsUnitsToSpeak(processedBody1);

					if (!myLang.equals("en"))
					{
						ArrayList<String> t1 = new ArrayList<>();
						t1.add(0, lastScanTextView.getText().toString());
						t1.add(1, txt_pillsInfo.getText().toString());

						Object[] views = new Object[2];
						views[0] = lastScanTextView;
						views[1] = txt_pillsInfo;

						myApplication.myUnivTranslatorForScanOnly(t1, views, ScanActivity.this);
					}

//					ForSpeak fs = new ForSpeak();
//					fs.execute();

					new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							if (!myLang.equalsIgnoreCase("en"))
							{
								tts.setLanguage(toLocale("en"));
								speakOut(txt_pillsInfo1.getText().toString());
								isTTSSpeaking();
							}
							else
							{
								tts.setLanguage(toLocale(myLang));
								speakOut(originalText);
							}
						}
					},3000);

					lastScanTextView.setVisibility(View.GONE);
					Log.e("ScanTextView", text2);
				}
				else
				{
					fab_more.setVisibility(View.INVISIBLE);
					lastScanTextView.setVisibility(View.VISIBLE);
				}
			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private static HashMap<String,String> getXMLQRScanMap(String xml)
	{
		HashMap<String, String> prescriptionHashMap = new HashMap<>();
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xml));
			while(parser.getEventType()!= XmlPullParser.END_DOCUMENT)
			{
				if (parser.getEventType() == XmlPullParser.START_TAG)
				{
					if (parser.getName().equals(SCAN_NUM_TAG))
					{
						prescriptionHashMap.put(SCAN_NUM_TAG, parser.nextText());
					}
					else if (parser.getName().equals(DATE_TAG))
					{
						prescriptionHashMap.put(DATE_TAG, parser.nextText());
					}
					else if (parser.getName().equals(PAT_NAME_TAG))
					{
						prescriptionHashMap.put(PAT_NAME_TAG, parser.nextText());
					}
					else if (parser.getName().equals(DRUG_NAME_TAG))
					{
						prescriptionHashMap.put(DRUG_NAME_TAG, parser.nextText());
					}
					else if (parser.getName().equals(EXP_INSTRUC_TAG))
					{
						prescriptionHashMap.put(EXP_INSTRUC_TAG, parser.nextText());
					}
					else if (parser.getName().equals(SCRIPT_ID_TAG))
					{
						prescriptionHashMap.put(SCRIPT_ID_TAG, parser.nextText());
					}
					else if (parser.getName().equals(PHARM_NAME_TAG))
					{
						prescriptionHashMap.put(PHARM_NAME_TAG, parser.nextText());
					}
					else if (parser.getName().equals(DATE_DISP_TAG))
					{
						prescriptionHashMap.put(DATE_DISP_TAG, parser.nextText());
					}
					else if (parser.getName().equals(CMI_NUM_TAG))
					{
						prescriptionHashMap.put(CMI_NUM_TAG, parser.nextText());
					}
					else if (parser.getName().equals(DATE_FRMT_TAG))
					{
						prescriptionHashMap.put(DATE_FRMT_TAG, parser.nextText());
					}
				}
				parser.next();
			}
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return prescriptionHashMap;
	}

	private static String getScanInfoToDisplay(HashMap<String, String> parseXml)
	{
		String date = "";
		if(parseXml.containsKey(DATE_DISP_TAG))
		{
			date = parseXml.get(DATE_DISP_TAG);
		}
		else
		{
			date = "(no date provided)";
		}

        if (parseXml.containsKey(PHARM_NAME_TAG))
        {
            if(parseXml.get(PHARM_NAME_TAG).trim().isEmpty())
            {
                if(date.equalsIgnoreCase("(no date provided)"))
                {
                    Log.e("Data to SHow 1", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

                    return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG);
                }
                else
                {
                    Log.e("Data to SHow 2",parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

                    return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG) + "\n\nDispensed: " + date;
                }
            }
            else
            {
                if (date.equalsIgnoreCase("(no date provided)"))
                {
                    Log.e("Data to SHow 3", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));
                    return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG);// + "\n\nPharmacy: " + parseXml.get(PHARM_NAME_TAG);
                }
                else
                {
                    Log.e("Data to SHow 4", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));
                    return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG) + "\n\nDispensed: " + date;// + "\nPharmacy: " + parseXml.get(PHARM_NAME_TAG);
                }
            }
        }
        else
        {
            myFlag = true;

            if(date.equalsIgnoreCase("(no date provided)"))
            {
                Log.e("Data to SHow 1", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

                return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG);
            }
            else
            {
                Log.e("Data to SHow 2",parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

                return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG) + "\n\nDispensed: " + date;
            }
        }
	}

	private static String getScanInfoToSpeek(HashMap<String, String> parseXml)
	{
		String date = "";
		if(parseXml.containsKey(DATE_DISP_TAG))
		{
			date = parseXml.get(DATE_DISP_TAG);
		}
		else
		{
			date = "(no date provided)";
		}

		if (parseXml.containsKey(PHARM_NAME_TAG))
		{
			if(parseXml.get(PHARM_NAME_TAG).trim().isEmpty())
			{
				if(date.equalsIgnoreCase("(no date provided)"))
				{
					Log.e("Data to SHow 1", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

					return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG);
				}
				else
				{
					Log.e("Data to SHow 2",parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

					return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG) + "\n\nDispensed: " + date;
				}
			}
			else
			{
				if (date.equalsIgnoreCase("(no date provided)"))
				{
					Log.e("Data to SHow 3", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

					return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG);
				}
				else
				{
					Log.e("Data to SHow 4", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));
					return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG) + "\n\nDispensed: " + date;
				}
			}
		}
		else
		{
			if(date.equalsIgnoreCase("(no date provided)"))
			{
				Log.e("Data to SHow 1", parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

				return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG);
			}
			else
			{
				Log.e("Data to SHow 2",parseXml.get(DRUG_NAME_TAG) + ":" + parseXml.get(PAT_NAME_TAG) + ":" + parseXml.get(EXP_INSTRUC_TAG) + "Dispensed: " + date + "Pharmacy: " + parseXml.get(PHARM_NAME_TAG));

				return parseXml.get(DRUG_NAME_TAG) + " \r\n\n" + parseXml.get(PAT_NAME_TAG) + ".\n" + parseXml.get(EXP_INSTRUC_TAG) + "\n\nDispensed: " + date;
			}
		}
	}

	@Override
	public void onInit(int status)
	{
		myttsStatus = status;
		Log.d("Status", status+"");
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

		if (b!=null)
		{
			if (x == 1)
			{
				startActivity(new Intent(ScanActivity.this, EmergencyActivity.class));
			}
			else if (x == 2)
			{
				startActivity(new Intent(ScanActivity.this, HistoryActivity.class));
			}
		}
		else
		{
			startActivity(new Intent(ScanActivity.this, MainActivity.class));
		}
		tts.stop();
		tts.shutdown();
		finish();
	}

	private void textToSpeech(String myText)
	{
		String text = myText;
		String[] textBreaker = text.split("Dispensed");

		tts.setLanguage(toLocale(myLang));
		if (myLang.equalsIgnoreCase("en"))
		{
			speakOut(myText);
		}
		else
		{
			try
			{
				Translate.setClientId("com_steve_OurPillsTalk007");
				Translate.setClientSecret("QR2+jqRWpURZXpfEOMD7h6+9Nqy83hYBw3UuKcYaQIw=");
				translatedText = Translate.execute(textBreaker[0], myApplication.country);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			speakOut(translatedText);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		tts.stop();
	}

	void webServicePart(String URL_FEED,Map<String,String> dataParam)
	{
		System.out.println("kkkkkkk " + URL_FEED + "*****" + dataParam.toString());

		CustomRequest jsonReq = new CustomRequest(Request.Method.POST, URL_FEED,dataParam, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					if(response!=null)
					{
						Log.d("Response: ", response.toString());
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
			public void onErrorResponse(VolleyError error)
			{
				NetworkResponse response = error.networkResponse;
				Log.e("My System", error.toString());
				if (error instanceof ServerError && response != null)
				{
					try
					{
						String res = new String(response.data,
								HttpHeaderParser.parseCharset(response.headers, "utf-8"));
						// Now you can use any deserializer to make sense of data
						JSONObject obj = new JSONObject(res);
					}
					catch (UnsupportedEncodingException e1) {
						// Couldn't properly decode data to string
						e1.printStackTrace();
					}
					catch (JSONException e2)
					{
						// returned data is not JSONObject?
						e2.printStackTrace();
					}
				}

			}
		});
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(jsonReq);
	}

	public void isTTSSpeaking()
	{
		final Handler h =new Handler();

		Runnable r = new Runnable()
		{
			public void run()
			{
				System.out.println("ttttssssssssss: inside IsSpeaking");
				if ((!tts.isSpeaking()) && flag==true)
				{
					onTTSSpeechFinished();
					System.out.println("ttttssssssssss: inside done");
				}

				h.postDelayed(this, 1000);
			}
		};

		h.postDelayed(r, 1000);
	}

	public void onTTSSpeechFinished()
	{
		flag = false;
		tts.setLanguage(toLocale(myLang));
		String talk = txt_pillsInfo.getText().toString();
		String talk1 = talk.substring(talk.substring(0, talk.length() - 1).lastIndexOf("\n"));
		String talk2 = talk.substring(0, talk.indexOf(talk1)-1);
		if (!myLang.equals("en"))
		{
			if (myApplication.hasConnection())
			{
				speakOut(talk2);
			}
			else
			{
				myLang = "en";
				speakOut(talk);
			}
		}
		else
		{
			speakOut(talk);
		}

	}
}