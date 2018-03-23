package com.dmsoft.firefly.gui.components.utils;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

/**
 * Created by julia on 16/8/24.
 */
public class FileUtil {
    /**
     * Get the all files of path.
     *
     * @param dirPath the path of top
     * @return  the file list
     */
    public static List<File> getFileList(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
//            throw new ApplicationException(ExceptionMessages.ERR_15004, ResourceBundleUtils.getString(ExceptionMessages.EXCEPTION_GLOBAL_FILE_PATH_DOESNOT_EXIST));
        }

        Vector<String> ver = new Vector<String>();
        List<File> fileResults = Lists.newArrayList();
        ver.add(dirPath);
        while (ver.size() > 0) {
            File[] files = new File(ver.get(0).toString()).listFiles();
            if (files == null) {
                return fileResults;
            }
            ver.remove(0);
            int len = files.length;
            for (int i = 0; i < len; i++) {
                String tmp = files[i].getAbsolutePath();
                if (files[i].isDirectory()) {
                    ver.add(tmp);
                } else {
                    fileResults.add(files[i]);
                }
            }
        }
        return fileResults;
    }

    /**
     * Copy file.
     *
     * @param oldPath the path of old file
     * @param newPath the path of new file
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
        } catch (Exception e) {
//            throw new ApplicationException(ExceptionMessages.ERR_20001, ResourceBundleUtils.getString(ExceptionMessages.EXCEPTION_GLOBAL_SYSTEM));
        }
    }
}
