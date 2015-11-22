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
public class GradeActivity extends AppCompatActivity {

    private static final String TAG_GRADEID = "GradeID";
    private static final String TAG_CLASSID = "ClassID";
    private static final String TAG_ASSIGNMENTNAME = "AssignmentName";
    private static final String TAG_ASSIGNMENTTYPE = "AssignmentType";
    private static final String TAG_ASSIGNMENTGRADE = "AssignmentGrade";

//    TAG_GRADEID
//    TAG_CLASSID
//    TAG_ASSIGNMENTNAME
//    TAG_ASSIGNMENTTYPE
//    TAG_ASSIGNMENTGRADE

    ArrayList<HashMap<String, String>> gradeList;
    ListView lView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        Intent intent = getIntent();
        Integer classID = intent.getIntExtra("ClassID", 0);
        gradeList = new ArrayList<HashMap<String, String>>();
        lView = (ListView)findViewById(R.id.semester_list);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getCount() - 1 == position) {
                    Intent addGrade = new Intent(parent.getContext(), AddGradeActivity.class);
                    startActivity(addGrade);
                }
            }
        });
        (new GetClasses()).execute(classID);
    }

    private class GetClasses extends AsyncTask<Integer, Void, Void> {

        public static final String GRADE_WEBSERVICE_URL = "http://ec2-52-25-2-234.us-west-2.compute.amazonaws.com/MyGradeService/api/Grade";

        @Override
        protected Void doInBackground(Integer... args) {
            Integer queryClassID = args[0];

            String jsonStr = "";
            try {
                String queryStr = GRADE_WEBSERVICE_URL; // + "/" + Integer.toString(queryClassID);
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
                    JSONArray grades = jsonObj.getJSONArray("Grades");

                    Log.e("DYLAN", grades.toString());


                    HashMap<String, String> homeworkHeader = new HashMap<String, String>();
                    //omeworkHeader.put(TAG_ASSIGNMENTNAME, "");
                    homeworkHeader.put(TAG_ASSIGNMENTNAME, "Homework");
                    //homeworkHeader.put(TAG_ASSIGNMENTNAME, "");
                    gradeList.add(homeworkHeader);

                    for (int i = 0; i < grades.length(); i++) {
                        JSONObject c = grades.getJSONObject(i);

                        String type = c.getString(TAG_ASSIGNMENTTYPE);
                        if (type.contains("Homework")) {

                            String assName = c.getString(TAG_ASSIGNMENTNAME);
                            Integer gradeInt = c.getInt(TAG_ASSIGNMENTGRADE);
                            HashMap<String, String> grade = new HashMap<String, String>();
                            grade.put(TAG_ASSIGNMENTNAME, assName);
                            grade.put(TAG_ASSIGNMENTGRADE, Integer.toString(gradeInt));

                            gradeList.add(grade);
                        }
                    }

                    HashMap<String, String> quizHeader = new HashMap<String, String>();
                    //quizHeader.put(TAG_ASSIGNMENTNAME, "");
                    quizHeader.put(TAG_ASSIGNMENTNAME, "Quizes");
                    //quizHeader.put(TAG_ASSIGNMENTNAME, "");
                    gradeList.add(quizHeader);

                    for (int i = 0; i < grades.length(); i++) {
                        JSONObject c = grades.getJSONObject(i);

                        String type = c.getString(TAG_ASSIGNMENTTYPE);
                        if (type.contains("Quiz")) {
                            String assName = c.getString(TAG_ASSIGNMENTNAME);
                            Integer gradeInt = c.getInt(TAG_ASSIGNMENTGRADE);

                            HashMap<String, String> grade = new HashMap<String, String>();
                            grade.put(TAG_ASSIGNMENTNAME, assName);
                            grade.put(TAG_ASSIGNMENTGRADE, Integer.toString(gradeInt));

                            gradeList.add(grade);
                        }
                    }

                    HashMap<String, String> finalHeader = new HashMap<String, String>();
                    finalHeader.put(TAG_ASSIGNMENTNAME, "Final");
                    gradeList.add(finalHeader);

                    for (int i = 0; i < grades.length(); i++) {
                        JSONObject c = grades.getJSONObject(i);

                        String type = c.getString(TAG_ASSIGNMENTTYPE);
                        if (type.contains("Final")) {
                            String assName = c.getString(TAG_ASSIGNMENTNAME);
                            Integer gradeInt = c.getInt(TAG_ASSIGNMENTGRADE);

                            HashMap<String, String> grade = new HashMap<String, String>();
                            grade.put(TAG_ASSIGNMENTNAME, assName);
                            grade.put(TAG_ASSIGNMENTGRADE, Integer.toString(gradeInt));

                            gradeList.add(grade);
                        }
                    }

                    HashMap<String, String> midtermHeader = new HashMap<String, String>();
                    midtermHeader.put(TAG_ASSIGNMENTNAME, "Midterm");
                    gradeList.add(midtermHeader);

                    for (int i = 0; i < grades.length(); i++) {
                        JSONObject c = grades.getJSONObject(i);

                        String type = c.getString(TAG_ASSIGNMENTTYPE);
                        if (type.contains("Midterm")) {
                            String assName = c.getString(TAG_ASSIGNMENTNAME);
                            Integer gradeInt = c.getInt(TAG_ASSIGNMENTGRADE);

                            HashMap<String, String> grade = new HashMap<String, String>();
                            grade.put(TAG_ASSIGNMENTNAME, assName);
                            grade.put(TAG_ASSIGNMENTGRADE, Integer.toString(gradeInt));

                            gradeList.add(grade);
                        }
                    }

                    HashMap<String, String> projectHeader = new HashMap<String, String>();
                    projectHeader.put(TAG_ASSIGNMENTNAME, "Project");
                    gradeList.add(projectHeader);

                    for (int i = 0; i < grades.length(); i++) {
                        JSONObject c = grades.getJSONObject(i);

                        String type = c.getString(TAG_ASSIGNMENTTYPE);
                        if (type.contains("Project")) {
                            String assName = c.getString(TAG_ASSIGNMENTNAME);
                            Integer gradeInt = c.getInt(TAG_ASSIGNMENTGRADE);

                            HashMap<String, String> grade = new HashMap<String, String>();
                            grade.put(TAG_ASSIGNMENTNAME, assName);
                            grade.put(TAG_ASSIGNMENTGRADE, Integer.toString(gradeInt));

                            gradeList.add(grade);
                        }
                    }

                    HashMap<String, String> addGrade = new HashMap<String, String>();
                    addGrade.put(TAG_ASSIGNMENTNAME, "Add New Grade");
                    gradeList.add(addGrade);


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

            ListAdapter adapter = new SimpleAdapter(getBaseContext(), gradeList,
                    R.layout.grades_list_item_layout,
                    new String[]{TAG_ASSIGNMENTNAME, TAG_ASSIGNMENTGRADE},
                    new int[]{R.id.homework_list_item_name, R.id.homework_list_item_grade
                    });
            lView.setAdapter(adapter);
        }
    }
}


