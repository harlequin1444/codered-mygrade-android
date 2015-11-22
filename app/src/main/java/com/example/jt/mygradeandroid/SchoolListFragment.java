package com.example.jt.mygradeandroid;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Miguel on 11/21/2015.
 */
public class SchoolListFragment extends Fragment {

    private static final String TAG_SCHOOL_NAME = "SchoolName";
    private static final String TAG_SCHOOL_STATE = "State";
    private static final String TAG_SCHOOL_CITY = "City";
    private static final String TAG_SCHOOL_ID = "SchoolID";
    private static final String TAG_STUDENT_ID = "StudentID";

    ListView listView;
    ArrayList<HashMap<String, String>> schoolList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragmentschools, container, false);
        listView = (ListView) rootview.findViewById(R.id.schools_list);

        schoolList = new ArrayList<HashMap<String, String>>();

        GetContacts getter = new GetContacts();
        getter.execute();

        return rootview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int duration = Toast.LENGTH_SHORT;

                HashMap<String, String> school = schoolList.get(position);
                String schoolName = school.get(TAG_SCHOOL_NAME);
                String schoolState = school.get(TAG_SCHOOL_STATE);
                String schoolCity = school.get(TAG_SCHOOL_CITY);
                Integer schoolID = Integer.decode(school.get(TAG_SCHOOL_ID));
                Integer studentID = Integer.decode(school.get(TAG_STUDENT_ID));

                Intent intent = new Intent(getActivity(), SemesterActivity.class);

                Toast toast = Toast.makeText(getActivity(), schoolName, duration);
                intent.putExtra("SchoolID", schoolID);
                intent.putExtra("StudentID", studentID);

                startActivity(intent);

                toast.show();
            }
        });
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        public static final String SCHOOL_WEBSERVICE_URL = "http://ec2-52-25-2-234.us-west-2.compute.amazonaws.com/MyGradeService/api/School";

        @Override
        protected Void doInBackground(Void... arg0) {
            String jsonStr = "";
            try {
                URL url = new URL(SCHOOL_WEBSERVICE_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                conn.connect();

                int response = conn.getResponseCode();
                Log.e("DYLAN", "The response is: " + response);

                InputStream is = conn.getInputStream();

                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }

                jsonStr = total.toString();
            } catch (Exception e) {
                Log.e("DYLAN", e.getMessage());
            }

            if (jsonStr != null) {
                jsonStr = "{\"schools\": " + jsonStr + " }";
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray schools = jsonObj.getJSONArray("schools");

                    for (int i = 0; i < schools.length(); i++) {
                        JSONObject c = schools.getJSONObject(i);

                        String name = c.getString(TAG_SCHOOL_NAME);
                        String state = c.getString(TAG_SCHOOL_STATE);
                        String city = c.getString(TAG_SCHOOL_CITY);
                        Integer schoolID = c.getInt(TAG_SCHOOL_ID);
                        Integer studentID = c.getInt(TAG_STUDENT_ID);

                        HashMap<String, String> school = new HashMap<String, String>();

                        school.put(TAG_SCHOOL_NAME, name);
                        school.put(TAG_SCHOOL_STATE, state);
                        school.put(TAG_SCHOOL_CITY, city);
                        school.put(TAG_SCHOOL_ID, Integer.toString(schoolID));
                        school.put(TAG_STUDENT_ID, Integer.toString(studentID));

                        schoolList.add(school);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(getActivity(), schoolList,
                    R.layout.school_list_item_layout,
                    new String[]{TAG_SCHOOL_NAME, TAG_SCHOOL_STATE, TAG_SCHOOL_CITY},
                    new int[]{R.id.school_list_item_name, R.id.state_list_item_name, R.id.city_list_item_name
                    });
            listView.setAdapter(adapter);
        }
    }

}
