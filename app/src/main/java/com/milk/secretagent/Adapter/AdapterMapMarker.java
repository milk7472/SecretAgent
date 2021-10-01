package com.milk.secretagent.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.milk.secretagent.R;

/**
 * Created by Milk on 2015/8/7.
 */
public class AdapterMapMarker implements GoogleMap.InfoWindowAdapter {
    View m_View = null;
    LayoutInflater m_Inflater = null;

    // System will call getInfoWindow first, if null, call getInfoContents after.
    // getInfoWindow can customize all view
    // getInfoContents only customize the info window content

    public AdapterMapMarker(LayoutInflater inflater) {
        this.m_Inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        m_View = m_Inflater.inflate(R.layout.map_marker_layout, null);

        TextView title = (TextView) m_View.findViewById(R.id.marker_title);
        title.setText(marker.getTitle());

        String[] snippets = marker.getSnippet().split(",");
        TextView textViewTime = (TextView) m_View.findViewById(R.id.marker_time);
        TextView textViewAddress = (TextView) m_View.findViewById(R.id.marker_address);

        textViewAddress.setText(snippets[0]);
        textViewTime.setText(snippets[1]);

        return m_View;
    }
}
