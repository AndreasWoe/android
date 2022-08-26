package com.example.goe;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyHTTPRequest implements Runnable {
    TextView txtOut;
    MainActivity activity;
    String data;
    boolean r = true;

    public Session getSession() {
        return s;
    }

    public void setSession(Session s) {
        this.s = s;
    }

    private Session s;

    public void setTxtOut(TextView txtOut) {
        this.txtOut = txtOut;
    }

    public MyHTTPRequest(MainActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void run() {
        while(r) {
            try {
                //URL url = new URL("http://www.snapmod.com/hello.html");
                //URL url = new URL("http://10.128.250.181/api/status?filter=tpa,sse,eto,amp,wh,cdi,nrg");
                URL url = new URL("http://192.168.0.22/api/status?filter=tpa,sse,eto,amp,wh,cdi,nrg");
                Log.i("goe", "Sending HTTP request ... ");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String line;

                    for (line = br.readLine(); line != null; line = br.readLine()) {
                        Log.i("goe", line);
                        data += line;
                    }

                    Log.i("goe", "Done - closing reader!");
                    br.close();
                } catch (Exception ex) {
                    Log.e("goe", "Something went wrong receiving http data!");
                    Log.e("goe", ex.getMessage());

                    //activate dummy data
                    data = "{\"tpa\":0,\"sse\":\"101004\",\"eto\":334209,\"amp\":14,\"wh\":36096.4,\"cdi\":{\"type\":1,\"value\":13413609},\"nrg\":[231,230,231,1,0,0,0,0,0,0,0,0,0,0,0,0]}";
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception ex) {
                Log.e("goe", ex.getMessage());
            }

            try {
                //parse received charger data
                JSONObject jObject = new JSONObject(data);
                int eto = jObject.getInt("eto");

                int amp = jObject.getInt("amp");
                double wh = jObject.getDouble("wh");

                JSONObject cdi = jObject.getJSONObject("cdi");

                int cdi_type = cdi.getInt("type");
                double cdi_value = cdi.getDouble("value");

                JSONArray nrg = jObject.getJSONArray("nrg");

                int nrg_0 = nrg.getInt(0);

                s.setEto(eto);

                Log.i("goe", String.valueOf(eto));
            }
            catch(JSONException ex) {
                Log.e("goe", "Something went wrong parsing JSON!");
                Log.e("goe", ex.getMessage());
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*
                    TextView worksalso = activity.findViewById(R.id.txtConsole);
                    worksalso.setText(data);
                    //txtOut.setText(line2);
                    */
                    activity.hello();
                }
            });

            r = false;
            //Thread.sleep(10000);
        }
    }
}
