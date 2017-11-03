package com.jingye.coffeemac.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Hades on 2016/11/2.
 */

public class CheckInItem implements Serializable, Parcelable {

    public static final Parcelable.Creator<CheckInItem> CREATOR = new Parcelable.Creator<CheckInItem>() {
        @Override
        public CheckInItem createFromParcel(Parcel source) {
            return new CheckInItem(source);
        }

        @Override
        public CheckInItem[] newArray(int size) {
            return new CheckInItem[size];
        }
    };
    private int id;
    private boolean isTitle=false;
    private String name;
    private boolean status=false;
    private String value;

    public CheckInItem(){}

    public CheckInItem(boolean isTitle,String name){
        this.isTitle=isTitle;
        this.name=name;
    }

    protected CheckInItem(Parcel in) {
        this.id = in.readInt();
        this.isTitle = in.readByte() != 0;
        this.name = in.readString();
        this.status = in.readByte() != 0;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeByte(this.isTitle ? (byte) 1 : (byte) 0);
        dest.writeString(this.name);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
    }
}
