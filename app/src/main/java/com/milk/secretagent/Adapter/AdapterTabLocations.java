package com.milk.secretagent.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.secretagent.MapsActivity;
import com.milk.secretagent.Model.LocationTask;
import com.milk.secretagent.R;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.ConnectionDetector;
import com.milk.secretagent.Utility.LocationHelper;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Milk on 2015/7/29.
 */
public class AdapterTabLocations extends RecyclerView.Adapter<AdapterTabLocations.ViewHolder> {
    Context m_Context;
    ArrayList<LocationTask> m_LocationTaskList;

    public AdapterTabLocations(Context context, ArrayList<LocationTask> locationTaskList) {
        this.m_Context = context;
        this.m_LocationTaskList = locationTaskList;
    }

    @Override
    public AdapterTabLocations.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.cardview_location, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterTabLocations.ViewHolder viewHolder, final int position) {
        final LocationTask locationTask = this.m_LocationTaskList.get(position);

        // Go to map
        viewHolder.m_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showProgressDialog(
                        m_Context,
                        m_Context.getString(R.string.progress_dialog_location_title),
                        m_Context.getString(R.string.progress_dialog_location_message));

                Intent intent = new Intent();
                intent.setClass(m_Context, MapsActivity.class);
                intent.putExtra(AppConstants.Extra.MAP_LOCATION_INDEX, position);
                m_Context.startActivity(intent);
            }
        });

        // Create Time
        viewHolder.m_CreateTime.setText(locationTask.getCreateTime());

        ArrayList<Location> locationList = LocationHelper.getInstance().getLocationListByIndex(position);

        // Start
        String startAddress = locationTask.getStartAddress();

        boolean isNeedUpdateTaskList = false;
        if (startAddress == null || startAddress.isEmpty()) {
            Location startLocation = locationList.get(0);

            if (ConnectionDetector.getInstance().isConnectingToInternet()) {
                startAddress = Utils.getCompleteAddress(m_Context, startLocation.getLatitude(), startLocation.getLongitude());
                if (startAddress != null && !startAddress.isEmpty()) {
                    locationTask.setStartAddress(startAddress);
                    isNeedUpdateTaskList = true;
                }
            }

            if (startAddress == null || startAddress.isEmpty()) {
                startAddress = String.format("%f, %f", startLocation.getLatitude(), startLocation.getLongitude());
            }
        }

        viewHolder.m_Start.setText(String.format(m_Context.getResources().getString(R.string.location_start), startAddress));

        // End
        String endAddress = locationTask.getEndAddress();

        if (endAddress == null || endAddress.isEmpty()) {
            Location endLocation = locationList.get(locationList.size() - 1);

            if (ConnectionDetector.getInstance().isConnectingToInternet()) {
                endAddress = Utils.getCompleteAddress(m_Context, endLocation.getLatitude(), endLocation.getLongitude());
                if (endAddress != null && !endAddress.isEmpty()) {
                    locationTask.setEndAddress(endAddress);
                    isNeedUpdateTaskList = true;
                }
            }

            if (startAddress == null || startAddress.isEmpty()) {
                endAddress = String.format("%f, %f", endLocation.getLatitude(), endLocation.getLongitude());
            }
        }

        viewHolder.m_End.setText(String.format(m_Context.getResources().getString(R.string.location_end), endAddress));

        if (isNeedUpdateTaskList) {
            Utils.saveTasks(m_Context);
        }

        // Duration
        viewHolder.m_Duration.setText(String.format(
                m_Context.getResources().getString(R.string.location_duration),
                Utils.formatMinutes(m_Context, locationTask.getRecordLength())));

        // Button Share
        viewHolder.m_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLocation(position);
            }
        });

        // Delete
        viewHolder.m_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocationFile(position);
            }
        });
    }

    private void shareLocation(int position) {
        final LocationTask locationTask = this.m_LocationTaskList.get(position);
        Uri uriLocation = Uri.fromFile(new File(locationTask.getFilePath()));

        String subject = String.format(
                m_Context.getString(R.string.location_share_file_subject),
                m_Context.getString(R.string.app_name),
                locationTask.getFileName()
        );

        String text = String.format(
                m_Context.getString(R.string.location_share_file_text),
                locationTask.getStartTime(),
                locationTask.getFinishTime()
        );

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriLocation);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        m_Context.startActivity(Intent.createChooser(shareIntent, m_Context.getResources().getString(R.string.location_share_file_title)));
    }

    private void deleteLocationFile(final int position) {
        final LocationTask locationTask = this.m_LocationTaskList.get(position);

        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(m_Context);
        dialogDelete.setTitle(m_Context.getResources().getString(R.string.location_delete_file_title));
        dialogDelete.setMessage(m_Context.getResources().getString(R.string.location_delete_file_message));
        dialogDelete.setPositiveButton(m_Context.getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(locationTask.getFilePath());
                        if (file.delete()) {
                            m_LocationTaskList.remove(position);
                            TaskHelper.getInstance().removeTaskById(m_Context, locationTask.getAlarmId());
                            Toast.makeText(m_Context, m_Context.getResources().getString(R.string.location_delete_success), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }
                });

        dialogDelete.setNegativeButton(m_Context.getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialogDelete.show();
    }

    @Override
    public int getItemCount() {
        return this.m_LocationTaskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView m_CreateTime;
        public TextView m_Start;
        public TextView m_End;
        public TextView m_Duration;
        public ImageView m_Map;
        public ImageView m_Share;
        public ImageView m_Delete;

        public ViewHolder(View view) {
            super(view);

            m_CreateTime = (TextView) view.findViewById(R.id.location_create_time);
            m_Start = (TextView) view.findViewById(R.id.location_start);
            m_End = (TextView) view.findViewById(R.id.location_end);
            m_Duration = (TextView) view.findViewById(R.id.location_duration);

            m_Map = (ImageView) view.findViewById(R.id.location_map);

            m_Share = (ImageView) view.findViewById(R.id.location_share);
            m_Delete = (ImageView) view.findViewById(R.id.location_delete);
        }
    }
}
