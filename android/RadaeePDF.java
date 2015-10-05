package it.almaviva.cordovaplugins;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import com.google.common.io.ByteStreams;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class RadaeePDF extends CordovaPlugin {


    private Context c;
    private CallbackContext callbackContext;
    private JSONObject params;
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
            this.callbackContext = callbackContext;
        	params = args.getJSONObject(0);
            String targetPath = params.optString("url");
            JSONObject header = params.getJSONObject("headerParams");

            if(targetPath != null && targetPath != ""){
                if(URLUtil.isFileUrl(targetPath)){
                    c = this.cordova.getActivity().getApplicationContext();
                    byte[] data = null;
                    try {
                        InputStream inputStream = new FileInputStream(targetPath);
                        data = ByteStreams.toByteArray(inputStream);
                    } catch (IOException e) {
                        callbackContext.error(e.getMessage());
                    }

                    Intent i = new Intent(c, ReaderActivity.class);
                    i.putExtra(ReaderActivity.EXTRA_PARAMS, params.toString());
                    i.putExtra(ReaderActivity.EXTRA_PARAMS_DATA, data);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(i);
                } else {
                    new DownloadFile(header).execute(targetPath);
                }
            } else {
                callbackContext.error("url is null or white space, this is a mandatory parameter");
            }

            /*c = this.cordova.getActivity().getApplicationContext();
            Intent i = new Intent(c, ReaderActivity.class);
            i.putExtra(RPDFViewActivity.EXTRA_PARAMS, params.toString());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);*/
        }
        else {
            return false;
        }
        return true;
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        private JSONObject header;

        public DownloadFile(JSONObject header) {
            super();
            this.header = header;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            FileDownloader fd = new FileDownloader(new Callback() {
                @Override
                public void pdfChargeDidFinishLoading(String data) {
                    try {
                        callbackContext.success(new JSONObject(data));
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }

                @Override
                public void pdfChargeDidFailWithError(String data) {
                    try {
                        callbackContext.error(new JSONObject(data));
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            fd.downloadFile(fileUrl, header);
            return null;
        }
    }

    public interface Callback {
        void pdfChargeDidFinishLoading(String data);
        void pdfChargeDidFailWithError(String data);
    }

    public class FileDownloader {
        private static final int  MEGABYTE = 1024 * 1024;

        public void setCbk(Callback cbk) {
            this.cbk = cbk;
        }

        private Callback cbk;

        public FileDownloader(Callback cbk){
            this.cbk = cbk;
        }

        public void downloadFile(String fileUrl, JSONObject header){
            try {

                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("POST");

                if(header != null && header.length() >= 0 ){
                    Iterator<?> keys = header.keys();
                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        try {
                            if ( header.get(key) instanceof String ) {
                                urlConnection.setRequestProperty (key, header.getString(key));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                byte[] data = ByteStreams.toByteArray(inputStream);

                if(data != null && data.length > 0){
                    cbk.pdfChargeDidFinishLoading("PDF download Success");
                } else {
                    cbk.pdfChargeDidFailWithError("ERROR DOWNLOAD PDF");
                }

                c = RadaeePDF.this.cordova.getActivity().getApplicationContext();
                Intent i = new Intent(c, ReaderActivity.class);
                i.putExtra(ReaderActivity.EXTRA_PARAMS, params.toString());
                i.putExtra(ReaderActivity.EXTRA_PARAMS_DATA, data);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(i);

            } catch (FileNotFoundException e) {
                cbk.pdfChargeDidFailWithError(String.format("{statusCode: %d, errorMessage: %s}", -1, e.getMessage()));
            } catch (MalformedURLException e) {
                cbk.pdfChargeDidFailWithError(String.format("{statusCode: %d, errorMessage: %s}", -1, e.getMessage()));
            } catch (IOException e) {
                cbk.pdfChargeDidFailWithError(String.format("{statusCode: %d, errorMessage: %s}", -1, e.getMessage()));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // Check which request we're responding to
            if (requestCode == ReaderActivity.READER_PDF_ACTIVITY_RESULT) {
                // Make sure the request was successful
                if (resultCode == ReaderActivity.READER_PDF_ACTIVITY_RESULT_OK) {
                    callbackContext.success(data.getStringExtra(ReaderActivity.EXTRA_PARAMS_RETURN));
                } else if (resultCode == ReaderActivity.READER_PDF_ACTIVITY_RESULT_KO) {
                    callbackContext.error(new JSONObject(data.getStringExtra(ReaderActivity.EXTRA_PARAMS_RETURN)));
                }
            }
        }
        catch (JSONException e) {
            callbackContext.error(e.getMessage());
        }
    }

}
