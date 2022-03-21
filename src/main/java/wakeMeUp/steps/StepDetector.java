package me.jfenn.wakeMeUp.steps;

import android.content.Context;

public class StepDetector {
    private static final long MAX_STEP_TIME_IN_MILLIS = (long) (1.5*1000);
    String TAG="StepDetector";
    private StepListener listener;
    boolean there_is_no_negative_velocity=true;
    int samples=0;
    long time;
    float start_velocity=0;
    int steps=0;
    private final long MIN_STEP_TIME= (long) (0.7*1000000000); //0.7 sec
    private final long MAX_STEP_TIME= (long) (1.5*1000000000); //1.5 sec
    private final int STEPS= (int) 5; //5 steps to walk
    private long lastStepTimeNs = 0;
    double average_velocity=0;
    double cumulative_velocities=0;
    boolean start_velocity_is_updated=false;
    double prevAvg;
    float prevX,prevY,prevZ;
    double currV;
    boolean tresholdCond;
    int numOfAvgBigger;
    private boolean shakeIsDetected;


    public  void registerListener(StepListener listener,Context context) {
     this.listener = listener;
    }


    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void updateAccel(long timeNs, float x, float y, float z, Context context, double X, int Y,float maxZ,float maxX) {

        long currTime=System.currentTimeMillis();

        currV=Math.abs(y);
        cumulative_velocities+=currV;
        samples++;
        long time_left=timeNs - lastStepTimeNs;
        average_velocity=cumulative_velocities/samples;

        if((samples==18||samples==19||samples==20)&&!start_velocity_is_updated){
            start_velocity+=currV;
        }
        if(samples==21&&!start_velocity_is_updated){
            start_velocity/=3;
            start_velocity_is_updated=true;
        }

        if(x<0){
            x=Math.abs(x);
            if(y<0)y=Math.abs(y);
        }


        if(average_velocity>prevAvg) numOfAvgBigger++;
        else                         numOfAvgBigger=0;

        //tresholdCond=numOfAvgBigger==Y&&average_velocity>=start_velocity+X&&currV>=X&&currV<=start_velocity+1.5&&average_velocity<=start_velocity+1.5&&2.5*x<y;
        tresholdCond=numOfAvgBigger==Y&&average_velocity>=start_velocity+X&&currV>=X&&currV<=start_velocity+1.5&&average_velocity<=start_velocity+1.5&&x<maxX&&x>-maxX;
        if (start_velocity_is_updated&&time_left>=MIN_STEP_TIME&&time_left<=MAX_STEP_TIME&&tresholdCond) {

            if(z>maxZ||z<-maxZ) shakeIsDetected=true;
            if(!shakeIsDetected){
                listener.step(timeNs);
                steps++;
                if(steps==1){
                    time=System.currentTimeMillis(); //the time of the first move
                }
            }

            nullifyAll(timeNs);
        }


        if(start_velocity_is_updated&&(time_left>MAX_STEP_TIME)){
           nullifyAll(timeNs);
        }

        if(start_velocity_is_updated&&(currV>=start_velocity+2&&average_velocity>=start_velocity+2)){
            nullifyAll(timeNs);
        }

        /*if(steps==STEPS){
            /*if(currTime-time>MAX_STEP_TIME_IN_MILLIS&&currTime-time<MIN_STEP_TIME_IN_MILLIS){
                //success
                Toast.makeText(context,"success",Toast.LENGTH_LONG).show();
                steps=0;
            }
            else{
              //reset everything
                Toast.makeText(context,"reset",Toast.LENGTH_LONG).show();
                steps=0;
            }*/
          /*  Toast.makeText(context,"success!",Toast.LENGTH_LONG).show();
            Log.i(TAG,"success!");
            //steps=0;
        }*/

        if(steps>=1&&currTime-time>steps*MAX_STEP_TIME_IN_MILLIS){
            steps=0;
            nullifyAll(timeNs);
        }

        prevX=x;
        prevY=y;
        prevZ=z;
        prevAvg=average_velocity;

    }

    private void nullifyAll(long timeNs) {
        samples=0;
        average_velocity=0;
        cumulative_velocities=0;
        lastStepTimeNs = timeNs;
        shakeIsDetected=false;
    }


    private double getSensorValue(float x, float y, float z) {
        return Math.sqrt(x*x +y*y+z*z);
    }
}
