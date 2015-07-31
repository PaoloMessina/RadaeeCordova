package it.almaviva.cordovaplugins;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.io.ByteStreams;
import com.radaee.pdf.DIB;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Matrix;
import com.radaee.pdf.Page;
import com.radaee.util.PDFAssetStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by pmessina on 30/07/15.
 */
public class RPDFViewActivity extends Activity {
    private Document m_doc = null;
    private PDFAssetStream m_asset_stream = null;
    private DIB m_dib = new DIB();
    private GLSurfaceView m_view;
    class SimpleRenderer implements Renderer
    {
        private int m_w;
        private int m_h;
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            m_w = m_view.getWidth();
            m_h = m_view.getHeight();
        }
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            m_w = width;
            m_h = height;
            int pageno = 0;
            float w = m_doc.GetPageWidth(pageno);
            float h = m_doc.GetPageHeight(pageno);
            if(w > 0 && h > 0)
            {
                float scale = m_w / w;
                int dibw = m_w;
                int dibh = (int) (h * scale);
                Page page = m_doc.GetPage(pageno);
                m_dib.CreateOrResize(dibw, dibh);
                page.RenderPrePare(m_dib);
                Matrix mat = new Matrix(scale, -scale, 0, dibh);
                page.Render(m_dib, mat);
                mat.Destroy();
                page.Close();
            }
            gl.glViewport(0, 0, m_w, m_h);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(0, m_w, m_h, 0, 1, -1);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        @Override
        public void onDrawFrame(GL10 gl)
        {
            gl.glClearColor(0.75f, 0.75f, 0.75f, 1);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            m_dib.GLDraw(gl, 0, 0);
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
                m_view = new GLSurfaceView(this);
                m_view.setRenderer(new SimpleRenderer());
                setContentView(m_view);
                break;
            default://unknown error
                onFail("Open Failed: Unknown Error");
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Global.Init( this );
        AssetManager am = getAssets();
        byte[] data = null;
        try {
            InputStream inputStream = am.open("test.PDF");
            data = ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_doc = new Document();
        int ret = m_doc.OpenMem(data, null);
        //int ret = m_doc.OpenStream(m_asset_stream, null);
        ProcessOpenResult(ret);
    }
    @Override
    protected void onDestroy()
    {
        if(m_doc != null) m_doc.Close();
        if(m_asset_stream != null) m_asset_stream.close();
        if(m_dib != null) m_dib.Free();
        Global.RemoveTmp();
        super.onDestroy();
    }
}
