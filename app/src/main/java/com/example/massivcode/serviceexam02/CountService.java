package com.example.massivcode.serviceexam02;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by massi on 2015-10-05.
 */
public class CountService extends Service {

    private static final String TAG = CountService.class.getSimpleName() ;
    private Binder mBinder;

    private Thread mThread;

    private int mCountNumber = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(mCountNumber < 101) {


                mCountNumber++;
                Log.d(TAG, "mCountNumber : " + mCountNumber);

                    if(mSampleEventListener != null) {
                        mSampleEventListener.onReceivedEvent(mCountNumber);
                    }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            }
        });

        mThread.start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mThread.interrupt();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new LocalBinder();
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        CountService getService() {
            return CountService.this;
        }
    }

    public int getCountNumber() {
        return mCountNumber;
    }

    public interface SampleEventListener{
        void onReceivedEvent(int countNumber);
    }

    private SampleEventListener mSampleEventListener;

    public void setOnSampleReceivedEvent(SampleEventListener listener){
        mSampleEventListener = listener;
    }
}

/*
*  액티비티와 서비스를 바인드해서 1초마다 1씩 증가하는 int mCountNumber을 반환받아서
*  액티비티의 텍스트 뷰에 지정해야 함.
*
*  mCountNumber는 서비스에 존재하며, 텍스트뷰는 액티비티에 존재하는데.
*  mCountNumber의 수치가 변화할 때마다 액티비티에 그 값을 알려줄 수는 없을까?
*
*  방법 1. 액티비티에서 작업스레드를 만들고, Thread.sleep(1000)을 주고
*           서비스의 getCountNumber 메소드를 호출하여 그 값을 지정한다.
*           => 뭔가 허접해보임. 그리고 mCountNumber가 0.5초에 1씩 증가하면? 양쪽 다 바꿔?
*
*  방법 2. 커스텀 리스너를 만들고, mCountNumber가 증가하는 스레드 내부에서 이벤트를 1초에 한번씩 발생시킨다.
*           액티비티에서 리스너를 연결한 후, mCountNumber를 텍스트 뷰에 지정한다.
*           => Log.d로 액티비티에서 출력할 때에는 분명 문제없이 잘 받아오지만, textView.setText 하는 순간부터 에러가 난다.
*           UI의 변경은 UI 스레드 에서만 가능하다는데, 그럼 어떻게 해야 할까.
*
*           1) // UI Thread 로 동작하게 해 주는 Activity 제공 메소드
                 runOnUiThread()
            2) ASyncTask
            3) Thread&Handler
                final Handler handler = new Handler() {
                 @Override
                 public void handleMessage(Message msg) {
                mNumberTextView1.setText("" + msg.arg1);
                 }
              };
*
*
*
* */