package com.wzx.wildworld.videocombine;

import java.io.File;

/**
 * Created by weizhixiang on 15/12/22.
 */
public class FileDelete {
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        return dirFile.delete();
    }
    public boolean deleteFolder(String filePath){
        File file=new File(filePath);
        if(!file.exists()){
            return false;
        }else{
            if(file.isFile()){
                return deleteFile(filePath);
            }else {
                return deleteDirectory(filePath);
            }
        }

    }
}
