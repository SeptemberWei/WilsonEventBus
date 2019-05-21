package com.wilson.wilsoneventbus;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.wilson.eventbus.EventBus;

public class PostActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }


    public void post(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post("123");
            }
        }).start();
    }

    public void postBackground(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post("aaa");
            }
        }).start();
    }
}
