package com.jingye.coffeemac.util.test;

import android.support.test.espresso.IdlingResource;

/**
 * Created by Hades on 2017/3/30.
 */

public class EspressoIdlingResource {

    private static final String RESOURCE="GLOBAL";

    private static SimpleCoutingIdlingResource mCountingIdlingResource=new SimpleCoutingIdlingResource(RESOURCE);

    public static void increment(){
        mCountingIdlingResource.increment();
    }

    public static void decrement(){
        mCountingIdlingResource.decrement();
    }

    public static IdlingResource getIdlingResource(){
        return mCountingIdlingResource;
    }
}
