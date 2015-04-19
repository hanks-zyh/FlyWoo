package com.zjk.wifiproject.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.zjk.wifiproject.music.MusicEntity;
import com.zjk.wifiproject.picture.PictureEntity;
import com.zjk.wifiproject.picture.PictureFolderEntity;

/**
 * @fileName FileUtils.java
 * @package szu.wifichat.android.util
 * @description 文件工具类
 */
public class FileUtils {

    /**
     * 判断SD
     * 
     * @return
     */
    public static boolean isSdcardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 创建根目录
     * 
     * @param path
     *            目录路径
     */
    public static void createDirFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 创建文件
     * 
     * @param path
     *            文件路径
     * @return 创建的文件
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }

    /**
     * 删除文件夹
     * 
     * @param folderPath
     *            文件夹的路径
     */
    public static void delFolder(String folderPath) {
        delAllFile(folderPath);
        String filePath = folderPath;
        filePath = filePath.toString();
        java.io.File myFilePath = new java.io.File(filePath);
        myFilePath.delete();
    }

    /**
     * 删除文件
     * 
     * @param path
     *            文件的路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        int mLength = tempList.length;
        for (int i = 0; i < mLength; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 获取文件的Uri
     * 
     * @param path
     *            文件的路径
     * @return
     */
    public static Uri getUriFromFile(String path) {
        File file = new File(path);
        return Uri.fromFile(file);
    }

    /**
     * 换算文件大小
     * 
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "未知大小";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 通过路径获得文件名字
     * 
     * @param path
     * @return
     */
    public static String getPathByFullPath(String fullpath) {
        return fullpath.substring(0, fullpath.lastIndexOf(File.separator));
    }

    /**
     * 通过路径获得文件名字
     * 
     * @param path
     * @return
     */
    public static String getNameByPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * 通过判断文件是否存在
     * 
     * @param path
     * @return
     */

    public static boolean isFileExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    /**
     * 获得SD卡路径
     * 
     * @param
     * @return String
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        }
        return null;
    }

    public static List<PictureFolderEntity> getPictureFolderList(Context context) {
        List<PictureFolderEntity> list = new ArrayList<PictureFolderEntity>();

        /**
         * 临时的辅助类，用于防止同一个文件夹的多次扫描
         */
        HashMap<String, Integer> tmpDir = new HashMap<String, Integer>();

        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.ImageColumns.DATA }, "", null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");
        if (mCursor.moveToFirst()) {
            int _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                // 获取图片的路径
                String path = mCursor.getString(_date);
                // 获取该图片的父路径名
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                PictureFolderEntity pictureFoldery = null;
                String dirPath = parentFile.getAbsolutePath();
                if (!tmpDir.containsKey(dirPath)) {
                    // 初始化PictureFolderEntity
                    pictureFoldery = new PictureFolderEntity();
                    pictureFoldery.setDir(dirPath);
                    pictureFoldery.setFirstImagePath(path);
                    list.add(pictureFoldery);
                    // Log.d("zyh", dirPath + "," + path);
                    tmpDir.put(dirPath, list.indexOf(pictureFoldery));
                } else {
                    pictureFoldery = list.get(tmpDir.get(dirPath));
                }
                pictureFoldery.images.add(new PictureEntity(path));
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
        }
        tmpDir = null;
        return list;
    }

    public static List<MusicEntity> getMusicList(Context context) {
        List<MusicEntity> list = new ArrayList<MusicEntity>();

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        /* 这个字符串数组表示要查询的列 */
        new String[] { MediaStore.Video.Media.TITLE, // 音乐名
                MediaStore.Audio.Media.DURATION, // 音乐的总时间
                MediaStore.Audio.Media.ARTIST, // 艺术家
                MediaStore.Audio.Media._ID, // id号
                MediaStore.Audio.Media.DISPLAY_NAME, // 音乐文件名
                MediaStore.Audio.Media.DATA // 音乐文件的路径
                }, null, // 查询条件，相当于sql中的where语句
                null, // 查询条件中使用到的数据
                null);
        while (cursor.moveToNext()) {
            MusicEntity music = new MusicEntity();
            music.setTitle(cursor.getString(0));
            music.setDuration(cursor.getLong(1));
            music.setArtist(cursor.getString(2));
            music.setId(cursor.getInt(3));
            music.setDisplayName(cursor.getString(4));
            music.setData(cursor.getString(5));
            // L.i(music.toString());
            list.add(music);
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;

    }
}
