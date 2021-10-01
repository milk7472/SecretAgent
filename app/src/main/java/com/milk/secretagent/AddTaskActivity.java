package com.milk.secretagent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.milk.secretagent.Utility.AppConstants;

public class AddTaskActivity extends Activity {

    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_task);

        this.setFinishOnTouchOutside(false);
    }

    public void addTask(View view) {

        switch (view.getId()) {
            case R.id.layout_add_task_record:
                intent = new Intent(this, RecordingActivity.class);
                break;
            case R.id.layout_add_task_location:
                intent = new Intent(this, LocationActivity.class);
                break;
        }

        if (intent != null) {
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 0) {
            return;
        }

        this.setResult(resultCode);
        finish();
    }
}
