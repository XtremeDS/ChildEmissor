package com.childlocator.xtreme.childemissor;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Normalizer;


public class Registo extends Activity {

    private String username;
    private String password;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo);

        TextView txtImei = (TextView) findViewById(R.id.txtImei);

        int num = 0;
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        txtImei.setText("Imei: " + mngr.getDeviceId());
        imei = mngr.getDeviceId();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String POST(String url, String username, String password, String imei){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();



            /*if (vistoria.getTipoVistoria().contains("Futebol"))
            {
                jsonObject.accumulate("futsal_futebol", 1);
            }
            else
            {
                jsonObject.accumulate("futsal_futebol", 0);
            }*/

            jsonObject.accumulate("imei", imei);
            jsonObject.accumulate("username", username);
            jsonObject.accumulate("password", password);

            json = jsonObject.toString();

            //System.out.println("Valores: " + coordLat + " , " + coordLong);

            try {
                System.out.println("Teste");
                File myFile = new File(Environment.getExternalStorageDirectory() + "/jsonRegisto.txt");
                System.out.println(Environment.getExternalStorageDirectory() + "/jsonRegisto.txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);
                myOutWriter.append(json);
                myOutWriter.close();
                fOut.close();

            } catch (Exception e) {
                System.out.println("Teste2");
            }

            json = Normalizer.normalize(json, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8"));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            EditText etUsername = (EditText) findViewById(R.id.etUsername);
            EditText etPassword = (EditText) findViewById(R.id.etPassword);

            return POST("http://divv.no-ip.org/assignImei", etUsername.getText().toString(), etPassword.getText().toString(), imei);

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Dados enviados!", Toast.LENGTH_LONG).show();
        }
    }

    public void RegistarImei( View v)
    {

        new HttpAsyncTask().execute();
        finish();
    }

}
