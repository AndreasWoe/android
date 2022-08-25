package com.example.goe;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyHTTPRequest implements Runnable {
    TextView txtOut;
    Activity activity;
    String line2;
    boolean r = true;

    public void setTxtOut(TextView txtOut) {
        this.txtOut = txtOut;
    }

    public MyHTTPRequest(Activity activity)
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
                        line2 += line;
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView worksalso = activity.findViewById(R.id.txtConsole);
                            worksalso.setText(line2);
                            //txtOut.setText(line2);
                        }
                    });

                    Log.i("goe", "Done - closing reader!");
                    br.close();

                } catch (Exception ex) {
                    Log.e("goe", ex.getMessage());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception ex) {
                Log.e("goe", ex.getMessage());
            }
            r = false;
            //Thread.sleep(10000);
        }
    }
}
