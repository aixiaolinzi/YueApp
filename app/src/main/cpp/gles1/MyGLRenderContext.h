//
// Created by yzzCool on 2021/4/13.
//

#ifndef YZZCOOL_MYGLRENDERCONTEXT_H
#define YZZCOOL_MYGLRENDERCONTEXT_H

#include "stdint.h"
#include <GLES3/gl3.h>

class MyGLRenderContext {
    MyGLRenderContext();

    ~MyGLRenderContext();

public:
    void SetImageData(int format, int width, int height, uint8_t *pData);

    void SetImageDataWithIndex(int index, int format, int width, int height, uint8_t *pData);

    void SetParamsInt(int paramType, int value0, int value1);

    void SetParamsFloat(int paramType, float value0, float value1);

    void SetParamsShortArr(short *const pShortArr, int arrSize);

    void UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY);

    void OnSurfaceCreated();

    void OnSurfaceChanged(int width, int height);

    void OnDrawFrame();

    static MyGLRenderContext *GetInstance();

    static void DestroyInstance();

private:
    static MyGLRenderContext *m_pContext;

    int m_ScreenW;
    int m_ScreenH;

};


#endif //YZZCOOL_MYGLRENDERCONTEXT_H
