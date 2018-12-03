package com.example.owner.grabalorry;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=7300;

    Button b1;

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        //Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                //R.anim.myanimation);
        //lorry.startAnimation(animation);



        //set status bar color to avoid all white and causes status cant be seen

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.colorBlack));


        new Handler().postDelayed(new Runnable(){
            @Override

            public void run(){
                Intent splashIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(splashIntent);
                finish();
            }

        },SPLASH_TIME_OUT);
    }
}
