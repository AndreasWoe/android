package at.htlwels.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //https://developer.android.com/training/basics/network-ops/xml
    private final String TAG = "RSSReader";

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String xmlFeed = "";

            InputStream in = null;

            try {
                URL url = new URL("https://rss.orf.at/news.xml");
                Log.i(TAG, "Sending HTTP request ... ");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);

                in = new BufferedInputStream(urlConnection.getInputStream());
                /*
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line;
                for (line = br.readLine(); line != null; line = br.readLine()) {
                    xmlFeed += line;
                }
                br.close();
                */
            }
            catch (Exception ex) {
                Log.i(TAG, ex.toString());
            }
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // pass xml input
                xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xpp.setInput(in, null);
                //xpp.setInput( new StringReader( xmlFeed ) );
                xpp.nextTag();

                int eventType = xpp.getEventType();
                int tagType = 0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    /*
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        Log.d(TAG,"Start document");
                    } else if(eventType == XmlPullParser.START_TAG) {
                        Log.d(TAG,"Start tag "+ xpp.getName());
                    } else if(eventType == XmlPullParser.END_TAG) {
                        Log.d(TAG,"End tag "+xpp.getName());
                    } else if(eventType == XmlPullParser.TEXT) {
                        Log.d(TAG,"Text "+xpp.getText()); // here you get the text from xml
                    }*/

                    if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("title")) {
                        tagType = 1;
                    }
                    else if(eventType == XmlPullParser.END_TAG) {
                        tagType = 0;
                    }
                    else if(eventType == XmlPullParser.TEXT && tagType == 1) {
                        Log.d(TAG,xpp.getText());
                    }

                    eventType = xpp.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread t = new Thread(runnable);
        t.start();
    }


    public void parseXml(){
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput( new StringReader( "<foo>Hello World!</foo>" ) ); // pass input whatever xml you have
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d(TAG,"Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    Log.d(TAG,"Start tag "+xpp.getName());
                } else if(eventType == XmlPullParser.END_TAG) {
                    Log.d(TAG,"End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d(TAG,"Text "+xpp.getText()); // here you get the text from xml
                }
                eventType = xpp.next();
            }
            Log.d(TAG,"End document");

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}