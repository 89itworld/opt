package au.com.ourpillstalk.ourpillstalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.activities.ScanActivity;
import au.com.ourpillstalk.ourpillstalk.database.DBHelper;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class CustomListItemAdapter extends BaseAdapter
{
    ArrayList<String> list, list1, dateList, dateList1, s_no, s_no1;
    Context con;
    DBHelper dbHelper;
    private static LayoutInflater inflater = null;
    MyApplication myApplication;

    public CustomListItemAdapter(ArrayList<String> list, ArrayList<String> dateList,
                                 ArrayList<String> s_no, Context con)
    {
        this.list = new ArrayList<>(list);
        this.list1 = new ArrayList<>();
        this.dateList = new ArrayList<>(dateList);
        this.dateList1 = new ArrayList<>();
        this.s_no = new ArrayList<>(s_no);
        this.s_no1 = new ArrayList<>();
        this.con = con;
        dbHelper = new DBHelper(con);
        myApplication = (MyApplication) con;

        for (int a = list.size()-1; a>=0; a--)
        {
            list1.add(list.get(a));
            dateList1.add(dateList.get(a));
            s_no1.add(s_no.get(a));
        }

        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        if (list1.size()<=0)
            return 1;
        else
            return list1.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public static class ViewHolder
    {
        public TextView text;
        public ImageView image;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        View view = convertView;
        final ViewHolder viewHolder;

        if (view==null)
        {
            view = inflater.inflate(R.layout.custom_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.txt_listItem);
            viewHolder.image=(ImageView)view.findViewById(R.id.img_delete);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dbHelper.deleteDataFromScannedPills(s_no1.get(position));
                list1.remove(position);
                dateList1.remove(position);
                s_no1.remove(position);
                System.out.println("my list items "+ position);
                notifyDataSetChanged();
            }
        });

        viewHolder.text.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (viewHolder.text.getText().equals("No Scanned Data"))
                {

                }
                else
                {
                    Intent i = new Intent(con, ScanActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle b = new Bundle();
                    Cursor c = dbHelper.getDataFromScannedPills(s_no1.get(position));
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

                    b.putInt("Key", 2);

                    i.putExtras(b);
                    con.startActivity(i);
                }
            }
        });

        if (list1.size()<=0)
        {
            viewHolder.text.setText("No Scanned Data");
            viewHolder.image.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.text.setText(list1.get(position)+"\n"+dateList1.get(position));
        }
        return view;
    }
}
