package com.example.safaa.pointsofinterest;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class SharedPreference {

    public static final String PREFS_NAME = "Favorite";
    public static final String FAVORITES = "Place_Favorite";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, ArrayList<PlaceData> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, PlaceData mInfo) {
        ArrayList<PlaceData> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<PlaceData>();
        favorites.add(mInfo);
        saveFavorites(context, favorites);
    }
    public void removeFavorite(Context context, PlaceData mInfo) {
        ArrayList<PlaceData> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(mInfo);
            saveFavorites(context, favorites);
        }
    }
    public ArrayList<PlaceData> getFavorites(Context context) {
        SharedPreferences settings;
        ArrayList<PlaceData> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            PlaceData[] favoriteItems = gson.fromJson(jsonFavorites,PlaceData[].class);

            favorites = new ArrayList<PlaceData>(Arrays.asList(favoriteItems));
            favorites = new ArrayList<PlaceData>(favorites);
        } else
            return null;

        return (ArrayList<PlaceData>) favorites;
    }

}
