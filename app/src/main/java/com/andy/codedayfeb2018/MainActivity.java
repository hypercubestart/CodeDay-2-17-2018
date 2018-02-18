package com.andy.codedayfeb2018;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    enum Type {
        LEFT, RIGHT
    }

    private final static double ACCELERATION_THRESHOLD = 0.5;

    private Button calibrationButton;
    private ToggleButton toggleButton;
    private SensorManager mSensorManager;
    private float[] prevAccelerations;
    private float[] prevRotations;

    private Type type = Type.LEFT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        calibrationButton = findViewById(R.id.calibration_button);
        toggleButton = findViewById(R.id.toggleButton);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()) {
                    type = Type.LEFT;
                } else {
                    type = Type.RIGHT;
                }
            }
        });

        calibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(MainActivity.this);

                mSensorManager.registerListener(MainActivity.this,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                        SensorManager.SENSOR_DELAY_NORMAL);

                mSensorManager.registerListener(MainActivity.this,
                        mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

    }


    private void makeSnackbar(String msg) {
        Snackbar.make(this.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (prevAccelerations == null) {
                prevAccelerations = sensorEvent.values;
                return;
            }

            if (prevRotations == null) return;

            float[] values = sensorEvent.values;
            double diff = 0;
            for (int i = 0; i < values.length; i++) {
                diff += Math.pow(values[i] - prevAccelerations[i], 2);
            }

            if (diff > ACCELERATION_THRESHOLD) {
                //REGISTER AS HIT
                if (type == Type.LEFT) {
                    //LEFT DRUM STICK
                    leftDrumHit();
                } else {
                    //RIGHT DRUM STICK
                    rightDrumHit();
                }
            }

            prevAccelerations = values;

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            prevRotations = sensorEvent.values;
        }
    }

    private void leftDrumHit() {
        
    }

    private void rightDrumHit() {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }
}
