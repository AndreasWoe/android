package at.htlwels.rssreader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//Text and scrolling views
//https://developer.android.com/codelabs/android-training-text-and-scrolling-views#3
//Async task and internet connection
//https://developer.android.com/codelabs/android-training-asynctask-asynctaskloader#5
//How to parse XML data
//https://developer.android.com/training/basics/network-ops/xml

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList> {
    private final String TAG = "RSSReader";

    @NonNull
    @Override
    public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args) {
        String url = "";

        if (args != null) {
            url = args.getString("url");
        }

        return new RSSReader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList> loader, ArrayList data) {
        //This is where you add the code to update your UI with the results
        StringBuffer sb = new StringBuffer();

        for(Object o : data) {
            Headline h = (Headline) o;
            sb.append(h);
            sb.append("\n\n");
        }

        TextView txt = findViewById(R.id.textView);
        txt.setText(sb.toString());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList> loader) {

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Thread t = new Thread(runnable);
        //t.start();

        Bundle queryBundle = new Bundle();
        queryBundle.putString("url", "https://rss.orf.at/news.xml");
        getSupportLoaderManager().restartLoader(0, queryBundle, this);
    }
}