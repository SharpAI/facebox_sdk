package com.sharpaiexample;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;


public class TrafficLightRenderer implements Renderer {
	/*Asset*/
	AssetManager am = null;
	
	//TrafficLight object
	private TrafficLight trafficLight = null;
	private MyTimer timer = null;

	public TrafficLight GetTL() { return trafficLight; }
	/*Store our model data in a float buffer*/
	private final FloatBuffer mTriangle1Vertices;
	
	private float[] mViewMatrix = new float[16];

	/*How many bytes per float*/
	private final int mBytesPerFloat = 4;

	//This will be used to pass in the transformation matrix.
	private int mMVPMatrixHandle;
	//This will be used to pass in model position information.
	private int mPositionHandle;
	//This will be used to pass in model color information.
	//private int mColorHandle;
	
	//Texture handles
	private int mTextureDataHandle_R = 0;
	private int mTextureDataHandle_Y = 0;
	private int mTextureDataHandle_G = 0;
	private int mTextureDataHandle_RY = 0;
	private int mTextureDataHandle_0 = 0;
	//This will be used to pass in the texture.
	private int mTextureUniformHandle = 0;
	//This will be used to pass in model texture coordinate information.
	private int mTextureCoordinateHandle = 0;
	
	
	//Program Handle
	private int mProgramHandle = 0;
	
	
	//Store the projection matrix. This is used to project the scene onto a 2D viewport.
	private float[] mProjectionMatrix = new float[16];
	
	//Store the model matrix. This matrix is used to move models from object space (where each model can be thought of being located at the center of the universe) to world space.
	private float[] mModelMatrix = new float[16];
	
	//Allocate storage for the final combined matrix. This will be passed into the shader program.
	private float[] mMVPMatrix = new float[16];
	
	//How many elements per vertex
	private final int mStrideBytes = (3+2) * mBytesPerFloat;
	
	//Offset of the position date
	private final int mPositionOffset = 0;
	
	//Size of the position data in elements.
	private final int mPositionDataSize = 3;
	
	//Offset of the color data.
	//private final int mColorOffset = 3*0;
	
	//Size of the color data in elements.
	//private final int mColorDataSize = 4*0;
	
	//Offset of the texture coordinate
	private final int mTexCoordOffset = 3;
	
	//Size of the texture coord data in elements.
	private final int mTexCoordDataSize = 2;
	
	public TrafficLightRenderer(AssetManager _am) {
		am = _am;
		trafficLight = new TrafficLight();
		trafficLight.SetOff();
		timer = new MyTimer();
		timer.Reset();
		
		final float oneh = 1.15f;
		final float onew = 1.00f;
		
		final float[] triangle1VerticesData = {
				//X,Y,Z,  U,V
				-onew, -oneh, 0.0f,		0.0f, 1.0f,
				-onew,  oneh, 0.0f,		0.0f, 0.0f,
				 onew, -oneh, 0.0f,		1.0f, 1.0f,
				 onew,  oneh, 0.0f,		1.0f, 0.0f
		};
		mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangle1Vertices.put(triangle1VerticesData).position(0);
	}
	
	private int LoadShader(final String shaderSource, final int shaderType)
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);
		
		if (shaderHandle != 0)
		{
			//Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);
			
			//Compile the shader.
			GLES20.glCompileShader(shaderHandle);
			
			//Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			
			//If the compilation failed, delete the shader.
			if (compileStatus[0] == 0)
			{
				Log.e("gles", GLES20.glGetShaderInfoLog(shaderHandle));
				
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
			throw new RuntimeException("Error creating vertex shader.");
		
		return shaderHandle;
	}
	
	private int LoadShaderFromFile(final String fileName, final int shaderType)
	{
		String shaderSource = "";
		
		InputStream is = null;
		try {
			is = am.open(fileName);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
			shaderSource = new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return LoadShader(shaderSource, shaderType);
	}
	
	private int LoadTexture(final String fileName)
	{
		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);
		
		if (textureHandle[0] != 0)
		{
			//final BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inScaled = false; //No pre-scaling

			InputStream is = null;
			
			try {
				is = am.open(fileName);

				//Read in the resource
				final Bitmap bitmap = BitmapFactory.decodeStream(is);
				
				//Bind to the texture in OpenGL
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
				
				//Set filtering
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				
				//Load the bitmap into the bound texture.
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				
				//Recycle the bitmap, since its data has been loaded into OpenGL.
				bitmap.recycle();
				
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (textureHandle[0] == 0)
		{
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];		
	}
	
	@Override
	public void onSurfaceCreated(GL10 notUsed, EGLConfig config) {
		//Set the background color to gray.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		//Position the eye behind the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 1.5f;
		
		//We are looking toward the distance
		final float centerX = 0.0f;
		final float centerY = 0.0f;
		final float centerZ = -0.5f;
		
		//Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;
		
		//Set the view matrix. This matrix can be said to represent the camera position.
		//NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		
		//Load in the vertex shader.
		int vertexShaderHandle = 0;
		int fragmentShaderHandle = 0;
		try {
			vertexShaderHandle = LoadShaderFromFile("vertexShader.vs", GLES20.GL_VERTEX_SHADER);
			fragmentShaderHandle = LoadShaderFromFile("fragmentShader.fs", GLES20.GL_FRAGMENT_SHADER);
		} catch (RuntimeException e) {
			throw e;
		}
		
		//Create a program object and store the handle to it.
		mProgramHandle = GLES20.glCreateProgram();
		
		if (mProgramHandle != 0)
		{
			//Bind the vertex shader to the program.
			GLES20.glAttachShader(mProgramHandle, vertexShaderHandle);
			//Bind the fragment shader to the program.
			GLES20.glAttachShader(mProgramHandle, fragmentShaderHandle);
			
			//Bind attributes
			GLES20.glBindAttribLocation(mProgramHandle, 0, "a_Position");
			//GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
			GLES20.glBindAttribLocation(mProgramHandle, 1, "a_TexCoordinate");
			
			//Link the two shaders together into a program.
			GLES20.glLinkProgram(mProgramHandle);
			
			//Get the link status
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(mProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			
			//If the link failed, delete the program.
			if (linkStatus[0] == 0)
			{
				GLES20.glDeleteProgram(mProgramHandle);
				mProgramHandle = 0;
			}
		}
		
		if (mProgramHandle == 0)
			throw new RuntimeException("Error creating program");

		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		//Load textures
		mTextureDataHandle_R = 0;
		mTextureDataHandle_Y = 0;
		mTextureDataHandle_G = 0;
		mTextureDataHandle_RY = 0;
		mTextureDataHandle_0 = 0;
		try {
			mTextureDataHandle_R = LoadTexture("textures/tl100.png");
			mTextureDataHandle_Y = LoadTexture("textures/tl010.png");
			mTextureDataHandle_G = LoadTexture("textures/tl001.png");
			mTextureDataHandle_RY = LoadTexture("textures/tl110.png");
			mTextureDataHandle_0 = LoadTexture("textures/tl000.png");
		} catch (RuntimeException e) {
			throw e;
		}
	}
	

	@Override
	public void onSurfaceChanged(GL10 notUsed, int width, int height) {
		//Set the OpenGL viewport to the same size as the surface
		GLES20.glViewport(0, 0, width, height);
		
		//Create a new perspective projection matrix. The height will stay the same while the width will vary as per aspect ratio.
		final float ratio = (float)width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public void onDrawFrame(GL10 notUsed) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
		//Do a complete rotation every 10 seconds.
		//long time = SystemClock.uptimeMillis() % 10000L;
		//float angleInDegrees = (360.0f / 10000.0f) * (int)time;
		
		//Draw the triangle facing straight on.
		Matrix.setIdentityM(mModelMatrix, 0);
		//Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
		
		//Tell OpenGL to use this program when rendering.
		GLES20.glUseProgram(mProgramHandle);

		// Set program handles. These will later be used to pass in values to the program.
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
		//mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
		mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

		
		//Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		//Bind the texture to this unit.
		int textureHandle = 0;
		
		timer.Actualize();
		trafficLight.Run(timer.getDelta());
		
		switch (trafficLight.GetActiveColor())
		{
		case C_RED: textureHandle = mTextureDataHandle_R; break;
		case C_REDYELLOW: textureHandle = mTextureDataHandle_RY; break;
		case C_GREEN: textureHandle = mTextureDataHandle_G; break;
		case C_YELLOW: textureHandle = mTextureDataHandle_Y; break;
		case C_OFF: textureHandle = mTextureDataHandle_0; break;
		default:
			break;
		}
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		
		//Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		GLES20.glUniform1i(mTextureUniformHandle, 0);
		drawTriangle(mTriangle1Vertices);
	}
	
	/**
	 * Draws a triangle from a given vertex data.
	 * 
	 * @param aTriangleBUffer The buffer containing the vertex data.
	 */
	private void drawTriangle(final FloatBuffer aTriangleBuffer)
	{
		//Pass in the position information
		aTriangleBuffer.position(mPositionOffset);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		//Pass in the color information
//		aTriangleBuffer.position(mColorOffset);
//		GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
//		GLES20.glEnableVertexAttribArray(mColorHandle);

		//Pass in the color information
		aTriangleBuffer.position(mTexCoordOffset);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTexCoordDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

		
		//This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix (which currently contains model*view)
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		
		//This multiplies the modelview by the projection matrix, and stores the result in the MVP matrix (which now contains model*view*projection).
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	}

	
}
