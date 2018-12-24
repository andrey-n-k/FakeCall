package com.xcuzme.app.data;

import java.io.Serializable;

public class ProfileInfo implements Serializable
{
	private String mId;
	private String mName;
	private String mSecondName;
	private String mPhone;
	private String mImageId;

	public ProfileInfo()
	{
		mId = "";
		mName = "John Smith";
		mSecondName = "";
		mPhone = "+1-541-754-3010";
		mImageId = "-1";
	}

	public static ProfileInfo build(String data)
	{
		ProfileInfo profileInfo = new ProfileInfo();
		if (data != null)
		{
			String[] tmp = data.split(":");
			profileInfo.mId = tmp[0];
			profileInfo.mName = tmp[1];
			profileInfo.mSecondName = tmp[2];
			profileInfo.mPhone = tmp[3];
			profileInfo.mImageId = tmp[4];
		}
		return profileInfo;
	}

	public static ProfileInfo build(String id, String name, String secondName, String phone, String imageId)
	{
		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.mId = id == null ? "" : id;
		profileInfo.mName = name == null ? "John Smith" : name;
		profileInfo.mSecondName = secondName == null ? "" : secondName;
		profileInfo.mPhone = phone == null ? "+1-541-754-3010" : phone;
		profileInfo.mImageId = imageId == null ? "" : imageId;
		return profileInfo;
	}

	public void setName(String value)
	{
		mName = value;
	}
	
	public void setSecondName(String value)
	{
		mSecondName = value;
	}
	
	public void setPhone(String value)
	{
		mPhone = value;
	}
	
	public void setID(String value)
	{
		mId = value;
	}
	
	public String getID()
	{
		return mId;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public String getSecondName()
	{
		return mSecondName;
	}
	
	public String getPhone()
	{
		return mPhone;
	}

	public String getImageId()
	{
		return mImageId;
	}

	public void setImageId(String imageId)
	{
		mImageId = imageId;
	}

	public String toString()
	{
		return mId + ":" + mName + ":" + mSecondName + ":" + mPhone + ":" + mImageId;
	}
	
}
