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

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private Button calibrationButton;
    private SensorManager mSensorManager;
    private double totalAcceleration;
    private Long prevTime;

    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private double positionX;
    private double positionY;
    private double positionZ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        calibrationButton = findViewById(R.id.calibration_button);
        calibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(MainActivity.this);

                velocityX = 0;
                velocityY = 0;
                velocityZ = 0;
                positionX = 0;
                positionY = 0;
                positionZ = 0;
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
            float[] values = sensorEvent.values;
            // Movement
            float x = values[0];
            float y = values[1];
            float z = values[2];

            totalAcceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            System.out.println("Acceleration: " + totalAcceleration);

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            if (prevTime == null) {
                prevTime = sensorEvent.timestamp;
                return;
            }
            float[] values = sensorEvent.values;
            // Movement
            float x = values[0];
            float y = values[1];
            float z = values[2];

            double deltaTime = (sensorEvent.timestamp-prevTime)/1000000000.0;
            double accelerationX = Math.sin(Math.PI * x) * totalAcceleration;
            double accelerationY = Math.sin(Math.PI * y) * totalAcceleration;
            double accelerationZ = Math.sin(Math.PI * z) * totalAcceleration;

            velocityX += accelerationX * deltaTime;
            velocityY += accelerationY * deltaTime;
            velocityZ += accelerationZ * deltaTime;
            positionX += 0.5 * accelerationX * Math.pow(deltaTime, 2) + velocityX * deltaTime;
            positionY += 0.5 * accelerationY * Math.pow(deltaTime, 2) + velocityY * deltaTime;
            positionZ += 0.5 * accelerationZ * Math.pow(deltaTime, 2) + velocityZ * deltaTime;

            System.out.println("X: " + positionX + " Y: " + positionY + " Z: " + positionZ);

            prevTime = sensorEvent.timestamp;
        }
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
