package com.example.ambit.recordingappwithfirebase;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button mRecordBtn;
    private TextView mRecordLabel;

    private MediaRecorder mRecorder;

    private String mFileName = null;

    private static final String LOG_TAG = "Record_log";

    StorageReference mStorage;

    ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorage = FirebaseStorage.getInstance().getReference();

        mProgress =  new ProgressDialog(this);

        mRecordBtn = findViewById(R.id.recordBtn);
        mRecordLabel = findViewById(R.id.recordLabel);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recoded_audio.mp3";

        mRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction()== MotionEvent.ACTION_DOWN){

                    startRecording();
                    mRecordLabel.setText("Recording Started...");

                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                    mRecordLabel.setText("Recording Stoped...");
                }

                return false;
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        uploadAudio();
    }

    private void uploadAudio() {

        mProgress.setMessage("Uploading Audio...");
        mProgress.show();

        StorageReference filepath = mStorage.child("Audio").child("new_audio.mp3");

        Uri uri =  Uri.fromFile(new File(mFileName));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mProgress.dismiss();

                mRecordLabel.setText("Uploding Completed...");

            }
        });

    }

}
