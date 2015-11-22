package com.example.jt.mygradeandroid;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by jt on 11/22/2015.
 */
public class ClassActivity extends AppCompatActivity {

    private static final String TAG_CLASSID = "ClassID";
    private static final String TAG_SEMESTERID = "SemesterID";
    private static final String TAG_CLASSNAME = "ClassName";
    private static final String TAG_OVERALLGRADE = "OverallGrade";
    private static final String TAG_HOMEWORKPERCENT = "HomeworkPercent";
    private static final String TAG_QUIZPERCENT = "QuizPercent";
    private static final String TAG_PROJECTPERCENT = "ProjectPercent";
    private static final String TAG_FIRSTEXAMPERCENT = "FirstExamPercent";
    private static final String TAG_SECONDEXAMPERCENT = "SecondExamPercent";
    private static final String TAG_THIRDEXAMPERCENT = "ThirdExamPercent";
    private static final String TAG_FOURTHEXAMPERCENT = "FourthExamPercent";
    private static final String TAG_FINALEXAMPERCENT = "FinalExamPercent";
    private static final String TAG_OTHERPERCENT = "OtherPercent";

    ArrayList<HashMap<String, String>> classList;
    ListView lView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        Intent intent = getIntent();
        Integer classID = intent.getIntExtra("ClassID", 0);
        classList = new ArrayList<HashMap<String, String>>();
        lView = (ListView)findViewById(R.id.semester_list);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getCount() - 1 == position) {
                    Intent addClass = new Intent(parent.getContext(), AddClassActivity.class);
                    startActivity(addClass);
                } else {
                    HashMap<String, String> school = classList.get(position);
                    Integer classID = Integer.decode(school.get(TAG_CLASSID));

                    Intent intent = new Intent(parent.getContext(), GradeActivity.class);
                    intent.putExtra("ClassID", classID);

                    startActivity(intent);
                }
            }
        });


        (new GetClasses()).execute(classID);
    }


    private class GetClasses extends AsyncTask<Integer, Void, Void> {

        public static final String CLASS_WEBSERVICE_URL = "http://ec2-52-25-2-234.us-west-2.compute.amazonaws.com/MyGradeService/api/Class";

        @Override
        protected Void doInBackground(Integer... args) {
            Integer queryClassID = args[0];

            String jsonStr = "";
            try {
                String queryStr = CLASS_WEBSERVICE_URL + "/" + Integer.toString(queryClassID);
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
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray schools = jsonObj.getJSONArray("Classes");

                    for (int i = 0; i < schools.length(); i++) {
                        JSONObject c = schools.getJSONObject(i);

                        String className = c.getString(TAG_CLASSNAME);
                        Double overallGrade = c.getDouble(TAG_OVERALLGRADE);
                        Integer homeworkPercent = c.getInt(TAG_HOMEWORKPERCENT);
                        Integer quizPercent = c.getInt(TAG_QUIZPERCENT);
                        Integer projectPercent = c.getInt(TAG_PROJECTPERCENT);
                        Integer firstExam = c.getInt(TAG_FIRSTEXAMPERCENT);
                        Integer second = c.getInt(TAG_SECONDEXAMPERCENT);
                        Integer third = c.getInt(TAG_THIRDEXAMPERCENT);
                        Integer fourth = c.getInt(TAG_FOURTHEXAMPERCENT);
                        Integer finalPercent = c.getInt(TAG_FINALEXAMPERCENT);
                        Integer other = c.getInt(TAG_OTHERPERCENT);
                        Integer classID = c.getInt(TAG_CLASSID);
                        //Integer semesterID = c.getInt(TAG_SEMESTERID);

                        HashMap<String, String> school = new HashMap<String, String>();

                        school.put(TAG_CLASSID, Integer.toString(classID));
                        //school.put(TAG_SEMESTERID, Integer.toString(semesterID));
                        school.put(TAG_CLASSNAME, className);
                        school.put(TAG_OVERALLGRADE, Double.toString(overallGrade));
                        school.put(TAG_HOMEWORKPERCENT, Integer.toString(homeworkPercent));
                        school.put(TAG_QUIZPERCENT, Integer.toString(quizPercent));
                        school.put(TAG_PROJECTPERCENT, Integer.toString(projectPercent));
                        school.put(TAG_FIRSTEXAMPERCENT, Integer.toString(firstExam));
                        school.put(TAG_SECONDEXAMPERCENT, Integer.toString(second));
                        school.put(TAG_THIRDEXAMPERCENT, Integer.toString(third));
                        school.put(TAG_FOURTHEXAMPERCENT, Integer.toString(fourth));
                        school.put(TAG_FINALEXAMPERCENT, Integer.toString(finalPercent));
                        school.put(TAG_OTHERPERCENT, Integer.toString(other));

                        classList.add(school);
                    }
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

            ListAdapter adapter = new SimpleAdapter(getBaseContext(), classList,
                    R.layout.class_list_item_layout,
                    new String[]{TAG_CLASSNAME},
                    new int[]{R.id.class_list_item_name
                    });
            lView.setAdapter(adapter);
        }
    }
}

