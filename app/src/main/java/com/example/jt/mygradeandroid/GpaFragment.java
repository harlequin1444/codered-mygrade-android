package com.example.jt.mygradeandroid;

import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * Created by Miguel on 11/21/2015.
 */
public class GpaFragment extends Fragment {
    private static final String TAG_SEMESTER_YEAR = "SemesterYear";
    private static final String TAG_SEMESTER_TYPE = "SemesterType";
    private static final String TAG_SEMESTER_STR = "SemesterSTR";
    private static final String TAG_SCHOOL_ID = "SchoolID";
    private static final String TAG_SEMESTER_ID = "SemesterID";
    private static final String TAG_SEMESTERGPA = "SemesterGPA";


    ListView listView;
    ArrayList<HashMap<String, String>> semesterList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragmentgpa, container, false);
        listView = (ListView) rootview.findViewById(R.id.gpa_list);

        semesterList = new ArrayList<HashMap<String, String>>();

        GetContacts getter = new GetContacts();
        getter.execute();

        return rootview;
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

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
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray schools = jsonObj.getJSONArray("Semesters");

                    for (int i = 0; i < schools.length(); i++) {
                        JSONObject c = schools.getJSONObject(i);

                        String semesterYear = c.getString(TAG_SEMESTER_YEAR);
                        String semesterType = c.getString(TAG_SEMESTER_TYPE);
                        Integer schoolID = c.getInt(TAG_SCHOOL_ID);
                        Integer semesterID = c.getInt(TAG_SEMESTER_ID);
                        Double gpa = c.getDouble(TAG_SEMESTERGPA);

                        HashMap<String, String> semester = new HashMap<String, String>();

                        semester.put(TAG_SEMESTER_YEAR, semesterYear);
                        semester.put(TAG_SEMESTER_TYPE, semesterType);
                        semester.put(TAG_SEMESTER_STR, semesterType + " " + semesterYear);
                        semester.put(TAG_SEMESTER_ID, Integer.toString(schoolID));
                        semester.put(TAG_SEMESTERGPA, Double.toString(gpa));

                        semesterList.add(semester);
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
            ListAdapter adapter = new SimpleAdapter(getActivity(), semesterList,
                    R.layout.gpa_list_item_layout,
                    new String[]{TAG_SEMESTER_STR, TAG_SEMESTERGPA},
                    new int[]{R.id.gpa_list_item_name, R.id.gpa_list_item_gpa}
            );
            listView.setAdapter(adapter);
        }

        public class EntryItem{
            public Boolean section = false;
            public String assign;
            public Integer grade;
        }

        public class EntryAdapter extends ArrayAdapter {
            private Context context;
            private ArrayList items;
            private LayoutInflater vi;
            public EntryAdapter(Context context,ArrayList items) {
                super(context,0, items);
                this.context = context;
                this.items = items;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
//                final ClipData.Item i = items.get(position);
//                if (i != null) {
//                    if(i.section){
//                        SectionItem si = (SectionItem)i;
//                        final TextView sectionView =
//                                (TextView) v.findViewById(R.id.list_item_section_text);
//                        sectionView.setText(si.getTitle());
//                    } else {
//                        EntryItem ei = (EntryItem)i;
//                        final TextView title =
//                                (TextView)v.findViewById(R.id.list_item_entry_title);
//                        final TextView subtitle =
//                                (TextView)v.findViewById(R.id.list_item_entry_summary);
//                        if (title != null) title.setText(ei.title);
//                        if(subtitle != null) subtitle.setText(ei.subtitle);
//                    }
//                }
                return v;
            }
        }

    }
}
