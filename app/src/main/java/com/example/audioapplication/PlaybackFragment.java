package com.example.audioapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.BIND_AUTO_CREATE;

public class PlaybackFragment extends Fragment {

    public static final String FRAGMENT_TAG = PlaybackFragment.class.getName();
    public static final String KEY_FILE = "KEY_FILE";
    public static final String KEY_POSITION = "KEY_POSITION";
    public static final String ACTION_PLAY = "com.example.audioapplication.PLAY";
    public static final String ACTION_PAUSE = "com.example.audioapplication.PAUSE";

    private final String LOG_TAG = "myLogs";

    private boolean isBound = false;
    private ServiceConnection sConn;
    private Intent intent;
    private MusicService musicService;
    private int duration;
    private int currentPosition;

    private ImageButton buttonPlay;
    private ImageButton buttonPrevious;
    private ImageButton buttonNext;
    private TextView textViewTitle;
    private SeekBar seekBarProgress;

    private List<AudioFile> files;
    private int position;
    private State state;

    public static PlaybackFragment newInstance(List<AudioFile> files, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FILE, (Serializable) files);
        bundle.putInt(KEY_POSITION, position);

        PlaybackFragment fragment = new PlaybackFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_playback, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt(KEY_POSITION);
            files = (List<AudioFile>) bundle.getSerializable(KEY_FILE);
        }

        buttonPlay = view.findViewById(R.id.buttonPlay);
        buttonPrevious = view.findViewById(R.id.buttonPrevious);
        buttonNext = view.findViewById(R.id.buttonNext);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        seekBarProgress = view.findViewById(R.id.seekBarProgress);


        intent = new Intent(getActivity(), MusicService.class);
        intent.setClass(view.getContext(), MusicService.class);
        getActivity().startService(intent);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(LOG_TAG, "PlaybackFragment onServiceConnected");
                musicService = ((MusicService.ServiceBinder) service).getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "PlaybackFragment onServiceDisconnected");
                musicService = null;
                isBound = false;
            }
        };

//        intent.putExtra(KEY_POSITION, files.get(position).getPath());
//        intent.setAction(ACTION_PLAY);
//        getActivity().startService(intent);
        buttonPlay.setImageResource(R.drawable.ic_pause_blue_24dp);
        textViewTitle.setText(files.get(position).getTitle());
        state = State.PLAY;
//        seekBarProgress.setMax(musicService.getMediaPlayerDuration());


        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBound) return;


                Log.d(LOG_TAG, "duration = " + duration);

                if (state.equals(State.PLAY)) {
                    Log.d(LOG_TAG, "state - pause");
                    musicService.pauseMusic();
                    buttonPlay.setImageResource(R.drawable.ic_play_arrow_blue_24dp);
                    state = State.PAUSE;
                } else {
                    Log.d(LOG_TAG, "state - play");
                    musicService.resumeMusic();
                    buttonPlay.setImageResource(R.drawable.ic_pause_blue_24dp);
                    state = State.PLAY;
                }

            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarProgress.setProgress(0);
                //mediaPlayer.seekTo(0);
                //mediaPlayer.pause();
                buttonPlay.setImageResource(R.drawable.ic_play_arrow_blue_24dp);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    mediaPlayer.seekTo(progress);
//
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (state == State.PLAY) {
//                    seekBarProgress.setProgress(musicService.getMediaPlayerCurrentPosition());
////                    if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration() || seekBarProgress.getMax() == seekBarProgress.getProgress()) {
////                        Log.d("myTag", "mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()");
////                        nextTrack();
////
////                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////                    }
//                }
//            }
//        }, 0, 1000);

    }


    @Override
    public void onResume() {
        super.onResume();

        if (musicService != null) {
            musicService.resumeMusic();
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        doUnbindService();
//        getActivity().stopService(intent);
//    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().bindService(intent, sConn, BIND_AUTO_CREATE);
        isBound = true;
    }


    void doUnbindService() {
        if (isBound) {
            getActivity().unbindService(sConn);
            isBound = false;
        }
    }
}
