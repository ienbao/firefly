package com.dmsoft.firefly.core.utils;


import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;

import java.io.*;

/**
 * Created by Lucien.Chen on 2018/2/7.
 */
public class JsonFileUtil {

    private static JsonMapper mapper = JsonMapper.defaultMapper();

    /**
     * writeJsonFile
     *
     * @param object         object
     * @param fileParentPath fileParentPath
     * @param fileName       fileName
     * @param <T>            T
     * @return boolean
     */
    public static <T> boolean writeJsonFile(T object, String fileParentPath, String fileName) {
        boolean flag = true;
        String fullPath = fileParentPath + File.separator + fileName + ".json";
        try {
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(mapper.toJson(object).toString());
            write.flush();
            write.close();
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }

        // 返回是否成功的标记
        return flag;
    }

    /**
     * get json file
     * @param fileParentPath file path
     * @param fileName file name
     */
    public static String readJsonFile(String fileParentPath, String fileName) {
        String json = null;

        // 拼接文件完整路径
        String fullPath = fileParentPath + File.separator + fileName + ".json";
        try {
            File file = new File(fullPath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            json = result.toString("UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * delete folder
     *
     * @param folderPath String
     * @return boolean
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * delete all file
     *
     * @param path String
     * @return boolean
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);
                delFolder(path + File.separator + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * copy folder
     *
     * @param oldPath String
     * @param newPath String
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs();
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + File.separator +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    copyFolder(oldPath + File.separator + file[i], newPath + File.separator + file[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        UserDto userDto = new UserDto();
        userDto.setUserName("xxx");
        userDto.setPassword("2344");
        writeJsonFile(userDto, JsonFileUtil.class.getResource("/").getPath() + "config", "user");
    }

}
