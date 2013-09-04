package jt.phoenix.android_deblur;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    private TextView v0;
    private TextView v1;
    private TextView v2;
    private TextView orien_v0;
    private TextView orien_v1;
    private TextView orien_v2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.GRAY);
        v0 = (TextView) findViewById(R.id.values0);
        v1 = (TextView) findViewById(R.id.values1);
        v2 = (TextView) findViewById(R.id.values2);
        orien_v0 = (TextView) findViewById(R.id.orien_values0);
        orien_v1 = (TextView) findViewById(R.id.orien_values1);
        orien_v2 = (TextView) findViewById(R.id.orien_values2);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }
    
    @Override
    protected void onResume() {
      super.onResume();
      // register this class as a listener for the orientation and
      // accelerometer sensors
      sensorManager.registerListener(this,
          sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
          SensorManager.SENSOR_DELAY_NORMAL);
      sensorManager.registerListener(this,
              sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
              SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
      // unregister listener
      super.onPause();
      sensorManager.unregisterListener(this);
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
          }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            getOrientation(event);
        }
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
