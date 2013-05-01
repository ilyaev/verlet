package pbartz.examples.verlet;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class VerletSurface extends SurfaceView implements Runnable, SensorEventListener  {

	SurfaceHolder sHolder;
	Thread sThread = null;
	boolean isRunning = false;
	
	float startX;
	float startY;
    
    private final static int 	MAX_FPS = 60;
    private final static int	MAX_FRAME_SKIPS = 5;
    private final static int	FRAME_PERIOD = 1000 / MAX_FPS;	
    
    private SensorManager mSensorManager;
    private float[] mAccelerometerReading;
    private float[] mMagneticFieldReading;
    private float[] mRotationMatrix = new float[16];
    private float[] mRemapedRotationMatrix = new float[16];
    public float[] mOrientation = new float[3];
    
    VerletTest verletTest;

	public VerletSurface(Context context) {
		super(context);
		sHolder = getHolder();		
		
		mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

		initialize();
	}
	
	private void initialize() {
		verletTest = new VerletTest(this);		
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		
		long beginTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped 

		sleepTime = 0;
		
		while (isRunning) {
			if (!sHolder.getSurface().isValid()) {
				continue;
			}

			Canvas canvas = null;
			
			try {			
				canvas = sHolder.lockCanvas();
				
				beginTime = System.currentTimeMillis();
				framesSkipped = 0;	
				
				// update data
				updateGameState(canvas);
				
				// draw data				
				synchronized (sHolder) {
					drawGameState(canvas);
				}
				
				// sleep stuff
				timeDiff = System.currentTimeMillis() - beginTime;
				sleepTime = (int)(FRAME_PERIOD - timeDiff);
				
				if (sleepTime > 0) {
					try {
						sThread.sleep(sleepTime);
					} catch (InterruptedException e) {}
				}

				while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
					updateGameState(canvas);
					sleepTime += FRAME_PERIOD;
					framesSkipped++;
				}		
				
			} finally {
				if (canvas != null) {
					sHolder.unlockCanvasAndPost(canvas);
				}
			}		
		}
	}
	
	private void drawGameState(Canvas canvas) {
		canvas.drawRGB(0, 0, 0);
		verletTest.draw(canvas);
	}

	public void updateGameState(Canvas canvas) {
		verletTest.TimeStep();
	}

	public void pause() {
		isRunning = false;
		mSensorManager.unregisterListener(this);	
		
		while (true) {
			try {
				sThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}

		sThread = null;
	}

	public void resume() {
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

		isRunning = true;
		sThread = new Thread(this);
		sThread.start();
	}

	public void processTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			startX = event.getX();
			startY = event.getY();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			float endX = event.getX();
			float endY = event.getY();
			
			//verlet.rebuild(startX, startY, endX, endY);
			verletTest.rebuild(startX, startY, endX, endY);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType())		{
			case Sensor.TYPE_ACCELEROMETER: {
				mAccelerometerReading = event.values.clone();
				break;
			}
			case Sensor.TYPE_MAGNETIC_FIELD: {
				mMagneticFieldReading = event.values.clone();
				break;
			}		
		}
		
		if (mAccelerometerReading != null && mMagneticFieldReading != null && SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagneticFieldReading))	{
			SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRemapedRotationMatrix);
			SensorManager.getOrientation(mRemapedRotationMatrix, mOrientation);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}
	
}
