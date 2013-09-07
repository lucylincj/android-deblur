package jt.phoenix.android_deblur;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;
    
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    
    private long lastUpdate;
    private TextView v0;
    private TextView v1;
    private TextView v2;
    private TextView orien_v0;
    private TextView orien_v1;
    private TextView orien_v2;
    private TextView gyro_v0;
    private TextView gyro_v1;
    private TextView gyro_v2;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v0 = (TextView) findViewById(R.id.values0);
        v1 = (TextView) findViewById(R.id.values1);
        v2 = (TextView) findViewById(R.id.values2);
        orien_v0 = (TextView) findViewById(R.id.orien_values0);
        orien_v1 = (TextView) findViewById(R.id.orien_values1);
        orien_v2 = (TextView) findViewById(R.id.orien_values2);
        gyro_v0 = (TextView) findViewById(R.id.gyro_values0);
        gyro_v1 = (TextView) findViewById(R.id.gyro_values1);
        gyro_v2 = (TextView) findViewById(R.id.gyro_values2);
        mButton = (Button) findViewById(R.id.button1);
        
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        lastUpdate = System.currentTimeMillis();
    }
    
    @Override
    protected void onResume() {
      super.onResume();
      // register this class as a listener for the orientation and
      // accelerometer sensors
      mLastAccelerometerSet = false;
      mLastMagnetometerSet = false;
      mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
      mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
      // unregister listener
      super.onPause();
      mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            getAccelerometer(event);
//          }
//        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
//            getOrientation(event);
//        }
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
            setAccelerometer(event.values);
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        } 
        if (event.sensor == mGyroscope) {
            setGyroscope(event);
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            setOrientation(mOrientation);
//            Log.i("OrientationTestActivity", String.format("Orientation: %f, %f, %f",
//                                                           mOrientation[0], mOrientation[1], mOrientation[2]));
        }
    }
    
    private void setAccelerometer(float values[]) {
        v0.setText(String.valueOf( (float) Math.round(100*values[0])/100  ));
        v1.setText(String.valueOf( (float) Math.round(100*values[1])/100  ));
        v2.setText(String.valueOf( (float) Math.round(100*values[2])/100  ));
    }
    
    private void setOrientation(float values[]) {
        orien_v0.setText(String.valueOf( (float) Math.round(100*values[0])/100  ));
        orien_v1.setText(String.valueOf( (float) Math.round(100*values[1])/100  ));
        orien_v2.setText(String.valueOf( (float) Math.round(100*values[2])/100  ));
    }
    
    private void setGyroscope(SensorEvent event) {

        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
          final float dT = (event.timestamp - timestamp) * NS2S;
          // Axis of the rotation sample, not normalized yet.
          float axisX = event.values[0];
          float axisY = event.values[1];
          float axisZ = event.values[2];
          
        gyro_v0.setText(String.valueOf( (float) Math.round(100*axisX)/100  ));
        gyro_v1.setText(String.valueOf( (float) Math.round(100*axisY)/100  ));
        gyro_v2.setText(String.valueOf( (float) Math.round(100*axisZ)/100  ));

          // Calculate the angular speed of the sample
          float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

          // Normalize the rotation vector if it's big enough to get the axis
          // (that is, EPSILON should represent your maximum allowable margin of error)
          if (omegaMagnitude > 0.001) {
            axisX /= omegaMagnitude;
            axisY /= omegaMagnitude;
            axisZ /= omegaMagnitude;
          }

          // Integrate around this axis with the angular speed by the timestep
          // in order to get a delta rotation from this sample over the timestep
          // We will convert this axis-angle representation of the delta rotation
          // into a quaternion before turning it into the rotation matrix.
          float thetaOverTwo = omegaMagnitude * dT / 2.0f;
          float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
          float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
//          deltaRotationVector[0] = sinThetaOverTwo * axisX;
//          deltaRotationVector[1] = sinThetaOverTwo * axisY;
//          deltaRotationVector[2] = sinThetaOverTwo * axisZ;
//          deltaRotationVector[3] = cosThetaOverTwo;
//        
        }
        timestamp = event.timestamp;
    }
    
    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        
        v0.setText(String.valueOf( (float) Math.round(100*x)/100  ));
        v1.setText(String.valueOf( (float) Math.round(100*y)/100  ));
        v2.setText(String.valueOf( (float) Math.round(100*z)/100  ));

      }
    private void getOrientation(SensorEvent event) {
        float[] values = event.values;
        
        float x = values[0];
        float y = values[1];
        float z = values[2];
        
        orien_v0.setText(String.valueOf( (float) Math.round(100*x)/100  ));
        orien_v1.setText(String.valueOf( (float) Math.round(100*y)/100  ));
        orien_v2.setText(String.valueOf( (float) Math.round(100*z)/100  ));
    }
   

}
