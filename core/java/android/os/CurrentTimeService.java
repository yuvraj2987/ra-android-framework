package android.os;

import android.os.IBinder;
import android.os.ICurrentTime;
import android.os.RemoteException;
import android.util.Log;
import java.util.Map;

public class CurrentTimeService {
  private static final String TAG = "CurrentTimeService";
  private final ICurrentTime mCurrentTime;
  private static CurrentTimeService currentTimeService;

  public static synchronized CurrentTimeService getCurrentTimeService() {
    if (currentTimeService == null) {
      IBinder binder = android.os.ServiceManager.getService("current_time_service");

      if (binder != null) {
        ICurrentTime ct = ICurrentTime.Stub.asInterface(binder);
        currentTimeService = new CurrentTimeService(ct);
      } else {
        Log.e(TAG, "CurrentTime binder is null");
      }
    }

    return currentTimeService;
  }

  CurrentTimeService(ICurrentTime ct) {
    if (ct == null) {
      throw new IllegalArgumentException("CurrentTime service is null");
    }

    mCurrentTime = ct;
  }

  public String getTime() throws RemoteException {
    return mCurrentTime.getTime();
  }

  public void sendMessage(String message) throws RemoteException {
    mCurrentTime.sendMessage(message);
  }

  public Map<String, String> getPidSensorMap() throws RemoteException {
    return mCurrentTime.getPidSensorMap();
  }

  public ICurrentTime getCurrentTime() {
    return mCurrentTime;
  }
}
