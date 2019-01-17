package com.lj.jpct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressLint("NewApi") public class MyRender  implements GLSurfaceView.Renderer {

	private long time = System.nanoTime();  
	private FrameBuffer fb = null;
	private Light sun = null;
	private Object3D cube = null;
	private World world = null;
	private int fps = 0;
	private Object3D rockModel;
	private Object3D chongLou;
	private Object3D mdModel;
	// TODO: 2019/1/14 添加指南针模型
	private Object3D mZhi;
	private Context mContext;

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private Texture[] mTextures;//多张纹理贴图的

	// 行走动画  
	private int an = 2;  
	private float ind = 0;  

	public MyRender(Context c) {
		mContext = c;
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {

		Log.e("TAG","onSurfaceChanged");

		if (fb != null) {
			fb.dispose();
		}
		fb = new FrameBuffer(gl, w, h);
		GLES20.glViewport(0, 0, w, h);

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.e("TAG","onSurfaceCreated");
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		world = new World();

		world.setAmbientLight(150, 150, 150);


		//Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.rock)), 64, 64));
		//Texture texture2 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.texture2)), 64, 64));
		//Texture texture3 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.ogrobase)), 64, 64));
		//Texture texture4=new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.t)),64,64));

		//TextureManager.getInstance().addTexture("texture", texture);
		//TextureManager.getInstance().addTexture("texture2", texture2);
		//TextureManager.getInstance().addTexture("texture3", texture3);
		//TextureManager.getInstance().addTexture("texture4",texture4);


		 cube = Primitives.getCube(10);
		 cube.calcTextureWrapSpherical();
		 //cube.setTexture("texture");
		 cube.strip();
		 cube.build();

		/**
		 * rockModel = load3dsModel("rock.3ds", 1);
		 rockModel.setTexture("texture");
		 rockModel.strip();
		 rockModel.build();
		 rockModel.translate(0, 5, 0);

		 chongLou = load3dsModel("hu.3ds", 1.5f);
		 chongLou.setTexture("texture2");
		 chongLou.strip();
		 chongLou.build();

		 mdModel = loadMd2Model("ogro.md2", 0.25f);
		 mdModel.setTexture("texture3");
		 mdModel.strip();
		 mdModel.build();
		 mdModel.translate(-2, 0, 0);
		 */

		mZhi=loadObjModel("1.obj","1.mtl",0.1f);
		//mZhi.setTexture("texture4");
		mZhi.strip();
		mZhi.build();

		//System.out.println(mdModel.getAnimationSequence().getName(1));

		//world.addObject(rockModel);
		//world.addObject(chongLou);
		//world.addObject(mdModel);
		world.addObject(mZhi);

		sun = new Light(world);
		sun.setIntensity(250, 250, 250);

		Camera cam = world.getCamera();
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
		cam.lookAt(cube.getTransformedCenter());

		SimpleVector sv = new SimpleVector();
		sv.set(cube.getTransformedCenter());
		sv.y -= 100;
		sv.z -= 100;
		sun.setPosition(sv);
		MemoryHelper.compact();
	}

	public void onDrawFrame(GL10 gl) {

		Log.e("TAG","onDrawFrame");

		// Clears the screen and depth buffer.  
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.  
				GL10.GL_DEPTH_BUFFER_BIT); 
		//doAnim();
		fb.clear(RGBColor.BLACK);
		world.renderScene(fb);
		world.draw(fb);
		fb.display();


		 if (touchTurn != 0) {
		 //rockModel.rotateY(touchTurn);
		 //chongLou.rotateY(touchTurn);
		// mdModel.rotateY(touchTurn);
		 mZhi.rotateY(touchTurn);
		 touchTurn = 0;
		 }


		 if (touchTurnUp != 0) {
		 //rockModel.rotateX(touchTurnUp);
		 //chongLou.rotateX(touchTurnUp);
		// mdModel.rotateX(touchTurnUp);
		 mZhi.rotateX(touchTurnUp);
		 touchTurnUp = 0;
		 }



		if (System.nanoTime() - time >= 1000000000) {
			Logger.log(fps + "fps");
			Log.e("FPSCounter", "fps: " + fps);

			//System.out.println(fps+"fps");
			fps = 0;
			time = System.nanoTime() ;
		}
		//
		fps++;
	}



	public static int loadShader(int type, String shaderCode){  

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)  
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)  
		int shader = GLES20.glCreateShader(type);  

		// add the source code to the shader and compile it  
		GLES20.glShaderSource(shader, shaderCode);  
		GLES20.glCompileShader(shader);  

		return shader;  
	}  

	public Object3D load3dsModel(String filename, float scale){
		InputStream is = null;
		try {
			is =mContext.getAssets().open(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object3D[] model = Loader.load3DS(is, scale);
		Object3D o3d = new Object3D(0);
		Object3D temp = null;
		for (int i = 0; i < model.length; i++) {
			temp = model[i];
			temp.setCenter(SimpleVector.ORIGIN);
			temp.rotateX((float)( -.5*Math.PI));
			temp.rotateMesh();
			temp.setRotationMatrix(new Matrix());
			o3d = Object3D.mergeObjects(o3d, temp);
			o3d.build();
		}
		return o3d;	
	}
	public Object3D loadObjModel(String objName,String mtlName,float scale){
		InputStream objIs=null;
		InputStream mtlIs=null;
		try {
			objIs=mContext.getAssets().open(objName);
			mtlIs=mContext.getAssets().open(mtlName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Object3D[] model=Loader.loadOBJ(objIs,mtlIs,scale);
        Object3D obj=new Object3D(0);
		Object3D temp = null;
		for (int i = 0; i < model.length; i++) {
			temp = model[i];
			temp.setCenter(SimpleVector.ORIGIN);
			temp.rotateX((float)( -.5*Math.PI));
			temp.rotateMesh();
			temp.setRotationMatrix(new Matrix());
			obj = Object3D.mergeObjects(obj, temp);
			obj.build();
		}
		return obj;
	}

	public Object3D loadMd2Model(String filename, float scale)
	{
		InputStream is = null;
		try {
			is =mContext.getAssets().open(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object3D model = Loader.loadMD2(is, scale);  
		return model;  

	}

	public void doAnim() {  
		//每一帧加0.018f  
		ind += 0.018f;  
		if (ind > 1f) {  
		ind -= 1f;
		}  
		mdModel.animate(ind, an); 
	}
	
	public void setTouchTurn(float count)
	{
		touchTurn = count;
	}

	public void setTouchTurnUp(float count)
	{
		touchTurnUp = count;
	}

	public void cleanUp(){

		TextureManager.getInstance().removeTexture("texture");
		TextureManager.getInstance().removeTexture("texture4");
	}
}
