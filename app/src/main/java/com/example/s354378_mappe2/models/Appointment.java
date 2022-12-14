package com.example.s354378_mappe2.models;

public class Appointment {
    long _ID;
    String name;
    String date;
    String time;
    String location;
    String participants;
    String message;

    public Appointment() {
    }

    public Appointment(String name, String date, String time, String location, String participants, String message) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.participants = participants;
        this.message = message;
    }

    public Appointment(long _ID, String name, String date, String time, String location, String participants, String message) {
        this._ID = _ID;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.participants = participants;
        this.message = message;
    }

    public long get_ID() {
        return _ID;
    }

    public void set_ID(long _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
