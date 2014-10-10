package com.childlocator.xtreme.childemissor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class Principal extends Activity {

    private LocationManager locationManager;
    private ProgressDialog dialog;
    TextView txt;

    private double locLat = 40;
    private double locLong = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        txt = (TextView) findViewById(R.id.txtCoord);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
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

    public void buscarcoord()
    {

        /*btnGPS = (Button) findViewById(R.id.fut11_btn1GPS);
        etLatGPS = (EditText) findViewById(R.id.fut11_et1LatGPS);
        etLongiGPS = (EditText) findViewById(R.id.fut11_et1LongiGPS);*/

        txt = (TextView) findViewById(R.id.txtCoord);

        /*btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {*/
                /*etLatGPS.setText("");
                etLongiGPS.setText("");
                dialog = ProgressDialog.show(Principal.this, "", "Loading. Please wait...", true);
                btnGPS.setEnabled(false);*/

                txt.setText("");
                dialog = ProgressDialog.show(Principal.this, "", "Loading. Please wait...", true);

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        /*etLatGPS.append("" + location.getLatitude());
                        etLongiGPS.append("" + location.getLongitude());*/

                        locLat = location.getLatitude();
                        locLong = location.getLongitude();



                        txt.setText("Coordenada: " + location.getLatitude() + " , " + location.getLongitude());

                        Principal.this.locationManager.removeUpdates(this);
                        //btnGPS.setEnabled(true);
                        dialog.dismiss();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Toast.makeText(getApplicationContext(), "GPS Ativado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(getApplicationContext(), "GPS Desativado", Toast.LENGTH_SHORT).show();
                    }
                };
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
            /*}
        });*/

    }

    public void enviarcoord (View v)
    {

        //txt.setText("Teste");
        new HttpAsyncTask().execute();


    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST("http://divv.no-ip.org/storeCoordinates", locLat, locLong);

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Dados enviados!", Toast.LENGTH_LONG).show();
        }
    }

    public static String POST(String url, double coordLat, double coordLong){
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

            jsonObject.accumulate("CoordLat", coordLat);
            jsonObject.accumulate("CoordLong", coordLong);

            json = jsonObject.toString();

            System.out.println("Valores: " + coordLat + " , " + coordLong);

            try {
                System.out.println("Teste");
                File myFile = new File(Environment.getExternalStorageDirectory() + "/json.txt");
                System.out.println(Environment.getExternalStorageDirectory() + "/json.txt");
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

    public void sendImei (View v)
    {
        int num = 0;
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        txt.setText("Imei: " + mngr.getDeviceId());



        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    buscarcoord();

                    new HttpAsyncTask().execute();
                }
            });

            /*try {
                System.out.println("Teste");
                File myFile = new File(Environment.getExternalStorageDirectory() + "/json.txt");
                System.out.println(Environment.getExternalStorageDirectory() + "/json.txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);
                myOutWriter.append(String.valueOf(locLat++));
                myOutWriter.close();
                fOut.close();

            } catch (Exception e) {
                System.out.println("Teste2");
            }*/



        }
    }, 1, 60000);

    }

    public void teste()
    {

        txt.setText("Valor: " + locLat++);

    }

}
