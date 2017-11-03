package com.jingye.coffeemac.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 远程调用和传输的组包
 *
 */
public class Remote implements Parcelable {
	
	private String key;
	private int what;
	private int result;      //执行的结果,视具体的情况而定
	private int action;
	private int seq;
	private String body;
 
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
    
 
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getWhat() {
		return what;
	}
	public void setWhat(int what) {
		this.what = what;
	}
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
 
	public static final Parcelable.Creator<Remote> CREATOR = new Creator<Remote>() {

		public Remote[] newArray(int size) {
		 
			return new Remote[size];
		}

		public Remote createFromParcel(Parcel source) {
			 
			return new Remote(source);
		}
	};

	public Remote() {
	}

	public Remote(Parcel pl) {
		
		this.key = pl.readString();
		this.what = pl.readInt();
		this.result = pl.readInt();
		this.action = pl.readInt();
		this.seq = pl.readInt();
		this.body = pl.readString();
	} 

	public int describeContents() {
	 
		return 0;
	}
 
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(key);
		dest.writeInt(what);
		dest.writeInt(result);
		dest.writeInt(action);
		dest.writeInt(seq);
		dest.writeString(body);
	}
	
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
}
