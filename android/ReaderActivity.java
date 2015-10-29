package it.almaviva.cordovaplugins;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.radaee.pdf.Document;
import com.radaee.pdf.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import it.almaviva.radaeepdfdemo.R;

/**
 * Created by pmessina on 30/07/15.
 */
public class ReaderActivity extends Activity {

    public static final String EXTRA_PARAMS = "RPDF_EXTRA_PARAMS";
    public static final String EXTRA_PARAMS_DATA = "RPDF_EXTRA_PARAMS_DATA";
    public static final String EXTRA_PARAMS_RETURN = "RPDF_EXTRA_PARAMS_RETURN";
    public static final int READER_PDF_ACTIVITY_RESULT = 1;
    public static final int READER_PDF_ACTIVITY_RESULT_OK = 0;
    public static final int READER_PDF_ACTIVITY_RESULT_KO = 1;

    private ReaderController m_vPDF = null;
    private Document doc = new Document();
    private byte[] data;
    private String titleString;

    ViewGroup parentRoot;
    View popUpView;
    CustomFontButton btnRight;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //plz set this line to Activity in AndroidManifes.xml:
        //    android:configChanges="orientation|keyboardHidden|screenSize"
        //otherwise, APP shall destroy this Activity and re-create a new Activity when rotate.
        Global.Init(this);

        // Get parameters from JS
        Intent startIntent = getIntent();
        String paramStr = startIntent.getStringExtra(EXTRA_PARAMS);
        JSONObject params;
        try { params = new JSONObject(paramStr); }
        catch (JSONException e) { params = new JSONObject(); }

        data = startIntent.getByteArrayExtra(EXTRA_PARAMS_DATA);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            View actionBarView = getLayoutInflater().inflate(R.layout.navigation_bar, null);
            RelativeLayout background = (RelativeLayout) actionBarView.findViewById(R.id.navigation_bar_background_layout);
            LinearLayout barBottomLine = (LinearLayout)actionBarView.findViewById(R.id.navigation_bar_bottom_line);
            CustomFontTextView txvTitle = (CustomFontTextView) actionBarView.findViewById(R.id.navigation_bar_txv_title);
            CustomFontButton btnLeft = (CustomFontButton) actionBarView.findViewById(R.id.navigation_bar_button_left);
            btnRight = (CustomFontButton) actionBarView.findViewById(R.id.navigation_bar_button_right);
            Resources r = actionBarView.getContext().getResources();


            String barColor = params.optString("barColor");
            if(barColor != null && barColor.length() > 0){
                barColor = barColor.startsWith("#") ? barColor : "#" + barColor;
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    background.setBackgroundDrawable(new ColorDrawable(Color.parseColor(barColor)));
                } else {
                    background.setBackground(new ColorDrawable(Color.parseColor(barColor)));
                }
            }

            String barBottomLineColor = params.optString("barBottomLineColor");
            if(barBottomLineColor != null && barBottomLineColor.length() > 0){
                barBottomLineColor = barBottomLineColor.startsWith("#") ? barBottomLineColor : "#" + barBottomLineColor;
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    barBottomLine.setBackgroundDrawable(new ColorDrawable(Color.parseColor(barBottomLineColor)));
                } else {
                    barBottomLine.setBackground(new ColorDrawable(Color.parseColor(barBottomLineColor)));
                }
            }

            titleString = params.optString("title");
            String titleTextColor = params.optString("titleTextColor");
            if(titleString != null && titleString.length() > 0){
                int titleSize = 16;
                txvTitle.setVisibility(View.VISIBLE);
                txvTitle.setText(titleString);
                //txvTitle.setCustomFont(actionBarView.getContext(), titleFont == null ? getDefaultFont(actionBarView.getContext()) : titleFont);
                txvTitle.setTextSize(titleSize > 0 ? titleSize : 11);
                if(titleTextColor != null && titleTextColor.length() > 0) txvTitle.setTextColor(Color.parseColor(titleTextColor));
                txvTitle.invalidate();
            }

            Boolean showClose = params.optBoolean("showClose");
            if(!showClose) {
                Drawable backButtonDrawable;
                btnLeft.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    backButtonDrawable = r.getDrawable(R.drawable.bar_back_image, actionBarView.getContext().getTheme());
                } else {
                    backButtonDrawable = r.getDrawable(R.drawable.bar_back_image);
                }
                if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btnLeft.setBackgroundDrawable(backButtonDrawable);
                } else {
                    btnLeft.setBackground(backButtonDrawable);
                }

                int rightBtnWidth = 40;
                int rightBtnWHeight = 40;
                ViewGroup.LayoutParams layoutParams = btnLeft.getLayoutParams();
                layoutParams.width =  Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightBtnWidth, r.getDisplayMetrics()));
                layoutParams.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightBtnWHeight, r.getDisplayMetrics()));
                btnLeft.setLayoutParams(layoutParams);
                btnLeft.invalidate();
                btnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReaderActivity.this.finish();
                    }
                });
            }

            String rightButtonText = params.optString("rightButtonText");
            String rightButtonTextColor = params.optString("rightButtonTextColor");
            int textSize = 16;
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setBackgroundResource(0);
            if(android.os.Build.VERSION.SDK_INT >=  Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                btnRight.setAllCaps(false);
            /*if(rightButtonLayout != null){
                LayoutParams params = btnRight.getLayoutParams();
                params.width =  Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightButtonLayout.width, r.getDisplayMetrics()));
                params.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightButtonLayout.height, r.getDisplayMetrics()));
                btnRight.setLayoutParams(params);
                btnRight.invalidate();
            }*/
            if(rightButtonText != null && rightButtonText.length() > 0){
                btnRight.setText(rightButtonText);
                //btnRight.setCustomFont(actionBarView.getContext(), rightButtonFont == null ? getDefaultFont(actionBarView.getContext()) : rightButtonFont);
                if(rightButtonTextColor != null && rightButtonTextColor.length() > 0) btnRight.setTextColor(Color.parseColor(rightButtonTextColor));
                btnRight.setTextSize(textSize);
            }
            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ReaderActivity.this.showPopUpConfirm(v);

                    // create temporary dictionary, to save media or attachment data.
                    /*File sdDir = Environment.getExternalStorageDirectory();
                    File ftmp;
                    if (sdDir != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                        ftmp = new File(sdDir, "salutile");
                    else
                        ftmp = new File(ReaderActivity.this.getFilesDir(), "salutile");
                    if (!ftmp.exists())// not exist? make it!
                        ftmp.mkdir();
                    String tmp_path = ftmp.getPath();
                    String fileName = (titleString != null && titleString.length() >= 0 ) ?  titleString.trim() + ".pdf" : ("referto_" + new Date().getTime()+ ".pdf" );
                    File outFile = new File(tmp_path + "/" + fileName);

                    try {
                        if(!outFile.exists())outFile.createNewFile();
                        BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(outFile));
                        fOut.write(ReaderActivity.this.data);
                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        Toast.makeText(ReaderActivity.this, "Errore durante il salvataggio", Toast.LENGTH_SHORT).show();
                    }*/
                }
            });

            actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toolbar parent =(Toolbar) actionBarView.getParent();
                parent.setContentInsetsAbsolute(0,0);
                actionBar.setElevation(0);
            }
        }



        m_vPDF = new ReaderController(this);
        doc.Close();

        /*byte[] data = null;
        try {
            InputStream inputStream = am.open("test.PDF");
            data = ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

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

    public void sendResult(int resultStatus, String resultJson){
        Intent data = new Intent();
        data.putExtra(EXTRA_PARAMS_RETURN, resultJson);
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

    public void showPopUpConfirm(View v){
        if(btnRight != null)
            btnRight.setEnabled(false);
        parentRoot = (ViewGroup) ReaderActivity.this.findViewById(android.R.id.content).getRootView();
        popUpView = getLayoutInflater().inflate(R.layout.pdf_popup_layout, null);
        TextView confirmTextView = (TextView)popUpView.findViewById(R.id.pdf_popup_confirm_click);
        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) popUpView.getParent()).removeView(popUpView);

                if(ReaderActivity.this.saveFilePdf()){
                    ReaderActivity.this.showPopUpOutcome(true);
                } else {
                    ReaderActivity.this.showPopUpOutcome(false);
                }

                if(btnRight != null)
                    btnRight.setEnabled(true);
            }
        });
        parentRoot.addView(popUpView);
    }

    public boolean saveFilePdf(){
        // create temporary dictionary, to save media or attachment data.
        File sdDir = Environment.getExternalStorageDirectory();
        File ftmp;
        if (sdDir != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            ftmp = new File(sdDir, "RadaeeDemo");
        else
            ftmp = new File(ReaderActivity.this.getFilesDir(), "RadaeeDemo");
        if (!ftmp.exists())// not exist? make it!
            ftmp.mkdir();
        String tmp_path = ftmp.getPath();
        String fileName = (titleString != null && titleString.length() >= 0 ) ?  titleString.trim() + ".pdf" : ("pdf" + new Date().getTime()+ ".pdf" );
        File outFile = new File(tmp_path + "/" + fileName);

        try {
            if(!outFile.exists())outFile.createNewFile();
            BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(outFile));
            fOut.write(ReaderActivity.this.data);
            fOut.flush();
            fOut.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void showPopUpOutcome(boolean isSuccess){
        if(btnRight != null)
            btnRight.setEnabled(false);
        parentRoot = (ViewGroup) ReaderActivity.this.findViewById(android.R.id.content).getRootView();
        popUpView = getLayoutInflater().inflate(R.layout.pdf_popup_outcome_layout, null);
        TextView bodyTextView = (TextView)popUpView.findViewById(R.id.pdf_popup_outcome_body);
        if(isSuccess){
            bodyTextView.setText("Il pdf del referto\n è stato salvato nella cartella RadaeeDemo");
        } else {
            bodyTextView.setText("Siamo spiacenti\n si è verificato un errore durante\n il salvataggio del referto.");
        }
        TextView confirmTextView = (TextView)popUpView.findViewById(R.id.pdf_popup_outcome_click);
        confirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) popUpView.getParent()).removeView(popUpView);

                if(btnRight != null)
                    btnRight.setEnabled(true);
            }
        });
        parentRoot.addView(popUpView);
    }

}
