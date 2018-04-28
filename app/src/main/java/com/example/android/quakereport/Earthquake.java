package com.example.android.quakereport;

public class Earthquake {
        private double mMagnitude;
        private String mNear;
        private String mLocation;
        private String mDate;
        private String mTime;
        private String mUrl;

        public Earthquake(double a,String b1,String b2,String c,String d,String e)
        {
            mMagnitude=a;
            mNear=b1;
            mLocation=b2;
            mDate=c;
            mTime=d;
            mUrl=e;
        }
        public double getMagnitude() {return mMagnitude;}
        public String getNear() {return mNear;}
        public String getLocation() {return mLocation;}
        public String getDate() {return mDate;}
        public String getTime(){return mTime;}
        public String getUrl() { return mUrl;}
    }
