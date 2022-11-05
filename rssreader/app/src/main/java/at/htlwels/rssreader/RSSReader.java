package at.htlwels.rssreader;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RSSReader extends AsyncTaskLoader<ArrayList> {

    private final String TAG = "RSSReader";
    private String url;

    public RSSReader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Nullable
    @Override
    public ArrayList loadInBackground() {
        ArrayList data = new ArrayList();
        InputStream in = null;

        try {
            URL url = new URL(this.url);
            Log.i(TAG, "Sending HTTP request ... ");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(1000);
            in = new BufferedInputStream(urlConnection.getInputStream());
        }
        catch (Exception ex) {
            Log.i(TAG, ex.toString());
        }
        //How to parse XML data
        //https://developer.android.com/training/basics/network-ops/xml
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            // pass xml input
            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xpp.setInput(in, null);
            xpp.nextTag();

            int eventType = xpp.getEventType();
            int tagType = 0;
            Headline h = new Headline();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("title")) {
                    tagType = 1;
                }
                else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("dc:subject")) {
                    tagType = 2;
                }
                else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("dc:date")) {
                    tagType = 3;
                }
                else if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("orfon:usid")) {
                    tagType = 4;
                }
                else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equals("item")) {
                        data.add(h);
                        h = new Headline();
                    }
                    tagType = 0;
                }
                else if(eventType == XmlPullParser.TEXT && tagType == 1) {
                    h.setTitle(xpp.getText());
                }
                else if(eventType == XmlPullParser.TEXT && tagType == 2) {
                    h.setSubject(xpp.getText());
                }
                else if(eventType == XmlPullParser.TEXT && tagType == 3) {
                    h.setDate(xpp.getText());
                }
                //primary key usable for DB?
                else if(eventType == XmlPullParser.TEXT && tagType == 4) {
                    h.setId(xpp.getText().substring(5,12));
                }

                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

}
