package com.vullnetlimani.englishdictionary.dataBaseHelper;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.vullnetlimani.englishdictionary.MainActivity;
import com.vullnetlimani.englishdictionary.R;
import com.vullnetlimani.englishdictionary.util.Constants;

import java.io.IOException;
import java.lang.ref.WeakReference;



public class LoadDatabaseAsync extends AsyncTask<String, String, Boolean> {

    private WeakReference<MainActivity> mainActivity;
    private AlertDialog alertDialog;
    private TextView mProgressTextView;
    private ProgressBar progressBar;

    public LoadDatabaseAsync(MainActivity mainActivity) {
        this.mainActivity = new WeakReference<>(mainActivity);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity.get());
        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity.get());
        View dialogView = layoutInflater.inflate(R.layout.alert_dialog_database_copying, null);

        mProgressTextView=dialogView.findViewById(R.id.mProgressTextView);
        progressBar=dialogView.findViewById(R.id.progressBar);

        dialog.setTitle(R.string.please_be_patient);
        dialog.setMessage(R.string.first_time_database_loading);
        dialog.setView(dialogView);
        alertDialog = dialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    @Override
    protected Boolean doInBackground(String... voids) {

        DatabaseHelper databaseHelper = new DatabaseHelper(mainActivity.get(), new ProgressInterface() {
            @Override
            public void onProgressUpdate(String progress) {
                Log.d(Constants.TAG, "progress - " + progress);
                publishProgress(progress);
            }
        });

        try {
            databaseHelper.createDatabase();
        } catch (IOException e) {
            throw new Error("Database was not Created");
        }
        databaseHelper.close();

        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);

        if(progressBar!=null){
            mProgressTextView.setText(progress[0]+" %");
            progressBar.setProgress(Integer.parseInt(progress[0]));
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        alertDialog.dismiss();

        MainActivity activity = mainActivity.get();

        if (activity == null || activity.isFinishing() || activity.isDestroyed())
            return;

        activity.openDatabase();

    }
}
