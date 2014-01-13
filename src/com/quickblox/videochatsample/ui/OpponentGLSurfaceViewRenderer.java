package com.quickblox.videochatsample.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 04.01.14
 * Time: 21:43
 */
public class OpponentGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    private byte[] data;
    private boolean isShouldLoadTexture;
    int i = 0;
    private int width;
    private int height;

    private int texWidth;
    private int texHeight;
    private int textureY;
    private int textureU;
    private int textureV;

    // Our texture id.
//    private int mTextureId = -1;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        data = new byte[0];
        // Do nothing special.
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
        width = 176;
        height = 144;
        // Set the background color to black ( rgba ).
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);
        // Depth buffer setup.
        gl.glClearDepthf(1.0f);
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // The type of depth testing to do.
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // Really nice perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        createTextures();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        updateTextures();

//        if (!isShouldLoadTexture) {
//            return;
//        }
//        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//        int textureId = loadGLTexture(gl);
//        isShouldLoadTexture = false;
//        gl.glEnable(GL10.GL_TEXTURE_2D);
//        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//
//        ++i;
////        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
//        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//        Log.d("onDrawFrame", "dataLength=" + data.length + " and textureId=" + textureId);
    }

    private int loadGLTexture(GL10 gl) {

        // Generate one texture pointer...
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);

        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // Create Nearest Filtered Texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        // Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);
        Bitmap mutableBmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        // Use the Android GLUtils to specify a two-dimensional texture image
        // from our bitmap
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mutableBmp, 0);
        mutableBmp.recycle();
        return textures[0];
    }


    public void setData(byte[] data) {
        this.data = data;
        isShouldLoadTexture = true;
    }


    protected boolean updateTextures() {
        if (!isShouldLoadTexture) {
            return true;
        }
        Log.d("processReceivedFrame", "processReceivedFrame2=" + String.valueOf(data.length));
//        Log.d("CAMERAPREVIEWSIZE", "size1=" + width + " and " + height);
//        int w = this._frame.width();
//        int h = this._frame.height();
//        this.mSizeIsChanged = ((this.mWidth != w) || (this.mHeight != h));
//        this.mWidth = w;
//        this.mHeight = h;
//        if (this.mSizeIsChanged) {
//            destroyTextures();
//            if (!createTextures()) {
//                return false;
//            }
//        }
        createTextures();
        int bufLength = width * height;

        int yOffset = 0;
        int uOffset = bufLength;
        int vOffset = uOffset + bufLength / 4;
//        byte[] data = this._frame.data();

        byte[] bufY = new byte[bufLength];
        byte[] bufU = new byte[bufLength / 4];
        byte[] bufV = new byte[bufLength / 4];

        System.arraycopy(data, yOffset, bufY, 0, bufLength);
        System.arraycopy(data, uOffset, bufU, 0, bufLength / 4);
        System.arraycopy(data, vOffset, bufV, 0, bufLength / 4);
//
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textureY);
        GLES20.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, width, height, 6409, 5121, ByteBuffer.wrap(data));

        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textureU);
        GLES20.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, 6409, 5121, ByteBuffer.wrap(bufU));

        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textureV);
        GLES20.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, 6409, 5121, ByteBuffer.wrap(bufV));
        isShouldLoadTexture = false;
        return true;
    }


    protected boolean createTextures() {


        texWidth = getPow2Align(width);
        texHeight = getPow2Align(height);

        int texUVWidth = getPow2Align(width / 2);
        int texUVHeight = getPow2Align(height / 2);

        textureY = generateAndBindTexture();
        GLES20.glActiveTexture(GL10.GL_TEXTURE0);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_LUMINANCE, texWidth, texHeight, 0, GL10.GL_LUMINANCE, GL10.GL_UNSIGNED_BYTE, null);

        textureU = generateAndBindTexture();
        GLES20.glActiveTexture(GL10.GL_TEXTURE1);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_LUMINANCE, texUVWidth, texUVHeight, 0, GL10.GL_LUMINANCE, GL10.GL_UNSIGNED_BYTE, null);

        textureV = generateAndBindTexture();
        GLES20.glActiveTexture(GL10.GL_TEXTURE2);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_LUMINANCE, texUVWidth, texUVHeight, 0, GL10.GL_LUMINANCE, GL10.GL_UNSIGNED_BYTE, null);
        return true;
    }

    public int generateAndBindTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        if (texture[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);//33071.0F
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);//33071.0F
        return texture[0];
    }

    public static int getPow2Align(int val) {
        int res = 64;
        while (res < val) {
            res <<= 1;
        }
        return res;
    }


}
