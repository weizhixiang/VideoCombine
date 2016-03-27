package com.wzx.wildworld.videocombine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/12/14.
 */
public class VideoFileComputer {
    public String[] getFileName(File file) {
        //获取文件夹文件名，并排序1，2，3，4，。。。
        File flist[] = file.listFiles();
        String fileName[] = new String[flist.length];
        List<File> fileList = new ArrayList<File>();
        for (File f : flist) {
            fileList.add(f);
        }
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.getName().compareTo(rhs.getName()) > 0) {
                    return 1;
                } else if (lhs.getName().compareTo(rhs.getName()) < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (int i = 0; i < fileList.size(); i++) {
            fileName[i] = fileList.get(i).getPath();
        }
        return fileName;
    }

    public long getFileSize(File f) throws Exception {
        //获得文件大小
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
            System.out.println("文件不存在");
        }
        return s;
    }

    public long getFilesSize(File f) throws Exception {
        //获得文件夹大小
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFilesSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    public String formateDate(Date date) {
        //格式化日期yyyy年MM月dd日
        String formateDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        formateDate = simpleDateFormat.format(date);
        return formateDate;
    }

    public String formatFileSize(long size) {
        //格式化文件大小B KB MB GB
        String fileSizeString = "";
        DecimalFormat df = new DecimalFormat("#.00");
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public long getFileNumber(File f) {
        //获取文件个数
        long num = 0;
        File flist[] = f.listFiles();
        num = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                num = num + getFileNumber(flist[i]);
                num--;
            }
        }
        return num;
    }

    public void readFileByLine(int bufSize, FileChannel fcin, ByteBuffer rBuffer, FileChannel fcout, ByteBuffer wBuffer) {
        String enterStr = "\n";
        try {
            byte[] bs = new byte[bufSize];
            int size = 0;
            StringBuffer strBuf = new StringBuffer("");
            while (fcin.read(rBuffer) != -1) {
                int rSize = rBuffer.position();
                rBuffer.rewind();
                rBuffer.get(bs);
                rBuffer.clear();
                String tempString = new String(bs, 0, rSize);
                int fromIndex = 0;
                int endIndex = 0;
                while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {
                    String line = tempString.substring(fromIndex, endIndex);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFileByLine(FileChannel fcout, ByteBuffer wBuffer, String line) {
        try {
            fcout.write(ByteBuffer.wrap(line.getBytes()), fcout.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyFile(String inFile1, String inFile2, String outFile) {
        try {
            FileInputStream fin1 = new FileInputStream(inFile1);
            FileInputStream fin2 = new FileInputStream(inFile2);
            FileOutputStream fout = new FileOutputStream(outFile);
            FileChannel fcin1 = fin1.getChannel();
            FileChannel fcin2 = fin2.getChannel();
            FileChannel fcout = fout.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                buffer.clear();
                int r = fcin1.read(buffer);
                if (r == -1) {
                    r = fcin2.read(buffer);
                    if (r == -1) {
                        break;
                    }
                }
                buffer.flip();
                fcout.write(buffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
