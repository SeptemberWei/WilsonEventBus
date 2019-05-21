package com.wilson.wilsoneventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wilson.eventbus.EventBus;
import com.wilson.eventbus.Subscribe;
import com.wilson.eventbus.ThreadModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().regist(this);
    }

    public void toPost(View view) {
        startActivity(new Intent(this, PostActivity.class));
    }

    @Subscribe(threadMode = ThreadModel.Background)
    public void receivedMain(String string) {
        Log.i("eventbus", string);

        Log.i("eventbus", Thread.currentThread().getName());
    }

}
