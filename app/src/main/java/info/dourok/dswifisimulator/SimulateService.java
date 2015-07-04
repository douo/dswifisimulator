package info.dourok.dswifisimulator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SimulateService extends Service {

    private final SimulateServiceBinder mBinder = new SimulateServiceBinder();
    private NextTimeGenerator timeGenerator;
    private UrlStore mUrlStore;
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;

    private String mUrl;
    private long mTime;
    private Callback mCallback;

    public interface Callback {
        void onNewQuery(String url, long now);
    }

    public class SimulateServiceBinder extends Binder {
        public SimulateService getService() {
            return SimulateService.this;
        }
    }


    public SimulateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        play();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("SimulateService");
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        timeGenerator = new NextTimeGenerator();
        mUrlStore = new UrlStore();
        mUrlStore.addUrl(new UrlObj("http://www.baidu.com", 1));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mServiceLooper.quit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        play();
        return super.onStartCommand(intent, flags, startId);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getTime() {
        return mTime;
    }

    private void play() {
        String url = mUrlStore.nextUrl();
        long time = timeGenerator.nextMoment();
        Message msg = Message.obtain(mServiceHandler, 0x1, url);
        mServiceHandler.sendMessageAtTime(msg, time);
        mUrl = url;
        mTime = time;
        if (mCallback != null) {
            mCallback.onNewQuery(mUrl, mTime);
        }
    }

    private byte[] BUFFER = new byte[1024 * 8];

    private void fetch(String url) {
        BufferedInputStream in = null;
        try {
            System.out.println("fetch:" + url);
            URL _url = new URL(url);
            URLConnection urlConnection = _url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            while (in.read(BUFFER) > 0) ;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            String url = (String) msg.obj;
            fetch(url);
            play();
        }
    }
}
