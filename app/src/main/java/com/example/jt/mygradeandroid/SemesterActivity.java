package com.example.jt.mygradeandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

public class SemesterActivity extends AppCompatActivity {

    private static final String TAG_SEMESTER_YEAR = "SemesterYear";
    private static final String TAG_SEMESTER_TYPE = "SemesterType";
    private static final String TAG_SEMESTER_STR = "SemesterSTR";
    private static final String TAG_SCHOOL_ID = "SchoolID";
    private static final String TAG_SEMESTER_ID = "SemesterID";

    ArrayList<HashMap<String, String>> semesterList;
    ListView lView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        //Toolbar t = (Toolbar)findViewById(R.id.toolbar2);
//        t.setTitle("Semester");

        Intent intent = getIntent();
        Integer schoolID = intent.getIntExtra("SemesterID", 0);
        semesterList = new ArrayList<HashMap<String, String>>();
        lView = (ListView)findViewById(R.id.semester_list);

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getCount() - 1 == position) {
                    Intent addSemester = new Intent(parent.getContext(), AddSemesterActivity.class);
                    startActivity(addSemester);
                } else {
                    HashMap<String, String> school = semesterList.get(position);
                    //Integer schoolID = Integer.decode(school.get(TAG_SCHOOL_ID));
                    Integer semesterID = Integer.decode(school.get(TAG_SEMESTER_ID));

                    Intent intent = new Intent(parent.getContext(), ClassActivity.class);
                    intent.putExtra("SchoolID", semesterID);

                    startActivity(intent);
                }
            }
        });

        (new GetSemesters()).execute(schoolID);
    }



    private class GetSemesters extends AsyncTask<Integer, Void, Void> {

        public static final String SCHOOL_WEBSERVICE_URL = "http://ec2-52-25-2-234.us-west-2.compute.amazonaws.com/MyGradeService/api/Semester";

        @Override
        protected Void doInBackground(Integer... args) {
            Integer querySchoolID = args[0];

            String jsonStr = "";
            try {
                String queryStr = SCHOOL_WEBSERVICE_URL; // + "/" + Integer.toString(querySchoolID);
                URL url = new URL(queryStr);

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
                //jsonStr = "{\"semesters\": " + jsonStr + " }";
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray schools = jsonObj.getJSONArray("Semesters");

                    for (int i = 0; i < schools.length(); i++) {
                        JSONObject c = schools.getJSONObject(i);

                        String semesterYear = c.getString(TAG_SEMESTER_YEAR);
                        String semesterType = c.getString(TAG_SEMESTER_TYPE);
                        Integer schoolID = c.getInt(TAG_SCHOOL_ID);
                        Integer semesterID = c.getInt(TAG_SEMESTER_ID);

                        HashMap<String, String> semester = new HashMap<String, String>();

                        semester.put(TAG_SEMESTER_YEAR, semesterYear);
                        semester.put(TAG_SEMESTER_TYPE, semesterType);
                        semester.put(TAG_SEMESTER_STR, semesterType + " " + semesterYear);
                        semester.put(TAG_SEMESTER_ID, Integer.toString(schoolID));
                        semester.put(TAG_SCHOOL_ID, Integer.toString(semesterID));

                        semesterList.add(semester);
                    }
                    HashMap<String, String> addSem = new HashMap<String, String>();

                    addSem.put(TAG_SEMESTER_STR, "Add New Semester");

                    semesterList.add(addSem);

                } catch (JSONException e) {
                    Log.e("DYLAN", "json errer: " + e.getMessage());
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
                    R.layout.semester_list_item_layout,
                    new String[]{TAG_SEMESTER_STR},
                    new int[]{R.id.semester_list_item_name
                    });
            lView.setAdapter(adapter);
        }
    }
}