package com.example.s354378_mappe2.service;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.s354378_mappe2.DBHandler;
import com.example.s354378_mappe2.R;
import com.example.s354378_mappe2.Utilities;
import com.example.s354378_mappe2.activities.AvtaleoversiktActivity;
import com.example.s354378_mappe2.models.Appointment;
import com.example.s354378_mappe2.models.Contact;

import java.util.List;

public class MinSendService extends Service {
    DBHandler dbHelper;
    SQLiteDatabase db;

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        dbHelper = new DBHandler(this);
        db = dbHelper.getWritableDatabase();

        List<Appointment> myAppointments = dbHelper.retrieveAllAppointments(db);

        //Sjekker dato på alle avtaler
        for (Appointment a : myAppointments){

            //If sjekker om datoen er riktig
            if (a.getDate().equals(Utilities.getTodaysDate())){
                for(Contact c : dbHelper.retrieveAllContacts(db)){

                    //If sjekker om at SMS sendes til riktig kontakt
                    if(c.get_ID() == Long.parseLong(a.getParticipants())){

                        //Sender SMS om til riktig kontakt om avtalen er i dag
                       sendSMSMessage(c.getPhone(), a.getMessage());
                       break;
                    }
                }
            }
        }

        //Oppretter notifikasjonen
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, AvtaleoversiktActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(this, 0, i, 0);
        Notification notifikasjon = new NotificationCompat.Builder(this, "MinKanal")
                .setContentTitle("Du har avtaler i dag!")
                .setContentText("Åpne for å se avtalene dine")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pintent).build();

        notifikasjon.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(88, notifikasjon);


        return super.onStartCommand(intent, flags, startId);
    }

    public void sendSMSMessage(String phoneNo, String message){
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        if(!sp.getBoolean("sjekkSMS", false)){
            Toast.makeText(this, "SMS-funksjoner er deaktivert", Toast.LENGTH_SHORT).show();
            return;
        }

        //Henter permissions
        int MY_PERMISSIONS_REQUEST_SEND_SMS = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int MY_PHONE_STATE_PERMISSION = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);

        //Sjekker om programmet har permissions.
        //Om det ikke er gitt permissions returnerer variablene over -1
        if(MY_PERMISSIONS_REQUEST_SEND_SMS >= 0 && MY_PHONE_STATE_PERMISSION >= 0){
            SmsManager smsMan = SmsManager.getDefault();
            smsMan.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(this, "SMS'er er sent ut!"+phoneNo, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "The app doesn't have SMS permissions", Toast.LENGTH_SHORT).show();
        }
    }
}

