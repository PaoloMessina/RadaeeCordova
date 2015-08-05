package it.almaviva.cordovaplugins;

import android.content.Context;
import android.content.Intent;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RadaeePDF extends CordovaPlugin {

    private Context c;
	/**
     * Constructor.
     */
    public RadaeePDF() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }
    
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
        	JSONObject params = args.getJSONObject(0);
            String targetPath = params.optString("url");

            c = this.cordova.getActivity().getApplicationContext();
            Intent i = new Intent(c, ReaderActivity.class);
            i.putExtra(RPDFViewActivity.EXTRA_PARAMS, params.toString());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);

            callbackContext.success(params);
        }
        else {
            return false;
        }
        return true;
    }
}
