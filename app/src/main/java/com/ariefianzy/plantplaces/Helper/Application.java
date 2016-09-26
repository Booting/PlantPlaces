package com.ariefianzy.plantplaces.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.ariefianzy.plantplaces.Item.Image;
import com.parse.Parse;
import com.parse.ParseObject;

public class Application extends android.app.Application {
    private static SharedPreferences preferences;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Connect ke server parse.com menggunakan ID
         */
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Image.class);
		//Parse.initialize(this, "gyRGZgwI00SBSvwxl0SlshBQJq3jkdoIqVtVzCEU", "EDQD7pSZMfnFkfQ9bposkZNqKwQps45ESuJjEm58");
        //Parse.initialize(this, "sKxaBDyfhEzLYpGcMLuN4wEFvlQSWZQeWN55Hjoj", "UBaQKml6ElRaR5qX5oFlkw1rqSaQfJhABD0nXgbN");
        Parse.initialize(this, "L9vzG2RvFDp5nIfwozUeBC4cBcxqywkf9CQB2bDH", "mbX9n37j5u31jG3nAwNAlJcNLaEJoArP88UXfhiZ");
        preferences = getSharedPreferences("PlantPlaces", Context.MODE_PRIVATE);
    }
}
