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

//https://developer.android.com/codelabs/android-training-text-and-scrolling-views#3
//https://developer.android.com/codelabs/android-training-asynctask-asynctaskloader#5
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList> {
    private final String TAG = "RSSReader";

    @NonNull
    @Override
    public Loader<ArrayList> onCreateLoader(int id, @Nullable Bundle args) {
        return new RSSReader(this, "https://rss.orf.at/news.xml");
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
        queryBundle.putString("queryString", "Hello World");
        getSupportLoaderManager().restartLoader(0, queryBundle, this);
    }
}