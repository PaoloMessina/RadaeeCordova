package it.almaviva.cordovaplugins;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/**
 * @author pmessina
 * 
 * Custom view used extends Button view for implementing an asset's font customization.
 * This class used in android layout xml like this:
 * 	<it.f6.<myPkg>.base.view.CustomFontTextView
 * 	...
 * 	... 
 *  font="fontName.fontExtensions"/>
 * 
 */
public class CustomFontButton extends Button{
	
	private final static String TAG = "CustomFontButton";
    
	/***
	 * CustomFontButton constructor used from creation on XML and Hardcoded
	 * @param context The application context
	 * @param attrs the AttributeSet, represents a structure from the xml set of layout
	 */
    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        final String font = attrs.getAttributeValue(null, "font");
        //if(C.D) Log.w(TAG, font == null ? "font � nullo" : font);
        setCustomFont(context, font);
    }
	
    /***
     * 
     * @param context The application context
	 * @param attrs the AttributeSet, represent a structure from the xml set of layout
     * @param defStyle 
     */
	public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final String font = attrs.getAttributeValue(null, "font");
        //if(C.D) Log.w(TAG, font == null ? "font � nullo" : font);
        setCustomFont(context, font);
    }
	
	/***
	 * 
	 * @param context The application context
	 * @param font font string, this string represent the location on the font file in the folder assets
	 */
	public void setCustomFont(Context context, String font){
		if(font == null) {
			ApplicationInfo ai;
			String defaultFont = "";
			try {
				ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				Bundle bundle = ai.metaData;   
			    defaultFont = bundle.getString("DefaultFont");
			} catch (NameNotFoundException e) {
				//if(C.D) Log.w(TAG, "Could not get default typeface: " + e.getMessage());
				setTypeface(Typeface.MONOSPACE);
				return;
			} 
			try{
	        	Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + defaultFont);
	        	setTypeface(myTypeface);
	    	}catch(Exception e){
	    		//if(C.D) Log.w(TAG, "Could not get typeface: " + e.getMessage());
	    		setTypeface(Typeface.MONOSPACE);
	    	}  
		} else {
			try{
	        	Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
	        	setTypeface(myTypeface);
	    	}catch(Exception e){
	    		//if(C.D) Log.w(TAG, "Could not get typeface: " + e.getMessage());
	    		setTypeface(Typeface.MONOSPACE);
	    	}   
		}
	}
}
