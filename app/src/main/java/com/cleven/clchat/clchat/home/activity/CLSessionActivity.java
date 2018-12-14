package com.cleven.clchat.clchat.home.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cleven.clchat.clchat.R;
import com.cleven.clchat.clchat.utils.CLMQTTService.CLMQTTManager;

import java.util.HashMap;

public class CLSessionActivity extends AppCompatActivity {

    private EditText editText;
    private Button btn_connect;
    private Button disconnect;
    private Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        editText = (EditText) findViewById(R.id.et_text);

        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 连接
                CLMQTTManager.getInstance().connect();
            }
        });

        disconnect = (Button) findViewById(R.id.btn_disconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CLMQTTManager.getInstance().disconnect();
            }
        });

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,String> dict = new HashMap<>();
                dict.put("id","12312312");
                dict.put("msg",editText.getText().toString().trim());
                CLMQTTManager.getInstance().sendMessage("2",dict);
            }
        });

    }

}
