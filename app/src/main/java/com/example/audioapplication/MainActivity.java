package com.example.audioapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayListFragment.OnSelectedTrackListener {

    private List<AudioFile> files;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, PlayListFragment.newInstance(), PlayListFragment.FRAGMENT_TAG)
                .commit();
    }


    @Override
    public void selectTrack(List<AudioFile> files, int position) {
        PlaybackFragment playbackFragment = PlaybackFragment.newInstance(files, position);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, playbackFragment, PlaybackFragment.FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }
}
