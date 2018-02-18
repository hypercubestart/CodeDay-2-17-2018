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

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    enum Type {
        LEFT, RIGHT
    }

    private final static double ACCELERATION_THRESHOLD = 2.5;

    private Button calibrationButton;
    private ToggleButton toggleButton;
    private SensorManager mSensorManager;
    private float[] prevAccelerations;
    private float[] prevRotations;
    private float[] initalRotations;

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
                    System.out.println("left");
                } else {
                    type = Type.RIGHT;
                    System.out.println("Right");
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
            for (int i = 0; i < values.length - 1; i++) {
                diff += values[i];
            }
            diff = Math.sqrt(Math.abs(diff));
            System.out.println(Arrays.toString(prevRotations));
            //System.out.println(diff);
            float[] rotValues = new float[3];
            for (int i = 0; i < rotValues.length; i++) {
                rotValues[i] = prevRotations[i] - initalRotations[i];
            }

            if (diff > ACCELERATION_THRESHOLD) {
                //REGISTER AS HIT
                //System.out.println(Arrays.toString(prevRotations));
                if (type == Type.LEFT) {
                    //LEFT DRUM STICK
                    leftDrumHit(rotValues);
                } else {
                    //RIGHT DRUM STICK
                    rightDrumHit(rotValues);
                }
            }

            prevAccelerations = values;

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            if (initalRotations == null) initalRotations = sensorEvent.values.clone();
            prevRotations = sensorEvent.values;
            //System.out.println(Arrays.toString(prevRotations));
        }
    }

    private void rightDrumHit(float[] vals) {
        if(vals[0] >= 0.125 && vals[0] < 0.333) {
            if(vals[2] <= 0.5 && vals[2] > 1/6){
                System.out.println("Crash Cymbal");
            }
            else if(vals[2] <= 1/6 && vals[2] >= 0) {
                System.out.println("High-Tom");
            }
            else if(vals[2] < 0 && vals[2] > -1/3) {
                System.out.println("Ride Cymbal");
            }
        }
        else if(vals[0] >= -0.125 && vals[0] < 0.125) {
            if(vals[2] <= 0.5 && vals[2] >= 0.25){
                System.out.println("Hi-Hat");
            }
            else if(vals[2] < 0.25 && vals[2] >= 0) {
                System.out.println("Snare");
            }
            else if(vals[2] < 0 && vals[2] > -0.5) {
                System.out.println("Low-Tom");
            }
        }
        else {
            System.out.println("Missed the drumset");
        }
    }

    private void leftDrumHit(float[] vals) {
        if(vals[0] >= 0.125 && vals[0] < 0.333) {
            if(vals[2] <= 1/3 && vals[2] > 1/8){
                System.out.println("Crash Cymbal");
            }
            else if(vals[2] <= 1/8 && vals[2] >= -0.25) {
                System.out.println("High-Tom");
            }
            else if(vals[2] < -0.25 && vals[2] > -0.5) {
                System.out.println("Ride Cymbal");
            }
        }
        else if(vals[0] >= -0.125 && vals[0] < 0.125) {
            if(vals[2] <= 0.5 && vals[2] >= 0){
                System.out.println("Hi-Hat");
            }
            else if(vals[2] < 0 && vals[2] >= -1/3) {
                System.out.println("Snare");
            }
            else if(vals[2] < -1/3 && vals[2] > -2/3) {
                System.out.println("Low-Tom");
            }
        }
        else {
            System.out.println("Missed the drumset");
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
