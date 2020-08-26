package com.valentingonzalez.turistear.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.valentingonzalez.turistear.R;

public class BasicToolbar {

    public static void show(AppCompatActivity activity, String title, boolean upEnabled){
        Toolbar mToolbar = activity.findViewById(R.id.register_toolbar);
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(title);
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upEnabled);
    }
}
