package com.example.manav.swiggysample;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // json object response url
    Button fetch;
    TextView textView1,textview2,textView3;
    private String urlJsonObj = "https://api.myjson.com/bins/3b0u2";
    ProgressDialog pDialog;
    ArrayList<String> al;
    ArrayAdapter<String> adapter;
    RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetch = (Button) findViewById(R.id.fetch);
        textView1 = (TextView) findViewById(R.id.textView1);
        textview2 = (TextView) findViewById(R.id.TextView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
       /* ListView listView = (ListView) findViewById(R.id.mobile_list);
        arrayAdapter=new ArrayAdapter <String>(this,R.layout.activity_listview,al);
        listView.setAdapter(arrayAdapter)*/
        final ListView listView=(ListView)findViewById(R.id.mobile_list);
        al=new ArrayList<String>();
        adapter=new ArrayAdapter(this,R.layout.activity_listview,R.id.label,al);
        listView.setAdapter(adapter);
     //   radioGroup=(RadioGroup) findViewById(R.id.optionRadioGroup);
                pDialog.show();
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, urlJsonObj, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                           pDialog.hide();
                        try {
                           /* textView1.setText(response.getString("name"));
                            textview2.setText(response.getString("email"));
                            JSONObject jsonObject=response.getJSONObject("phone");
                            textView3.setText(jsonObject.getString("home"));*/
                            JSONObject jsonObject=response.getJSONObject("variants");
                            JSONArray jsonArray=jsonObject.getJSONArray("variant_groups");
                            ArrayList<String> newVariant=new ArrayList<String>();

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject one=jsonArray.getJSONObject(i);
                               // textView1.setText(one.getString("name"));
                                al.add(one.getString("name"));
                                JSONArray forvaiants=one.getJSONArray("variations");
                                RadioButton radioButton[]=new RadioButton[(forvaiants.length())];
                                ArrayList<String> myitem=new ArrayList<String>();
                                for(int j=0;j<forvaiants.length();j++)
                                {
                                     JSONObject insidevariant =forvaiants.getJSONObject(j);
                                    radioButton[j]=new RadioButton(com.example.manav.swiggysample.MainActivity.this);
                                    radioButton[j].setText(insidevariant.getString("name"));
                                    myitem.add(insidevariant.getString("name"));
                                   // radioGroup.addView(radioButton[j]);

                                    Log.d("FUCKEDUP",insidevariant.getString("name"));
                                    //   radioButton[j].setText(insidevariant.getString("name"));
                                   // radioGroup.addView(radioButton[j]);

                                }
                                // textview2.setText(one.getString("group_id"));
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        Log.d("MYMSG",response.toString());
                        adapter.notifyDataSetChanged();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                String item=(String)listView.getItemAtPosition(position);
                                if(item.equalsIgnoreCase("Crust")) {
                                    FragmentManager fragmentManager=getFragmentManager();
                                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                                    com.example.manav.swiggysample.MyListFragment myListFragment=new com.example.manav.swiggysample.MyListFragment();
                                    fragmentTransaction.add(R.id.listFragment,myListFragment,"hello");
                                   // myListFragmentfindViewById(R.id.radioButton);
                                    ArrayList<String> al=new ArrayList<String>();
                                    al.add("thick");
                                    al.add("cheeese burst");
                                    al.add("thin");
                                    fragmentTransaction.commit();

                                }

                                if(item.equalsIgnoreCase("size")) {
                                    Intent intent = new Intent(com.example.manav.swiggysample.MainActivity.this, com.example.manav.swiggysample.Picker.class);
                                    String message = "abc";
                                    intent.putExtra("RoFL", message);
                                    startActivity(intent);
                                }
                                if(item.equalsIgnoreCase("sauce")) {
                                    Intent intent = new Intent(com.example.manav.swiggysample.MainActivity.this, com.example.manav.swiggysample.Picker.class);
                                    String message = "abc";
                                    intent.putExtra("RoFL", message);
                                    startActivity(intent);
                                }
                            }

                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(com.example.manav.swiggysample.MainActivity.this,"Something Went Wrong", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });
                com.example.manav.swiggysample.AppController.getInstance(com.example.manav.swiggysample.MainActivity.this).addtoRequestQueue(jsonObjectRequest);


    }
}