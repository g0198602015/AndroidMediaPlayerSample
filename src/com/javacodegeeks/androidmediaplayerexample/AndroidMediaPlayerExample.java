package com.javacodegeeks.androidmediaplayerexample;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class AndroidMediaPlayerExample extends Activity {

	private MediaPlayer mediaPlayer;
	public TextView songName, duration;
	private double timeElapsed = 0, finalTime = 0;
	private int forwardTime = 2000, backwardTime = 2000;
	private Handler durationHandler = new Handler();
	private SeekBar seekbar;
	private AudioManager audioManager;
    private Button normalButn;
    private Button vibrateButn;
    private Button silentButn;
    private Button upButn;
    private Button downButn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//set the layout of the Activity
		setContentView(R.layout.activity_main);

		//initialize views
		initializeViews();
		normalButn = (Button) findViewById(R.id.normalButn);
		vibrateButn = (Button) findViewById(R.id.vibrateButn);
		silentButn = (Button) findViewById(R.id.silentButn);
		upButn = (Button) findViewById(R.id.upButn);
		downButn = (Button) findViewById(R.id.downButn);

		// 取得音量控制器
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// 正常模式
		normalButn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			}
		});

		// 震動模式
		vibrateButn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
		});

		// 靜音模式
		silentButn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				new Thread(new Runnable(){
				    @Override
				    public void run() {

						
						try 
						{
							audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
							Thread.sleep(2000);

							audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }            
				}).start();
				
					//durationHandler.postDelayed(modifyVoiceManagerVolume, 100);
			}
		});

		// 增大音量
		upButn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
			}
		});

		// 減少音量
		downButn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				audioManager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
			}
		});
	}
	
	public void initializeViews(){
		songName = (TextView) findViewById(R.id.songName);
		mediaPlayer = MediaPlayer.create(this, R.raw.sample_song);
		finalTime = mediaPlayer.getDuration();
		duration = (TextView) findViewById(R.id.songDuration);
		seekbar = (SeekBar) findViewById(R.id.seekBar);
		songName.setText("Sample_Song.mp3");
		
		seekbar.setMax((int) finalTime);
		seekbar.setClickable(false);
	}
	// play mp3 song
	public void play(View view) {
		mediaPlayer.start();
		timeElapsed = mediaPlayer.getCurrentPosition();
		seekbar.setProgress((int) timeElapsed);
		durationHandler.postDelayed(updateSeekBarTime, 100);
	}

	//handler to change seekBarTime
	private Runnable updateSeekBarTime = new Runnable() {
		public void run() {
			//get current position
			timeElapsed = mediaPlayer.getCurrentPosition();
			//set seekbar progress
			seekbar.setProgress((int) timeElapsed);
			//set time remaing
			double timeRemaining = finalTime - timeElapsed;
			duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
			
			//repeat yourself that again in 100 miliseconds
			durationHandler.postDelayed(this, 100);
		}
	};
	//handler to change seekBarTime
	private Runnable modifyVoiceManagerVolume = new Runnable() {
		public void run() {

			
			try {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				wait(2000);
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};
		
	// pause mp3 song
	public void pause(View view) {
		mediaPlayer.pause();
	}

	// go forward at forwardTime seconds
	public void forward(View view) {
		//check if we can go forward at forwardTime seconds before song endes
		if ((timeElapsed + forwardTime) <= finalTime) {
			timeElapsed = timeElapsed + forwardTime;

			//seek to the exact second of the track
			mediaPlayer.seekTo((int) timeElapsed);
		}
	}

	// go backwards at backwardTime seconds
	public void rewind(View view) {
		//check if we can go back at backwardTime seconds after song starts
		if ((timeElapsed - backwardTime) > 0) {
			timeElapsed = timeElapsed - backwardTime;
			
			//seek to the exact second of the track
			mediaPlayer.seekTo((int) timeElapsed);
		}
	}

}