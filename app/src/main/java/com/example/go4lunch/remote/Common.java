package com.example.go4lunch.remote;

public class Common {

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/maps/api/";

    public static IGoogleApiInterface getGoogleApiService(){
        return  RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleApiInterface.class);
    }
}
