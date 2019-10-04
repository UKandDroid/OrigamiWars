#include <jni.h>
#include <android/log.h>
#include "GLES/gl.h"

#define LOG(...)  __android_log_print(ANDROID_LOG_INFO, "NDK_Debug", __VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

static GLfloat vertices[] = {0,0,0,	1,0,0,	1,1,0,	0,1,0};
static GLfloat texture[]  = {0,0,  1,0,  1,1,  0,1};
static GLbyte   indices[] = {0,1,2,	0,2,3};


JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_testMethod    (JNIEnv * env, jclass cls) 			{ LOG("Test Method called");}
JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_pushMatrix    (JNIEnv * env, jclass cls) 			{ glPushMatrix(); }
JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_loadIdentity  (JNIEnv * env, jclass cls) 			{ glLoadIdentity(); }
JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_matrixMode    (JNIEnv * env, jclass cls, jint mode) { glMatrixMode(mode); }
JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_translate  	  (JNIEnv * env, jclass cls, jfloat x, jfloat y, jfloat z)  			{ glTranslatef(x,y,z); }
JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_scale  	  	  (JNIEnv * env, jclass cls, jfloat x, jfloat y, jfloat z) 				{ glScalef(x,y,z); }
JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_rotate  	  (JNIEnv * env, jclass cls, jfloat angle, jfloat x, jfloat y, jfloat z){ glRotatef(angle, x,y,z); }
JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_color  	  	  (JNIEnv * env, jclass cls, jfloat r, jfloat g, jfloat b, jfloat alpha){ glColor4f(r,g,b,alpha); }


JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_transform__FF    (JNIEnv * env, jclass cls, jfloat transX, jfloat transY) 	{
			glMatrixMode(GL_MODELVIEW);
			glTranslatef(transX, transY, 0);
}

JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_transform__FFFF    (JNIEnv * env, jclass cls, jfloat scaleX, jfloat scaleY, jfloat transX, jfloat transY) 	{
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glScalef(scaleX, scaleY, 1);
			glTranslatef(transX, transY, 0);
}

JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_transform__FFFFF    (JNIEnv * env, jclass cls, jfloat scaleX, jfloat scaleY, jfloat transX, jfloat transY, jfloat rotateZ) 	{
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glScalef(scaleX, scaleY, 1);
			glTranslatef(transX, transY, 0);
			glTranslatef(0.5f, 0.5f, 0);
			glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);
			glTranslatef(-0.5f, -0.5f, 0);
}


JNIEXPORT void JNICALL Java_com_BreakthroughGames_OrigamiWars_GLWrapper_draw    (JNIEnv * env, jclass cls, jfloat transX, jfloat transY, jint iTexture, jfloat Opacity, jfloat txtStartX, jfloat txtStartY, jfloat txtEndX, jfloat txtEndY) 	{

		texture[0] = txtStartX;
		texture[1] = txtStartY;
		texture[2] = txtEndX;
		texture[3] = txtStartY;
		texture[4] = txtEndX;
		texture[5] = txtEndY;
		texture[6] = txtStartX;
		texture[7] = txtEndY;

		glMatrixMode(GL_TEXTURE);
		glLoadIdentity();
		glTranslatef(transX, transY, 0);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 2);
		glBindTexture(GL_TEXTURE_2D, iTexture);
		glColor4f(Opacity, Opacity, Opacity, Opacity);

		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(3, GL_FLOAT, 0, vertices);
		glTexCoordPointer(2, GL_FLOAT, 0, texture);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, indices);
}


#ifdef __cplusplus
}
#endif

