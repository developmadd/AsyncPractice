package com.example.asyncpractice;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class ProgressHandler extends Handler {


    ProgressBehaviour progressBehaviour;
    public void setProgressBehaviour(ProgressBehaviour progressBehaviour) {
        this.progressBehaviour = progressBehaviour;
    }


    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if( msg.getData().getString("message") != null) {
            progressBehaviour.onFinishProgress(msg.getData().getString("message"));
        } else {
            progressBehaviour.setProgress(msg.getData().getInt("value"));
        }

    }

    interface ProgressBehaviour{
        void setProgress(int progress);
        void onFinishProgress(String message);
    }
}