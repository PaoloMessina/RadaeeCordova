package it.almaviva.cordovaplugins;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.common.io.ByteStreams;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.util.PDFAssetStream;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pmessina on 30/07/15.
 */
public class ReaderActivity extends Activity {

    public static final String EXTRA_PARAMS = "RPDF_EXTRA_PARAMS";
    private ReaderController m_vPDF = null;
    private Document doc = new Document();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //plz set this line to Activity in AndroidManifes.xml:
        //    android:configChanges="orientation|keyboardHidden|screenSize"
        //otherwise, APP shall destroy this Activity and re-create a new Activity when rotate.
        Global.Init(this);

        ActionBar bar = getActionBar();
        AssetManager am = getAssets();

        // Get parameters from JS
        Intent startIntent = getIntent();
        String paramStr = startIntent.getStringExtra(EXTRA_PARAMS);
        JSONObject params;
        try { params = new JSONObject(paramStr); }
        catch (JSONException e) { params = new JSONObject(); }

        String barColor = params.optString("barColor");
        if(barColor != null && barColor.length() > 0){
            barColor = barColor.startsWith("#") ? barColor : "#" + barColor;
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(barColor)));
        }

        String titleString = params.optString("title");
        if(titleString != null && titleString.length() > 0){
            bar.setTitle(titleString);
        } else {
            bar.setTitle("");
        }

        Boolean showClose = params.optBoolean("showClose");
        if(!showClose){
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }


        m_vPDF = new ReaderController(this);
        doc.Close();

        byte[] data = null;
        try {
            InputStream inputStream = am.open("test.PDF");
            data = ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc = new Document();

        {
            int ret = doc.OpenMem(data, null);
            switch( ret )
            {
                case -1://need input password
                    finish();
                    break;
                case -2://unknown encryption
                    finish();
                    break;
                case -3://damaged or invalid format
                    finish();
                    break;
                case -10://access denied or invalid file path
                    finish();
                    break;
                case 0://succeeded, and continue
                    break;
                default://unknown error
                    finish();
                    break;
            }

            m_vPDF.open(doc);
        }
        setContentView( m_vPDF );
        //m_vPDF = new PDFSimple(ReaderActivity.this);
    }


    public void onDestroy()
    {
        if( m_vPDF != null )
        {
            m_vPDF.close();
            m_vPDF = null;
        }
        if( doc != null )
        {
            doc.Close();
            doc = null;
        }
        Global.RemoveTmp();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
