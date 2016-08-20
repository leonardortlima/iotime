package io.leonardortlima.iotsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

import io.leonardortlima.iotsensor.model.SendData;
import io.leonardortlima.iotsensor.util.DateHelper;

public class SendActivity extends AppCompatActivity implements IMqttActionListener,
                                                               SensorEventListener {

    private static final String LOGTAG = SendActivity.class.getSimpleName();

    private MqttAndroidClient client;
    private MemoryPersistence memPer;
    private final String username = UUID.randomUUID().toString();

    private SensorManager manager;

    private TextView connectionStatus;
    private TextView positionX;
    private TextView positionY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectionStatus = (TextView) findViewById(R.id.connection_status);
        positionX = (TextView) findViewById(R.id.x_position);
        positionY = (TextView) findViewById(R.id.y_position);
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

        manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        client.unregisterResources();
        client.close();
    }

    @Override
    public void onSuccess(IMqttToken mqttToken) {
        Log.i(LOGTAG, "Client connected");
        connectionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark,
                null));
        connectionStatus.setText("Connected");

        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
//        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
//                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onFailure(IMqttToken arg0, Throwable arg1) {
        connectionStatus.setText("Connection failed");
        Log.i(LOGTAG, "Client connection failed: " + arg1.getMessage());
    }

    private void sendMessage(String rawMessage, String topic) {
        MqttMessage message = new MqttMessage(rawMessage.getBytes());
        message.setQos(2);
        message.setRetained(false);

        try {
            client.publish("iot-univem/iotime/" + topic, message);
        } catch (MqttException e) {
            Log.e(LOGTAG, "Error", e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            processAccelerometerData(event);
        }
    }

    private void processAccelerometerData(SensorEvent event) {
        String mSensorX = String.valueOf(event.values[0]);
        String mSensorY = String.valueOf(event.values[1]);

        positionX.setText(mSensorX);
        positionY.setText(mSensorY);

        String currentDate = DateHelper.getCurrentDateAsISOString();

        sendSensorData(new SendData(currentDate, String.valueOf(mSensorX)), "sensorx");
        sendSensorData(new SendData(currentDate, String.valueOf(mSensorY)), "sensory");
    }

    private void sendSensorData(SendData sensorData, String topic) {
        String json = new Gson().toJson(sensorData);
        sendMessage(json, topic);
    }
}
