package com.example.jt.mygradeandroid;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;
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

    ListView listView;
    String[] listViewContent;

    // URL to get contacts JSON
    public static final String schoolUrl = "http://localhost:8080/wenservice/services/Connectionserver?wsdl";
    public static final String NAMESPACE = "http://pkg";
    public static final String SOAP_ACTION_PREFIX = "/";
    private static final String METHOD = "get";

    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_SCHOOL_NAME = "SchoolName";

    // contacts JSONArray
    JSONArray contacts = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragmentschools, container, false);
        listView = (ListView) rootview.findViewById(R.id.schools_list);

        contactList = new ArrayList<HashMap<String, String>>();

//        // Listview on item click listener
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // getting values from selected ListItem
//                String name = ((TextView) view.findViewById(R.id.name))
//                        .getText().toString();
//                // Starting single contact activity
//                Intent in = new Intent(getApplicationContext(),
//                        SingleClassActivity.class);
//                in.putExtra(TAG_NAME, name);
//                startActivity(in);
//
//            }
//        });

        // Calling async task to get json
        GetContacts getter = new GetContacts();
        getter.execute();

        return rootview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listViewContent = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, listViewContent);

        //listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int duration = Toast.LENGTH_SHORT;

                String toastStr = listViewContent[position];

                ;
                Toast toast = Toast.makeText(getActivity(), toastStr, duration);
                toast.show();
                //launch new activity from fragment
                if (position == parent.getCount()){
                    Intent addSchool = new Intent(getActivity(), AddSchoolActivity.class);
                    startActivity(addSchool);

                }
            }

        });
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            //ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            //String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            String jsonStr = "";
            try {

                URL url = new URL(schoolUrl);

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
            }
            catch (Exception e)
            {
                //return e.getMessage();
            }



            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    contacts = jsonObj.getJSONArray(TAG_CONTACTS);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_SCHOOL_NAME);
                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_SCHOOL_NAME, name);
                        // adding contact to contact list
                        contactList.add(contact);
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
            ListAdapter adapter = new SimpleAdapter(getContext(), contactList,
                    R.layout.school_list_item_layout,
                    new String[]{TAG_SCHOOL_NAME},
                    new int[]{R.id.school_list_item_name});

            listView.setAdapter(adapter);
        }
    }

}
