package com.fcugis.testbed11.ucr.wfs_tdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends ActionBarActivity {

    private Button send = null,
                    map = null;
    private EditText input = null,
                     typeName = null;
    private TextView output = null;
    private Spinner functionSpinner = null;
    private String url = "http://192.168.6.104:10000/geoserver/wfs?service=wfs&request=";
    private String[] func = {"GetCapabilities", "GetFeature"};
    private LinearLayout ll = null;
    private int funcNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        typeName = (EditText)findViewById(R.id.et_typeName);
        input = (EditText)findViewById(R.id.et_inputText);
        output = (TextView)findViewById(R.id.txv_outputText);
        send = (Button)findViewById(R.id.btn_send);
        functionSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, func);
        functionSpinner.setAdapter(ad);
        functionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                funcNumber = i;

                if (funcNumber == 1)
                    setAdditionalFieldVisibility(true);
                else
                    setAdditionalFieldVisibility(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                funcNumber = -1;
            }
        });
        ll = (LinearLayout)findViewById(R.id.ll_additional);
        map = (Button)findViewById(R.id.btn_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Map.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<GML_MEMBER> parseGML(String gml) {
        ArrayList<GML_MEMBER> retList = new ArrayList<GML_MEMBER>();

        SAXParserFactory sf = SAXParserFactory.newInstance();
        SAXParser sp;
        try {
            sp = sf.newSAXParser();
            DefaultHandler dh = new DefaultHandler();
            sp.parse(gml, dh);
        } catch (Exception e) {
            Log.e("SAX", e.toString());
        }


        return retList;
    }

    private void openDialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s);
        builder.setTitle("Target URL");

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                new task().execute(s);
            }
        });

        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void setAdditionalFieldVisibility(boolean visi) {
        if (visi)
            ll.setVisibility(View.VISIBLE);
        else
            ll.setVisibility(View.GONE);
    }

    private class task extends AsyncTask<String, String, String> {

        private ProgressDialog pd;

        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("Download Data");
            pd.setMessage("Wait...");
            pd.show();
        }

        protected String doInBackground(String... url) {
            return accessServer(url[0]);
        }

        protected void onPostExecute(String result) {
            setOutputText(result);

            if (pd.isShowing())
                pd.dismiss();
        }
    }

    private void setOutputText(String s) {
        output.setText(s);
    }

    private String accessServer(String urlString) {
        //Toast.makeText(this, urlString, Toast.LENGTH_SHORT).show();

        String ret = "";

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(urlString);
        HttpResponse res;
        HttpEntity entity;

        try {
            res = client.execute(get);
            entity = res.getEntity();
            ret = EntityUtils.toString(entity);
        } catch (Exception e) {
            Log.e("accessServer", e.toString());
        }

        return ret;
    }
}
