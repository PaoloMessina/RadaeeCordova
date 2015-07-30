package com.radaee.reader;

import com.radaee.pdf.Document;
import com.radaee.pdf.Page;
import com.radaee.pdf.Matrix;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.radaee.pdf.Global;
import com.radaee.util.PDFAssetStream;
import com.radaee.viewlib.R;

import fi.harism.curl.CurlPage;
import fi.harism.curl.CurlView;

/**
 * Created by radaee on 2015/3/5.
 * this class use opensource "android_page_curl".
 * more details, see https://github.com/harism/android_page_curl
 */
public class PDFCurlViewAct extends Activity
{
    static protected Document ms_tran_doc;
    static private int m_tmp_index = 0;
    private PDFAssetStream m_asset_stream = null;
    private Document m_doc;
    private int m_page_cnt;
    private int m_page_cur;
    private CurlView m_view;
    private int m_w;
    private int m_h;
    private Bitmap m_bmps[];
    private boolean m_status[];
    private PDFCurlThread m_thread;
    public class PDFCurlThread extends Thread
    {
        private Handler m_hand = null;
        private boolean is_notified = false;
        private boolean is_waitting = false;
        private synchronized void wait_init()
        {
            try
            {
                if( is_notified )
                    is_notified = false;
                else
                {
                    is_waitting = true;
                    wait();
                    is_waitting = false;
                }
            }
            catch(Exception e)
            {
            }
        }
        private synchronized void notify_init()
        {
            if( is_waitting )
                notify();
            else
                is_notified = true;
        }
        private synchronized void wait_render(int pageno)
        {
            try {
                while (m_status[pageno]) {
                    wait(50);
                }
            }
            catch(Exception e)
            {
            }
        }
        @Override
        public void start()
        {
            super.start();
            wait_init();
        }
        private Bitmap render_page(int pageno)
        {
            Bitmap bmp = Bitmap.createBitmap(m_w, m_h, Bitmap.Config.ARGB_8888);
            bmp.eraseColor(0xFFFFFFFF);//background
            Page page = m_doc.GetPage(pageno);
            float scale1 = m_w/m_doc.GetPageWidth(pageno);
            float scale2 = m_h/m_doc.GetPageHeight(pageno);
            if(scale1 > scale2) scale1 = scale2;
            float x0 = (m_w - scale1 * m_doc.GetPageWidth(pageno)) / 2;
            float y0 = (m_h + scale1 * m_doc.GetPageHeight(pageno)) / 2;
            Matrix mat = new Matrix(scale1, -scale1, x0, y0 );
            page.RenderToBmp(bmp, mat);
            mat.Destroy();
            page.Close();
            m_bmps[pageno] = bmp;
            return bmp;
        }
        @Override
        public void run()
        {
            Looper.prepare();
            m_hand = new Handler(Looper.myLooper())
            {
                public void handleMessage(Message msg)
                {
                    if( msg != null )
                    {
                        if( msg.what == 0 )//render function
                        {
                            render_page(msg.arg1);
                            m_status[msg.arg1] = false;
                            super.handleMessage(msg);
                        }
                       else if( msg.what == 100 )//quit
                        {
                            super.handleMessage(msg);
                            getLooper().quit();
                        }
                    }
                    else
                    {
                        getLooper().quit();
                    }
                }
            };
            notify_init();
            Looper.loop();
        }
        protected Bitmap render_sync( int pageno )
        {
            if(m_status[pageno])
                wait_render(pageno);
            if(m_bmps[pageno] != null && !m_bmps[pageno].isRecycled())
                return m_bmps[pageno];
            else
                return render_page(pageno);
        }
        protected void render_async(int pageno)
        {
            if(m_status[pageno]) return;
            if(m_bmps[pageno] == null || m_bmps[pageno].isRecycled())
            {
                m_status[pageno] = true;
                m_hand.sendMessage(m_hand.obtainMessage(0, pageno, 0));
            }
        }
        public synchronized void destroy_thread()
        {
            try
            {
                m_hand.sendEmptyMessage(100);
                join();
                m_hand = null;
            }
            catch(InterruptedException e)
            {
            }
        }
    }
    private class PDFPageProvider implements CurlView.PageProvider
    {
        @Override
        public int getPageCount()
        {
            return m_page_cnt;
        }

        private Bitmap loadBitmap(int width, int height, int index)
        {
            Bitmap bmp = m_thread.render_sync(index);//load current page
            if(index > 0)
                m_thread.render_async(index - 1);//load previous page
            if(index < m_page_cnt - 1)
                m_thread.render_async(index + 1);//load next page
            if(index < m_page_cnt - 2)
                m_thread.render_async(index + 2);//load next's next page
            return bmp;
        }
        @Override
        public void updatePage(CurlPage page, int width, int height, int index)
        {
            int cur = index - 2;
            if(cur >= 0 && m_bmps[cur] != null && !m_bmps[cur].isRecycled()) {
                m_bmps[cur].recycle();
                m_bmps[cur] = null;
            }
            cur = index + 3;
            if(cur < m_bmps.length && m_bmps[cur] != null && !m_bmps[cur].isRecycled()) {
                m_bmps[cur].recycle();
                m_bmps[cur] = null;
            }
            Bitmap bmp = loadBitmap(width, height, index);
            page.setTexture(bmp, CurlPage.SIDE_BOTH);
        }
    }
    private void onFail(String msg)//treat open failed.
    {
        m_doc.Close();
        m_doc = null;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }
    private final void ProcessOpenResult(int ret)
    {
        switch( ret )
        {
            case -1://need input password
                onFail("Open Failed: Invalid Password");
                break;
            case -2://unknown encryption
                onFail("Open Failed: Unknown Encryption");
                break;
            case -3://damaged or invalid format
                onFail("Open Failed: Damaged or Invalid PDF file");
                break;
            case -10://access denied or invalid file path
                onFail("Open Failed: Access denied or Invalid path");
                break;
            case 0://succeeded, and continue
                m_page_cnt = m_doc.GetPageCount();
                m_bmps = new Bitmap[m_page_cnt];
                m_status = new boolean[m_page_cnt];
                m_thread = new PDFCurlThread();
                m_thread.start();
                break;
            default://unknown error
                onFail("Open Failed: Unknown Error");
                break;
        }
    }
    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Global.Init(this);
        setContentView(R.layout.pdf_curl);
        m_view = (CurlView) findViewById(R.id.curl_view);;
        m_view.setPageProvider(new PDFPageProvider());
        m_view.setSizeChangedObserver(new PDFSizeChangedObserver());
        m_view.setCurrentIndex(0);
        m_view.setBackgroundColor(0xFF202830);
        Intent intent = getIntent();
        if(ms_tran_doc != null)
        {
            m_doc = ms_tran_doc;
            ms_tran_doc = null;
            m_doc.SetCache(String.format("%s/temp%08x.dat", Global.tmp_path, m_tmp_index));//set temporary cache for editing.
            m_tmp_index++;
        }
        else
        {
            String pdf_asset = intent.getStringExtra("PDFAsset");
            String pdf_path = intent.getStringExtra("PDFPath");
            String pdf_pswd = intent.getStringExtra("PDFPswd");
            if( pdf_asset != null && pdf_asset != "" )
            {
                m_asset_stream = new PDFAssetStream();
                m_asset_stream.open(getAssets(), pdf_asset);
                m_doc = new Document();
                int ret = m_doc.OpenStream(m_asset_stream, pdf_pswd);
                ProcessOpenResult(ret);
            }
            else if( pdf_path != null && pdf_path != "" )
            {
                m_doc = new Document();
                int ret = m_doc.Open(pdf_path, pdf_pswd);
                m_doc.SetCache(String.format("%s/temp%08x.dat", Global.tmp_path, m_tmp_index));//set temporary cache for editing.
                m_tmp_index++;
                ProcessOpenResult(ret);
            }
        }
    }
    @SuppressLint("InlinedApi")
    @Override
    protected void onDestroy()
    {
        if(m_thread != null)
        {
            m_thread.destroy_thread();
            m_thread = null;
        }
        if(m_bmps != null)
        {
            int cur = 0;
            while(cur < m_page_cnt)
            {
                if( m_bmps[cur] != null && !m_bmps[cur].isRecycled() )
                    m_bmps[cur].recycle();
                cur++;
            }
            m_bmps = null;
            m_page_cnt = 0;
        }
        if(m_doc != null)
        {
            m_doc.Close();
            m_doc = null;
        }
        if( m_asset_stream != null )
        {
            m_asset_stream.close();
            m_asset_stream = null;
        }
        Global.RemoveTmp();
        super.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        m_view.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        m_view.onResume();
    }
    /**
     * CurlView size changed observer.
     */
    private class PDFSizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h)
        {
            m_w = w;
            m_h = h;
            if (w > h) {
                m_view.setViewMode(CurlView.SHOW_TWO_PAGES);
                m_view.setMargins(0, 0, 0, 0);
            } else {
                m_view.setViewMode(CurlView.SHOW_ONE_PAGE);
                m_view.setMargins(0, 0, 0, 0);
            }
        }
    }
}
