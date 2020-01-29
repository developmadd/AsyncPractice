package com.example.asyncpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    ProgressBar progressBarHandlerMessage;
    ProgressBar progressBarHandlerMessageClass;
    Button buttonNotSync;
    Button buttonThread;
    Button buttonAsyncTask;
    Button buttonHandler;
    Button buttonHandlerMessage;
    Button buttonHandlerMessageClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNotSync = findViewById(R.id.BTN_Not_Sync);
        buttonThread = findViewById(R.id.BTN_Thread);
        buttonAsyncTask = findViewById(R.id.BTN_AsyncTask);
        buttonHandler = findViewById(R.id.BTN_Handler);
        buttonHandlerMessage = findViewById(R.id.BTN_Handler_Message);
        buttonHandlerMessageClass = findViewById(R.id.BTN_Handler_Message_Class);

        progressBarNotSync = findViewById(R.id.PB_Not_Sync);
        progressBarThread = findViewById(R.id.PB_Thread);
        progressBarAsyncTask = findViewById(R.id.PB_AsyncTask);
        progressBarHandler = findViewById(R.id.PB_Handler);
        progressBarHandlerMessage = findViewById(R.id.PB_Handler_Message);
        progressBarHandlerMessageClass = findViewById(R.id.PB_Handler_Message_Class);


        buttonNotSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarNotSync.setMax(60);
                // This long task is going to block the main thread
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
                // This new thread is going to execute a long task and communicate with the
                // UI via runOnUiThread
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

                // This class extends from AsyncTask and executes a interface with the interest steps
                // on the async task, this interface implementation allows to communicatie with the UI
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

                // This handler owns to main thread
                final Handler handler = new Handler();

                // This new thread is going to execute a long task and use the main thread
                // handler to communicate with UI
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for( int i = 1 ; i <= 10 ; i++){
                            task();
                            final int finalI = i;
                            handler.post(new Runnable(){
                                @Override
                                public void run() {
                                    progressBarHandler.setProgress(finalI);
                                }
                            });

                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Listo Handler", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).start();

            }
        });


        buttonHandlerMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // This handler owns to main thread and execute instruction in the UI(Main thread)
                // according to the messages received from long task thread
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if( msg.getData().getString("message") != null) {
                            Toast.makeText(getApplicationContext(),msg.getData().getString("message") , Toast.LENGTH_LONG).show();
                        } else {
                            progressBarHandlerMessage.setProgress(msg.getData().getInt("value"));
                        }
                    }
                };

                // This new thread is going to execute a long task and use the main thread
                // handler to communicate with UI via messages
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for( int i = 1 ; i <= 10 ; i++){
                            task();

                            Bundle bundle = new Bundle();
                            bundle.putInt("value",i);
                            Message message = new Message();
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString("message","Listo Handler Message");
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);

                    }
                }).start();
            }
        });


        buttonHandlerMessageClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // This handler owns to main thread and execute instruction in the UI(Main thread)
                // according to the messages received from long task thread
                final ProgressHandler handler = new ProgressHandler();
                handler.setProgressBehaviour(new ProgressHandler.ProgressBehaviour() {
                    @Override
                    public void setProgress(int progress) {
                        progressBarHandlerMessageClass.setProgress(progress);
                    }

                    @Override
                    public void onFinishProgress(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                });


                // This new thread is going to execute a long task and use the main thread
                // handler to communicate with UI via messages
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for( int i = 1 ; i <= 10 ; i++){
                            task();

                            Bundle bundle = new Bundle();
                            bundle.putInt("value",i);
                            Message message = new Message();
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString("message","Listo Handler Message Class");
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);

                    }
                }).start();
            }
        });


    }






    void task() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
