package com.example.smssendingbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE=11;

    String [] requiredPermission={Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // check android version
      // if version >=13 then take notification permission
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            requiredPermission=new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS

            };
        }


      checkPermission();

         myToken();


    }

    // checkPermmission
    public void checkPermission(){

        List<String> requestToPermisson=new ArrayList<>();

        for(String permission : requiredPermission){

            if(ContextCompat.checkSelfPermission(MainActivity.this,permission)!=PackageManager.PERMISSION_GRANTED){
                requestToPermisson.add(permission);

            }
        }

        if(requestToPermisson.isEmpty()){    // that meeans all permission grantd

            permissionGrantedExecution();

        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,requestToPermisson.toArray(new String[0]),REQ_CODE);
        }


    }

    //when permission Granted
    public void permissionGrantedExecution(){


        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();

    }


    // call back method which check result of permisson


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allGranted=true;

        for(int result : grantResults){

            if(result !=PackageManager.PERMISSION_GRANTED){

                allGranted=false;
                break;
            }
        }


        if(!allGranted){
            permissionDialogbox();
        }
        else{
            permissionGrantedExecution();
        }



    }

    // permission dialog box

    public void permissionDialogbox(){

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Permissions Required");
        builder.setMessage("This app needs access to Sms, Location, and Notification to function properly");
        builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent settngsIntent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package",getPackageName(),null));

               startActivity(settngsIntent);


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();



    }

    // token retrieve
    public void myToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("firebaseToken", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("firebaseToken",token);
                    }
                });
    }



}