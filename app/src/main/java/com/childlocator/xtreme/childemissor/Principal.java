package com.childlocator.xtreme.childemissor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Principal extends Activity {

    private LocationManager locationManager;
    private ProgressDialog dialog;
    TextView txt;

    private static List<Coordenada> lstCoord = new ArrayList<Coordenada>();

    private double locLat = 15;
    private double locLong = 15;

    private String imei;

    private final int lowspeed = 5000;
    private final int mediumspeed= 5000;
    private final int highspeed = 5000;

    private int newtiming = lowspeed;

    private int arranque=0;

    private NetworkInfo mWifi;

    private boolean timing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei = mngr.getDeviceId();

        txt = (TextView) findViewById(R.id.txtCoord);
        TextView txtGPS = (TextView) findViewById(R.id.txtGPS);
        TextView txtInternet = (TextView) findViewById(R.id.txtInternet);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            txtGPS.setText("GPS ativo");
        }else{
            txtGPS.setText("GPS inativo");
        }

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            txtInternet.setText("Está ligado à internet");
        }
        else
        {
            txtInternet.setText("Não está ligado à internet");
        }

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

        Intent intent;

        switch(item.getItemId()) {
            case R.id.action_registar:

                intent = new Intent(Principal.this, Registo.class);
                startActivity(intent);

            break;

            case R.id.action_fechar:

                finish();

                break;

            //case R.id.action_webservice:

            //Mais código

            //break;
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


                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        /*etLatGPS.append("" + location.getLatitude());
                        etLongiGPS.append("" + location.getLongitude());*/

                        Coordenada coord = new Coordenada(location.getLatitude(), location.getLongitude(), String.valueOf(System.currentTimeMillis() / 1000L));

                        lstCoord.add(coord);

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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            /*}
        });*/

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST("https://divv.no-ip.org:80/storeCoordinates", locLat, locLong, imei);

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Dados enviados!", Toast.LENGTH_LONG).show();

            /*if (result.contains("Dentro"))
            {

                if (newtiming == lowspeed || newtiming == highspeed)
                {
                    newtiming = mediumspeed;
                    timing = true;
                }

            }
            else
            {

                newtiming = highspeed;
                timing = true;

            }

            if (result.contains("Success"))
            {
                lstCoord.clear();
            }*/

        }
    }

    public static String POST(String url, double coordLat, double coordLong, String imei){
        InputStream inputStream = null;
        String result = "";
        int cont=0;

        try {

            // 1. create HttpClient
            HttpClient httpclient = getNewHttpClient();


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

            /*jsonObject.accumulate("lat", coordLat);
            jsonObject.accumulate("lng", coordLong);*/
            jsonObject.accumulate("imei", imei);


            JSONArray jsonArray = new JSONArray();
            JSONObject arrayCoord = new JSONObject();
            
            for (Coordenada coord: lstCoord)
            {
                JSONArray subjsonArray = new JSONArray();
                JSONObject subarrayCoord = new JSONObject();
                
                subarrayCoord.put("lat", coord.getLat());
                subarrayCoord.put("lng", coord.getLng());
                subarrayCoord.put("timestamp", coord.getTimestamp());

                subjsonArray.put(subarrayCoord);

                jsonArray.put( subjsonArray);

                cont++;
            }

            jsonObject.put("arrayCoord", jsonArray);

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

    public void SendImei (View v)
    {
        int num = 0;
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //txt.setText("Imei: " + mngr.getDeviceId());

        if (mngr.getDeviceId() == null)
        {
            WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            imei = info.getMacAddress();

            txt.setText("Mac Adress: " + imei);

        }
        else
        {

            txt.setText("Imei: " + mngr.getDeviceId());
            imei = mngr.getDeviceId();

        }

        temporizador(newtiming);

        }

    /*public void onBackPressed()
    {

    }*/

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private void temporizador (int internaltimming)
    {

        final Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {



                        if (timing)
                        {
                            mTimer.cancel();
                            //System.out.println("Acabou");
                            timing=false;
                            temporizador(newtiming);
                        }
                        else
                        {
                            buscarcoord();

                            /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date


                            Coordenada coord = new Coordenada(++locLat, ++locLong, String.valueOf(System.currentTimeMillis() / 1000L));

                            lstCoord.add(coord);*/

                            //txt.setText("Coordenada: " + location.getLatitude() + " , " + location.getLongitude());
                            new HttpAsyncTask().execute();

                            arranque++;
                            if (arranque == 1)
                            {

                                newtiming = mediumspeed;
                                timing = true;


                            }

                        }

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
        }, 1, internaltimming);

    }

    public void changeTiming(View v)
    {

        timing = true;
        newtiming = 5000;

    }

}
