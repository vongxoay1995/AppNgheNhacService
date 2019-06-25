package com.example.appnghenhackhongservice.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.example.appnghenhackhongservice.model.BaiHat;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service
{
    public static final String CURRENT_POSITION = "position";
    MediaPlayer mediaPlayer;
    private List<BaiHat> listBaiHat;
    private int position;
    private IBinder iBinder = new BindService();
    Intent intent;
    Intent intentStartCommand;
    LocalBroadcastManager localBroadcastManager;
    Thread thread;
    BaiHat baiHat;

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        listBaiHat = new ArrayList<>();
    }

    public MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        intentStartCommand = intent;
        startMusic();
/*        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            int a = 1;
            @Override
            public void run()
            {
                Log.d("start ", "value " + a++);
            }
        }, 0, 1000);*/
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return iBinder;
    }

    public void startMusic()
    {
        //Log.d("----","Soure " + listBaiHat.get(position).getData());
/*        listBaiHat = intentStartCommand.getParcelableArrayListExtra(MusicAdapter.LISTBAIHAT);
        position = intentStartCommand.getIntExtra(MusicAdapter.POSITION, -1);*/
        //baiHat = intent.getParcelableExtra(MusicAdapter.BAIHAT);
        //if(baiHat != null)
        if (listBaiHat != null && listBaiHat.size() > 0)
        {
            //Log.d("---","Soure " + listBaiHat.get(position).getData());
            mediaPlayer = new MediaPlayer();
            try
            {
                if (position != -1)
                {
                    mediaPlayer.setDataSource(listBaiHat.get(position).getData());
                    mediaPlayer.prepare();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            mediaPlayer.start();
            updateUI();
        }
    }

    public void updateUI()
    {
        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (mediaPlayer != null && mediaPlayer.isPlaying())
                {
                    try
                    {
                        guiDuLieu();
                        Thread.sleep(1000);
                        //Log.d("ThreadName ", thread.getName());
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void guiDuLieu()
    {
        intent = new Intent(CURRENT_POSITION);
        intent.putExtra(CURRENT_POSITION, mediaPlayer.getCurrentPosition());
        //Log.d("Value ", mediaPlayer.getCurrentPosition() + "");
        localBroadcastManager.sendBroadcast(intent);
    }

    public void xuLyNext(int position)
    {
        this.position = position;
        mediaPlayer.stop();
        mediaPlayer.release();
        startMusic();
    }

    public void xuLyPrev(int position)
    {
        this.position = position;
        mediaPlayer.stop();
        mediaPlayer.release();
        startMusic();
    }

    public void pause()
    {
        mediaPlayer.pause();
    }

    public boolean isPlaying()
    {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void seekTo(int msec)
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.seekTo(msec);
        }
    }

    public void play()
    {
        mediaPlayer.start();
    }

    public class BindService extends Binder
    {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }

    public List<BaiHat> getListBaiHat()
    {
        return listBaiHat;
    }

    public void setListBaiHat(List<BaiHat> listBaiHat)
    {
        this.listBaiHat = listBaiHat;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        //Toast.makeText(getApplicationContext(), "onUnbind", Toast.LENGTH_SHORT).show();
        // giải phóng tài nguyên khi nó không được liên kết
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(), "Service is destroyed", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}