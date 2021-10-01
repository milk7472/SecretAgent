package com.milk.secretagent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class AboutActivity extends Activity {

    TextView m_TextSendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);

        this.setFinishOnTouchOutside(false);

        m_TextSendMail = (TextView) findViewById(R.id.about_email);
        m_TextSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "zuzu7472@gmail.com";
                String subject = String.format("[%s] ", getString(R.string.app_name));

                Intent sendTo = new Intent(Intent.ACTION_SENDTO);
                sendTo.setData(Uri.parse("mailto:"));
                sendTo.putExtra(Intent.EXTRA_EMAIL, email);
                sendTo.putExtra(Intent.EXTRA_SUBJECT, subject);
                if (sendTo.resolveActivity(getPackageManager()) != null) {
                    startActivity(sendTo);
                }
            }
        });

    }
}
