package com.kristian.travelerblog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private static LayoutInflater inflater = null;
    private List<String> listOfpath;

    public GridViewAdapter(Activity activity, Context context, String[] filepath){
        super();
        //4. krok Galéria fotografií
        this.context=context;
        //vytvorenie inštancie inflatera
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listOfpath = new ArrayList<String>(Arrays.asList(filepath));
    }
    @Override
    public int getCount() {
        return listOfpath.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null){
            row = inflater.inflate(R.layout.grid_item_layout,parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Picasso.with(context).cancelRequest(holder.image);
        Picasso.with(context).load(new File(listOfpath.get(position))).fit().centerCrop()
                .into(holder.image);
        return row;
    }
    static class ViewHolder {
        ImageView image;
    }
}
