package au.com.ourpillstalk.ourpillstalk.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import au.com.ourpillstalk.ourpillstalk.R;
import au.com.ourpillstalk.ourpillstalk.util.MyApplication;

public class UserInfoFragment extends Fragment
{
    ListView list_userInfo;
    ArrayList<String> forUserInfo;
    ArrayList<String> userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_user_info, container, false);
        list_userInfo = (ListView) v.findViewById(R.id.list_userInfo);
        userInfo = new ArrayList<String>();
        forUserInfo = new ArrayList<String>();
        userInfo.add(MyApplication.firstName);
        forUserInfo.add("First Name: ");
        userInfo.add(MyApplication.lastName);
        forUserInfo.add("Last Name: ");
        userInfo.add(MyApplication.gender);
        forUserInfo.add("Gender: ");
        userInfo.add(MyApplication.emailAddress);
        forUserInfo.add("Email Id: ");
        userInfo.add(MyApplication.yearOfBirth);
        forUserInfo.add("Year Of Birth: ");
        userInfo.add(MyApplication.postCode);
        forUserInfo.add("Postcode: ");
        userInfo.add(MyApplication.countryOfOrigin);
        forUserInfo.add("Country of Origin: ");
        userInfo.add(MyApplication.preferedLang);
        forUserInfo.add("Prefered Lang.: ");
        userInfo.add(MyApplication.allergies);
        forUserInfo.add("Allergies: ");
        userInfo.add(MyApplication.emergencyContactNumber);
        forUserInfo.add("Emergency contact number: ");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, userInfo)
        {
            @Override
            public int getCount()
            {
                return userInfo.size()==0?1:userInfo.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v;

                if (userInfo.size()<=0)
                {
                    userInfo.add(""); // for showing No Scanned Data

                    v = super.getView(position, convertView, parent);

                    ((TextView) v.findViewById(v.getId())).
                            setTextColor(getResources().getColor(R.color.black));
                    ((TextView) v.findViewById(v.getId())).setText("No User Information");
                }
                else
                {
                    v = super.getView(position, convertView, parent);

                    ((TextView)v.findViewById(v.getId())).
                            setTextColor(getResources().getColor(R.color.black));
                    ((TextView)v.findViewById(v.getId())).
                            setText(forUserInfo.get(position) + userInfo.get(position));
                }
                return v;
            }
        };
        list_userInfo.setAdapter(adapter2);
        return v;
    }

    public static UserInfoFragment newInstance(String text)
    {
        UserInfoFragment f = new UserInfoFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

}
