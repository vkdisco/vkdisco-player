package io.github.vkdisco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.github.vkdisco.service.PlayerService;

public class PlayerCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("COMMON", this.getClass().getName() + ".onCreate(" + ((savedInstanceState == null) ? "null" : "obj") + ")");
        startService(new Intent(getApplicationContext(), PlayerService.class));
    }
}
