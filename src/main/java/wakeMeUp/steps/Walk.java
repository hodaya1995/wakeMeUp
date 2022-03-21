package me.jfenn.wakeMeUp.steps;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class Walk implements SensorEventListener, StepListener {
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel,magnometer;
    private int numSteps,samples,counter,yTimes;
    //private TextView stepsNum;
    float xTimes=0.1f;
    private float[] gravity = new float[]{ 0, 0, 0 };
    private float[] linearAcceleration = new float[]{ 0, 0, 0 };
    private float[] input = new float[]{ 0, 0, 0 };
    float mid,max,avg;
    private boolean testIsClicked,magnometerIsClicked;
    float minMagno=100;
    private float weigtedMagno,maxMagno,currAvgOfMid,cumaltiveMid, x_uncalib,y_uncalib,z_uncalib;
    private int counterMagno,numOfAvg;
    private boolean magnometerTestMode;
    private final int SAMPLES=100;
    private float maxX,cumaltiveX,avgX,currAvgOfX;
    private float midX=2.5f;
    SharedPreferences preferencesOfAvg;

    float avgXP,avgZP;
    Context context;

    public Walk(Context context){
        this.context=context;
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(Walk.this, context);
        preferencesOfAvg = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        avgXP = preferencesOfAvg.getFloat("currAvgOfX", -1);
        avgZP = preferencesOfAvg.getFloat("currAvgOfMid", -1);

        /*if (avgXP == -1 || avgZP == -1) {
            Toast.makeText(context, "please caliberate your phone", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "phone is caliberated", Toast.LENGTH_LONG).show();
        }*/


    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            input[0] = event.values[0];
            input[1] = event.values[1];
            input[2] = event.values[2];

            final float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * input[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * input[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * input[2];
            linearAcceleration[0] = input[0] - gravity[0];
            linearAcceleration[1] = input[1] - gravity[1];
            linearAcceleration[2] = input[2] - gravity[2];

            if(!testIsClicked)simpleStepDetector.updateAccel(event.timestamp, linearAcceleration[0], linearAcceleration[1], linearAcceleration[2], context, xTimes, yTimes,avgZP,avgXP);
            else{
                if(counter>=21){
                    avg+=linearAcceleration[2];
                    avgX+=linearAcceleration[0];
                    samples++;
                    if(linearAcceleration[2]>max)max=linearAcceleration[2];
                    if(linearAcceleration[0]>maxX)maxX=linearAcceleration[0];
                }else{
                    counter++;
                }
            }
            if(simpleStepDetector.getSteps()==0&&!testIsClicked){
                numSteps=0;
                // stepsNum.setText(TEXT_NUM_STEPS + numSteps);
            }

        }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            counterMagno++;

            x_uncalib=event.values[0];
            y_uncalib=event.values[1];
            z_uncalib=event.values[2];

            weigtedMagno=(float) Math.sqrt(x_uncalib*x_uncalib+y_uncalib*y_uncalib+z_uncalib*z_uncalib);


            if(magnometerTestMode){

               /* if (counterMagno<SAMPLES){
                    if(minMagno==100&&weigtedMagno>100) minMagno=weigtedMagno;
                    if((weigtedMagno<minMagno&&weigtedMagno>100))minMagno=weigtedMagno;
                }else{
                    Toast.makeText(getContext(),"caliberation is completed, min is:"+minMagno,Toast.LENGTH_LONG).show();
                    counterMagno=0;
                    sensorManager.unregisterListener(CalibrationFragment.this);
                }

            }else{*/
                if (counterMagno<SAMPLES){
                    if(weigtedMagno>maxMagno)maxMagno=weigtedMagno;
                }else{
                    counterMagno=0;
                    sensorManager.unregisterListener(Walk.this);
                    if(maxMagno>minMagno){
                       // Toast.makeText(context, "detected fridge", Toast.LENGTH_LONG).show();
                    }else{
                       // Toast.makeText(context, "there is no fridge", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        // stepsNum.setText(TEXT_NUM_STEPS + numSteps);
    }

    private void startMagnet() {
        magnometerTestMode=true;
        maxMagno=0;
        sensorManager.registerListener(Walk.this, magnometer, SensorManager.SENSOR_DELAY_UI) ;
    }

    private void startWalking() {
        numSteps = 0;
        sensorManager.registerListener(Walk.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void stopWalking(){
        numSteps = 0;
        sensorManager.unregisterListener(Walk.this);
    }

    private void resetCurrSample() {
        maxX=0.0f;
        cumaltiveX=0.0f;
        avgX=0.0f;
        mid=0.0f;
        max=0.0f;
        avg=0.0f;
        currAvgOfX=0;
        midX=2.5f;
        counterMagno=0;
        numOfAvg=0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
