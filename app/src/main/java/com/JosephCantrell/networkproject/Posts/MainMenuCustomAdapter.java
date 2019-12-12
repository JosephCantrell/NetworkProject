package com.JosephCantrell.networkproject.Posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.JosephCantrell.networkproject.R;

import java.util.ArrayList;
import java.util.List;

public class MainMenuCustomAdapter extends ArrayAdapter<MainDataSet> {

    private Context mContext;
    private List<MainDataSet> maincontent = new ArrayList<>();

    public MainMenuCustomAdapter(@NonNull Context context, @LayoutRes ArrayList<MainDataSet> list) {
        super(context, 0, list);
        mContext = context;
        maincontent = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.listview_item,parent,false);

        MainDataSet currentItem = maincontent.get(position);


        TextView PostId = (TextView)listItem.findViewById(R.id.TV_LV_ID);
        PostId.setText(currentItem.getPostID());

        TextView userId = (TextView)listItem.findViewById(R.id.TV_LV_Username);
        userId.setText(currentItem.getUsername());

        TextView title = (TextView)listItem.findViewById(R.id.TV_LV_Title);
        title.setText(currentItem.getTitle());

        return listItem;
    }

}
