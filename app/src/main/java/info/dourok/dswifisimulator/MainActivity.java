package info.dourok.dswifisimulator;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SimulateService.Callback {
    TextView mCount;
    ServiceConnection mConnection;
    SimulateService mService;
    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCount = (TextView) findViewById(R.id.count);
        mBtn = (Button) findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    stopService();
                } else {
                    startService();
                }
            }
        });
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBtn.setText("Stop");
                mService = ((SimulateService.SimulateServiceBinder) service).getService();
                mService.setCallback(MainActivity.this);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBtn.setText("Start");
                mService.setCallback(null);
                mService = null;
                handler.removeMessages(1);
            }
        };
    }



    @Override
    protected void onDestroy() {
        handler.removeMessages(1);
        unbindService(mConnection);
        super.onDestroy();
    }

    private void startService() {
        Intent i = new Intent(this, SimulateService.class);
        startService(i);
        Intent bind = new Intent(this, SimulateService.class);
        bindService(bind, mConnection, BIND_AUTO_CREATE);
    }

    private void stopService() {
        System.out.println("stopService");
        mService.stopSelf();
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshTime(long time) {
        if (time > 0) {
            mCount.setText(time / 1000 / 60 + ":" + (time / 1000) % 60);
            handler.sendEmptyMessageDelayed(1, 100);
        }
    }

    Handler handler = new RefreshHandler(this);

    static class RefreshHandler extends Handler {
        private MainActivity mActivity;

        RefreshHandler(MainActivity activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            long time = mActivity.mService.getTime() - SystemClock.uptimeMillis();
            mActivity.refreshTime(time);
        }

    }

    @Override
    public void onNewQuery(String url, long now) {
        handler.sendEmptyMessage(1);
    }
}
