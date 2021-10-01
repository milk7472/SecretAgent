package com.milk.secretagent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.milk.secretagent.Adapter.AdapterTabTasks;
import com.milk.secretagent.Model.BaseTask;
import com.milk.secretagent.Utility.TaskHelper;

import java.util.ArrayList;

public class TabTask extends Fragment {
    View viewRoot;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    public static AdapterTabTasks adapterTabTasks;
    public static ArrayList<BaseTask> m_TaskList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.tab_main, container, false);

        initComponents();


        return viewRoot;
    }

    private void initComponents() {
        linearLayoutManager = new LinearLayoutManager(this.getActivity());

        recyclerView = (RecyclerView) viewRoot.findViewById(R.id.recyclerview_tasks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(onScrollListener);

        TaskHelper.getInstance().getAvailableTaskList(m_TaskList);

        adapterTabTasks = new AdapterTabTasks(getActivity(), m_TaskList);
        recyclerView.setAdapter(adapterTabTasks);

        //Toast.makeText(this.getActivity(), String.valueOf(MainActivity.NumberOfTabs), Toast.LENGTH_SHORT).show();
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //super.onScrolled(recyclerView, dx, dy);

            int firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (firstPosition > 0) {
                //MainActivity.swipeRefreshLayout.setEnabled(false);
            }
            else {
                //MainActivity.swipeRefreshLayout.setEnabled(true);
            }
        }
    };
}
