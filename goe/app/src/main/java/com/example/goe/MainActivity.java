package com.example.goe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    private SessionData s = new SessionData();
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

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

    public void hello() {
        Log.i("goe", "Hello");

        //TextView c = findViewById(R.id.txtConsole);
        //c.setText(String.valueOf(s.getEto()));

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
        TextView txtConsole = findViewById(R.id.txtConsole);

        MyHTTPRequest http = new MyHTTPRequest(MainActivity.this);
        http.setTxtOut(txtConsole);
        http.setSessionData(s);
        ExecutorService mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(http);

        start();
    }

    public void btnCancelClick(View view) {
        cancel();
    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 60000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval, pendingIntent);
        Log.i("goe", "Alarm Set");
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Log.i("goe", "Alarm Canceled");
    }
}