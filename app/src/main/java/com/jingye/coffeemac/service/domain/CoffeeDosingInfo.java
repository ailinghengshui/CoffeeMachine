package com.jingye.coffeemac.service.domain;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class CoffeeDosingInfo implements Serializable, Parcelable, Comparable<CoffeeDosingInfo>{

	public static final Parcelable.Creator<CoffeeDosingInfo> CREATOR = new Parcelable.Creator<CoffeeDosingInfo>() {

		@Override
		public CoffeeDosingInfo createFromParcel(Parcel source) {
			return new CoffeeDosingInfo(source);
		}

		@Override
		public CoffeeDosingInfo[] newArray(int size) {
			return new CoffeeDosingInfo[size];
		}

	};
	private int id;
	private String name;
	private double value;
	private int order;
    private int water;
    private int ejection;   //出料调速比
    private int stirvol;    //搅拌调速比
    private int stirtime;   //搅拌持续时间
    private double factor;  //矫正因子
    private int macConifg;
    private int boxID;

	public CoffeeDosingInfo() {

	}

	private CoffeeDosingInfo(Parcel source) {
		id = source.readInt();
		name = source.readString();
		value = source.readDouble();
		order = source.readInt();
        water = source.readInt();
        ejection = source.readInt();
        stirvol = source.readInt();
        stirtime = source.readInt();
        macConifg = source.readInt();
        boxID = source.readInt();
        factor = source.readDouble();
	}

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public int getMacConifg() {
        return macConifg;
    }

    public void setMacConifg(int macConifg) {
        this.macConifg = macConifg;
    }

    public int getBoxID() {
        return boxID;
    }

    public void setBoxID(int boxID) {
        this.boxID = boxID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public int getEjection() {
        return ejection;
    }

    public void setEjection(int ejection) {
        this.ejection = ejection;
    }

    public int getStirvol() {
        return stirvol;
    }
	
    public void setStirvol(int stirvol) {
        this.stirvol = stirvol;
    }
	
    public int getStirtime() {
        return stirtime;
    }
	
    public void setStirtime(int stirtime) {
        this.stirtime = stirtime;
    }
	
	public void fromJSONString(JSONObject jsonObject) throws JSONException {
		id = jsonObject.getIntValue(TAG.ID);
		if(jsonObject.containsKey(TAG.NAME)){
            name = jsonObject.getString(TAG.NAME);
		}
		value = jsonObject.getDoubleValue(TAG.VALUE);
		if(jsonObject.containsKey(TAG.ORDER)){
			order = jsonObject.getIntValue(TAG.ORDER);
		}
        if(jsonObject.containsKey(TAG.WATER)){
            water = jsonObject.getIntValue(TAG.WATER);
        }
        if(jsonObject.containsKey(TAG.EJECTION)){
            ejection = jsonObject.getIntValue(TAG.EJECTION);
        }
        if(jsonObject.containsKey(TAG.STIRVOL)){
            stirvol = jsonObject.getIntValue(TAG.STIRVOL);
        }
        if(jsonObject.containsKey(TAG.STIRTIME)){
            stirtime = jsonObject.getIntValue(TAG.STIRTIME);
        }
        if(jsonObject.containsKey(TAG.IS_CONFIG)){
            macConifg = jsonObject.getBoolean(TAG.IS_CONFIG) ? 1 : 0;
        }
        if(jsonObject.containsKey(TAG.TRANSFORM_FACTOR)){
            factor = jsonObject.getDoubleValue(TAG.TRANSFORM_FACTOR) <= 0 ? 1 : jsonObject.getDoubleValue(TAG.TRANSFORM_FACTOR);
        }
	}

	public String toString() {
		return "CoffeeDosing: " + "id = " + id + ",name = " + name + ",value = " + value + ",order = " + order + ",water = " + water
                + ",ejection = " + ejection + ",stirvol = " + stirvol + ",stirtime = " + stirtime + ", machine_configured = " + macConifg
                + ",transform_factor = " + factor;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeDouble(value);
		dest.writeInt(order);
        dest.writeInt(water);
        dest.writeInt(ejection);
        dest.writeInt(stirvol);
        dest.writeInt(stirtime);
        dest.writeInt(macConifg);
        dest.writeInt(boxID);
        dest.writeDouble(factor);
	}
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int compareTo(CoffeeDosingInfo another) {
		return this.order-another.order;
	}

	private static final class TAG {
        static String ID = "id";
        static String NAME = "dosing_name";
        static String VALUE = "value";
        static String ORDER = "order";
        static String WATER = "water";
        static String EJECTION = "ejection";
        static String STIRVOL = "stirvol";
        static String STIRTIME = "stirtime";
        static String IS_CONFIG = "machine_configured";
        static String TRANSFORM_FACTOR = "transform_factor";
    }
}
