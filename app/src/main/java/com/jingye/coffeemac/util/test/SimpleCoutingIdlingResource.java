package com.jingye.coffeemac.util.test;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Hades on 2017/3/30.
 */

public class SimpleCoutingIdlingResource implements IdlingResource {

    private final String mResourceName;
    private final AtomicInteger counter=new AtomicInteger(0);
    private volatile ResourceCallback resourceCallback;

    public SimpleCoutingIdlingResource(String resourceName){
        this.mResourceName=resourceName;
    }


    @Override
    public String getName() {
        return mResourceName;
    }

    @Override
    public boolean isIdleNow() {
        return counter.get()==0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback=callback;

    }

    public void increment(){
        counter.getAndIncrement();
    }

    public void decrement(){
        int counterVal=counter.decrementAndGet();
        if(counterVal==0){
            if(resourceCallback!=null){
                resourceCallback.onTransitionToIdle();
            }
        }

        if(counterVal<0){
            throw new IllegalArgumentException("Counter has been corrupted");
        }

    }
}
