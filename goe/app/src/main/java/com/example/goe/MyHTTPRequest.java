package com.example.goe;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyHTTPRequest {

    public SessionData doRequest(String ip) {
        SessionData s = new SessionData();
        String data = "";

        try {
            //connection via go-e IP address
            URL url = new URL("http://" + ip + "/api/status?filter=tpa,sse,eto,amp,wh,cdi,nrg");
            Log.i("goe", "Sending HTTP request ... ");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(1000);
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
                //activate dummy data
                //{"tpa":0,"sse":"101004","eto":333661,"amp":14,"wh":35546.84,"cdi":{"type":0,"value":44940},"nrg":[227,228,227,1,13.8,14.1,13.9,3200,3200,3200,0,9730,100,100,100,9]}
                //{"tpa":0,"sse":"101004","eto":334209,"amp":14,"wh":36096.4,"cdi":{"type":1,"value":13413609},"nrg":[231,230,231,1,0,0,0,0,0,0,0,0,0,0,0,0]}
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

            //fill session data object
            //---timestamp
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            s.setTimestamp(df.format(c));
            //---energy total [Wh]
            s.setEto(eto);
            //---requested current [A]
            s.setAmp(amp);
            //---energy since car connected [Wh]
            s.setWh(wh);
            //---charging duration info [ms]
            if(cdi_type == 1)
                s.setCdi(cdi_value);
            else
                s.setCdi(0);

            Log.i("goe", String.valueOf(eto));
        }
        catch(JSONException ex) {
            Log.e("goe", "Something went wrong parsing JSON!");
            Log.e("goe", ex.getMessage());
        }

        Log.i("goe", s.toString());
        return s;
    }
}
