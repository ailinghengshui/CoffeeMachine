
package com.jingye.coffeemac.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jingye.coffeemac.helper.bitmaploader.BitmapLoader;
import com.jingye.coffeemac.util.log.LogUtil;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 用于把附件保存到文件系统中
 * 
 * @author yangwc
 */
public class AttachmentStore {
    public static long copy(String srcPath, String dstPath) {
    	return save(load(srcPath), dstPath);
    }
	
    public static void copyfile(File fromFile, File toFile){

    	if(!fromFile.exists()){
    		return;
    	}
    	
    	if(!fromFile.isFile()){
    		return;
    	}
    	
    	if(!fromFile.canRead()){
    		return;
    	}
    	
    	if(!toFile.getParentFile().exists()){
    		toFile.getParentFile().mkdirs();
    	}
    	if(toFile.exists()){
    		toFile.delete();
    	}
    	
    	try {
    		java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
    		java.io.FileOutputStream fosto = new FileOutputStream(toFile);
    		byte bt[] = new byte[1024];
    		int c;
    		while ((c = fosfrom.read(bt)) > 0) {
    			fosto.write(bt, 0, c); //将内容写到新文件当中
    		}

    		fosfrom.close();
    		fosto.close();
    	} catch (Exception ex) {
    		Log.e("readfile", ex.getMessage());
    	} catch (OutOfMemoryError err) {
    		err.printStackTrace();
    	}
	}
    
    public static boolean copyfileWithReturn(File fromFile, File toFile){

    	if(!fromFile.exists()){
    		return false;
    	}
    	
    	if(!fromFile.isFile()){
    		return false;
    	}
    	
    	if(!fromFile.canRead()){
    		return false;
    	}
    	
    	if(!toFile.getParentFile().exists()){
    		toFile.getParentFile().mkdirs();
    	}
    	if(toFile.exists()){
    		toFile.delete();
    	}
    	
    	try {
    		java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
    		java.io.FileOutputStream fosto = new FileOutputStream(toFile);
    		byte bt[] = new byte[1024];
    		int c;
    		while ((c = fosfrom.read(bt)) > 0) {
    			fosto.write(bt, 0, c); //将内容写到新文件当中
    		}

    		fosfrom.close();
    		fosto.close();
    	} catch (Exception ex) {
    		Log.e("readfile", ex.getMessage());
    		return false;
    	} catch (OutOfMemoryError err) {
    		err.printStackTrace();
    		return false;
    	}
    	
    	return true;
	}

    /**
     * 把数据保存到文件系统中，并且返回其大小
     * 
     * @param data
     * @param filePath
     * @return 如果保存失败,则返回-1
     */
    public static long save(byte[] data, String filePath) {
        File f = new File(filePath);        
        if(f.getParentFile() == null) {
        	return -1;
        }
        
        if (!f.getParentFile().exists()) {// 如果不存在上级文件夹
            f.getParentFile().mkdirs();
        }
        try {
            f.createNewFile();
            FileOutputStream fout = new FileOutputStream(f);
            fout.write(data);
            fout.flush();
            fout.close();
        } catch (IOException e) {
        	e.printStackTrace();
            return -1;
        }
        return f.length();
    }

    /**
     * @param is
     * @param filePath
     * @return 保存失败，返回-1
     */
    public static long save(InputStream is, String filePath) {
        File f = new File(filePath);
        if (!f.getParentFile().exists()) {// 如果不存在上级文件夹
            f.getParentFile().mkdirs();
        }
        try {
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            is.close();
            out.flush();
            out.close();
            return f.length();
        } catch (IOException e) {
        	if(f!=null && f.exists()){
        		f.delete();
        	}
        	LogUtil.e("picdown", "filename="+filePath + ";下载异常 message=" + e.getMessage());
            return -1;
        }
    }

    /**
     * 把文件从文件系统中读取出来
     * 
     * @param url
     * @return 如果无法读取,则返回null
     */
    public static byte[] load(String url) {
        try {
        	File f = new File(url);
            byte[] buf = new byte[(int)f.length()]; // 读取文件长度
            FileInputStream fin = new FileInputStream(f);
            fin.read(buf); // 把文件一次性读取出来
            fin.close();
            return buf;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 删除指定路径文件
     * 
     * @param url
     */
    public static void delete(String url) {
        if(url==null||url==""){
            return;
        }
        File f = new File(url);
        if (f.exists()) {
            f.delete();
        }
    }
    
    public static Bitmap getBitmap(String pathName){
		 File file = new File(pathName);
		 if(!file.exists()){
			 return null;
		 }
		 Bitmap bitmap = BitmapLoader.getThumbnail(pathName);
		return bitmap;
	 }

    public static Map<String, Bitmap> getAllAvatar(String path){
    	Map<String, Bitmap> buddyHeadImages = new LinkedHashMap<String, Bitmap>();
    	File file = new File(path);
    	if(file.exists() && file.isDirectory()){
    		String[] strings = file.list();
    		for(String string:strings){
    			Bitmap bitmap = getBitmap(path+string);
    			if(bitmap != null){
    				buddyHeadImages.put(string, bitmap);
    			}
    		}
    	}
    	return buddyHeadImages;
    }
}
