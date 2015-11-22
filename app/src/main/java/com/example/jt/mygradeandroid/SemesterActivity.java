package com.example.jt.mygradeandroid;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

public class SemesterActivity extends ListActivity {

    private static final String TAG_SEMESTER_YEAR = "SemesterYear";
    private static final String TAG_SEMESTER_TYPE = "SemesterType";
    private static final String TAG_SEMESTER_STR = "SemesterSTR";
    private static final String TAG_SCHOOL_ID = "SchoolID";
    private static final String TAG_SEMESTER_ID = "SemesterID";

    ListView listView;
    ArrayList<HashMap<String, String>> semesterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        Intent intent = getIntent();
        Integer schoolID = intent.getIntExtra("SchoolID", 0);
        Integer studentID = intent.getIntExtra("StudentID", 0);

        // Set the text view as the activity layout
//        setContentView(textView);
    }



    private class GetSemesters extends AsyncTask<Void, Void, Void> {

        public static final String SCHOOL_WEBSERVICE_URL = "http://ec2-52-25-2-234.us-west-2.compute.amazonaws.com/MyGradeService/api/Semester";

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
                jsonStr = "{\"semesters\": " + jsonStr + " }";
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray schools = jsonObj.getJSONArray("semesters");

                    for (int i = 0; i < schools.length(); i++) {
                        JSONObject c = schools.getJSONObject(i);

                        String semesterYear = c.getString(TAG_SEMESTER_YEAR);
                        String semesterType = c.getString(TAG_SEMESTER_TYPE);
                        Integer schoolID = c.getInt(TAG_SCHOOL_ID);
                        Integer semesterID = c.getInt(TAG_SEMESTER_ID);

                        HashMap<String, String> school = new HashMap<String, String>();

                        school.put(TAG_SEMESTER_YEAR, semesterYear);
                        school.put(TAG_SEMESTER_TYPE, semesterType);
                        school.put(TAG_SEMESTER_STR, semesterYear + " " + semesterType);
                        school.put(TAG_SEMESTER_ID, Integer.toString(schoolID));
                        school.put(TAG_SCHOOL_ID, Integer.toString(semesterID));


                        semesterList.add(school);
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
 
            ListAdapter adapter = new SimpleAdapter(getBaseContext(), semesterList,
                    R.layout.school_list_item_layout,
                    new String[]{TAG_SEMESTER_STR},
                    new int[]{R.id.semester_list_item_name
                    });
            listView.setAdapter(adapter);
        }
    }
}
