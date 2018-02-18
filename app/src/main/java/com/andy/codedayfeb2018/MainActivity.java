package com.andy.codedayfeb2018;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    enum Type {
        LEFT, RIGHT
    }

    private final static double ACCELERATION_THRESHOLD = 3.0;

    private Button calibrationButton;
    private ToggleButton toggleButton;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    // Gravity rotational data
    private float gravity[];
    // Magnetic rotational data
    private float magnetic[]; //for magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];

    // azimuth, pitch and roll
    private float azimuth;
    private float pitch;
    private float roll;

    private Type type = Type.LEFT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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


                azimuth = 0;
                pitch = 0;
                roll = 0;
                gravity = new float[3];

                mSensorManager.registerListener(MainActivity.this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(MainActivity.this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

    }


    private void makeSnackbar(String msg) {
        Snackbar.make(this.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = sensorEvent.values.clone();
                break;
        }
        if (mags != null && accels != null) {
            gravity = new float[9];
            magnetic = new float[9];
            SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
            float[] outGravity = new float[9];
            SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X,SensorManager.AXIS_Z, outGravity);
            SensorManager.getOrientation(outGravity, values);

            azimuth = values[0] * 57.2957795f;
            pitch =values[1] * 57.2957795f;
            roll = values[2] * 57.2957795f;

            System.out.println(azimuth + " " + pitch + " " + roll);
        }
    }

    private void rightDrumHit(double azimuth, double pitch, double roll) {
        pitch *= -1;
        if(pitch >= 5 && pitch < 60) {
            if(azimuth >= -90 && azimuth < -30){
                System.out.println("Crash Cymbal");
            }
            else if(azimuth >= -30 && azimuth <= 0) {
                System.out.println("High-Tom");
            }
            else if(azimuth > 0 && azimuth < 60) {
                System.out.println("Ride Cymbal");
            }
        }
        else if(pitch >= -22.5 && pitch < 5) {
            if(azimuth >= -60 && azimuth <= -45){
                System.out.println("Hi-Hat");
            }
            else if(azimuth > -45 && azimuth <= 0) {
                System.out.println("Snare");
            }
            else if(azimuth > 0 && azimuth < 90) {
                System.out.println("Low-Tom");
            }
        }
        else {
            System.out.println("Missed the drumset");
        }
    }

    private void leftDrumHit(double azimuth, double pitch, double roll) {
        pitch *= -1;
        if(pitch >= 5 && pitch < 60) {
            if(azimuth >= -60 && azimuth < 0){
                System.out.println("Crash Cymbal");
            }
            else if(azimuth >= 0 && azimuth <= 22.5) {
                System.out.println("High-Tom");
            }
            else if(azimuth > 22.5 && azimuth < 45) {
                System.out.println("Ride Cymbal");
            }
        }
        else if(pitch >= -22.5 && pitch < 5) {
            if(azimuth >= -60 && azimuth <= 0){
                System.out.println("Hi-Hat");
            }
            else if(azimuth > 0 && azimuth <= 60) {
                System.out.println("Snare");
            }
            else if(azimuth > 60 && azimuth < 120) {
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

    private void playCrashCymbal() {
        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.cymbal);
        mp.start();
    }

    private void playHighTom() {

    }

    private void playRideCymbal() {

    }

    private void playHiHat() {


    }

    private void playLowTom() {

    }

    private void playSnare() {
        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.snare);
        mp.start();
    }

    public double getDelta(double finalVal, double initialVal) {
        if (finalVal == initialVal){
            return 0;
        }
        else if (finalVal > initialVal) {
            return Math.min(initialVal + 360 - finalVal, finalVal - initialVal);
        }
        else {
            return Math.min(finalVal + 360 - initialVal, finalVal - initialVal);
        }
    }
}
