package com.milk.secretagent.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.milk.secretagent.Model.RecordTask;
import com.milk.secretagent.PlayerActivity;
import com.milk.secretagent.R;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;
import com.milk.secretagent.Utility.VoiceRecorderHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Milk on 2015/7/14.
 */
public class AdapterTabRecordings extends RecyclerView.Adapter<AdapterTabRecordings.ViewHolder> {
    View m_View;
    Context m_Context;
    ArrayList<RecordTask> m_RecordTaskList;

    public AdapterTabRecordings(Context context, ArrayList<RecordTask> recordTaskList) {
        this.m_Context = context;
        this.m_RecordTaskList = recordTaskList;
    }

    @Override
    public AdapterTabRecordings.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        m_View = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.cardview_recording, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(m_View);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterTabRecordings.ViewHolder viewHolder, final int position) {
        final RecordTask recordTask = this.m_RecordTaskList.get(position);

        // Button Play
        viewHolder.m_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showProgressDialog(
                        m_Context,
                        m_Context.getString(R.string.progress_dialog_recording_title),
                        m_Context.getString(R.string.progress_dialog_recording_message));

                Intent intent = new Intent();
                intent.setClass(m_Context, PlayerActivity.class);
                intent.putExtra(AppConstants.Extra.PLAYER_RECORDING_INDEX, position);
                m_Context.startActivity(intent);
            }
        });

        // Task create time
        viewHolder.m_DateTime.setText(recordTask.getCreateTime());

        // File name
        String stringFileName = getFileNameWithSuffix(recordTask.getFileName(), recordTask.getFilePath());
        viewHolder.m_FileName.setText(String.format(
                m_Context.getResources().getString(R.string.recording_file_name), stringFileName));

        // Recording length
        viewHolder.m_FileLength.setText(String.format(
                m_Context.getResources().getString(R.string.recording_file_length),
                recordTask.getFormattedFileLength()));

        // File size
        viewHolder.m_FileSize.setText(String.format(
                m_Context.getResources().getString(R.string.recording_file_size),
                recordTask.getFileSize()));

        // Button Edit
        viewHolder.m_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFileName(position);
            }
        });

        // Button Share
        viewHolder.m_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRecording(position);
            }
        });

        // Button Delete
        viewHolder.m_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecordingFile(position);
            }
        });
    }

    private void editFileName(final int position) {
        final RecordTask recordTask = this.m_RecordTaskList.get(position);

        final EditText editText = new EditText(m_Context);
        editText.setText(recordTask.getFileName());

        AlertDialog.Builder dialogEdit = new AlertDialog.Builder(m_Context);
        dialogEdit.setTitle(m_Context.getResources().getString(R.string.recording_edit_file_name));
        dialogEdit.setView(editText);
        dialogEdit.setPositiveButton(m_Context.getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String stringFileName = "";
                        boolean isUseAAC = false;
                        if (recordTask.getFileName().contains(AppConstants.File.RECORDING_SUFFIX_AMR)) {
                            stringFileName = recordTask.getFileName().replace(AppConstants.File.RECORDING_SUFFIX_AMR, "");
                            isUseAAC = false;
                        }
                        else if (recordTask.getFileName().contains(AppConstants.File.RECORDING_SUFFIX_AAC)) {
                            stringFileName = recordTask.getFileName().replace(AppConstants.File.RECORDING_SUFFIX_AAC, "");
                            isUseAAC = true;
                        }

                        if ("".equals(editText.getText().toString().trim()) ||
                                editText.getText().toString().equals(stringFileName)) {
                            dialog.dismiss();
                        } else {
                            File directory = new File(VoiceRecorderHelper.getRecordingFolder(true));
                            File fileFrom = new File(directory, recordTask.getFileName());
                            String suffix;
                            if (isUseAAC) {
                                suffix = AppConstants.File.RECORDING_SUFFIX_AAC;
                            }
                            else {
                                suffix = AppConstants.File.RECORDING_SUFFIX_AMR;
                            }
                            File fileTo = new File(directory, editText.getText().toString() + suffix);
                            if (fileFrom.renameTo(fileTo)) {
                                recordTask.setFileName(fileTo.getName().replace(suffix, ""));

                                Toast.makeText(m_Context, m_Context.getResources().getString(R.string.recording_edit_success), Toast.LENGTH_SHORT).show();
                                notifyItemChanged(position);
                            }
                        }
                    }
                });

        dialogEdit.setNegativeButton(m_Context.getResources().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialogEdit.show();
    }

    private void shareRecording(final int position) {
        final RecordTask recordTask = this.m_RecordTaskList.get(position);
        Uri uriRecording = Uri.fromFile(new File(recordTask.getFilePath()));

        String subject = String.format(
                m_Context.getString(R.string.recording_share_file_subject),
                m_Context.getString(R.string.app_name),
                recordTask.getFileName()
        );

        String text = String.format(
                m_Context.getString(R.string.recording_share_file_text),
                recordTask.getStartTime(),
                recordTask.getFinishTime()
        );

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriRecording);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("audio/*");
        m_Context.startActivity(Intent.createChooser(shareIntent, m_Context.getResources().getString(R.string.recording_share_file_title)));
    }

    private void deleteRecordingFile(final int position) {
        final RecordTask recordTask = this.m_RecordTaskList.get(position);

        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(m_Context);
        dialogDelete.setTitle(m_Context.getResources().getString(R.string.recording_delete_file_title));
        dialogDelete.setMessage(m_Context.getResources().getString(R.string.recording_delete_file_message));
        dialogDelete.setPositiveButton(m_Context.getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(recordTask.getFilePath());
                        if (file.delete()) {
                            m_RecordTaskList.remove(position);
                            TaskHelper.getInstance().removeTaskById(m_Context, recordTask.getAlarmId());
                            Toast.makeText(m_Context, m_Context.getResources().getString(R.string.recording_delete_success), Toast.LENGTH_SHORT).show();
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

    private String getFileNameWithSuffix(String fileName, String filePath) {
        String stringFileName = "";
        if (filePath.contains(AppConstants.File.RECORDING_SUFFIX_AMR)) {
            stringFileName = fileName + AppConstants.File.RECORDING_SUFFIX_AMR;
        }
        else if (filePath.contains(AppConstants.File.RECORDING_SUFFIX_AAC)) {
            stringFileName = fileName + AppConstants.File.RECORDING_SUFFIX_AAC;
        }

        return stringFileName;
    }

    @Override
    public int getItemCount() {
        return this.m_RecordTaskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView m_DateTime;
        public TextView m_FileName;
        public TextView m_FileLength;
        public TextView m_FileSize;
        public ImageView m_Play;
        public ImageView m_Edit;
        public ImageView m_Share;
        public ImageView m_Delete;

        public ViewHolder(View view) {
            super(view);

            m_DateTime = (TextView) view.findViewById(R.id.recording_date_time);
            m_FileName = (TextView) view.findViewById(R.id.recording_file_name);
            m_FileLength = (TextView) view.findViewById(R.id.recording_file_length);
            m_FileSize = (TextView) view.findViewById(R.id.recording_file_size);

            m_Play = (ImageView) view.findViewById(R.id.recording_play);
            m_Edit = (ImageView) view.findViewById(R.id.recording_edit);
            m_Share = (ImageView) view.findViewById(R.id.recording_share);
            m_Delete = (ImageView) view.findViewById(R.id.recording_delete);
        }

    }
}
