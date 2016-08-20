package io.leonardortlima.iotsensor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

import io.leonardortlima.iotsensor.model.SendData;

public class ReceiveActivity extends AppCompatActivity implements IMqttActionListener,
                                                                  MqttCallback {

    private static final String LOGTAG = ReceiveActivity.class.getSimpleName();

    private final String username = UUID.randomUUID().toString();

    private MqttAndroidClient client;
    private MemoryPersistence memPer;

    private TextView connectionStatus;
    private TextView value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectionStatus = (TextView) findViewById(R.id.connection_status);
        value = (TextView) findViewById(R.id.value);
    }

    @Override
    protected void onStart() {
        super.onStart();

        memPer = new MemoryPersistence();
        client = new MqttAndroidClient(this,
                "tcp://test.mosquitto.org:1883", username, memPer);
        try {
            client.connect(null, this);
            Log.i(LOGTAG, "Connected");
        } catch (MqttException e) {
            Log.i(LOGTAG, "Connect failed: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        client.unregisterResources();
        client.close();
    }

    @Override
    public void onSuccess(IMqttToken mqttToken) {
        Log.i(LOGTAG, "Client connected");
        connectionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark,
                null));
        connectionStatus.setText("Connected");
        subscribe();
    }

    @Override
    public void onFailure(IMqttToken arg0, Throwable arg1) {
        connectionStatus.setText("Connection failed");
        Log.i(LOGTAG, "Client connection failed: " + arg1.getMessage());
    }

    private void subscribe() {
        try {
            client.subscribe("iot-univem/iotime/ultrasom", 0);
        } catch (MqttException e) {
            Log.i(LOGTAG, "Connect failed: " + e.getMessage());
        }
        client.setCallback(this);
    }


    @Override
    public void connectionLost(Throwable cause) {
        Log.e(LOGTAG, "Connection lost ", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String rawJson = new String(message.getPayload());
        SendData sendData = new Gson().fromJson(rawJson, SendData.class);
        value.setText(sendData.getValue());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
