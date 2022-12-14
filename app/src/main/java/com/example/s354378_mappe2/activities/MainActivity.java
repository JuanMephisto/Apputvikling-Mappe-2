package com.example.s354378_mappe2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.s354378_mappe2.service.MinBroadcastReceiver;
import com.example.s354378_mappe2.service.MinSendService;
import com.example.s354378_mappe2.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String CHANNEL_ID = "MinKanal";

    public static String PROVIDER = "com.example.contentprovidercontact" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        //Henter knapper
        Button btnRegistrer = findViewById(R.id.btnCreateContact);
        Button btnShowContacts = findViewById(R.id.btnShowContactsPage);
        Button btnShowAppointments = findViewById(R.id.btnShowAppointmentsPage);
        Button btnCreateAppointment = findViewById(R.id.btnCreateAppointment);
        Button btnToggleSMS = findViewById(R.id.toggleSMS);
        Button btnToggleService = findViewById(R.id.toggleService);

        //Initialiserer sharedpreferences om det er første gang siden lastes
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        if(!sp.getBoolean("initialized", false)){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("sjekkSMS", true);
            editor.putString("standardMessage", "Det er ikke satt noen melding");
            editor.putInt("smsTime", 25200000);
            editor.putBoolean("sjekkNotifikasjon", true);
            editor.putBoolean("initialized", true);
            editor.apply();
        }

        //Gir symbolene riktig drawable
        btnToggleSMS.setBackgroundResource(sp.getBoolean("sjekkSMS", false) ? R.drawable.messages : R.drawable.messages_off);
        btnToggleService.setBackgroundResource(sp.getBoolean("sjekkNotifikasjon", false) ? R.drawable.notifications : R.drawable.notifications_off);

        //Starter broadcastreceiver
        BroadcastReceiver myBroadcastReceiver = new MinBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.example.service.MITTSIGNAL");
        filter.addAction("com.example.service.MITTSIGNAL");
        this.registerReceiver(myBroadcastReceiver, filter);
        startService(new View(this));


        //Setter onclicklistener til knappene som bytter activity
        btnRegistrer.setOnClickListener(view -> activityAddContacts());
        btnShowContacts.setOnClickListener(view -> activityKontaktoversikt());
        btnCreateAppointment.setOnClickListener(view -> activityAddAppointment());
        btnShowAppointments.setOnClickListener(view -> activityAvtaleoversikt());
    }

    //Metode som skrur av og på SMS
    public void toggleSMS(View v){
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        boolean sjekkSMS = sp.getBoolean("sjekkSMS", false);
        SharedPreferences.Editor editor = sp.edit();
        Button btnToggleSMS = findViewById(R.id.toggleSMS);


        if(sjekkSMS){ //Hvis SMS er på skal det skrus av
            editor.putBoolean("sjekkSMS", false);
            Toast.makeText(MainActivity.this, "SMS ble deaktivert!", Toast.LENGTH_SHORT).show();
            btnToggleSMS.setBackgroundResource(R.drawable.messages_off);
        }
        else{ //Eller skal det skrus på

            //Kan ikke skrus på om notifikasjoner er av
            if(!sp.getBoolean("sjekkNotifikasjon", false)){
                Toast.makeText(this, "Du må aktivere notifikasjoner først!", Toast.LENGTH_SHORT).show();
                return;
            }
            editor.putBoolean("sjekkSMS", true);
            Toast.makeText(MainActivity.this, "SMS ble aktivert!", Toast.LENGTH_SHORT).show();
            btnToggleSMS.setBackgroundResource(R.drawable.messages);
        }
        editor.apply();
    }

    public void startService (View v){
        Intent intent = new Intent();
        intent.setAction("com.example.service.MITTSIGNAL");
        sendBroadcast(intent);
    }

    private void createNotificationChannel(){
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    public void togglePeriodisk(View v){
        SharedPreferences sp = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        Button btnToggleService = findViewById(R.id.toggleService);
        SharedPreferences.Editor editor = sp.edit();

        if(!sp.getBoolean("sjekkNotifikasjon", false)){ //Gjør dette om notifikasjoner er skrudd av
            editor.putBoolean("sjekkNotifikasjon", true);
            startService(v);
            Toast.makeText(this, "Notifikasjoner er aktivert!", Toast.LENGTH_SHORT).show();
            btnToggleService.setBackgroundResource(R.drawable.notifications);
            editor.apply();
            return;
        }

        editor.putBoolean("sjekkNotifikasjon", false);
        editor.apply();

        //Skrur av SMS om det er på
        if(sp.getBoolean("sjekkSMS", false)){
            toggleSMS(v);
        }

        //Stopper notifikasjoner
        Intent i = new Intent(this, MinSendService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(alarm != null){
            alarm.cancel(pintent);
        }

        btnToggleService.setBackgroundResource(R.drawable.notifications_off);
        Toast.makeText(this, "Notifikasjoner og SMS er deaktivert!", Toast.LENGTH_SHORT).show();
    }

    private void activityAddContacts() {
        Intent myIntent = new Intent(this, CreateContactActivity.class);
        startActivity(myIntent);
    }
    private void activityKontaktoversikt() {
        Intent myIntent = new Intent(this, KontaktoversiktActivity.class);
        startActivity(myIntent);
    }
    private void activityAvtaleoversikt() {
        Intent myIntent = new Intent(this, AvtaleoversiktActivity.class);
        startActivity(myIntent);
    }
    private void activityAddAppointment() {
        Intent myIntent = new Intent(this, CreateAppointmentActivity.class);
        startActivity(myIntent);
    }
}