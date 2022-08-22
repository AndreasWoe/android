package com.example.goe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnExportClick(View view) {
        final String TXTNAME = "config.txt";

        File dataDir = Environment.getDataDirectory();
        File sd = getExternalFilesDir(null);

        String currenTXTPathIn = dataDir + "/data/" + this.getPackageName() + "/file/" + TXTNAME;
        String currentTXTPathEx = sd + "/" + TXTNAME;

        try {
            final File file = new File(currentTXTPathEx);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write("Hello World");

            //Timestamp
            Date c = Calendar.getInstance().getTime();
            osw.write("Current time => " + c);
            osw.write("\n");

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = df.format(c);
            osw.write(formattedDate);

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

    public void btnOkClick(View view) {
        TextView txtConsole = findViewById(R.id.txtConsole);

        MyHTTPRequest http = new MyHTTPRequest(MainActivity.this);
        http.setTxtOut(txtConsole);
        ExecutorService mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(http);


        //txtConsole.setText("Hallooooo!");
    }
}