package com.mobiledev.uom.flyme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

//Αρχική Οθόνη με το logo της εφαρμογής

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView airplaneImg = (ImageView) findViewById(R.id.splash_image_airplane);
        final ImageView lettersImg = (ImageView) findViewById(R.id.splash_image_letters);
        final Animation rotationAnim = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
        final Animation fadeOutAnim = AnimationUtils.loadAnimation(getBaseContext(),R.anim.logo_fade_out);

        airplaneImg.startAnimation(rotationAnim);
        rotationAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            //Όταν θα τελειώνει το animation του logo
            public void onAnimationEnd(Animation animation) {
                airplaneImg.startAnimation(fadeOutAnim);
                finish();  //Ώστε όταν πατάει το πίσω στην MainActivity να μην του εμφανίζεται ξανά το logo

                /* airplaneImg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                },3000);*/

                //Καλεί την mainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_airplane);;
        //imageView.setImageBitmap(bitmap);
    }
}


