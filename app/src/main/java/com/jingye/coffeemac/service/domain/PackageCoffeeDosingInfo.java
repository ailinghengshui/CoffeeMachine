package com.jingye.coffeemac.service.domain;

/**
 * Created by Hades on 2016/12/27.
 */

public class PackageCoffeeDosingInfo {


    /**
     * id : 5
     * dosing_name : 咖啡豆
     * value : 8
     * is_addable : true
     * order : 1
     * water : 230
     * ejection : 0
     * stirvol : 0
     * stirtime : 0
     * machine_configured : false
     * transform_factor : 1
     */

    private int id;
    private String dosing_name;
    private double value;
    private boolean is_addable;
    private int order;
    private int water;
    private int ejection;
    private int stirvol;
    private int stirtime;
    private boolean machine_configured;
    private double transform_factor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDosing_name() {
        return dosing_name;
    }

    public void setDosing_name(String dosing_name) {
        this.dosing_name = dosing_name;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean is_addable() {
        return is_addable;
    }

    public double getTransform_factor() {
        return transform_factor;
    }

    public void setTransform_factor(double transform_factor) {
        this.transform_factor = transform_factor;
    }

    public boolean isIs_addable() {
        return is_addable;
    }

    public void setIs_addable(boolean is_addable) {
        this.is_addable = is_addable;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public int isMachine_configured() {

        return machine_configured?1:0;
    }

    public void setMachine_configured(boolean machine_configured) {
        this.machine_configured = machine_configured;
    }

}
