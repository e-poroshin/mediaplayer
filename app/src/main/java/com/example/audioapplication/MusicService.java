package com.example.audioapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class MusicService extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder binder = new ServiceBinder();

    private final String LOG_TAG = "myLogs";
    private final String CHANNEL_ID = "myChannel";
    private final int NOTIFICATION_ID = 101;
    private MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;
    private int path;
    private String title;
    private int pausePosition = 0;

    public MusicService() {
    }

    public class ServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MusicService onCreate");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MusicService onStartCommand");
        path = intent.getIntExtra(PlaybackFragment.KEY_PATH, 0);
        title = intent.getStringExtra(PlaybackFragment.KEY_TITLE);

        startMusic();
        showNotification();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "MusicService onBind");
        return binder;
    }


    public void pauseMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pausePosition = mediaPlayer.getCurrentPosition();
            }
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(pausePosition);
                mediaPlayer.start();
            }
        }
    }

    public void startMusic() {
        mediaPlayer = MediaPlayer.create(this, path);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {
                onError(mediaPlayer, what, extra);
                return true;
            }
        });

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(50, 50);
            mediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "MusicService onDestroy");
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
//        notificationManager.cancelAll();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(LOG_TAG, "MusicService onError");
        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } finally {
                mediaPlayer = null;
            }
        }
        return false;
    }

    public int getMediaPlayerCurrentPosition() {
        int currentPosition = 0;
        if (mediaPlayer != null) {
            currentPosition = mediaPlayer.getCurrentPosition();
        }
        return currentPosition;
    }

    public int getMediaPlayerDuration() {
        int duration = 0;
        if (mediaPlayer != null) {
            duration = mediaPlayer.getDuration();
        }
        return duration;
    }

    public void seekMediaPlayerTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    public void showNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("MyChannelDescription");
            channel.enableVibration(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent playbackIntent = new Intent(getApplicationContext(), PlaybackFragment.class);
        PendingIntent playbackPendingIntent = PendingIntent.getActivity(this, 0, playbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_audiotrack_blue_24dp)
                .setContentTitle("Audio player")
                .setContentText(title)
                .setContentIntent(playbackPendingIntent);

        Notification notification = builder.build();

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

}
