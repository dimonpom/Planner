package com.dimonpom.planner;


import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> titleA;
    private final ArrayList<Bitmap> imageA;
    private final ArrayList<String> dateA;
    public CustomList(Activity context,
                      ArrayList titleA, ArrayList imageId, ArrayList dateA) {
        super(context, R.layout.list_todo, titleA);
        this.context = context;
        this.titleA = titleA;
        this.imageA = imageId;
        this.dateA = dateA;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_todo, null, true);
        TextView txtTitle = rowView.findViewById(R.id.task_title);
        TextView txtDate = rowView.findViewById(R.id.task_date);
        ImageView imageView = rowView.findViewById(R.id.imageView3);

        txtTitle.setText(titleA.get(position));
        txtDate.setText(dateA.get(position));
        if (imageA!=null)
            imageView.setImageBitmap(imageA.get(position));
        else
            Log.d("Custom List", "imageA == null");
        return rowView;
    }
}
