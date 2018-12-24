package com.xcuzme.app.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class BookUtils
{
	
	/**return contact phone by lookup_key
	 * @param id - lookup key of contact
	 * @return phone string**/
	public static String getPhoneByID(Context context, String id)
	{
		Cursor secC = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY +" = ?",
                new String[] {id},
                null);
    	if (secC.moveToFirst())
		{
			String phone = secC.getString(secC.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			secC.close();
			return phone;
		}
		secC.close();
    	return "+1-541-754-3010";
	}
	
	/**return cursor to get contact info: given and family names
	 * @param id - lookup key of contact **/
	private static Cursor getFamilyInfo(Context context, String id)
	{
		String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY + " = ?";
		String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id };
	    return context.getContentResolver().query(
	    		ContactsContract.Data.CONTENT_URI,
	    		new String[] {	ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY,
	    						ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
	    						ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
	    						},
			    whereName, 
	    		whereNameParams, 
	    		null);
	    
	}

	public static String getName(Context context, String id)
	{
		Cursor ci = getFamilyInfo(context, id);
		String name = "John Smith";
		if (ci.moveToFirst())
		{
			String given = ci.getString(ci.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));

			if (given != null)
			{
				name = given;
			}
		}
		ci.close();
		return name;
	}

	public static String getSecondName(Context context, String id)
	{
		Cursor ci = getFamilyInfo(context, id);
		String secondName = "";
		if (ci.moveToFirst())
		{
			String family = ci.getString(ci.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));

			if (family != null)
			{
				secondName = family;
			}
		}
		ci.close();
		return secondName;
	}

	public static Uri getPersonImage(Context context, String id) throws Exception
	{
		Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
		return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}
}
