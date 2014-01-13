package com.quickblox.videochatsample.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 04.01.14
 * Time: 21:42
 */
public class OpponentGLSurfaceView extends GLSurfaceView {

    OpponentGLSurfaceViewRenderer opponentGLSurfaceViewRenderer;


    public OpponentGLSurfaceView(Context context) {
        super(context);

    }

    public OpponentGLSurfaceView(Context context, AttributeSet attribs) {
        super(context, attribs);
        opponentGLSurfaceViewRenderer = new OpponentGLSurfaceViewRenderer();
        setRenderer(opponentGLSurfaceViewRenderer);
    }


    public OpponentGLSurfaceViewRenderer getOpponentGLSurfaceViewRenderer() {
        return opponentGLSurfaceViewRenderer;
    }

}
