package com.yue.opengl.deep3;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.yue.opengl.utils.LoadGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Time: 2021/4/21
 * Author:yzzCool
 * Description: 正方形的绘制。
 * 3.1 使用GL_TRIANGLE_STRIP的方式
 * 3.2 使用GL_TRIANGLES_FAN的方式
 * 3.3 使用GL_TRIANGLES和顶点矩阵数组加位置矩阵数组的方式
 */
public class GLRendererSquare3 implements GLSurfaceView.Renderer {
    public static final int BYTES_PER_FLOAT = 4;//每个浮点数:坐标个数* 4字节
    private final Context mContext;
    private FloatBuffer vertexBuffer;//顶点缓冲

    /********3.1.1  添加一个顶点 start********************/
    //顶点的坐标系
//    private static float TRIANGLE_COORDINATES[] = {
//            //Order of coordinates: X, Y, Z
//            -0.5f, 0.5f, 0.0f, // top left
//            0.5f, 0.5f, 0.0f,// top right
//            -0.5f, -0.5f, 0.0f, // bottom left
//            0.5f, -0.5f, 0.0f,   // bottom right
//    };

    /********3.1.1  添加一个顶点 end********************/

    /********3.2.1  添加一个顶点 start********************/
    //顶点的坐标系
//    private static float TRIANGLE_COORDINATES[] = {
//            //Order of coordinates: X, Y, Z
//            -0.5f, 0.5f, 0.0f, // top left
//            -0.5f, -0.5f, 0.0f, // bottom left
//            0.5f, -0.5f, 0.0f,   // bottom right
//            0.5f, 0.5f, 0.0f,// top right
//    };

    /********3.2.1  添加一个顶点 end********************/

    /********3.3.1  添加一个顶点 start********************/
    //顶点的坐标系
    private static float TRIANGLE_COORDINATES[] = {
            //Order of coordinates: X, Y, Z
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f,// top right
            -0.5f, 0.5f, 0.0f, // top left
    };

    /********3.3.1  添加一个顶点 end********************/

    private int mProgramObjectId;

    //在数组中，一个顶点需要3个来描述其位置，需要3个偏移量
    private static final int COORDINATES_PER_VERTEX = 3;
    private static final int COORDINATES_PER_COLOR = 0;

    //在数组中，描述一个顶点，总共的顶点需要的偏移量。这里因为只有位置顶点，所以和上面的值一样
    private static final int TOTAL_COMPONENT_COUNT = COORDINATES_PER_VERTEX + COORDINATES_PER_COLOR;
    //一个点需要的byte偏移量。
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    // 颜色，rgba  更换颜色
    float TRIANGLE_COLOR[] = {0.5176471f, 0.77254903f, 0.9411765f, 1.0f};


    /********2.1  投影矩阵 添加相应的变量 start********************/
    private float[] mProjectionMatrix = new float[16];
    private int uMatrix;

    /********2.1  投影矩阵 添加相应的变量 end********************/

    public GLRendererSquare3(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //初始化顶点字节缓冲区, 每个浮点数:坐标个数* 4字节
        ByteBuffer bb = ByteBuffer.allocateDirect(TRIANGLE_COORDINATES.length * 4);
        bb.order(ByteOrder.nativeOrder());//使用本机硬件设备的字节顺序
        vertexBuffer = bb.asFloatBuffer();// 从字节缓冲区创建浮点缓冲区
        vertexBuffer.put(TRIANGLE_COORDINATES);// 将坐标添加到FloatBuffer
        vertexBuffer.position(0);//设置缓冲区以读取第一个坐标

        //0.简单的给窗口填充一种颜色
        GLES20.glClearColor(1.0f, 0f, 0f, 0f);//rgba

        //在创建的时候，去创建这些着色器
        //1.根据String进行编译。得到着色器id
        int vertexShaderObjectId = LoadGLUtils.loadShaderAssets(mContext, GLES20.GL_VERTEX_SHADER, "deep2.vert");
        int fragmentShaderObjectId = LoadGLUtils.loadShaderAssets(mContext, GLES20.GL_FRAGMENT_SHADER, "deep2.frag");

        //2.继续处理。取得到program
        mProgramObjectId = GLES20.glCreateProgram();
        //将shaderId绑定到program当中
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);

        //3.最后，启动GL link program
        GLES20.glLinkProgram(mProgramObjectId);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //在窗口改变的时候调用
        GLES20.glViewport(0, 0, width, height);//GL视口


        /******************2.2  设置相应的比例 start**********************************/
        //主要还是长宽进行比例缩放
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            //横屏。需要设置的就是左右。
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1f, -1.f, 1f);
        } else {
            //竖屏。需要设置的就是上下
            Matrix.orthoM(mProjectionMatrix, 0, -1, 1f, -aspectRatio, aspectRatio, -1.f, 1f);
        }
        /******************2.2  设置相应的比例 end **********************************/

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //0.glClear（）的唯一参数表示需要被清除的缓冲区。当前可写的颜色缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //0.先使用这个program?这一步应该可以放到onCreate中进行
        GLES20.glUseProgram(mProgramObjectId);

        //1.根据我们定义的取出定义的位置
        int uPosition = GLES20.glGetAttribLocation(mProgramObjectId, "vPosition");
        //2.开始启用我们的position
        GLES20.glEnableVertexAttribArray(uPosition);


        uMatrix = GLES20.glGetUniformLocation(mProgramObjectId, "u_Matrix");


        //3.将坐标数据放入
        GLES20.glVertexAttribPointer(
                uPosition,  //上面得到的id
                COORDINATES_PER_VERTEX, //告诉他用几个偏移量来描述一个顶点
                GLES20.GL_FLOAT, false,
                STRIDE, //一个顶点需要多少个字节的偏移量
                vertexBuffer);


        /********2.3  mProjectionMatrix矩阵值做相应的传递 start********************/
        //传递给着色器
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, mProjectionMatrix, 0);
        /********2.3  mProjectionMatrix矩阵值做相应的传递 start********************/


        //取出颜色
        int uColor = GLES20.glGetUniformLocation(mProgramObjectId, "vColor");

        //开始绘制
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(
                uColor,
                1,
                TRIANGLE_COLOR,
                0
        );

        //绘制三角形.
        //draw arrays的几种方式
        //




        /********3.1.2  使用GLES20.GL_TRIANGLE_STRIP绘制 start********************/
        //GL_TRIANGLE_STRIP三角形带的方式(开始的3个点描述一个三角形，后面每多一个点，多一个三角形)
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, TRIANGLE_COORDINATES.length / 3);
        /********3.1.2  使用GLES20.GL_TRIANGLE_STRIP绘制 end********************/


        /********3.2.2  使用GLES20.GL_TRIANGLE_FAN绘制 start********************/
        //GL_TRIANGLE_FAN扇形(可以描述圆形)
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, TRIANGLE_COORDINATES.length / 3);
        /********3.2.2  使用GLES20.GL_TRIANGLE_FAN绘制 end********************/

        /********3.3.2  使用GLES20.GL_TRIANGLES绘制 start********************/
        //GL_TRIANGLES三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, TRIANGLE_COORDINATES.length / 3);
        /********3.3.2  使用GLES20.GL_TRIANGLES绘制 end********************/


        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(uPosition);

    }

}
