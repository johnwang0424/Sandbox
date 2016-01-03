package com.johnwang.trident.trident;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.johnwang.trident.trident.Adapters.PropertyListingAdapter;
import com.johnwang.trident.trident.Common.CommonString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView propertyListing;
    private static String postcode = "ec1y0sj";
    private static String area = "Clerkenwell";
    private static String listingStatus = "rent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        propertyListing = (ListView) findViewById(R.id.property_listing);

        new WebAsyncTask().execute(CommonString.ZOOPLA_LIST_PROPERTY_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Web request handler.
     */
    private class WebAsyncTask extends AsyncTask<String, Void, List<Property>> {
        @Override
        protected List<Property> doInBackground(String... params) {
            InputStream is = null;
            try {
                URL url = new URL(String.format("%s?api_key=%s&postcode=%s&area=%s&listing_status=%s",
                        params[0], CommonString.ZOOPLA_API_KEY, postcode, area, listingStatus));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                if (conn.getResponseCode() != 200) {
                    Log.d(CommonString.DEBUG_TAG, "Calling zoopla api returns " + conn.getResponseCode());
                }
                is = conn.getInputStream();
                return parseJson(readIt(is));
            } catch (Exception e) {
                Log.d(CommonString.DEBUG_TAG, e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.d(CommonString.DEBUG_TAG, "Can't close input stream" + e.getMessage());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Property> properties) {
            PropertyListingAdapter adapter = new PropertyListingAdapter(MainActivity.this,
                    R.layout.property_listing_item, properties);
            propertyListing.setAdapter(adapter);
        }

        // Reads an InputStream and converts it to a String.
        private String readIt(InputStream stream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }

        private List<Property> parseJson(String input) {
            List<Property> properties = new ArrayList<Property>();
            if (input == null) {
                Log.d(CommonString.DEBUG_TAG, "input string is null");
                return properties;
            }
            try {
                JSONObject obj = new JSONObject(input);
                JSONArray listings = obj.getJSONArray(CommonString.ZOOPLA_JSON_LISTING);
                for (int a = 0; a < listings.length(); a++) {
                    Property p = new Property();
                    p.setStreetName(listings.getJSONObject(a).getString(CommonString.ZOOPLA_JSON_STREET_NAME));
                    p.setAgentPhone(listings.getJSONObject(a).getString(CommonString.ZOOPLA_JSON_AGENT_PHONE));
                    p.setPrice(listings.getJSONObject(a).getString(CommonString.ZOOPLA_JSON_PRICE));
                    URL url = new URL(listings.getJSONObject(a).getString(CommonString.ZOOPLA_JSON_THUMBNAIL_URL));
                    p.setThumbnail(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                    properties.add(p);
                }
            } catch (JSONException e) {
                Log.d(CommonString.DEBUG_TAG, "Can't parse input string", e);
            } catch (MalformedURLException e) {
                Log.d(CommonString.DEBUG_TAG, "Found malformed URL", e);
            } catch (IOException e) {
                Log.d(CommonString.DEBUG_TAG, "Found IO exception", e);
            }
            return properties;
        }
    }
}
