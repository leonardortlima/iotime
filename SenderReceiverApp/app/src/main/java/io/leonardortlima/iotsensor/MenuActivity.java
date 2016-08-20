package io.leonardortlima.iotsensor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button send = (Button) findViewById(R.id.button);
        assert send != null;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSend();
            }
        });

        Button receive = (Button) findViewById(R.id.button2);
        assert receive != null;
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReceive();
            }
        });
    }

    private void goToSend() {
        Intent intent = new Intent(this, SendActivity.class);
        startActivity(intent);
    }

    private void goToReceive() {
        Intent intent = new Intent(this, ReceiveActivity.class);
        startActivity(intent);
    }
}
