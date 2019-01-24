package com.lj.jpct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

	private List<String> mTextureNames;//多张纹理贴图的
	private String mObjPath;//模型路径

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

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		world = new World();
		// 设置了环境光源强度。负:整个场景会变暗;正:将照亮了一切。
		world.setAmbientLight(150, 150, 150);

		// 在World中创建一个新的光源
		sun = new Light(world);
		sun.setIntensity(250, 250, 250);


		Texture texture4=new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.t)),64,64));

		TextureManager.getInstance().addTexture("texture4",texture4);

		//3D对象
		mZhi=loadObjModel("1.obj","1.mtl",0.1f);
		mZhi.setTexture("texture4");
		mZhi.strip();
		mZhi.build();
		//将Object3D对象添加到world集合
		world.addObject(mZhi);

		Camera cam = world.getCamera();
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);// 以50有速度向后移动Camera（相对于目前的方向）
		cam.lookAt(mZhi.getTransformedCenter());//返回对象的中心--object3D.getTransformedCenter()

		SimpleVector sv = new SimpleVector();//三维矢量的基础类
		sv.set(mZhi.getTransformedCenter());
		sv.y -= 100;//Y方向上减去100
		sv.z -= 100;//Z方向上减去100
		sun.setPosition(sv);//设置光源位置

		MemoryHelper.compact();
		// 强制GC和finalization工作来试图去释放一些内存，同时将当时的内存写入日志，
		// 这样可以避免动画不连贯的情况，然而，它仅仅是减少这种情况发生的机率
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.e("TAG","onSurfaceCreated");
		/**
		 * gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		 world = new World();

		 world.setAmbientLight(150, 150, 150);

		 Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.rock)), 64, 64));
		 //Texture texture2 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.texture2)), 64, 64));
		 //Texture texture3 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.ogrobase)), 64, 64));
		 Texture texture4=new Texture(BitmapHelper.rescale(BitmapHelper.convert(mContext.getResources().getDrawable(R.drawable.t)),64,64));

		 TextureManager.getInstance().addTexture("texture", texture);
		 //TextureManager.getInstance().addTexture("texture2", texture2);
		 //TextureManager.getInstance().addTexture("texture3", texture3);
		 TextureManager.getInstance().addTexture("texture4",texture4);


		 cube = Primitives.getCube(10);
		 cube.calcTextureWrapSpherical();
		 cube.setTexture("texture");
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
		 **/
        /**
		mZhi=loadObjModel("1.obj","1.mtl",0.1f);
		mZhi.setTexture("texture4");
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
		 */
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

	/**
	 * 解析OBJ模型 得到MTL文件
	 * @param objPath
	 * @return
	 */
	private String readMtlName(String objPath){
		mObjPath=objPath;
		String mtlName=null;
		File objFile=new File(objPath);
		try {
			InputStream inputStream=new FileInputStream(objFile);
		    Log.e("TAG","inputStream:"+(inputStream==null?"null":inputStream));
		    if (inputStream!=null){
				InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
				BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
				String line;
				//分行读取
				while ((line=bufferedReader.readLine())!=null){
					 Log.e("TAG","line:"+line);
					 int idx=line.indexOf("mtllib");
					 if (idx>0){
					 	mtlName=line.substring(idx+7);
					 	break;
					 }else if (line.startsWith("v")){//顶点

						 break;
					 }
				}
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mtlName;
	}

	/**
	 * 解析mtl文件，得到texture文件
	 * @param mtlPath
	 * @return
	 */
    private int parseTextureNames(String mtlPath){

		try {
			File mtlFile=new File(mtlPath);
			String textureName=null;
			InputStream inputStream=new FileInputStream(mtlFile);
			Log.e("TAG","inputStream--"+(inputStream==null?"null":inputStream));
			if (inputStream!=null){
				InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
				BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
				String line;
				//分行读取
				while ((line=bufferedReader.readLine())!=null){
                         Log.e("TAG","line--"+line);
                         int idx=line.indexOf("map_Kd");
                         if (idx>0){
							 textureName=line.substring(idx+7);
                              if (!(TextureManager.getInstance().containsTexture(textureName))){
                                   TextureManager.getInstance().addTexture(textureName);
                                   mTextureNames.add(textureName);
                              }
						 } else if ((idx=line.indexOf("map_Ka"))>=0){
                              	     textureName=line.substring(idx+7);
                              	     if (!TextureManager.getInstance().containsTexture(textureName)){
										 TextureManager.getInstance().addTexture(textureName);
										 mTextureNames.add(textureName);
									 }
							  }
				}
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return TextureManager.getInstance().getTextureCount();
	}

	/**
	 * 加载所有纹理
	 */
	private void loadTextures(){

		TextureManager tm=TextureManager.getInstance();
		for (int i=0;i<mTextureNames.size();i++){
			 String name=mTextureNames.get(i);
		     Log.e("TAG","texure name"+name);
		     if (tm.containsTexture(name)){
		     	tm.removeAndUnload(name,fb);
			 }
			 else {
		     	int idx=mObjPath.lastIndexOf('/');

		     	String tPath=mObjPath.substring(0,idx+1)+name;

		     	Log.e("TAG","texure path:"+tPath);

		     	try {
					Bitmap bmp = BitmapFactory.decodeFile(tPath);
					Bitmap inputBmp = bmp;
					int w = bmp.getWidth();
					if ((w & (w - 1)) != 0) { //w不是2的n次幂,需要特殊处理

						inputBmp = scaleBitmap(bmp);
					}

					Texture texture = new Texture(inputBmp);
					Log.e("TAG", "纹理添加成功");
					tm.addTexture(name, texture);
				}catch (Exception e){

                     e.printStackTrace();
				}
			 }
		}
	}

	public Bitmap scaleBitmap(Bitmap bitmap){
        int w=bitmap.getWidth();
        int h=bitmap.getHeight();
        int destW=1024;
        int destH=h*destW/w;

        Bitmap newBm=Bitmap.createScaledBitmap(bitmap,destW,destH,true);

        return newBm;
	}
}
