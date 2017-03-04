package com.example.andresarango.aughunt.challenge;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.andresarango.aughunt.R;
import com.example.andresarango.aughunt.camera.AspectRatioFragment;
import com.example.andresarango.aughunt.camera.CameraCallback;
import com.example.andresarango.aughunt.location.DAMLocation;
import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;


public class ChallengeTemplate extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        AspectRatioFragment.Listener,ViewGroup.OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int LOCATION_PERMISSION = 1245;

    private CameraView mCameraView;
    private Button mTakePhotoButton;
    private CameraCallback mCameraCallback;
    private FrameLayout mPhoto;
    private Button mHint;
    private Button mSubmit;
    private Challenge mChallenge;
    private DAMLocation mLocation;
    private  String  mHintText = "";
    private FbEmulator mFbEmulator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_template);
        mPhoto=(FrameLayout) findViewById(R.id.photo);
        mPhoto.setOnClickListener(this);
        mHint=(Button) findViewById(R.id.leave_hint);
        mHint.setOnClickListener(this);
        mSubmit=(Button) findViewById(R.id.submit_challenge);
        mSubmit.setOnClickListener(this);
        mLocation = new DAMLocation(this);
        initializeCamera();
        initializeTakePhotoButton();
        requestPermission();
        //getLocation();

    }

//    private void getLocation() {
//
//        GoogleApiClient client = new GoogleApiClient.Builder(getApplicationContext())
//                .addApi(Awareness.API)
//                .build();
//        client.connect();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Awareness.SnapshotApi.getLocation(client)
//                .setResultCallback(new ResultCallback<LocationResult>() {
//                    @Override
//                    public void onResult(@NonNull LocationResult locationResult) {
//                        System.out.println(locationResult.getStatus().getStatusMessage());
//                        if (!locationResult.getStatus().isSuccess()) {
//                            System.out.println("dont work");
//                            return;
//                        }
//                        mLocation = locationResult.getLocation();
//                        System.out.println("Lat: " + mLocation.getLatitude() + ", Lng: " + mLocation.getLongitude());
//                    }
//                });
//
//        System.out.println("made it");
//    }

    private void initializeCamera() {
        mCameraView = (CameraView) findViewById(R.id.activity_main_camera);
        mCameraCallback = new CameraCallback(this, mPhoto);


        if (mCameraView != null) {
            mCameraView.addCallback(mCameraCallback);
        }
    }

    private void initializeTakePhotoButton() {
        mTakePhotoButton = (Button) findViewById(R.id.take_photo);
        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.takePicture();
            }
        });
    }

    @Override
    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
        if (mCameraView != null) {
            mCameraView.setAspectRatio(ratio);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCameraPermission();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraCallback.destroyHandler();
    }


    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void requestPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(ChallengeTemplate.this, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean locationPermissionIsNotGranted = locationPermission != PackageManager.PERMISSION_GRANTED;
        boolean APILevelIsTwentyThreeOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        if(locationPermissionIsNotGranted && APILevelIsTwentyThreeOrHigher){
            marshamallowRequestPermission();
        }
        if (locationPermissionIsNotGranted) {
            ActivityCompat.requestPermissions(ChallengeTemplate.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void marshamallowRequestPermission() {
        boolean userHasAlreadyRejectedPermission = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
        if(userHasAlreadyRejectedPermission){
            showMessageOKCancel("We need your location to find nearby challenges, is this too much trouble ?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ChallengeTemplate.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION);
                        }
                    });
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(ChallengeTemplate.this)
                .setMessage(message)
                .setPositiveButton("NO", onClickListener)
                .setNegativeButton("YES", null)
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.photo:

            mPhoto.setVisibility(View.INVISIBLE);

                break;
            case R.id.leave_hint:

                createDialog();
                break;

            case R.id.submit_challenge:


                mChallenge=new Challenge(mCameraCallback.getmBitmap(),mLocation);
                mChallenge.setmHint(mHintText);
                mFbEmulator= new FbEmulator(mChallenge,this);
                mFbEmulator.saveToDB();

                Toast.makeText(getApplicationContext(), "Challenge submitted", Toast.LENGTH_SHORT)
                        .show();
                finish();
                break;
        }

    }
    public  void createDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);


        final EditText edittext = new EditText(getApplicationContext());
        alert.setMessage("Enter Your Hint");

        alert.setView(edittext);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                mHintText = edittext.getText().toString();

            }
        });

        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();

    }
}
