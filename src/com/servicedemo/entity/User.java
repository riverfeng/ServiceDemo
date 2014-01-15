package com.servicedemo.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

	/**
	 * The class of implementing Parcelable can't get through between different process.
	 * unuseful
	 */
	
	public int userid;
	public String username;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(userid);
		dest.writeString(username);
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
		
		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
		
		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}
	};
	
	private User(Parcel in){
		userid = in.readInt();
		username = in.readString();
	}

	public User(){}
}
