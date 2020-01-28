package com.example.asyncpractice;

import android.os.AsyncTask;

public class GenericAsyncTask  extends AsyncTask<Integer,Integer,String> {

    public static String OK_RESULT = "ok";
    public static String CANCEL_RESULT = "cancel";
    public static String ERROR_RESULT = "error";

    GenericAsyncTaskInterface genericAsyncTaskInterface;

    void setBehaviour(GenericAsyncTaskInterface genericAsyncTaskInterface) {
        this.genericAsyncTaskInterface = genericAsyncTaskInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        genericAsyncTaskInterface.preExecute();
    }

    @Override
    protected String doInBackground(Integer... integers) {
        return genericAsyncTaskInterface.doInBackground();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        genericAsyncTaskInterface.progressExecution(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        genericAsyncTaskInterface.postExecution(result);
    }

    public void setProgress(Integer progress) {
        publishProgress(progress);
    }

    public Boolean getCanceled() {
        return isCancelled();
    }

    interface GenericAsyncTaskInterface {
        void preExecute();

        String doInBackground();

        void progressExecution(Integer progress);

        void postExecution(String result);
    }
}