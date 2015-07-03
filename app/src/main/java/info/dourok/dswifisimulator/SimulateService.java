package info.dourok.dswifisimulator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SimulateService extends Service {

    private final SimulateServiceBinder mBinder = new SimulateServiceBinder();
    private NextTimeGenerator timeGenerator;
    private UrlStore mUrlStore;

    public class SimulateServiceBinder extends Binder {
        public SimulateService getService() {
            return SimulateService.this;
        }
    }


    public SimulateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timeGenerator = new NextTimeGenerator();
        mUrlStore = new UrlStore();
    }
}
