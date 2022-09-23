package com.example.goe;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MainActivity extends AppCompatActivity {

    private Handler bgHandler;
    private Handler uiHandler;

    private static final int MENU_ITEM_ITEM1 = 1;

    private String goeIp = "10.128.250.181";

    protected static WakeLock wakeLock = null;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MyHTTPRequest httprq = new MyHTTPRequest();
            SessionData sd = httprq.doRequest(goeIp);
            //send received data to handler
            Message msg = uiHandler.obtainMessage(1, sd);
            msg.sendToTarget();
            //repeat
            bgHandler.postDelayed(this,10000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "Hello");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch(item.getItemId()) {
            case MENU_ITEM_ITEM1:
                Log.i("goe", "Menu clicked!");
                //close app
                finishAndRemoveTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("goe", "Main Activity Created");

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "goe:DoNotSleep");
        wakeLock.acquire();

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("go-eCharger");
        actionBar.setSubtitle("... charger log!");
        //actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize handler
        HandlerThread thread = new HandlerThread("bg-thread");
        thread.start();

        //https://gist.github.com/ErikHellman/148434264edb186d5498
        bgHandler  = new Handler(thread.getLooper());
        uiHandler  = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1) {
                    Log.i("goe", "One");
                    SessionData sd = (SessionData) msg.obj;
                    //export to txt file
                    export(sd);
                    //do GUI update
                    hello(sd);
                }
                else
                    Log.i("goe", "Other");
            }
        };
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        //release wake lock
        wakeLock.release();

        bgHandler.removeCallbacksAndMessages(null);
        uiHandler.removeCallbacksAndMessages(null);
        // Shut down the background thread
        bgHandler.getLooper().quit();
    }

    public void export(SessionData data) {
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

            osw.write(data.toString());
            osw.write(System.lineSeparator());

            osw.flush();
            osw.close();
        }
        catch (Exception ex) {
            //went wrong
        }

        /*
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
        */
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

    public void btnStartClick(View view) {
        EditText txtIp = findViewById(R.id.txtIp);
        this.goeIp = txtIp.getText().toString();

        //https://developer.android.com/guide/background/threading
        //handler.postDelayed(runnable, 1000); // Call the handler for the first time.
        bgHandler.post(runnable);
    }

    public void btnStopClick(View view) {
        bgHandler.removeCallbacks(runnable);
    }

}