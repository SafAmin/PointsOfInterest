package com.example.safaa.pointsofinterest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PlacesAdapter extends BaseAdapter {
    private final String LOG_TAG = PlacesAdapter.class.getSimpleName();
    private Context context;
    private ArrayList pDataArrayList;
    private static LayoutInflater inflater = null;
    PlaceData placesData;

    public PlacesAdapter(Context c, ArrayList al) {
        this.context = c;
        this.pDataArrayList = al;
    }

    /**
     * @return the size of passed ArrayList
     */
    @Override
    public int getCount() {
        if (pDataArrayList.size() <= 0)
            return 0;
        return pDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        ImageView placeImageView;
        TextView placeNameView;
        TextView placeAddressView;
        CheckBox addToFavoritView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        /**
         * LayoutInflater to call external XML layout
         */
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.place_data,parent, false);//get items from movie_poster
            holder = new ViewHolder();
            holder.placeImageView = (ImageView) convertView.findViewById(R.id.placeImage);
            holder.placeNameView = (TextView) convertView.findViewById(R.id.placeName);
            holder.placeAddressView = (TextView) convertView.findViewById(R.id.placeAddress);
            holder.addToFavoritView = (CheckBox) convertView.findViewById(R.id.checkBox);


            convertView.setTag(holder);

            holder.addToFavoritView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.addToFavoritView.setChecked(true);

                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        placesData = (PlaceData) pDataArrayList.get(position);
        Picasso.with(context).load(placesData.getPlaceImage()).into(holder.placeImageView);
        holder.placeNameView.setText(placesData.getPlaceName());
        holder.placeAddressView.setText(placesData.getPlaceAddress());
        if( placesData.getMark() == 1){
            holder.addToFavoritView.setChecked(true);
        }
        return convertView;
    }

}
