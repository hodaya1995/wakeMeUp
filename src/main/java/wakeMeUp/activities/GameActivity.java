package me.jfenn.wakeMeUp.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.steps.StepCount;
import me.jfenn.wakeMeUp.walk.Walk;

public class GameActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    int stepsDone;
    private long DURATION;
    private ImageView player,coin1,coin2,coin3,coin4,coin5,openBox,closeBox;
    float playerX,playerY,coin1X,coin1Y,coin2X,coin2Y,coin3X,coin3Y,coin4X,coin4Y,coin5X,coin5Y,boxX,boxY;
    float fromXvalL,fromYvalL;
    int toCookieNumber=1;
    int width,height;
    boolean isCookie;
    boolean running;
    private int currCoin=1;
    boolean isRight=true;
    private boolean stepIsDone=true;
    private int steps;
    private SensorManager sensorManger;
     TextView explaination;
    Walk walk;
    StepCount stepCounter;
    private int runLater;
    boolean firstStepIsDone;
    ObjectAnimator animation;
    private boolean isCompleted;
    String TAG="GameActivity";
    boolean preview;
    private boolean fromCalibration;
    private TextView tv;
    private FrameLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);





        
        
        
        
        SharedPreferences pref = GameActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE);
        preview= pref.getBoolean("preview",false);
        Log.d(TAG,"preview: "+preview);

        stepCounter=new StepCount() {
            @Override
            public void doStep() {
                stepsDone++;
                stepIsTaken();
            }
        };


        explaination=(TextView)findViewById(R.id.explaination);
         mProgressBar=(ProgressBar)findViewById(R.id.progressbar);
        animation = ObjectAnimator.ofInt(mProgressBar, "progress", 100, 0);
        fl=(FrameLayout)findViewById(R.id.game_instruction);
        tv=(TextView)findViewById(R.id.game_instruction2);

        mProgressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        final long duration=15*1000;
        animation.setDuration(duration);
        //DecelerateInterpolator
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                 final long SPLASH_TIME = duration/2;

                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

                                }
                            }, SPLASH_TIME);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                SharedPreferences prefs = GameActivity.this.getSharedPreferences("threshold", GameActivity.this.MODE_PRIVATE );
                if(!isCompleted){
                    fromCalibration=prefs.getBoolean("fromCalibration",false);
                    Log.d(TAG,"fromCalibration: "+fromCalibration);
                  if(fromCalibration){
                     SharedPreferences.Editor editor = GameActivity.this.getSharedPreferences("threshold", GameActivity.this.MODE_PRIVATE).edit();
                      editor.putBoolean("needCalibration",true);
                      editor.apply();
                      walk.unregisterListener();

                      String msg="Calibration failed. Try again, and follow the instructions properly.";
                      showDialog(msg);


                  }
                  else{
                      boolean preview=prefs.getBoolean("preview",false);
                      Log.d(TAG,"PREVIEW: "+preview);
                      if(preview){
                          SharedPreferences.Editor editor = GameActivity.this.getSharedPreferences("threshold", GameActivity.this.MODE_PRIVATE).edit();
                          editor.putBoolean("preview", false);
                          editor.apply();

                      }else {
                          SharedPreferences.Editor editor = GameActivity.this.getSharedPreferences("threshold", GameActivity.this.MODE_PRIVATE).edit();
                          editor.putBoolean("snoozed", true);
                          editor.apply();
                      }
                      goBackToFragment();
                  }


                }


            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        //animation.start();





        sensorManger = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);


        DURATION= (this.getResources().getInteger(R.integer.duration))*25;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;



        coin1 = (ImageView) findViewById(R.id.coin1);
        coin1.setBackgroundResource(R.drawable.coin);
        ((AnimationDrawable) coin1.getBackground()).start();
        coin2 = (ImageView) findViewById(R.id.coin2);
        coin2.setBackgroundResource(R.drawable.coin);
        ((AnimationDrawable) coin2.getBackground()).start();
        coin3 = (ImageView) findViewById(R.id.coin3);
        coin3.setBackgroundResource(R.drawable.coin);
        ((AnimationDrawable) coin3.getBackground()).start();
        coin4 = (ImageView) findViewById(R.id.coin4);
        coin4.setBackgroundResource(R.drawable.coin);
        ((AnimationDrawable) coin4.getBackground()).start();
        coin5 = (ImageView) findViewById(R.id.key);
        player = (ImageView) findViewById(R.id.character);
        player.setBackgroundResource(R.drawable.idle);
        ((AnimationDrawable) player.getBackground()).start();
        openBox =(ImageView) findViewById(R.id.box_open);
        closeBox = (ImageView) findViewById(R.id.box_closed);

        setOnScreen();


        //walk=new Walk(this,this,stepCounter);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWalk();
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWalk();
            }
        });

    }

    private void startWalk() {
        tv.setVisibility(View.GONE);
        fl.setVisibility(View.GONE);
        Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vi.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vi.vibrate(250);
        }
        animation.start();
        walk=new Walk(GameActivity.this,GameActivity.this,stepCounter);
    }

    private void goBackToFragment() {
        findViewById(R.id.game_instruction).setVisibility(View.INVISIBLE);
        findViewById(R.id.game_instruction2).setVisibility(View.INVISIBLE);
        startActivity(new Intent(GameActivity.this,MainActivity.class));
    }



    private void setOnScreen() {
        ViewTreeObserver vto = player.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                player.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] playerXY = new int[2];
                int[] boxXY = new int[2];
                int[] coin1XY = new int[2];
                int[] coin2XY = new int[2];
                int[] coin3XY = new int[2];
                int[] coin4XY = new int[2];
                int[] coin5XY = new int[2];

                closeBox.getLocationOnScreen(boxXY);
                boxX = boxXY[0];
                boxY = boxXY[1];

                player.getLocationOnScreen(playerXY);

                playerX = playerXY[0];
                playerY = playerXY[1];

                coin1.getLocationOnScreen(coin1XY);
                coin1X = coin1XY[0];
                coin1Y = coin1XY[1];

                coin2.getLocationOnScreen(coin2XY);
                coin2X = coin2XY[0];
                coin2Y = coin2XY[1];

                coin3.getLocationOnScreen(coin3XY);
                coin3X = coin3XY[0];
                coin3Y = coin3XY[1];

                coin4.getLocationOnScreen(coin4XY);
                coin4X = coin4XY[0];
                coin4Y = coin4XY[1];

                coin5.getLocationOnScreen(coin5XY);
                coin5X = coin5XY[0];
                coin5Y = coin5XY[1];



                playerX/=width;
                coin1X/=width;
                coin2X/=width;
                coin3X/=width;
                coin4X/=width;
                coin5X/=width;
                boxX/=width;

                playerY/=height;
                coin1Y/=height;
                coin2Y/=height;
                coin3Y/=height;
                coin4Y/=height;
                coin5Y/=height;
                boxY/=height;



            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setOnScreenPlayer(final ImageView player){
        ViewTreeObserver vto = player.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                player.getViewTreeObserver().removeGlobalOnLayoutListener(this);


                int[] playerXY = new int[2];
                player.getLocationOnScreen(playerXY);
                playerX = playerXY[0];
                playerY = playerXY[1];

                playerY /= height;
                playerX /= width;
            }
        });
    }


    public void stepIsTaken(){
        player.setBackgroundResource(R.drawable.walk);
        steps++;
       if(stepIsDone){
            step();
        }else{
            runLater++;
        }

    }

    private void step() {
            runOnUiThread(new Runnable() {
                Animation mAnimation;

                @Override
                public void run() {
                    player.post(new Runnable() {

                        @Override
                        public void run() {
                            AnimationDrawable ad = (AnimationDrawable) player.getBackground();
                            ad.stop();
                            ad.run();

                        }

                    });
                    float toXval = 0, toYval = 0;
                    ImageView imageView = null;

                    int[] playerXY = new int[2];
                    player.getLocationOnScreen(playerXY);
                    playerX = playerXY[0];
                    playerX /= width;
                    switch (toCookieNumber) {
                        case 1:
                            isCookie = true;
                            toXval = coin1X - playerX;
                            imageView = coin1;
                            break;
                        case 2:
                            isCookie = true;
                            toXval = coin2X - playerX;
                            imageView = coin2;
                            break;
                        case 3:
                            isCookie = true;
                            toXval = coin3X - playerX;
                            imageView = coin3;
                            break;
                        case 4:
                            isCookie = true;
                            toXval = coin4X - playerX;
                            imageView = coin4;
                            break;
                        case 5:
                            isCookie = true;
                            toXval = coin5X - playerX;
                            imageView = coin5;
                            break;
                        case 6:
                            isCookie = false;
                            toXval = boxX - playerX;
                            imageView = closeBox;
                            break;
                        case 7:
                            isCookie = false;
                            toXval = 1 - playerX;
                            imageView = openBox;
                            break;

                        default:
                            break;
                    }


                    mAnimation = new TranslateAnimation(
                            TranslateAnimation.RELATIVE_TO_PARENT, fromXvalL,
                            TranslateAnimation.RELATIVE_TO_PARENT, toXval,
                            TranslateAnimation.RELATIVE_TO_PARENT, fromYvalL,
                            TranslateAnimation.RELATIVE_TO_PARENT, toYval);


                    fromXvalL = toXval;
                    fromYvalL = toYval;


                    mAnimation.setFillEnabled(true);
                    mAnimation.setFillAfter(true);
                    mAnimation.setDuration(DURATION);
                    final ImageView finalImageView = imageView;
                    mAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            if (!isCookie&&toCookieNumber == 6) {
                                isCompleted=true;
                                if(walk.fromCalibration){
                                    SharedPreferences.Editor editor = GameActivity.this.getSharedPreferences("threshold", GameActivity.this.MODE_PRIVATE).edit();
                                    editor.putBoolean("needCalibration",false);
                                    editor.apply();
                                }else{
                                    SharedPreferences p=GameActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editorNeedToPay=GameActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
                                    boolean preview=p.getBoolean("preview",false);
                                    if (preview) {
                                        editorNeedToPay.putBoolean("preview",false);
                                        editorNeedToPay.apply();
                                    }else {
                                        editorNeedToPay.putBoolean("isFirst", true);
                                        editorNeedToPay.apply();
                                        SharedPreferences.Editor editor = GameActivity.this.getSharedPreferences("threshold", GameActivity.this.MODE_PRIVATE).edit();
                                        editor.putBoolean("snoozed", false);
                                        editor.apply();
                                        cancelTimeHandler();
                                    }
                                }



                            }
                            stepIsDone = false;

                        }



                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Toast.makeText(GameActivity.this,"walk.stepCount: "+ walk.stepCount,Toast.LENGTH_SHORT).show();
                            if (finalImageView != null && toCookieNumber < 6) finalImageView.setVisibility(View.INVISIBLE);
                            if (!isCookie) {
                                if (toCookieNumber == 6) {
                                    toCookieNumber++;
                                    closeBox.setVisibility(View.GONE);
                                    openBox.setVisibility(View.VISIBLE);
                                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.level_complete);
                                    mp.start();
                                    step();
                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            if(walk.fromCalibration){
                                                walk.unregisterListener();
                                                String msg="Calibration completed successfully.";
                                                showDialog(msg);

                                            }else{
                                                goBackToFragment();
                                                openApps();
                                            }

                                        }
                                    });

                                }
                            } else {
                                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.coin_collect);
                                mp.start();
                            }
                            if (toCookieNumber < 6) {
                                toCookieNumber++;
                           }

                            stepIsDone = true;

                            if (runLater > 0 && toCookieNumber <= 6) {
                                   runLater--;
                                   step();

                            }
                            else {
                                player.setBackgroundResource(R.drawable.idle);
                                ((AnimationDrawable) player.getBackground()).stop();
                            }


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    player.startAnimation(mAnimation);

                }
            });

    }

    private void openApps() {
        boolean openGmail=PreferenceData.GMAIL.getValue(GameActivity.this);
        boolean openCalander=PreferenceData.CALANDAR.getValue(GameActivity.this);
         boolean wifi=PreferenceData.WIFI.getValue(GameActivity.this);
        if(wifi){
            if(openGmail) {
                MainActivity.turnWifiOn(GameActivity.this);

                 new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                         MainActivity.openGmail(GameActivity.this);
                    }
                });
            }else if(openCalander){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.openCalandar(GameActivity.this);
                    }
                });
            }
        }else{
            if(openGmail) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.openGmail(GameActivity.this);
                    }
                });
            }else if(openCalander){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.openCalandar(GameActivity.this);
                    }
                });
            }
        }
    }

    private void showDialog(String msg) {
        me.jfenn.wakeMeUp.dialogs.AlertDialog dialog = new me.jfenn.wakeMeUp.dialogs.AlertDialog(GameActivity.this, false,true)
                .setContent(msg)

                .setListener(new me.jfenn.wakeMeUp.dialogs.AlertDialog.Listener() {
                    @Override
                    public void onDismiss(me.jfenn.wakeMeUp.dialogs.AlertDialog dialog, boolean ok) {
                        goBackToFragment();


                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }


    private void cancelTimeHandler() {
        Intent intent = new Intent(getApplicationContext(), AlarmActivity.CountDownReciever.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmActivity.CountDownReciever.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
         AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences p=GameActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorNeedToPay=GameActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
        boolean preview=p.getBoolean("preview",false);
        boolean fromCalibration=p.getBoolean("fromCalibration",false);
        if (preview) {
            editorNeedToPay.putBoolean("preview",false);
            editorNeedToPay.apply();
        }
        if(fromCalibration){
            editorNeedToPay.putBoolean("fromCalibration",false);
            editorNeedToPay.apply();
        }
        super.onDestroy();
    }
}
