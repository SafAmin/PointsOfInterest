package com.example.safaa.pointsofinterest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    int checkFavorite = 0;
    int changeView = 0;
    private ProgressDialog pdia;

    private PlacesAdapter placesAdapter;
    TextView hiTextView;
    ListView placesListView;
    Button enableLocation;
    PlaceData placeDataObj;
    ArrayList<PlaceData> placeDataArr = new ArrayList<PlaceData>();
    ArrayList<PlaceData> favoritesArr = new ArrayList<PlaceData>();
    SharedPreference sharedPreference;
    Activity activity;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    LocationManager lm;
    String LATITUDE_PARAM = "30.056243";
    String LONGITUDE_PARAM = "31.19084";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        sharedPreference = new SharedPreference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        hiTextView = (TextView) rootView.findViewById(R.id.hintTextView);
        placesListView = (ListView) rootView.findViewById(R.id.places_listView);
        placesListView.setAdapter(placesAdapter);

        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);


                if (placeDataArr.get(position).getMark() == 0) {
                    placeDataArr.get(position).setMark(1);
                    checkBox.setChecked(true);
                    sharedPreference.addFavorite(activity, placeDataArr.get(position));
                    Toast.makeText(activity, activity.getResources().getString(R.string.add_favr), Toast.LENGTH_SHORT).show();

                } else if (placeDataArr.get(position).getMark() == 1) {

                    placeDataArr.get(position).setMark(0);
                    checkBox.setChecked(false);
                    sharedPreference.removeFavorite(activity, placeDataArr.get(position));

                    Toast.makeText(activity, activity.getResources().getString(R.string.remove_favr), Toast.LENGTH_SHORT).show();
                }

            }
        });

        enableLocation = (Button) rootView.findViewById(R.id.locationButton);
        enableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiTextView.setVisibility(View.INVISIBLE);

                lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                } catch (Exception ex) {
                }

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                }

                if (!gps_enabled && !network_enabled) {
                    // Log.v("CheckGPS",">>" + gps_enabled);
                    // notify user
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
                    dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                            // will get gps here
                        }
                    });
                    dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Toast.makeText(activity, "Please enable GPS", Toast.LENGTH_LONG).show();

                        }
                    });
                    dialog.show();
                } else {
                    new FetchPlacesTask().execute();

                }

            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get favorite items from SharedPreferences.
        sharedPreference = new SharedPreference();
        favoritesArr = sharedPreference.getFavorites(activity);

        if (favoritesArr == null) {
            hiTextView.setVisibility(View.VISIBLE);
        } else {
            hiTextView.setVisibility(View.INVISIBLE);

            PlacesAdapter placesAdapter = new PlacesAdapter(getActivity(), favoritesArr);
            placesListView.setAdapter(placesAdapter);
        }
    }

    // AsyncTask method will parse String value(URL) and return Array of String
    public class FetchPlacesTask extends AsyncTask<Void, Void, ArrayList<PlaceData>> {
        private final String LOG_TAG = FetchPlacesTask.class.getSimpleName();

        private ArrayList<PlaceData> getPlacesDateFromJSON(String moviesJsonStr) throws JSONException {

            final String RESPONSE = "response";
            final String placeID = "id";
            final String placeName = "name";
            final String placeAddress = "location";

            JSONObject placesJSON = new JSONObject(moviesJsonStr);
            JSONObject venuesJSON = placesJSON.getJSONObject(RESPONSE);
            JSONArray venuesArray = venuesJSON.getJSONArray("venues");

            for (int i = 0; i < venuesArray.length(); i++) {

                JSONObject getPlacesData = venuesArray.getJSONObject(i);
                JSONObject getLocationData = getPlacesData.getJSONObject("location");
                JSONArray getAddressData = getLocationData.getJSONArray("formattedAddress");
                JSONArray getCategories = getPlacesData.getJSONArray("categories");
                JSONObject getIconObj = getCategories.getJSONObject(0);
                JSONObject getIcon = getIconObj.getJSONObject("icon");

                String ID = getPlacesData.getString(placeID);
                String NAME = getPlacesData.getString(placeName);
                String ADDRESS = getAddressData.optString(0);

                String PREFIX = getIcon.optString("prefix");
                String SUFFIX = getIcon.optString("suffix");
                String Icon = PREFIX + "bg_64" + SUFFIX;
                // Log.v("venuesArrayAddress",">>"+ ADDRESS);
                //Log.v("venuesICON",">>"+ Icon);
                placeDataArr.add(i, new PlaceData(ID, NAME, ADDRESS, Icon));
            }

//            for (int i = 0; i < placeDataArr.size(); i++) {
//                Log.v(LOG_TAG, "Places Data: " + placeDataArr.get(i).getPlaceName());
//            }

            return placeDataArr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity());
            pdia.setMessage("Getting Places...");
            pdia.show();
        }

        @Override
        protected ArrayList<PlaceData> doInBackground(Void... params) {
            /*if (params.length == 0) {
                return null;
            }*/

            /**
             * These two need to be declared outside the try/catch
             * so that they can be closed in the finally block.
             */
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            /* Will contain the raw JSON response as a string.*/
            String placesJsonStr = null;


            try {


                // the foursquare client_id and the client_secret
                final String CLIENT_ID = "F1HYCDMSBYO5HDSQJ5VVIIMOMDIWH0KYCL1UZSCJ52E0DNVD";
                final String CLIENT_SECRET = "I4UMXZMK10O50FNWWKM5FCSUVTB2UK43NAJ41IELJCHD3RUQ";

                // Construct the URL for the ThePlaceDatabase query
                final String PLACE_BASE_URL = "https://api.foursquare.com/v2/venues/search?client_id=" +
                        CLIENT_ID + "&client_secret=" +
                        CLIENT_SECRET +
                        "&v=20130815&ll=" + LATITUDE_PARAM + "," + LONGITUDE_PARAM;
                Uri builtUri = Uri.parse(PLACE_BASE_URL).buildUpon().build().buildUpon()
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "BuiltURL" + builtUri.toString());

                // Create the request to ThePlaceDatabase, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    placesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                placesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Places String: " + placesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the places data, there's no point in attemping
                // to parse it.
                placesJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {

                return getPlacesDateFromJSON(placesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PlaceData> result) {
            if (result != null) {
            }
            placesAdapter = new PlacesAdapter(getActivity(), placeDataArr);
            placesListView.setAdapter(placesAdapter);
            placesAdapter.notifyDataSetChanged();
            pdia.dismiss();
        }
    }
}
