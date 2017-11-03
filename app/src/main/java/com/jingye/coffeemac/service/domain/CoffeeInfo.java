package com.jingye.coffeemac.service.domain;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CoffeeInfo extends Ancestor{

	public final static int SugarNum0=1;
	public final static int SugarNum1=2;
	public final static int SugarNum2=3;
	public final static int SugarNum3=4;
	private static final long serialVersionUID = -5113394451363486332L;
	
	private int coffeeId = -1;
	private String coffeeTitle;
	private double price;
    private double discount;
	private String imgUrl ;
	private int soldNum;
	private String type ;
    private boolean isHot;
    private boolean isNew;
    private double volume;
	private boolean isAddIce;
	private boolean isSold;
	private boolean isSweet;
	private String coffeeTitleEn;
	private boolean isPackage;

	private String desc;
	private String descEn;
	private String doing;
	private List<CoffeeInfo> coffeesPackage=new ArrayList<CoffeeInfo>();
	private ArrayList<CoffeeDosingInfo> dosingList = new ArrayList<CoffeeDosingInfo>();
    private boolean isLackMaterials;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDescEn() {
		return descEn;
	}

	public void setDescEn(String descEn) {
		this.descEn = descEn;
	}

	public List<PackageCoffeeDosingInfo> getPackageDoing() {
		List<PackageCoffeeDosingInfo> packageCoffeeDosingInfos= JSONArray.parseArray(doing,PackageCoffeeDosingInfo.class);
		return packageCoffeeDosingInfos;
	}

	public void setDoing(String doing) {
		this.doing = doing;
	}

	public List<CoffeeInfo> getCoffeesPackage(){
		return coffeesPackage;
	}

	public void addCoffeeToPackage(CoffeeInfo coffeeInfo){
		coffeesPackage.add(coffeeInfo);
	}

	public int getPackageNum(){
		return coffeesPackage.size();
	}

	public boolean isPackage() {
		return isPackage;
	}

	public void setPackage(boolean aPackage) {
		isPackage = aPackage;
	}

    public boolean isLackMaterials() {
        return isLackMaterials;
    }

    public void setLackMaterials(boolean isLackMaterials) {
        this.isLackMaterials = isLackMaterials;
    }
	
	public int getCoffeeId() {
		return coffeeId;
	}
	
	public void setCoffeeId(int coffeeId) {
		this.coffeeId = coffeeId;
	}
	
	public String getCoffeeTitle() {
		return coffeeTitle;
	}
	
	public void setCoffeeTitle(String coffeeTitle) {
		this.coffeeTitle = coffeeTitle;
	}

	public String getCoffeeTitleEn() {
		return coffeeTitleEn;
	}

	public void setCoffeeTitleEn(String coffeeTitleEn) {
		this.coffeeTitleEn = coffeeTitleEn;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}
	
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public int getSoldNum() {
		return soldNum;
	}
	
	public void setSoldNum(int soldNum) {
		this.soldNum = soldNum;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean isHot) {
        this.isHot = isHot;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

	public boolean isAddIce() {
		return isAddIce;
	}

	public void setAddIce(boolean isAddIce) {
		this.isAddIce = isAddIce;
	}

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

	public ArrayList<CoffeeDosingInfo> getDosingList() {
		return dosingList;
	}

	public void addDosingInfo(CoffeeDosingInfo info){
		if(dosingList == null){
			dosingList = new ArrayList<CoffeeDosingInfo>();
		}
		
		dosingList.add(info);
	}

	public boolean isSold() {
		return isSold;
	}

	public void setIsSold(boolean isSold) {
		this.isSold = isSold;
	}

	public boolean isSweet() {
		return isSweet;
	}

	public void setSweet(boolean sweet) {
		isSweet = sweet;
	}
}
