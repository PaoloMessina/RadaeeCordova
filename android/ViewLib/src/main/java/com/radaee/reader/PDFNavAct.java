package com.radaee.reader;

import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.util.PDFGridItem;
import com.radaee.util.PDFGridView;
import com.radaee.viewlib.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PDFNavAct extends Activity implements OnItemClickListener
{
	private LinearLayout m_layout;
	private PDFGridView m_grid;
	private EditText m_path;
    @SuppressLint("InlinedApi")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //plz set this line to Activity in AndroidManifes.xml:
        //    android:configChanges="orientation|keyboardHidden|screenSize"
        //otherwise, APP shall destroy this Activity and re-create a new Activity when rotate. 
        Global.Init( this );
		m_layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.pdf_nav, null);
		m_grid = (PDFGridView)m_layout.findViewById(R.id.pdf_nav);
		m_path = (EditText)m_layout.findViewById(R.id.txt_path);
		m_grid.PDFSetRootPath("/mnt");
		m_path.setText(m_grid.getPath());
		m_path.setEnabled(false);
		m_grid.setOnItemClickListener(this);
		setContentView(m_layout);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
    @SuppressLint("InlinedApi")
	@Override
    protected void onDestroy()
    {
        Global.RemoveTmp();
        super.onDestroy();
    }
    private void onFail(Document doc, String msg)//treat open failed.
    {
    	doc.Close();
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void InputPswd(PDFGridItem item)//treat password
    {
		LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.dlg_pswd, null);
		final EditText tpassword = (EditText)layout.findViewById(R.id.txt_password);
		final PDFGridItem gitem = item;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which)
			{
				String password = tpassword.getText().toString();
				Document doc = new Document();
				int ret = gitem.open_doc(doc, password);
				switch( ret )
				{
				case -1://need input password
					doc.Close();
					InputPswd(gitem);
					break;
				case -2://unknown encryption
					onFail(doc, "Open Failed: Unknown Encryption");
					break;
				case -3://damaged or invalid format
					onFail(doc, "Open Failed: Damaged or Invalid PDF file");
					break;
				case -10://access denied or invalid file path
					onFail(doc, "Open Failed: Access denied or Invalid path");
					break;
				case 0://succeeded, and continue
					InitView(doc);
					break;
				default://unknown error
					onFail(doc, "Open Failed: Unknown Error");
					break;
				}
			}});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}});
		builder.setTitle("Input Password");
		builder.setCancelable(false);
		builder.setView(layout);
		
		AlertDialog dlg = builder.create();
		dlg.show();
    }
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)//listener for icon clicked.
	{
		PDFGridItem item = (PDFGridItem)arg1;
		if( item.is_dir() )
		{
			m_grid.PDFGotoSubdir(item.get_name());
			m_path.setText(m_grid.getPath());
		}
		else
		{
			Document doc = new Document();
			int ret = item.open_doc(doc, null);
			switch( ret )
			{
			case -1://need input password
				doc.Close();
				InputPswd(item);
				break;
			case -2://unknown encryption
				onFail(doc, "Open Failed: Unknown Encryption");
				break;
			case -3://damaged or invalid format
				onFail(doc, "Open Failed: Damaged or Invalid PDF file");
				break;
			case -10://access denied or invalid file path
				onFail(doc, "Open Failed: Access denied or Invalid path");
				break;
			case 0://succeeded, and continue
				InitView(doc);
				break;
			default://unknown error
				onFail(doc, "Open Failed: Unknown Error");
				break;
			}
		}
	}
    private void InitView(Document doc)//process to view PDF file
    {
		PDFViewAct.ms_tran_doc = doc;
		Intent intent = new Intent(this, PDFViewAct.class);  
		startActivity(intent);
    }
}
