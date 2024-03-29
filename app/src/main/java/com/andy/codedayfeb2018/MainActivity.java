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

    private final static double ACCELERATION_THRESHOLD = 5;

    private Button calibrationButton;
    private ToggleButton toggleButton;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    float[] inR = new float[16];
    float[] I = new float[16];
    float[] gravity = new float[3];
    float[] geomag = new float[3];
    float[] orientVals = new float[3];

    double[] initialOrientation;
    double azimuth = 0;
    double pitch = 0;
    double roll = 0;

    int counter;
    private final int waitCount = 20;

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


                initialOrientation = null;
                azimuth = 0;
                pitch = 0;
                roll = 0;
                counter = 0;
                inR = new float[16];
                I = new float[16];
                gravity = new float[3];
                geomag = new float[3];
                orientVals = new float[3];
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
        // Gets the value of the sensor that has been changed
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if (counter < waitCount) {
                    counter++;
                    Log.d("Msg", String.valueOf(counter));
                    return;
                }

                gravity = sensorEvent.values.clone();
                double diff = 0;
                for (int i = 0; i < gravity.length - 1; i++) {
                    diff += gravity[i];
                }
                diff = Math.sqrt(Math.abs(diff));
                Log.d("diff", String.valueOf(diff));
                if (diff > ACCELERATION_THRESHOLD) {

                    // If gravity and geomag have values then find rotation matrix
                    if (gravity != null && geomag != null && initialOrientation != null) {

                        // checks that the rotation matrix is found
                        boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
                        if (success) {
                            SensorManager.getOrientation(inR, orientVals);
                            azimuth = Math.toDegrees(orientVals[0]);
                            pitch = Math.toDegrees(orientVals[1]);
                            roll = Math.toDegrees(orientVals[2]);

                            double deltaAzimuth = azimuth - initialOrientation[0];
                            deltaAzimuth += (deltaAzimuth>180) ? -360 : (deltaAzimuth<-180) ? 360 : 0;
                            double deltaPitch = pitch - initialOrientation[1];
                            deltaPitch += (deltaPitch>180) ? -360 : (deltaPitch<-180) ? 360 : 0;
                            //deltaPitch *= -1;
                            double deltaRoll = roll - initialOrientation[2];
                            deltaRoll += (deltaRoll>180) ? -360 : (deltaRoll<-180) ? 360 : 0;

                            System.out.println(Arrays.toString(initialOrientation));
                            System.out.println(azimuth + " " + pitch);
                            System.out.println(deltaAzimuth + " " + deltaPitch);
                            if (type == Type.LEFT) {
                                //LEFT DRUM STICK
                                leftDrumHit(deltaAzimuth, deltaPitch, deltaRoll);
                            } else {
                                //RIGHT DRUM STICK
                                rightDrumHit(deltaAzimuth, deltaPitch, deltaRoll);
                            }
                            //System.out.println(azimuth + "      " + pitch + "       " + roll);
                        }
                    }
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomag = sensorEvent.values.clone();
                break;
        }
        // If gravity and geomag have values then find rotation matrix
        if (counter == waitCount && initialOrientation == null && gravity != null && geomag != null) {

            // checks that the rotation matrix is found
            boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
            if (success) {
                SensorManager.getOrientation(inR, orientVals);

                initialOrientation = new double[]{Math.toDegrees(orientVals[0]), Math.toDegrees(orientVals[1]), Math.toDegrees(orientVals[2]) };
            }
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
            if(azimuth >= -90 && azimuth <= -45){
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
            if(azimuth >= -90 && azimuth <= 0){
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
}
