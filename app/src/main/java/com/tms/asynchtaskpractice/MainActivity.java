package com.tms.asynchtaskpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Pero";

    private Button startButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_Bar);
        startButton = findViewById(R.id.button);
        startButton.setOnClickListener(listener);
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Log.d(TAG, "onClick: You pressed me");


            ExampleAsyncTask task = new ExampleAsyncTask(MainActivity.this);
            task.execute(10);
        }
    };



    //the first parameter is the value we pass into the backround task to work with
    // the second parameter is what type we will use to measue progress
    // third (String) is a parameter which we get back from the AsynchTask

    //Since we can have memory lead because we Access UI elements from the mainActivity , if we destroy the activity the AsynchTask continues it's work
    //until is done, this may cause memory leaks
    // we can solve this using weak reference and making inner AsynchTask static
    private static class ExampleAsyncTask extends AsyncTask <Integer, Integer, String> {

        private WeakReference<MainActivity> activityWeakReference;

        ExampleAsyncTask(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        //this method is 1 called
        //this runs on UI thread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        //this is our separate backround thread, not UI thread
        //this is the place we do our heavy work
        @Override
        protected String doInBackground(Integer... integers) {

            for (int i = 0; i < integers[0]; i++) {
                publishProgress((i * 100) / integers[0]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return "Finished - result that we return from out task";
        }

        //this is 2 called
        //this runs on UI thread
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.progressBar.setProgress(values[0]);
        }


        //this is 3 called
        //the string that is passed is the result after our doInBackround, it is passed from the doInBackround
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
        }


    }


}
