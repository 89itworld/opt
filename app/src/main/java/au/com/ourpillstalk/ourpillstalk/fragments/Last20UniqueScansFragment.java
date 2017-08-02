package au.com.ourpillstalk.ourpillstalk.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.activities.ScanActivity;
import au.com.ourpillstalk.ourpillstalk.database.DBHelper;

public class Last20UniqueScansFragment extends Fragment
{
    ListView list_allScans1;
    ArrayList<String> myAllScan, myAllScan1, myAllScan2, myDate, myDate1, myDate2, s_no, s_no1, s_no2;
    DBHelper dbHelper;
    Cursor c1;
    int count =0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_last20_unique_scans, container, false);

        dbHelper = new DBHelper(getActivity());
        myAllScan = new ArrayList<String>();
        myAllScan1 = new ArrayList<String>();
        myAllScan2 = new ArrayList<String>();
        myDate = new ArrayList<String>();
        myDate1 = new ArrayList<String>();
        myDate2 = new ArrayList<String>();
        s_no = new ArrayList<String>();
        s_no1 = new ArrayList<String>();
        s_no2 = new ArrayList<String>();

        list_allScans1 = (ListView) v.findViewById(R.id.list_allScans1);

        c1 = dbHelper.getDataFromScannedPills();
        if (c1!=null && c1.getCount()>0)
        {
            c1.moveToFirst();
            do
            {
                s_no.add(c1.getString(0));
                myAllScan.add(c1.getString(2));
                myDate.add(c1.getString(3));
            }
            while (c1.moveToNext());
        }

        for (int i = myAllScan.size()-1; i>=0; i--)
        {
            if (!myAllScan1.contains(myAllScan.get(i)))
            {
                s_no1.add(s_no.get(i));
                myAllScan1.add(myAllScan.get(i));
                myDate1.add(myDate.get(i));
            }
        }

        if (myAllScan1.size()>0)
        {
            int index = myAllScan1.size()-1;
            do
            {
                s_no2.add(s_no.get(index));
                myAllScan2.add(myAllScan1.get(index));
                myDate2.add(myDate1.get(index));
                index--;
                count++;
            }
            while (count<20 && index>=0);
        }

        s_no.clear();
        myAllScan.clear();
        myDate.clear();

        for (int a = myAllScan2.size()-1; a >= 0; a--)
        {
            s_no.add(s_no2.get(a));
            myAllScan.add(myAllScan2.get(a));
            myDate.add(myDate2.get(a));
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, myAllScan)
        {
            @Override
            public int getCount()
            {
                return myAllScan.size()==0?1:myAllScan.size();
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent)
            {
                View v;
                if (myAllScan.size()<=0)
                {
                    myAllScan.add(""); // for showing No Scanned Data

                    v = super.getView(position, convertView, parent);

                    ((TextView) v.findViewById(v.getId())).
                            setTextColor(getResources().getColor(R.color.black));
                    ((TextView) v.findViewById(v.getId())).
                            setText("No Scanned Data");
                }
                else
                {       v = super.getView(position, convertView, parent);
                    ((TextView) v.findViewById(v.getId())).
                            setTextColor(getResources().getColor(R.color.black));
                    ((TextView) v.findViewById(v.getId())).
                            setText(myAllScan.get(position)+"\n"+myDate.get(position));
                }

                v.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (((TextView) v.findViewById(v.getId())).getText().equals("No Scanned Data"))
                        {

                        }
                        else
                        {
                            Intent i = new Intent(getActivity(), ScanActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle b = new Bundle();
                            Cursor c = dbHelper.getDataFromScannedPills(s_no.get(position));
                            c.moveToFirst();
                            do
                            {
                                b.putString("SNo", c.getString(0));
                                b.putString("XML", c.getString(1));
                                b.putString("DrugName", c.getString(2));
                                b.putString("PharmacyName", c.getString(4));
                                b.putString("DDate", c.getString(5));
                            }
                            while(c.moveToNext());
                            b.putInt("Key", 1);
                            i.putExtras(b);
                            getActivity().startActivity(i);
                        }
                    }
                });

                return v;
            }
        };
        list_allScans1.setAdapter(adapter1);

        return v;
    }

    public static Last20UniqueScansFragment newInstance(String text)
    {
        Last20UniqueScansFragment f = new Last20UniqueScansFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);
        return f;
    }
}
