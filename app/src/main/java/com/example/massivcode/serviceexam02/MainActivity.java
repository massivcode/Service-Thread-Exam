package com.example.massivcode.serviceexam02;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by massi on 2015-10-05.
 */
public class MainActivity extends Activity implements View.OnClickListener, CountService.SampleEventListener {

    private Button mStartService, mStopService, mBindService, mUnBindService;
    private TextView mCountNumber;

    private CountService mService;

    private CountService.SampleEventListener mSampleEventListener;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((CountService.LocalBinder)service).getService();
            mService.setOnSampleReceivedEvent(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartService = (Button) findViewById(R.id.start_service);
        mStopService = (Button) findViewById(R.id.stop_service);
        mBindService = (Button) findViewById(R.id.bind_service);
        mUnBindService = (Button) findViewById(R.id.unbind_service);

        mCountNumber = (TextView) findViewById(R.id.countNumber);

        mStartService.setOnClickListener(this);
        mStopService.setOnClickListener(this);
        mBindService.setOnClickListener(this);
        mUnBindService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                Intent startIntent = new Intent(MainActivity.this, CountService.class);
                startService(startIntent);
                break;
            case R.id.stop_service:
                Intent stopIntent = new Intent(MainActivity.this, CountService.class);
                stopService(stopIntent);
                break;
            case R.id.bind_service:
                Intent bindIntent = new Intent(MainActivity.this, CountService.class);
                bindService(bindIntent, mConnection, BIND_AUTO_CREATE);
                break;
            case R.id.unbind_service:
                unbindService(mConnection);
                break;
        }
    }


    @Override
    public void onReceivedEvent(int countNumber) {
        Log.d("test", "" + countNumber);
    }

    @Override
    protected void onDestroy() {
        Intent stopIntent = new Intent(MainActivity.this, CountService.class);
        stopService(stopIntent);
        unbindService(mConnection);
        super.onDestroy();
    }
}
