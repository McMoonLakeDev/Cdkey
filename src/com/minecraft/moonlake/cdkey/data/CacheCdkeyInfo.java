package com.minecraft.moonlake.cdkey.data;

public class CacheCdkeyInfo {

	private final String cdkey;
	private final String date;
	private final String user;

	public CacheCdkeyInfo(String cdkey, String date, String user) {

		this.cdkey = cdkey;
		this.date = date;
		this.user = user;
	}
	
	public String getCdkey() {

		return cdkey;
	}

	public String getDate() {

		return date;
	}

	public String getUser() {

		return user;
	}
}
