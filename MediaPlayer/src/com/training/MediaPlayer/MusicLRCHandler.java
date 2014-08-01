package com.training.MediaPlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class MusicLRCHandler {
	private static final String TAG = "MediaPlayer";
    public static ArrayList<String> mWords = new ArrayList<String>();
    public static ArrayList<Integer> mTimeList = new ArrayList<Integer>();
    public static HashMap<String, String> mInfo = new HashMap<String, String>();
    @SuppressLint("SdCardPath")
	private static final String FOLDER_LYRICS = "/sdcard/Lyrics";

    //处理歌词文件
    public void readLRC(String path) {
    	Log.i(TAG, "readLRC: "+ path);
        File file = new File(FOLDER_LYRICS+"/"+path);
        mWords.clear();
        mTimeList.clear();

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String s = "";
            while ((s = bufferedReader.readLine()) != null) {
            	//Log.i(TAG, "LRC: "+ s);
                addTimeToList(s);
                if ((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:") != -1)
                        || (s.indexOf("[by:") != -1)) {
                    s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
                    continue;
                } else {
                	if(s.indexOf("]") != -1) {
                        String ss = s.substring(s.indexOf("["), s.indexOf("]") + 1);
                        s = s.replace(ss, "");
                	} else {
                        continue;
                	}
                }
                mWords.add(s);
            }

            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mWords.add("没有歌词文件，赶紧去下载");
        } catch (IOException e) {
            e.printStackTrace();
            mWords.add("没有读取到歌词");
        }
    }
   public ArrayList<String> getWords() {
        return mWords;
   }

    public ArrayList<Integer> getTime() {
        return mTimeList;
    }

    // 分离出时间
    private int timeHandler(String string) {
        string = string.replace(".", ":");
        String timeData[] = string.split(":");
        // 分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);
        // 计算上一行与下一行的时间转换为毫秒数
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
    	//Log.i(TAG, "timeHandler: "+ currentTime);
        return currentTime;
   }

   private void addTimeToList(String string) {
        Matcher matcher = Pattern.compile(
                "\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
        if (matcher.find()) {
            String str = matcher.group();
        	//Log.i(TAG, "matcher: "+ string + " group: " + str);
            mTimeList.add(this.timeHandler(str.substring(1,
                    str.length() - 1)));
        }
    }
   
   public static HashMap<String, String> getMusicInfo(Context context, String music) {

       Log.i(TAG, "getMusicInfo: "+ music);
	   // clear first
	   mInfo.clear();
	   
       // ContentProvider（内容提供者） 和 ContentResolver（内容解析器），用于管理和发布和应用程序相关的持久性数据
       ContentResolver resolver = context.getContentResolver();
       
       String musicName = MediaStore.Audio.Media.DISPLAY_NAME;

	   String selection = musicName + " LIKE '%" + music + "%'";

       Cursor cursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null,
            null);

       if (null == cursor) {
       	   Log.i(TAG, "cursor is null");
           return null;
       }
   	   Log.i(TAG, "cursor size " + cursor.getCount());
       cursor.moveToFirst();
       if (cursor.moveToFirst()) {
           do {
               long id = cursor.getLong(cursor
                       .getColumnIndex(MediaStore.Audio.Media._ID));
               String title = cursor.getString(cursor
                       .getColumnIndex(MediaStore.Audio.Media.TITLE));
               if(title == null) {
            	   title = "";
               }
               String artist = cursor.getString(cursor
                       .getColumnIndex(MediaStore.Audio.Media.ARTIST));
               if ("<unknown>".equals(artist)) {
                   artist = "未知艺术家";
               }
               String album = cursor.getString(cursor
                       .getColumnIndex(MediaStore.Audio.Media.ALBUM));
               if(album == null) {
            	   album = "";
               }
               long size = cursor.getLong(cursor
                       .getColumnIndex(MediaStore.Audio.Media.SIZE));
               double fSize = size/(1024*1024);
               fSize = (double)(Math.round(fSize*100)/100.0);
               long duration = cursor.getLong(cursor
                       .getColumnIndex(MediaStore.Audio.Media.DURATION));
               String strDuratoin = "" + convertDuration(duration/60000) + ":" + convertDuration((duration/1000)%60);
               String url = cursor.getString(cursor
                       .getColumnIndex(MediaStore.Audio.Media.DATA));
               if(url == null) {
            	   url = "";
               }
               String name = cursor.getString(cursor
                       .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
               if(name == null) {
            	   name = "";
               }
               if (duration >= 1000 && duration <= 900000) {
            	   mInfo.put("Title", title);
            	   mInfo.put("Artist", artist);
            	   mInfo.put("Album", album);
            	   mInfo.put("Size", ""+fSize+"M");
            	   mInfo.put("Duration", strDuratoin);
            	   mInfo.put("DurationValue", ""+duration/1000);
            	   mInfo.put("Url", url);
            	   mInfo.put("Name", name);
            	   mInfo.put("Id", ""+id);
               }
           } while (cursor.moveToNext());
       } else {
    	   mInfo.put("Title", "");
    	   mInfo.put("Artist", "");
    	   mInfo.put("Album", "");
    	   mInfo.put("Size", "0M");
    	   mInfo.put("Duration", "0");
    	   mInfo.put("DurationValue", "0");
    	   mInfo.put("Url", "");
    	   mInfo.put("Name", "");
    	   mInfo.put("Id", "");
       }
       
       if (cursor != null) {
           cursor.close();
       }
       Log.i(TAG, "getMusicInfo: return");
       return mInfo;
   }
   
	public static String convertDuration(long time){ 
		String strTime = String.valueOf(time);
		if(strTime.length() > 2 || strTime.length() < 0) {
			return "";
		}
		String [] ss = {"00", "0", ""};
		strTime = ss[strTime.length()] + strTime;
		return strTime;
	} 

}
