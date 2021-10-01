package com.milk.secretagent.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.milk.secretagent.R;

/**
 * Created by Milk on 2015/7/10.
 */
public class AdapterLocationSettings extends BaseAdapter {
    LayoutInflater layoutInflater = null;
    String[] settingTitles;
    String[] settingHints;

    public AdapterLocationSettings(Context context, String[] settingTitles, String[] settingHits) {
        layoutInflater = LayoutInflater.from(context);
        this.settingTitles = settingTitles;
        this.settingHints = settingHits;
    }

    @Override
    public int getCount() {
        return settingTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return settingTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.listview_item_record_setting, null);

        ImageView imageViewIcon = (ImageView) convertView.findViewById(R.id.adapter_imageview_icon);
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.adapter_textview_title);
        TextView textViewSetting = (TextView) convertView.findViewById(R.id.adapter_textview_setting);

        int imageResource = 0;
        switch (position) {
            case 0:
                imageResource = R.drawable.settings_date;
                break;
            case 1:
                imageResource = R.drawable.settings_time;
                break;
            case 2:
                imageResource = R.drawable.settings_length;
                break;
            case 3:
                imageResource = R.drawable.settings_notification;
                break;
        }

        imageViewIcon.setImageResource(imageResource);
        //imageViewIcon.setBackgroundColor(Color.rgb(100, 100, 50));
        textViewTitle.setText(settingTitles[position]);
        textViewSetting.setText(settingHints[position]);

        return convertView;
    }
}
