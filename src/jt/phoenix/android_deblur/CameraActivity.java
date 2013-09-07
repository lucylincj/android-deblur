package jt.phoenix.android_deblur;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class CameraActivity extends Activity implements SurfaceHolder.Callback
{
    public static String TAG = "jt";
    private SurfaceHolder surfaceHolder;
    private Camera myCamera;
    private SurfaceView surfaceView;
    private ImageView imageView;
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback(); 
//    private CamerTimerTask camerTimerTask;
    private Button buttonClick;
    private Boolean mPreviewRunning = false;

    private Timer mTimer = new Timer(true);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera);
        this.setRequestedOrientation(1);
        findControl();
        buttonClick = (Button) findViewById(R.id.buttonClick);
        buttonClick.setOnClickListener( new OnClickListener() {
            public void onClick(View v) {
                //myCamera.takePicture(null, null, jpegCallback); 
                //takePhoto();
                myCamera.autoFocus(mAutoFocusCallback);
//                if (mPreviewRunning) {
//                    myCamera.stopPreview();
//                }
//                Camera.Parameters parameters = myCamera.getParameters();
//                parameters.setFocusMode("auto");
//                myCamera.setParameters(parameters);
//
//                try {
//                    myCamera.setPreviewDisplay(surfaceHolder);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                myCamera.startPreview();
//                mPreviewRunning = true;
                
            }
        });
    }
    
    private File getOutputMediaFile(int type) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                      Environment.DIRECTORY_PICTURES), getPackageName());
        if (!dir.exists()) {
          if (!dir.mkdirs()) {
            Log.e(TAG, "Failed to create storage directory.");
            return null;
          }
        }
        String timeStamp = 
            new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            Log.e(TAG, Environment.getExternalStorageDirectory() + File.separator + "IMG_"  
                    + timeStamp + ".jpg");
          return new File(Environment.getExternalStorageDirectory() + File.separator + "IMG_O_"  
                          + timeStamp + ".jpg");
        } else {
          return null;
        }
      }
    
    protected static final int MEDIA_TYPE_IMAGE = 0; 
    
    public final class AutoFocusCallback implements  
    android.hardware.Camera.AutoFocusCallback {  
        @Override
        public void onAutoFocus(boolean focused, Camera camera) {  
            if (focused) {  
                takePhoto();  
            }  
        }  
    };  
    private void takePhoto() {
        PictureCallback pictureCB = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera cam) {
              File picFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
              if (picFile == null) {
                Log.e(TAG, "Couldn't create media file; check storage permissions?");
                return;
              }
          
              try {
                FileOutputStream fos = new FileOutputStream(picFile);
                fos.write(data);
                fos.close();
              } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found: " + e.getMessage());
                e.getStackTrace();
              } catch (IOException e) {
                Log.e(TAG, "I/O error writing file: " + e.getMessage());
                e.getStackTrace();
              }
            }
          };
      
      myCamera.takePicture(null, null, pictureCB);
//      if (mPreviewRunning) {
//          myCamera.stopPreview();
//      }
//      Camera.Parameters parameters = myCamera.getParameters();
//      parameters.setFocusMode("auto");
//      myCamera.setParameters(parameters);
//
//      try {
//          myCamera.setPreviewDisplay(surfaceHolder);
//      } catch (IOException e) {
//          e.printStackTrace();
//      }
//      myCamera.startPreview();
//      mPreviewRunning = true;
    }

    private void findControl()
    {
        surfaceView = (SurfaceView) findViewById(R.id.cameraSurfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {       
        if (mPreviewRunning) {
            myCamera.stopPreview();
        }
        Camera.Parameters parameters = myCamera.getParameters();
        parameters.setFocusMode("auto");
//        int index = parameters.getMinExposureCompensation();
//        parameters.setExposureCompensation(index);
        myCamera.setParameters(parameters);

        try {
            myCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myCamera.startPreview();
        mPreviewRunning = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        
        try
        {
            myCamera = Camera.open();
            myCamera.setPreviewDisplay(surfaceHolder);
            myCamera.setDisplayOrientation(90);
            //set expoesure
            Camera.Parameters param = myCamera.getParameters();
            param.setFocusMode("auto");
//            int index = param.getMinExposureCompensation();
//            param.setExposureCompensation(index);
            myCamera.setParameters(param);
            mPreviewRunning = true;

        }
        catch (IOException e)
        {
            myCamera.release();
            myCamera = null;
        }

    }
    
    private void initCamera() {
        if (myCamera != null) {  
            Camera.Parameters parameters = myCamera.getParameters();
            parameters.setFocusMode("auto");
            myCamera.setParameters(parameters);
            myCamera.startPreview();
            mPreviewRunning = true;
        } else {
            try
            {
                myCamera = Camera.open();
                myCamera.setPreviewDisplay(surfaceHolder);
                myCamera.setDisplayOrientation(90);

            }
            catch (IOException e)
            {
                myCamera.release();
                myCamera = null;
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        stopCamera();
        myCamera.release();
        myCamera = null;
    }
    
    private void stopCamera() {
        try {
            myCamera.stopPreview();
            mPreviewRunning = false;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    private class CameraPreviewCallback  implements Camera.PreviewCallback
    {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            if (data != null)
            {
                Camera.Parameters parameters = camera.getParameters();
                int imageFormat = parameters.getPreviewFormat();
                Log.i("map", "Image Format: " + imageFormat);

                Log.i("CameraPreviewCallback", "data length:" + data.length);
                if (imageFormat == ImageFormat.NV21)
                {
                    // get full picture
                    
                    Bitmap image = null;
                    int w = parameters.getPreviewSize().width;
                    int h = parameters.getPreviewSize().height;
                      
                    Rect rect = new Rect(0, 0, w, h); 
                    YuvImage img = new YuvImage(data, ImageFormat.NV21, w, h, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                      
                    if (img.compressToJpeg(rect, 100, baos)) 
                    { 
                        image =  BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
                        imageView.setImageBitmap(image);
                    }
            
                }
            }
        }
    }

}
