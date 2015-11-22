package com.example.jt.mygradeandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AddSchoolActivity extends AppCompatActivity {

    int studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);

        Intent intent = getIntent();
        studentID = intent.getIntExtra("StudentID", 0);
    }

    public void insertNewSchool(View view) {
        EditText name = (EditText) findViewById(R.id.txtName);
        EditText state = (EditText) findViewById(R.id.txtState);
        EditText city = (EditText) findViewById(R.id.txtCity);

        String json = "{ \"school\": " +
                "{ " +
                "\"StudentID\": \"" + studentID +
                "\", \"SchoolName\": \"" + name.getText() +
                "\", \"City\": \"" + city.getText() +
                "\", \"State\": \"" + state.getText() +
                "}}";
        (new InsertSchool()).execute(json);
    }


    private class InsertSchool extends AsyncTask<String, Void, Void> {

        public static final String SCHOOL_WEBSERVICE_URL = "http://ec2-52-25-2-234.us-west-2.compute.amazonaws.com/MyGradeService/api/School";

        @Override
        protected Void doInBackground(String... json) {
            try {
                URL url = new URL(SCHOOL_WEBSERVICE_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");



//                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
//                out.write(json[0]);
//                out.close();
//                conn.getInputStream();
//
//





                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(json[0]);

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();


                //conn.connect();
                Log.e("DYLAN", "The response is: " + responseCode);
            } catch (Exception e) {
                Log.e("DYLAN", e.getMessage());
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            ListAdapter adapter = new SimpleAdapter(getActivity(), schoolList,
//                    R.layout.school_list_item_layout,
//                    new String[]{TAG_SCHOOL_NAME, TAG_SCHOOL_STATE, TAG_SCHOOL_CITY},
//                    new int[]{R.id.school_list_item_name, R.id.state_list_item_name, R.id.city_list_item_name
//                    });
//            listView.setAdapter(adapter);
        }
    }

}
