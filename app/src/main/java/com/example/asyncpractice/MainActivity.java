package com.example.asyncpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import static com.example.asyncpractice.GenericAsyncTask.CANCEL_RESULT;
import static com.example.asyncpractice.GenericAsyncTask.OK_RESULT;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBarNotSync;
    ProgressBar progressBarThread;
    ProgressBar progressBarAsyncTask;
    ProgressBar progressBarHandler;
    Button buttonNotSync;
    Button buttonThread;
    Button buttonAsyncTask;
    Button buttonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNotSync = findViewById(R.id.BTN_Not_Sync);
        buttonThread = findViewById(R.id.BTN_Thread);
        buttonAsyncTask = findViewById(R.id.BTN_AsyncTask);
        buttonHandler = findViewById(R.id.BTN_Handler);

        progressBarNotSync = findViewById(R.id.PB_Not_Sync);
        progressBarThread = findViewById(R.id.PB_Thread);
        progressBarAsyncTask = findViewById(R.id.PB_AsyncTask);
        progressBarHandler = findViewById(R.id.PB_Handler);



        buttonNotSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarNotSync.setMax(60);
                for( int i = 0 ; i <= 60 ; i++){
                    task();
                    progressBarNotSync.setProgress(i);
                }
            }
        });



        buttonThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarThread.setMax(10);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for( int i = 1 ; i <= 10 ; i++){
                            task();
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarThread.setProgress(finalI);
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Listo Thread", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).start();
            }
        });



        buttonAsyncTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GenericAsyncTask genericAsyncTask = new GenericAsyncTask();
                genericAsyncTask.setBehaviour(new GenericAsyncTask.GenericAsyncTaskInterface() {
                    @Override
                    public void preExecute() {
                        progressBarAsyncTask.setMax(10);
                    }

                    @Override
                    public String doInBackground() {
                        for( int i = 1 ; i <= 10 ; i++){
                            task();
                            genericAsyncTask.setProgress(i);
                            if(genericAsyncTask.getCanceled()){
                                return CANCEL_RESULT;
                            }
                        }

                        return OK_RESULT;
                    }

                    @Override
                    public void progressExecution(Integer progress) {
                        progressBarAsyncTask.setProgress(progress);
                    }

                    @Override
                    public void postExecution(String result) {
                        if(result.equals(OK_RESULT)) {
                            Toast.makeText(getApplicationContext(), "Listo Async Task", Toast.LENGTH_LONG).show();
                        } else if( result.equals(CANCEL_RESULT)){
                            Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                genericAsyncTask.execute();

            }
        });

        buttonHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarHandler.setMax(10);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        for( int i = 1 ; i <= 10 ; i++){
                            task();
                            progressBarHandler.setProgress(i);
                        }
                    }
                });
            }
        });


    }





    void task()  {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
