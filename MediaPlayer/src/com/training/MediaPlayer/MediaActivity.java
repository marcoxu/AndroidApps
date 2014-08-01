package com.training.MediaPlayer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.*;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MediaActivity extends Activity {
	// Tag that we will use for logging
	private final static String TAG = "MediaPlayer";
    // flag to turn on/off debug messages from this class
    private static final boolean DEBUG = true;
    // Links to Java instances of our UI widgets in our Activity
    // Buttons
    private ImageButton playButton;
    private ImageButton preButton;
    private ImageButton nextButton;
    private ImageButton stopButton;
    private ImageButton modButton;
    // Text view to display play progress
    private TextView playDuration;
    // Text view to display total duration
    private TextView totalDuration;
    // Song name founded in folder
    private TextView songName;
    // progress bar to show current position
    private ProgressBar songProgress;
    // flag to check is player in pause state
    private boolean isPaused;
    // interface to our service
    public IMediaPlayer player;
    // flag to specify is service bound or not
    private boolean isBound = false;
    // our broadcast receiver event handler
    private PlayerEventReceiver eventReceiver = new PlayerEventReceiver();
    // message IDs that we will send to Handler that is responsible for UI update
    private static final int MSG_DURATION = 0;
    private static final int MSG_POSITION = 1;
    private static final int MSG_COMPLETE = 2;
    private static final int MSG_SHOWINFO = 3;
    private static final int MESSAGE_LOADINFO = 4;
    private static final int MESSAGE_UPDATEDURATION = 5;
    private static final int MSG_DOWN = 6;
    private static final int MSG_SHOWLYRICS = 7;
    private static final int MESSAGE_UPDATEINDEX = 8;
    
    private static final String TAB_INFO = "INFO";
    private static final String TAB_LYRICS = "LYRICS";
    
    private static String fileIsPlaying;
    @SuppressLint("SdCardPath")
	private ActionBar mTabBar = null;
    private LrcFragment mFeed;
    private MusicInfoFragment mFriend;
    private Handler mLRCHandler = new Handler();
    private Thread mLRCThread = null;
    private Thread mDurationThread = null;
    private Context mContext;
    private int mLRCIndex = 0;
    public static int mWidth;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set main.xml as layout for our activity
        setContentView(R.layout.main);
        
        this.setTitle("");
        mContext = this;
        
        // Install the music files first
        copyMusics();

        // get the width of the display
        DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mWidth = metric.widthPixels;

        // get links to widgets. Use findViewById method to find exact widget by its id
        playButton = (ImageButton)findViewById(R.id.play_button);
        nextButton = (ImageButton)findViewById(R.id.next_button);
        preButton = (ImageButton)findViewById(R.id.previous_button);
        stopButton = (ImageButton)findViewById(R.id.stop_button);
        modButton = (ImageButton)findViewById(R.id.playmode_button);
        
        // work around: Scale the stop button since the size of icon is same as others
        Bitmap origBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.is_player_stop_default);
        Matrix matrix = new Matrix();
        int width = origBitmap.getWidth();
        int height = origBitmap.getHeight();
        // resize the Bitmap from 56*56 to 48*48
        float scaleRate = ((float)48/(float)width);
        matrix.postScale(scaleRate, scaleRate);
        // scale the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(origBitmap, 0, 0, width,
        		height, matrix, true);
        stopButton.setImageBitmap(resizedBitmap);
        
        origBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_player_mode_all_pressed);
        matrix = new Matrix();
        width = origBitmap.getWidth();
        height = origBitmap.getHeight();
        // resize the Bitmap from 42*42 to 48*48
        scaleRate = ((float)48/(float)width);
        matrix.postScale(scaleRate, scaleRate);
        // scale the new Bitmap
        resizedBitmap = Bitmap.createBitmap(origBitmap, 0, 0, width,
        		height, matrix, true);
        modButton.setImageBitmap(resizedBitmap);
        
    	playDuration = (TextView)findViewById(R.id.media_folder);
    	totalDuration = (TextView)findViewById(R.id.folder_name);
        songName = (TextView)findViewById(R.id.song_name);
        songProgress = (ProgressBar)findViewById(R.id.song_progress);
        isPaused = true;
        // Add the click listener for play button
        playButton.setOnClickListener(new ButtonListener());
        // Add onClickListener for previous button
        preButton.setOnClickListener(new ButtonListener());
        // add onClickListener to next button
        nextButton.setOnClickListener(new ButtonListener());
        // add onClickListener to stop button
        stopButton.setOnClickListener(new ButtonListener());

        mTabBar = getActionBar();
        mTabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mFeed = new LrcFragment();
		mTabBar.addTab(mTabBar.newTab().setText(TAB_LYRICS)
				.setTabListener(new TabListener(mFeed, handler)));

		mFriend = new MusicInfoFragment();
		mTabBar.addTab(mTabBar.newTab().setText(TAB_INFO)
				.setTabListener(new TabListener(mFriend, handler)));
        
    }
    

    private void startTextViewAnimation(TextView v, String text) {
    	v.setText(text);
        // Start animation
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
        TranslateAnimation anim = new TranslateAnimation(-width/2, width,0, 0); 
        v.setAnimation(anim);
        anim.setDuration(5000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.startNow();
    }
    
    private void stopTextViewAnimation(TextView v) {
        if(v.getAnimation() != null) {
            v.clearAnimation();
        }
    }

    private long parseTime() {
    	String current = playDuration.getText().toString();
    	String minute = current.substring(0, current.indexOf(":"));
    	String second = current.substring(current.indexOf(":")+1, current.length());
        long iCurrent = Integer.parseInt(minute)*60 + Integer.parseInt(second);
        log("iCurrent " +iCurrent);
        return iCurrent;
    }
    
    private void handlePlayClick(View v) {
    	ImageButton button = (ImageButton) v;
    	if(!isPaused) {
    		log("Pause media");
    	} else {
    		log("Play media");
    	}

        if (isBound) {
        	if(!isPaused){
                try {
                    player.pause();
                    isPaused = true;
                    button.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play_default));
                    stopTextViewAnimation(songName);
                    songName.setText(fileIsPlaying);
                    long current = parseTime();
                	displayDuration(Long.parseLong(MusicLRCHandler.mInfo.get("DurationValue")), current);
                } catch (RemoteException e) {
                    Log.e(TAG, "Remote player service died");
                }
        	} else {
                
                // if paused, just resume
                if (fileIsPlaying != null) {
                    try {
                        player.resume();
                        isPaused = false;
                        button.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_pause_default));
                        startTextViewAnimation(songName, fileIsPlaying);
                        long current = parseTime();
                    	displayDuration(Long.parseLong(MusicLRCHandler.mInfo.get("DurationValue")), current);
    					showMusiLyrics(handler);
                    } catch (RemoteException e) {
                    	log("Remote player service died");
                    }
                } else {
                	// otherwise, start playing and update song name in UI
                	String playMusic = null;
					try {
						playMusic = player.findMusic(null, 0);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
            		playMusic(playMusic);
                }
        	}
        }
    }
    
    private void handleStopClick() {
        log("Stop playing media");

        if (isBound) {
            try {
                player.stop();
                isPaused = false;
            } catch (RemoteException e) {
            	log("Remote player service died");
            }
        }
        resetToDefault();
    }
    
    private void resetToDefault() {
        isPaused = true;
        fileIsPlaying = null;
        
        stopTextViewAnimation(songName);
        songName.setText(R.string.no_song);
        playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play_default));
		mFriend.showMedia(mContext, fileIsPlaying);
		mFeed.mLRCView.initMusicLRC(fileIsPlaying);
		displayLRC(0);
       	playDuration.setText("00:00");
    	totalDuration.setText("--:--");
        songProgress.setProgress(0);
        songProgress.setMax(0);
    }
    
    private void handleNextClick() {
        log("Start to play next media");

        if (isBound) {
			try {
				String playMusic = player.findMusic(fileIsPlaying, 0);
	    		playMusic(playMusic);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
        }
    }

    private void handlePrevClick(View v) {
        log("Start to play prev media");

        if (isBound) {
			try {
				String playMusic = player.findMusic(fileIsPlaying, 1);
	    		playMusic(playMusic);
			} catch (RemoteException e) {
				log("Remote player service failed");
			}
        }
    }

    private void playMusic(String music) {
    	if(music == null) {
    		return;
    	}
    	
        try {
            startTextViewAnimation(songName, new File(music).getName());
            
            String lrc = null;
			try {
				lrc = player.findLrcFileName(music);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

            if(lrc != null) {
            	if(mFeed.mLRCView != null){
            		mFeed.mLRCView.initMusicLRC(lrc);
            	} else {
            		log("mFeed.mLRCView is null");
            	}
            }
            isPaused = false;
    		displayLRC(0);
            player.play(music);
            fileIsPlaying = music;
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_pause_default));
            
            // start to load music info
            Message msg = handler.obtainMessage(MESSAGE_LOADINFO, music);
            handler.sendMessage(msg);
            showMusiInfo(handler);
      } catch (RemoteException e) {
    	    log("Remote player service died");
      }
    }
    
    class ButtonListener implements OnClickListener, OnTouchListener{

        public boolean onTouch(View v, MotionEvent event) {  
            switch(v.getId()){
            case R.id.previous_button:
                if(event.getAction() == MotionEvent.ACTION_UP){  
                    preButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_prev_default));
                }   
                if(event.getAction() == MotionEvent.ACTION_DOWN){  
                    preButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_prev_pressed));
                }  
                break;
            case R.id.play_button:
                if(event.getAction() == MotionEvent.ACTION_UP){  
                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play_default));
                }   
                if(event.getAction() == MotionEvent.ACTION_DOWN){  
                    playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play_pressed));
                }  
                break;
            case R.id.next_button:
                if(event.getAction() == MotionEvent.ACTION_UP){  
                    nextButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_next_default));
                }   
                if(event.getAction() == MotionEvent.ACTION_DOWN){  
                    nextButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_next_pressed));
                }  
                break;
            case R.id.stop_button:
                if(event.getAction() == MotionEvent.ACTION_UP){  
                    stopButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_next_default));
                }   
                if(event.getAction() == MotionEvent.ACTION_DOWN){  
                    stopButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_next_pressed));
                }  
                break;
            }  
            return false;  
        }  

		@Override
		public void onClick(View v) {
            switch(v.getId()){
            case R.id.previous_button:
            	handlePrevClick(v);  
                break;
            case R.id.play_button:
            	handlePlayClick(v);  
                break;
            case R.id.next_button:
            	handleNextClick(); 
                break;
            case R.id.stop_button:
            	handleStopClick();
            	break;
            }  
			
		}
    }
    
    // Override onStart method in Activity to registry our broadcast receiver
    @Override
    protected void onStart() {
        super.onStart();
        // Create IntentFilter with action from our service
        IntentFilter filter = new IntentFilter(MediaService.PLAY_ACTION);
        // Register our class responsible for broadcast receiver events handling.
        registerReceiver(eventReceiver, filter);
        // Now bind to the service. To do so, create explicit Intent
        Intent serviceIntent = new Intent(this, MediaService.class);
        if (!isBound) {
        	// if we are not already bound, bind to the service
            bindService(serviceIntent, playerConnection, BIND_AUTO_CREATE);
        }
    }
    // override onStop method of Activity to unregister our broadcast receiver and unbind from service  
    @Override
    protected void onStop() {
        super.onStop();
        log("MediaActivity onStop");
        // unregister our broadcast receiver
        unregisterReceiver(eventReceiver);
        if (isBound) {
        	// unbind from our service
        	try {
				player.stop();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
            unbindService(playerConnection);
            isBound = false;
        }
        this.resetToDefault();
    }

    // Service connection implementation to handle events from bind/unbind state changes in service
    private ServiceConnection playerConnection = new ServiceConnection() {
    	// service is bound
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        	// get the interface to our service
            player = IMediaPlayer.Stub.asInterface(service);
            isBound = true;
            log("onServiceConnected");
        }
        // service is unbound
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            player = null;
        }
    };
    // implementation of our broadcast receiver handler
    private class PlayerEventReceiver extends BroadcastReceiver {
    	// broadcast event received
        @Override
        public void onReceive(Context context, Intent intent) {
        	log("PlayerEventReceiver onReceive");

            // take the media file duration value from event, second parameter is default value
            int duration = intent.getIntExtra(MediaService.SET_DURATION, -1);
            if (duration > 0) {
            	// create message to be handled by our Handler that is responsible for UI update
                Message m = Message.obtain(handler, MSG_DURATION, duration, 0);
                // send message to the handler. Handler will handle it on UI thread
                handler.sendMessage(m);
            }
            // take the current position of playback
            int position = intent.getIntExtra(MediaService.SET_POSITION, -1);
            if (position >= 0 || position <= duration) {
            	// create message to be handled by our Handler that is responsible for UI update
                Message m = Message.obtain(handler, MSG_POSITION, position, 0);
                // send message to the handler. Handler will handle it on UI thread
                handler.sendMessage(m);
            }
            // take the play event
            int event = intent.getIntExtra(MediaService.EVENT_STOP, 0);
            if (event == 1) {
            	// create message to be handled by our Handler that is responsible for UI update
                Message m = Message.obtain(handler, MSG_COMPLETE, 0, 0);
                // send message to the handler. Handler will handle it on UI thread
                handler.sendMessage(m);
            }
            // take the service event
            int serviceEvent = intent.getIntExtra(MediaService.EVENT_DOWN, 0);
            if (serviceEvent == 1) {
            	log("PlayerEventReceiver serviceEvent");
            	// create message to be handled by our Handler that is responsible for UI update
                Message m = Message.obtain(handler, MSG_DOWN, 0, 0);
                // send message to the handler. Handler will handle it on UI thread
                handler.sendMessage(m);
            }
        }
    }

    // Handler class implementation
    @SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
    	// message handle callback. It's invoked on UI thread
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            	// handle the duration event and update progress bar accordingly
                case MSG_DURATION:
                   	log("Duration: " + msg.arg1);
                    songProgress.setMax(msg.arg1);
                    break;
                 // handle the position event and update progress bar accordingly    
                case MSG_POSITION:
                	log("Position: " + msg.arg1);
                    songProgress.setProgress(msg.arg1);
                    break;
                case MSG_COMPLETE:
                	//music is completed
                	finishPlayCurrentMusic();
                	break;
                case MSG_SHOWINFO:
					mFriend.showMedia(mContext, fileIsPlaying);
					break;
                case MESSAGE_LOADINFO:
                	MusicLRCHandler.getMusicInfo(mContext, (String) msg.obj);
                	if(MusicLRCHandler.mInfo == null) {
                		log("MESSAGE_LOADINFO: mInfo is null");
                        break;
                	} else if (totalDuration == null) {
                		log("MESSAGE_LOADINFO: totalDuration is null");
                        break;
                	} else if (MusicLRCHandler.mInfo.get("Duration") == null) {
                		log("MESSAGE_LOADINFO: Duration is null");
                        break;
                	}
                	log("Duration: " + MusicLRCHandler.mInfo.get("Duration").toString());
                	totalDuration.setText(MusicLRCHandler.mInfo.get("Duration").toString());
                	displayDuration(Long.parseLong(MusicLRCHandler.mInfo.get("DurationValue")), 0);
                	break;
                case MESSAGE_UPDATEDURATION:
                	playDuration.setText(msg.obj.toString());
                	break;
                case MSG_DOWN:
                	log("MSG_DOWN");
                	handleStopClick();
                	break;
                case MSG_SHOWLYRICS:
                	log("MSG_SHOWLYRICS ");
                	doShowMusicLyrics();
                	break;
                case MESSAGE_UPDATEINDEX:
                	mLRCIndex = msg.arg1;
                	break;
            }
        }
    };

	public static class TabListener implements ActionBar.TabListener {
		private final Fragment mFragment;
		private Handler mHandler;

		public TabListener(Fragment fragment, Handler handler) {
			log("TabListener ");
			mFragment = fragment;
			mHandler = handler;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			log("onTabReselected ");
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			log("onTabSelected ");
			if (null != mFragment) {
				ft.replace(R.id.fragment_container, mFragment);
				if(tab.getText().equals(TAB_INFO)){
					showMusiInfo(mHandler);
				} else {
					log("showMusiLyrics ");
					showMusiLyrics(mHandler);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			log("onTabUnselected ");
			if (null != mFragment)
				ft.remove(mFragment);
		}
	}
	
	private static void showMusiInfo(Handler handler) {
		Message msg = handler.obtainMessage(MSG_SHOWINFO);
		handler.sendMessageDelayed(msg, 300);
	}
	
	private static void showMusiLyrics(Handler handler) {
		Message msg = handler.obtainMessage(MSG_SHOWLYRICS);
		handler.sendMessageDelayed(msg, 300);
	}
	
	private void doShowMusicLyrics() {
        String lrc = null;
        if(fileIsPlaying == null) {
        	return;
        }
        
		try {
			lrc = player.findLrcFileName(fileIsPlaying);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

        if(lrc != null) {
        	if(mFeed.mLRCView != null){
        		mFeed.mLRCView.setCurrentLyricLine(mLRCIndex);
        		mFeed.mLRCView.showMusicLRC(lrc);
        		displayLRC(mLRCIndex);
        	} else {
        		log("mFeed.mLRCView is null");
        	}
        }
	}
	
	public void onDestroy(){
		log("MediaActivity onDestroy ");
		super.onDestroy();
	}
	
	private void displayLRC(int start) {
		if(mLRCThread != null) {
			mLRCThread.interrupt();
		}
		
		LRCThreadRunnable lrcRunnable = new LRCThreadRunnable();
		lrcRunnable.setStartLine(start);
		mLRCThread = new Thread(lrcRunnable);
		mLRCThread.start();
	}

	private void displayDuration(long duration, long start) {
		if(mDurationThread != null) {
			mDurationThread.interrupt();
		}
		
		LRCDurationRunnable durationRunable = new LRCDurationRunnable();
		durationRunable.setTargetDuration(duration);
		durationRunable.setCurrentDuration(start);
		mDurationThread = new Thread(durationRunable);
		mDurationThread.start();
	}

	private void updateDuration(long current) {
		long minute = current/60;
		long second = current%60;
		String progress = MusicLRCHandler.convertDuration(minute) + ":" + MusicLRCHandler.convertDuration(second);

		log("updateDuration " + progress);
		Message msg = handler.obtainMessage();
		msg.what = MESSAGE_UPDATEDURATION;
		msg.obj = progress;
		handler.sendMessage(msg);
	}

	private void updateLyricIndex(int index) {
		Message msg = handler.obtainMessage();
		msg.what = MESSAGE_UPDATEINDEX;
		msg.arg1 = index;
		handler.sendMessage(msg);
	}

	private class LRCThreadRunnable implements Runnable {
		public boolean isInterrupted = false;
		public int startLine = 0;

		public void setStartLine(int start) {
			startLine = start;
		}
		@Override
		public void run() {
			while (!isPaused) {
				if(isInterrupted) {
					break;
				}
				
				try {
					if (startLine > MusicLRCHandler.mTimeList.size() - 1) {
						log("Finish for " + startLine + ", size " + MusicLRCHandler.mTimeList.size());
						break;
					}
					int startTimeStamp = 0;
					int endTimeStamp = 0;
					if(startLine >= 0) {
						endTimeStamp = MusicLRCHandler.mTimeList.get(startLine);
						if(startLine == 0) {
							startTimeStamp = 0;
						} else {
							startTimeStamp = MusicLRCHandler.mTimeList.get(startLine-1);
						}
					}
					updateLyricIndex(startLine);
					log("Sleep start for " + (endTimeStamp - startTimeStamp));
					Thread.sleep(endTimeStamp - startTimeStamp);
					log("Sleep stop for " + (endTimeStamp - startTimeStamp));
					startLine++;
				} catch (InterruptedException e) {
					break;
				} catch (IndexOutOfBoundsException e) {
					break;
				}
				mLRCHandler.post(new Runnable() {
					@Override
					public void run() {
					mFeed.mLRCView.invalidate();
					}
				});
			}
		}	
	}
	
	private class LRCDurationRunnable implements Runnable {
		public boolean isInterrupted = false;
		private long targetDuration = 0;
		private long currentTime = 0;
		

		public void setTargetDuration(long duration) {
			targetDuration = duration;
		}
		public void setCurrentDuration(long current) {
			log("setCurrentDuration from " + currentTime + " to " + current);
			currentTime = current;
		}
		@Override
		public void run() {
			updateDuration(currentTime);
			while (!isPaused) {
				if(isInterrupted) {
					break;
				}
				
				try {
					Thread.sleep(1000);
					currentTime += 1;
					if(currentTime > targetDuration){
						break;
					}
				} catch (InterruptedException e) {
					break;
				}
				if(!isPaused) {
					log("Sleep done " + currentTime);
					updateDuration(currentTime);
				}
			}
		}	

	}
	
	private void finishPlayCurrentMusic() {
		try {
			player.stop();
			handleNextClick();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void copyMusics(){
        String[] files = null;
        AssetManager am = getAssets();
        try {
            files = am.list("Musics");
            log("asset Musics " + Integer.toString(files.length)); 
             
        } catch (IOException e) {
            e.printStackTrace();
        }
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        for(String music: files) {
        	copyFile(music, "Musics", storagePath+"/Music");
        }
        
        try {
            files = am.list("Lyrics");
            log("asset Lyrics " + Integer.toString(files.length)); 
             
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String lyrics: files) {
        	copyFile(lyrics, "Lyrics", storagePath+"/Lyrics");
        }
    }

    private void copyFile(String music, String srcPath, String destPath){
        String filePath = destPath + "/" + music;
        
        File musicFile = new File(filePath); 
        // check if file already exists
        if (musicFile.exists()) { 
        	return;
        } 

        InputStream musicFileStream = null;
        try {
            musicFileStream = this.getAssets().open(srcPath+"/"+music);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        File musicDir = new File(destPath); 
        // create dir if it does not exist
        if (!musicDir.exists()) { 
              try { 
                  musicDir.mkdirs(); 
            } catch (Exception e) { 
                e.printStackTrace();
            } 
        } 

        // create file if it does not exist
        if (!musicFile.exists()) { 
            try { 
                musicFile.createNewFile(); 
            } catch (Exception e) { 
                e.printStackTrace();
            } 

            OutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(filePath, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
            byte[] buffer = new byte[1024];
            int length = 0;
            // copy file from source dir to target dir
            try {
                while ((length = musicFileStream.read(buffer)) > 0) {
                    fileOut.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOut.flush();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }        
        }
        
        try {
            musicFileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    private static void log(String content) {
    	if(DEBUG) {
    		Log.i(TAG, content);
    	}
    }
}
