package com.example.myfacerecognition;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLL;
    SliderAdapter adapter;
    private ArrayList<SliderModal> sliderModalArrayList;
    private TextView[] dots;
    int size;
    TextView idBtnSkip, idBtnDone;
    String prevStarted = "yes";

//    @Override
//    protected  void onResume(){
//        super.onResume();
//        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
//        if (!sharedpreferences.getBoolean(prevStarted, false)) {
//            SharedPreferences.Editor editor = sharedpreferences.edit();
//            editor.putBoolean(prevStarted, Boolean.TRUE);
//            editor.apply();
//        } else {
//            startActivity(new Intent(this,SplashActivity.class));
//        }
//   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This method is used so that your splash activity can cover the entire screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        viewPager = findViewById(R.id.idViewPager);
        dotsLL = findViewById(R.id.idLLDots);
        sliderModalArrayList = new ArrayList<>();
        idBtnSkip = findViewById(R.id.idBtnSkip);
        idBtnDone = findViewById(R.id.idBtnDone);

        sliderModalArrayList.add(new SliderModal("Slide 1 ", "Slide 1 heading", "https://github.com/Ndetis/face_recognition_app/blob/cedric-/Images/111.jpg", R.drawable.gradient_one));
        sliderModalArrayList.add(new SliderModal("Slide 2 ", "Slide 2 heading", "https://images.unsplash.com/photo-1610783131813-475d08664ef6?ixid=MXwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwxMnx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60", R.drawable.gradient_two));
        sliderModalArrayList.add(new SliderModal("Slide 3 ", "Slide 3 heading", "https://images.unsplash.com/photo-1610832958506-aa56368176cf?ixid=MXwxMjA3fDB8MHxlZGl0b3JpYWwtZmVlZHwxN3x8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60", R.drawable.gradient_three));

        adapter = new SliderAdapter(SplashActivity.this, sliderModalArrayList);
        viewPager.setAdapter(adapter);
        size = sliderModalArrayList.size();
        addDots(size, 0);
        viewPager.addOnPageChangeListener(viewListener);

        idBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDots(size, 2);
                viewPager.setCurrentItem(2);
            }
        });
        idBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        });

    }

    private void addDots(int size, int pos) {
        dots = new TextView[size];
        dotsLL.removeAllViews();
        for (int i = 0; i < size; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);

            // below line is called when the dots are not selected.
            dots[i].setTextColor(getResources().getColor(R.color.black));
            dotsLL.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[pos].setTextColor(getResources().getColor(R.color.purple_700));
        }
    }

    // creating a method for view pager for on page change listener.
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(size, position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}