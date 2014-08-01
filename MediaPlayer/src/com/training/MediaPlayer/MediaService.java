package com.training.MediaPlayer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MediaService extends Service {

	// tag string used as first parameter for the logger
    private static final String TAG = "MediaPlayer";

    // Intent action value for the service. We will use it to send broadcast event. 
    public static final String PLAY_ACTION = "playback_action";
    // parameters for intent above (track duration and current position)
    public static final String SET_DURATION = "set_duration";
    public static final String SET_POSITION = "set_position";
    public static final String EVENT_STOP   = "event_stop";
    public static final String EVENT_DOWN   = "event_down";

    @SuppressLint("SdCardPath")
	private static final String FOLDER_LYRICS = "/Lyrics";
	private static final String FOLDER_MUSIC = "/Music";
	private static final String FOLDER_STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath();

	// media player itself. Android class that can play media
    private MediaPlayer player;
    // Uri represents path to media file
    private Uri mediaUri;
    // media file duraion value
    private int duration;
    // An ExecutorService that can schedule commands to run after a given delay, 
    // or to execute periodically. We will use it to update playback position once in secons. 
    private ScheduledExecutorService executorService;
    // Action that executed by ScheduledExecutorService
    private ScheduledFuture taskHandle;

    // MediaPlayerImpl is internal class that implements our service interface from IMediaPlayer.aidl
    // See its implementation below
    private IBinder binder = new MediaPlayerImpl();

    /** This method is invoked by system when client is binding to service. 
     * As result we should return IBinder interface that client can use for communication
     */
    public IBinder onBind(Intent intent) {
    	// Creates a thread pool that can schedule commands to run after a given delay, 
    	// or to execute periodically. Parameter is thread pool size. It's enough for us to have 1 thread. 
        executorService = Executors.newScheduledThreadPool(1);
        Log.i(TAG, "MediaService.onBind");
        // return IBinder interface that is actually MediaPlayerImpl instance
        return binder;
    }

    /**
     * Called when all clients have disconnected from a particular interface published by the service. 
     */
    @Override
    public boolean onUnbind(Intent intent) {
    	// shutdown our executor service
        executorService.shutdown();
        Log.i(TAG, "MediaService.onUnbind");
        // Return true if you would like to have the service's onRebind(Intent) method 
        // later called when new clients bind to it. No, we don't want it, so return false. 
        return false;
    }

	public void onDestroy() {
		Log.i(TAG, "MediaService.onDestroy");
        // create Intent for broadcast event
        Intent intent = new Intent(PLAY_ACTION);
        // put extra parameter inside (duration value)
        intent.putExtra(EVENT_DOWN, 1);
        // send the broadcast event
        sendBroadcast(intent);
		super.onDestroy();
	}

	/**
     * We use this internal class to send brodcast event about updated playback position
     * run method will be invoked in separate thread by executor service once a second
     */
    private class PositionUpdater implements Runnable {

        @Override
        public void run() {
        	// ensure we have valid player instance
            if (player != null) {
            	// Create intent for the broadcast event. 
                Intent intent = new Intent(PLAY_ACTION);
                // Put additional parameter to the Intent (player current position value). 
                intent.putExtra(SET_POSITION, player.getCurrentPosition());
                // ship it!
                sendBroadcast(intent);
            }
        }
    }

    /**
     * Implement player start functionality 
     */
    private void doPlay() {
    	// check if we have valid player instance
        if (player != null) {
        	// if player is already playing, stop it
            doStop();
        }

        // recreate player instance
        player = new MediaPlayer();
        try {
        	// set data source for the player. mediaUri contains path to media file
            player.setDataSource(this, mediaUri);
            // set the listener to handle "prepare" state event
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            	// player is prepared so we know the meta data and ready to start playing
                @Override
                public void onPrepared(MediaPlayer mp) {
                	// get media file duration value
                    duration = player.getDuration();
                    // create Intent for broadcast event
                    Intent intent = new Intent(PLAY_ACTION);
                    // put extra parameter inside (duration value)
                    intent.putExtra(SET_DURATION, duration);
                    // send the broadcast event
                    sendBroadcast(intent);
                    // start playing
                    doResume();
                }
            });
            // add the playing completion handler
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                	// when file is over, stop playing
                    doStop();

                    // create Intent for broadcast event
                    Intent intent = new Intent(PLAY_ACTION);
                    // put extra parameter inside (duration value)
                    intent.putExtra(EVENT_STOP, 1);
                    // send the broadcast event
                    sendBroadcast(intent);
                }
            });

            // player is created and listeners are set. So we can start preparation asynchronously.
            // Where we get result of this operation?
            // Right! In OnPreparedListener (see the code above)
            player.prepareAsync();
        } catch (IOException e) {
        	// something goes bad. User will be angry... 
            Log.e(TAG, "Bad media URI " + mediaUri);
        }
    }

    /**
     * Do playback stop functionality
     */
    private void doStop() {
    	// We already have player?
        if (player != null) {
        	
            if (taskHandle != null) {
            	// If we have PositionUpdater, cancel it. See the taskHandle creation below. 
                taskHandle.cancel(false);
            }
            // reset the listeners
            player.setOnPreparedListener(null);
            player.setOnCompletionListener(null);
            // do real stop
            player.stop();
            // release the link and allow garbage collector to free the memory 
            player = null;
        }
    }

    /**
     * pause player
     */
    private void doPause() {
        if (player != null && player.isPlaying()) {
            if (taskHandle != null) {
                taskHandle.cancel(false);
            }
            // just pause player. Easy?
            player.pause();
        }
    }

    /**
     * start the player
     */
    private void doResume() {
    	// if we are not already playing something
        if (player != null && (!player.isPlaying())) {
        	// create the taskHandler that will invoke PositionUpdater.run method once a second
        	// first parameter is class that implements Runnable interface
        	// second is the delay before first invocation
        	// third parameter is period for periodic invocations
        	// the last one is time unit. We use seconds
            taskHandle = executorService.scheduleAtFixedRate(new PositionUpdater(), 0, 1, TimeUnit.SECONDS);
            // And start the player
            player.start();
        }
    }

    /**
     * Time to implement our service interface.
     * Functionality is pretty simple so there are no comments inside. 
     * We just invoke parent class methods to do necessary job 
     */
    private class MediaPlayerImpl extends IMediaPlayer.Stub {
        @Override public void play(String music) throws RemoteException {
            mediaUri = Uri.parse(FOLDER_STORAGE + FOLDER_MUSIC + "/" + music);
            doPlay();
        }
        @Override public void pause() throws RemoteException {
            doPause();
        }
        @Override public void resume() throws RemoteException {
            doResume();
        }
        @Override public void stop() throws RemoteException {
            doStop();
        }
        @Override public String findLrcFileName(String music) throws RemoteException {
            return doFindLrc(music);
        }
        @Override public String findMusic(String current, int type) throws RemoteException {
        	return doGetMusic(current, type);
        }
    }
    
    private String doFindLrc(String music) {
        // Create File object and direct it to folder name
        File root = new File(FOLDER_STORAGE+FOLDER_LYRICS);
        // take the first available file in directory
        String[] musicLrcList = root.list();
        
        if(musicLrcList.length == 0) {
        	return null;
        }
        
        if(music == null) {
        	Log.i(TAG, "current null: " + root.list()[0]);
        	return root.list()[0];
        }
        
        String musicWoExt = music.substring(0, music.indexOf(".")-1);
        // find the music in the list
        int position = 0;
        for(String lrc: musicLrcList){
        	Log.i(TAG, "lrc: "+ lrc);
        	if(lrc.contains(musicWoExt)){
        		break;
        	}
        	position += 1;
        }
    	Log.i(TAG, "position: "+ position);
    	if(position >= musicLrcList.length){
    		return null;
    	}
    	
        return musicLrcList[position];
    }

    private String doGetMusic(String current, int type) {
        // Create File object and direct it to folder name
        File root = new File(FOLDER_STORAGE+FOLDER_MUSIC);
        // take the first available file in directory
        String[] musicList = root.list();
        
        if(musicList.length == 0) {
        	return null;
        }
        
        if(current == null) {
        	Log.i(TAG, "current null: " + root.list()[0]);
        	return root.list()[0];
        }
        
    	Log.i(TAG, "current: "+ current + " length: " + musicList.length);
        // find the music in the list
        int position = 0;
        for(String music: musicList){
        	Log.i(TAG, "music: "+ music);
        	if(music.equals(current)){
        		break;
        	}
        	position += 1;
        }
    	Log.i(TAG, "position: "+ position);
        switch(type) {
        case 0:
        	// get next music
        	position = (position+1+musicList.length)%musicList.length;
        	break;
        case 1:
        	// get previous music
        	position = (position-1+musicList.length)%musicList.length;
        	break;
        default:
        	position = -1;
        	break;
        }
        if(position == -1) {
        	return null;
        }
        return root.list()[position];
        
    }
}
