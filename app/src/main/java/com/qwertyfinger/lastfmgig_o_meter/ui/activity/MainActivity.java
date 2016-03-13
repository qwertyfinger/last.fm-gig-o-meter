package com.qwertyfinger.lastfmgig_o_meter.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qwertyfinger.lastfmgig_o_meter.R;
import com.qwertyfinger.lastfmgig_o_meter.ui.mvpview.MainMvpView;

public class MainActivity extends AppCompatActivity implements MainMvpView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
