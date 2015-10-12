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
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.radaee.pdf.Document;
import com.radaee.pdf.Global;

import org.json.JSONException;
import org.json.JSONObject;

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
            CustomFontButton btnRight = (CustomFontButton) actionBarView.findViewById(R.id.navigation_bar_button_right);
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
            if(barBottomLineColor != null && barColor.length() > 0){
                barBottomLineColor = barColor.startsWith("#") ? barBottomLineColor : "#" + barBottomLineColor;
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    barBottomLine.setBackgroundDrawable(new ColorDrawable(Color.parseColor(barBottomLineColor)));
                } else {
                    barBottomLine.setBackground(new ColorDrawable(Color.parseColor(barBottomLineColor)));
                }
            }

            String titleString = params.optString("title");
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
                    Toast.makeText(ReaderActivity.this, "Prova download", Toast.LENGTH_SHORT).show();

                }
            });


            actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            Toolbar parent =(Toolbar) actionBarView.getParent();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

}
