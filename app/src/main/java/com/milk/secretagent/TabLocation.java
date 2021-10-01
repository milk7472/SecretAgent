package com.milk.secretagent;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.milk.secretagent.Adapter.AdapterTabLocations;
import com.milk.secretagent.Model.LocationTask;
import com.milk.secretagent.Utility.LocationHelper;
import com.milk.secretagent.Utility.TaskHelper;

import java.util.ArrayList;

public class TabLocation extends Fragment {

    View m_ViewRoot;
    RecyclerView m_RecyclerView;
    LinearLayoutManager m_LinearLayoutManager;
    public static AdapterTabLocations m_AdapterTabLocations;
    public static ArrayList<LocationTask> m_LocationTaskList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_ViewRoot = inflater.inflate(R.layout.tab_location,container,false);

        initComponents();

        return m_ViewRoot;
    }

    private void initComponents() {
        m_LinearLayoutManager = new LinearLayoutManager(this.getActivity());

        m_RecyclerView = (RecyclerView) m_ViewRoot.findViewById(R.id.recyclerview_locations);
        m_RecyclerView.setHasFixedSize(true);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RecyclerView.addOnScrollListener(onScrollListener);

        TaskHelper.getInstance().getLocationList(m_LocationTaskList);

        m_AdapterTabLocations = new AdapterTabLocations(getActivity(), m_LocationTaskList);
        m_RecyclerView.setAdapter(m_AdapterTabLocations);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //super.onScrolled(recyclerView, dx, dy);

            int firstPosition = m_LinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (firstPosition > 0) {
                //MainActivity.swipeRefreshLayout.setEnabled(false);
            }
            else {
                //MainActivity.swipeRefreshLayout.setEnabled(true);
            }
        }
    };
}
