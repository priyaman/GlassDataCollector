package priyaman.com.glasscamera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



/**
 * Created by priya_000 on 5/21/2015.
 */
public class SensorController implements SensorEventListener {

    private Context context;
    // Sensor manager
    private SensorManager mSensorManager = null;

    // Motion sensors
    private Sensor mSensorAccelerometer = null;
    private Sensor mSensorGravity = null;
    private Sensor mSensorLinearAcceleration = null;
    private Sensor mSensorGyroscope = null;
    private Sensor mSensorRotationVector = null;
    String mediaStorageDir = null;

    private long lastTimeStamp;

    private void initializeSensorManager()
    {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }


    public SensorController(Context context){
            Log.d("onServiceStart() called.");
            this.context = context;

            initializeSensorManager();
            mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL);

            mediaStorageDir = CameraHelper.getOutputMediaFile(3).toString();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.v("onSensorChanged() called.");

        processMotionSensorData(event);
        if(StaticConstants.logSensorValues){
            StringBuffer strBuf = new StringBuffer();
            if(StaticConstants.lastSensorValuesRotationVector != null) {
                strBuf.append("Timestamp:" + lastTimeStamp + "\t");
            }
            if(StaticConstants.lastSensorValuesAccelerometer != null) {
                strBuf.append("Accelerometer:" + StaticConstants.lastSensorValuesAccelerometer.toString() + "\t");
            }
            if(StaticConstants.lastSensorValuesGravity != null) {
                strBuf.append("Gravity:" + StaticConstants.lastSensorValuesGravity.toString() + "\t");
            }
            if(StaticConstants.lastSensorValuesLinearAcceleration != null) {
                strBuf.append("Linear Acceleration:" + StaticConstants.lastSensorValuesLinearAcceleration.toString() + "\t");
            }
            if(StaticConstants.lastSensorValuesGyroscope != null) {
                strBuf.append("Gyroscope:" + StaticConstants.lastSensorValuesGyroscope.toString() + "\t");
            }
            if(StaticConstants.lastSensorValuesRotationVector != null) {
                strBuf.append("Rotation Vector:" + StaticConstants.lastSensorValuesRotationVector.toString() + "\t");
            }

            strBuf.append("\n");
            if(StaticConstants.firstLine){
                writeToFile("VideoStartTime:" + lastTimeStamp + "\n");
                StaticConstants.firstLine = false;
            }
            writeToFile(strBuf.toString());
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("onAccuracyChanged() called.");
    }

    private void processMotionSensorData(SensorEvent event)
    {
        long now = System.currentTimeMillis();

        Sensor sensor = event.sensor;
        int type = sensor.getType();
        long timestamp = event.timestamp;
        float[] values = event.values;
        int accuracy = event.accuracy;
        SensorValueStruct data = new SensorValueStruct(type, timestamp, values, accuracy);
        lastTimeStamp = timestamp;
        switch(type) {
            case Sensor.TYPE_ACCELEROMETER:
                StaticConstants.lastSensorValuesAccelerometer = data;
                break;
            case Sensor.TYPE_GRAVITY:
                StaticConstants.lastSensorValuesGravity = data;
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                StaticConstants.lastSensorValuesLinearAcceleration = data;
                break;
            case Sensor.TYPE_GYROSCOPE:
                StaticConstants.lastSensorValuesGyroscope = data;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                StaticConstants.lastSensorValuesRotationVector = data;
                break;
            default:
                Log.w("Unknown type: " + type);
        }



    }

    private void writeToFile(String data) {
        try {
            //OutputStreamWriter outputStreamWriter = new FileOutputStream(new File(mediaStorageDir),true);//new OutputStreamWriter(context.openFileOutput(mediaStorageDir, Context.MODE_PRIVATE));
            FileOutputStream outputStreamWriter = new FileOutputStream(new File(mediaStorageDir),true);//new OutputStreamWriter(context.openFileOutput(mediaStorageDir, Context.MODE_PRIVATE));
            outputStreamWriter.write(data.getBytes());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception" + "File write failed: " + e.toString());
        }
    }

}
