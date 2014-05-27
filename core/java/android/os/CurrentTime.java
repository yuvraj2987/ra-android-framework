package android.os;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.ICurrentTime;
import android.util.Log;
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrentTime extends ICurrentTime.Stub{
	/*
	 * Stores the information about pid and to which sensors it has registered or unregistered.
	 * Needs a ConcurrentHashMap because it will be read and written in different threads
	 * Integer to String mapping is not possible because aidl doesn't support Integer
	 */
	public static ConcurrentHashMap <String, String> log_pid_sensors = new ConcurrentHashMap<String, String>();
	
	public CurrentTime(Context context) {
		// TODO Auto-generated constructor stub
		super();
	}
	public CurrentTime(){
	super();
	}
	public String getTime(){
		log_pid_sensors.put("21", "demoSensor");//Just to check whether I am able to retrieve the value from app
		Calendar c = Calendar.getInstance();
		Log.d("CSE622:ASST4","Inside android.os.CurrentTime class. Returning time.");
		return c.getTime().toString();
	}
	/*
	 * Returns the ConcurrentHashMap which has pid to sensors mapping
	 * @return ConcurrentHashMap
	 */
	public Map<String, String> getPidSensorMap(){
		Log.d("CSE622:ASST4","getPidSensorMap" + log_pid_sensors.toString());
		return log_pid_sensors;
	}
	
	/*
	 * 1. Creates a new thread which will populate log_pid_sensors
	 * 2. Creates a new thread and sends message to the C side over the socket.
	 * @param message
	 */
	public void sendMessage(String message){
		/* Starting new thread to populate 
		 * Actually there is no need to create new thread.
		 * If this code works correctly, please try to remove SavePidSensorMapping thread and
		 * move that code into one function and run it.
		 */
		new SavePidSensorMapping().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,message);
		
		//new SendMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
	 
		/* This code didn't work, that's why we moved this code in separate thread
		 * try{
			String TAG="SENSOREVENTLOG_SERVICE";
			int PORT=6666;
			Socket socket = new Socket("localhost", PORT);
			String params = message;//m[0];//msgQ.take();
			//params=message;//"addLog|compass,proximity|490";
			Log.d(TAG,params);	
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
			outStream.writeBytes(params);
			Log.d(TAG,params);
			outStream.close();
			socket.close();
			Log.d(TAG,"value sent");
			
			
		}catch(Exception e){
			String TAG="SENSOREVENTLOG_SERVICE";
			Log.d(TAG,"exception" + e.toString());
		}*/
	}
	/*
	 * Saves pid-sensor mapping in log_pid_sensors
	 * @author satish
	 */
	private class SavePidSensorMapping extends  AsyncTask<String, Void, String> {
		public SavePidSensorMapping(){}
		@Override
		protected String doInBackground(String... m) {
			String TAG="SENSOREVENTLOG_SERVICE";
			int PORT=6666;
			String params = m[0];
			/*
			 * Format of params:
			 * "addLog|compass,proximity|490";
			 */
			String[] allParams = params.split("\\|");
			/*
			 * For example:
			 * allParams[0] : addLog
			 * allParams[1]	: compass,proximity
			 * allParams[2]	: 490
			 */
			Log.d(TAG,"MAP: " +params);	
			Log.d(TAG,"allParams[0]: " + allParams[0]);	
			Log.d(TAG,"allParams[1]: " + allParams[1]);	
			Log.d(TAG,"allParams[2]: " + allParams[2]);	
			if( allParams[0].equals("addLog") ){
				log_pid_sensors.put(allParams[2],allParams[1]);
			}
			else if(allParams[0].equals("removeLog")){
				// App is unregistering with the sensor(s)
				log_pid_sensors.remove(allParams[2]);
			}
			
			//ending the thread
			//this.cancel(true); 
			return null;

		}
		@Override
		protected void onPostExecute(String result) {
			String TAG="SENSOREVENTLOG_SERVICE";
			Log.d(TAG,"on post execute of SavePidSensorMapping");
		}
	}
	
	/*
	 * Sends values to C side through socket
	 */
	private class SendMessageTask extends  AsyncTask<String, Void, String> {
		public SendMessageTask(){
			
		}
		@Override
		protected String doInBackground(String... m) {
			try{
				String TAG="SENSOREVENTLOG_SENDMESSAGE_SERVICE";
				int PORT=6666;
        Log.d(TAG, "Create socket");
				Socket socket = new Socket("localhost", PORT);
				String params = m[0];//m[0];//msgQ.take();
				//params=message;//"addLog|compass,proximity|490";
				Log.d(TAG,params);
        Log.d(TAG, "Create data output stream");
				DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
        Log.d(TAG, "Wite Bytes");
				outStream.writeBytes(params);
				Log.d(TAG,params);
				outStream.close();
				socket.close();
				Log.d(TAG,"value sent");
				
			}catch(Exception e){
				String TAG="SENSOREVENTLOG_SERVICE";
				Log.d(TAG,"exception" + e.toString());
			}
			
			this.cancel(true);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			String TAG="SENSOREVENTLOG_SERVICE";
			Log.d(TAG,"on post execute");
		}

	}
}
