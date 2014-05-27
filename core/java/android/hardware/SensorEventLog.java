/*Add the log		:	addLog
  Remove from the log	: 	removeLog 
  Contructor		:	EventLog
 */
package android.hardware;
import android.os.*;
import java.net.*;
import java.util.*;
import android.util.Log;
import java.io.*;
import java.util.concurrent.*;
import android.os.ICurrentTime;
import android.content.Context;
public class SensorEventLog extends AsyncTask<String, Void, String> {

	public HashMap <Integer, String> associatedSensors;
	public static final String TAG="SensorEventLog";
	public static final String ADD_LOG="addLog";
	public static final String REMOVE_LOG="removeLog";
	public String message = null;
	private Socket socket = null;
	public boolean loop_end = false;
	public SensorEventLog(){
		associatedSensors = new HashMap<Integer, String>();
		String strSensorFusion = "comapss,gyro";
		String strAllSensors = "comapss,gyro,lightsensor-level,proximity";
		associatedSensors.put(new Integer(1), "compass");
		associatedSensors.put(new Integer(2), "compass");
		associatedSensors.put(new Integer(3), "compass");
		associatedSensors.put(new Integer(4), "gyro");
		associatedSensors.put(new Integer(5), "lightsensor-level");
		//associatedSensors.put(new Integer(6), "pressure");
		//associatedSensors.put(new Integer(7), "temprature");
		associatedSensors.put(new Integer(8), "proximity");
		associatedSensors.put(new Integer(9), strSensorFusion);
		associatedSensors.put(new Integer(10), strSensorFusion);
		associatedSensors.put(new Integer(11), strSensorFusion);
		associatedSensors.put(new Integer(12), strSensorFusion);
		associatedSensors.put(new Integer(-1), strAllSensors);
	}
	public SensorEventLog(String message) {
		// TODO Auto-generated constructor stub
		this.message = message;
		Log.d(TAG,message);
	}

	@Override
	protected String doInBackground(String... m) {
		// TODO Auto-generated method stub
		String[] result = m[0].split(",");
		Log.d(TAG,"result "+result.length);
		int sensorType = Integer.parseInt(result[2]);
		String actualSensors = associatedSensors.get(sensorType);
		message=result[0]+"|"+actualSensors+"|"+result[1];
		
		ICurrentTime time = ICurrentTime.Stub.asInterface(ServiceManager.getService(Context.CURRENT_TIME_SERVICE));
		try {
			Log.d(TAG, "Going to call service;msg= "+message);
			time.sendMessage(message);
			Log.d(TAG, "Service called succesfully.");

		}
		catch (Exception e) {
			Log.d(TAG, "FAILED to call service "+e.toString());

		}
		/*		
		String params;
		DataOutputStream outStream;
	       while(!loop_end){ 			
		try {
			socket = new Socket("localhost", PORT);
			 params = m[0];//msgQ.take();
			params="addLog|compass,proximity|490";
			Log.d(TAG,params);	
			 outStream = new DataOutputStream(socket.getOutputStream());
			outStream.writeBytes(params);
			Log.d(TAG,params);
			outStream.close();
			//socket.close();
			Log.d(TAG,"value sent");
			loop_end=true;
		}
		catch(Exception e)
		{
			loop_end=true;
			if(socket!=null){
				try { 

					socket.close();

				}catch(Exception se){
					Log.d(TAG,"Server not found "+e.toString());	
				}
			}
			Log.d(TAG,"Exception: "+e.toString());
		}
		}//while
		 */
		//this.cancel(true);
		return null;
	}
	@Override
	protected void onPostExecute(String result) {
		Log.d(TAG,"on post execute");
	}


}

