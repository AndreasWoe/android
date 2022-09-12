package com.example.goe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // The method you want to call every now and then.
            MyHTTPRequest httprq = new MyHTTPRequest();
            SessionData sd = httprq.doRequest();

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.hello(sd);
                }
            });

            handler.postDelayed(this,10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("goe", "Main Activity Created");
        //https://developer.android.com/guide/background/threading
        handler.postDelayed(runnable, 1000); // Call the handler for the first time.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnExportClick(View view) {
        TextView txtConsole = findViewById(R.id.txtConsole);

        final String TXTNAME = "config.txt";

        File dataDir = Environment.getDataDirectory();
        File sd = getExternalFilesDir(null);

        String currenTXTPathIn = dataDir + "/data/" + this.getPackageName() + "/file/" + TXTNAME;
        String currentTXTPathEx = sd + "/" + TXTNAME;

        try {
            final File file = new File(currentTXTPathEx);
            file.createNewFile();
            //open file and enable appending
            FileOutputStream fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            //Timestamp
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = df.format(c);

            //write one line
            String q = "\""; //quotation mark
            String s = ";"; //separator

            osw.write(q + formattedDate + q + s);
            osw.write(System.lineSeparator());

            osw.flush();
            osw.close();
        }
        catch (Exception ex) {
            Log.e("ego", ex.getLocalizedMessage());
        }

        try {
            final File file = new File(currentTXTPathEx);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader r = new BufferedReader(isr);
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            isr.close();

            Log.i("ego", "Read from file: " + total.toString());
        }
        catch (Exception ex) {
            Log.e("ego", ex.getLocalizedMessage());
        }

    }

    public void hello(SessionData s) {
        Log.i("goe", "Hello");

        TextView time = findViewById(R.id.txtTimestamp);
        time.setText(String.valueOf(s.getTimestamp()));
        TextView eto = findViewById(R.id.txtEto);
        eto.setText(String.valueOf(s.getEto() / 1000.0) + " kWh");
        TextView amp = findViewById(R.id.txtAmp);
        amp.setText(String.valueOf(s.getAmp()) + " A");
        TextView wh = findViewById(R.id.txtWh);
        wh.setText(String.valueOf(s.getWh() / 1000.0) + " kWh");
        TextView cdi = findViewById(R.id.txtCdi);
        int seconds = (int)(s.getCdi() / 1000);
        int sc = seconds % 60;
        int m = (seconds / 60) % 60;
        int h = (seconds / (60 * 60)) % 24;

        cdi.setText(String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(sc));
    }

    public void btnOkClick(View view) {

    }

    public void btnCancelClick(View view) {

    }

}