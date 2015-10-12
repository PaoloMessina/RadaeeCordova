/**
 * 
 */
package it.almaviva.cordovaplugins;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * @author pmessina
 *
 */
public class CustomFontTextView extends TextView {
	private final static String TAG = "CustomFontTextView";
    
    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        final String font = a.getString(R.styleable.CustomFontTextView_font);
        if(C.D) Log.i(TAG, font == null ? "font  nullo" : font);
        a.recycle();*/
        final String font = attrs.getAttributeValue(null, "font");
        //if(C.D) Log.w(TAG, font == null ? "font  nullo" : font);
        setCustomFont(context, font);
    }
	
	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /*TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        final String font = a.getString(R.styleable.CustomFontTextView_font);
        Log.i(TAG, font == null ? "font  nullo" : font);
        a.recycle();*/
        final String font = attrs.getAttributeValue(null, "font");
        //if(C.D) Log.w(TAG, font == null ? "font  nullo" : font);
        setCustomFont(context, font);
    }
	
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
