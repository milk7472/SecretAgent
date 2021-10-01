package com.milk.secretagent;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;

import com.milk.secretagent.Model.RecordTask;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.MediaPlayerHelper;
import com.milk.secretagent.Utility.Utils;

import java.io.File;


public class PlayerActivity
        extends Activity
        implements MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    TextView m_FileName;
    TextView m_CurrentPlayTime;
    TextView m_FileLength;
    SeekBar m_SeekBar;
    ImageButton m_Previous;
    ImageButton m_Backward;
    ImageButton m_Play;
    ImageButton m_Forward;
    ImageButton m_Next;

    int m_Index;
    RecordTask m_recordTask;

    // Media Player
    File m_File;
    MediaPlayer m_MediaPlayer;
    int m_SeekForwardTime = 5000; // 5000 milliseconds
    int m_SeekBackwardTime = 5000; // 5000 milliseconds
    Handler m_PlayerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_player);

        setFinishOnTouchOutside(false);

        m_Index = getIntent().getIntExtra(AppConstants.Extra.PLAYER_RECORDING_INDEX, 0);
        m_recordTask = TabRecording.m_RecordTaskList.get(m_Index);

        m_PlayerHandler = new Handler();

        initComponents();
        setupControllers();

        Utils.m_ProgressDialog.dismiss();

        play();
    }

    private void initComponents() {
        m_FileName = (TextView) findViewById(R.id.player_file_name);
        m_CurrentPlayTime = (TextView) findViewById(R.id.player_current_play_time);
        m_FileLength = (TextView) findViewById(R.id.player_file_length);
        m_SeekBar = (SeekBar) findViewById(R.id.player_seekbar);
        m_Previous = (ImageButton) findViewById(R.id.btnPrevious);
        m_Backward = (ImageButton) findViewById(R.id.btnBackward);
        m_Play = (ImageButton) findViewById(R.id.btnPlay);
        m_Forward = (ImageButton) findViewById(R.id.btnForward);
        m_Next = (ImageButton) findViewById(R.id.btnNext);

        resetTimerText();
    }

    private void resetTimerText() {
        m_FileName.setText(m_recordTask.getFileName());
        m_CurrentPlayTime.setText(MediaPlayerHelper.milliSecondsToTimer(0));
        m_FileLength.setText(MediaPlayerHelper.milliSecondsToTimer(m_recordTask.getFileLength()));
    }

    private void setupControllers() {

        m_MediaPlayer = new MediaPlayer();
        m_MediaPlayer.setOnCompletionListener(this); // Important
        m_SeekBar.setOnSeekBarChangeListener(this); // Important

        m_Previous.setOnClickListener(this);
        m_Backward.setOnClickListener(this);
        m_Play.setOnClickListener(this);
        m_Forward.setOnClickListener(this);
        m_Next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrevious:
                previous();
                break;
            case R.id.btnBackward:
                backward();
                break;
            case R.id.btnPlay:
                handlePlayButton();
                break;
            case R.id.btnForward:
                forward();
                break;
            case R.id.btnNext:
                next();
                break;
        }
    }

    private void handlePlayButton() {
        if (m_MediaPlayer == null) {
            return ;
        }

        if (m_MediaPlayer.isPlaying()) {
            m_MediaPlayer.pause();
            m_Play.setImageResource(R.drawable.btn_play);
        }
        else {
            m_MediaPlayer.start();
            m_Play.setImageResource(R.drawable.btn_pause);
        }
    }

    private void previous() {
        if (m_Index > 0) {
            m_Index--;
        }
        else {
            m_Index = TabRecording.m_RecordTaskList.size() - 1;
        }

        play();
    }

    private void backward() {
        // get current song position
        int currentPosition = m_MediaPlayer.getCurrentPosition();

        // check if seekBackward time is greater than 0 sec
        if (currentPosition - m_SeekBackwardTime >= 0) {
            // forward song
            m_MediaPlayer.seekTo(currentPosition - m_SeekBackwardTime);
        }
        else {
            // backward to starting position
            m_MediaPlayer.seekTo(0);
        }
    }

    private void play() {
        m_recordTask = TabRecording.m_RecordTaskList.get(m_Index);
        m_File = new File(m_recordTask.getFilePath());
        m_FileName.setText(m_recordTask.getFileName());

        if (m_File == null || !m_File.exists()) {
            return ;
        }

        try {
            m_MediaPlayer.reset();
            m_MediaPlayer.setDataSource(m_File.getAbsolutePath());
            m_MediaPlayer.prepare();
            m_MediaPlayer.start();

            m_Play.setImageResource(R.drawable.btn_pause);

            m_SeekBar.setProgress(0);
            m_SeekBar.setMax(100);

            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        m_PlayerHandler.removeCallbacks(m_UpdateTimeTask);

        if (m_MediaPlayer != null) {
            m_MediaPlayer.release();
            m_MediaPlayer = null;
        }
    }

    private void forward() {
        // get current song position
        int currentPosition = m_MediaPlayer.getCurrentPosition();

        // check if seekForward time is lesser than song duration
        if(currentPosition + m_SeekForwardTime <= m_MediaPlayer.getDuration()){
            // forward song
            m_MediaPlayer.seekTo(currentPosition + m_SeekForwardTime);
        }
        else{
            // forward to end position
            m_MediaPlayer.seekTo(m_MediaPlayer.getDuration());
        }
    }

    private void next() {
        // Check if next recording is there or not
        if (m_Index < TabRecording.m_RecordTaskList.size() - 1) {
            m_Index++;
        }
        else {
            m_Index = 0;
        }

        play();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        m_MediaPlayer.seekTo(0);
        m_SeekBar.setProgress(0);
        m_CurrentPlayTime.setText(MediaPlayerHelper.milliSecondsToTimer(0));
        m_Play.setImageResource(R.drawable.btn_play);
    }

    public void updateProgressBar() {
        m_PlayerHandler.postDelayed(m_UpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable m_UpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = m_MediaPlayer.getDuration();
            long currentDuration = m_MediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            m_FileLength.setText("" + MediaPlayerHelper.milliSecondsToTimer(totalDuration));

            // Displaying time completed playing
            m_CurrentPlayTime.setText("" + MediaPlayerHelper.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = MediaPlayerHelper.getProgressPercentage(currentDuration, totalDuration);

            //Log.d("Progress", ""+progress);
            m_SeekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            m_PlayerHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        m_PlayerHandler.removeCallbacks(m_UpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        m_PlayerHandler.removeCallbacks(m_UpdateTimeTask);

        int totalDuration = m_MediaPlayer.getDuration();
        int currentPosition = MediaPlayerHelper.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        m_MediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stop();
    }
}
