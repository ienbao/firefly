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
        // 标记文件生成是否成功
        boolean flag = true;

        // 拼接文件完整路径
        String fullPath = fileParentPath + File.separator + fileName + ".json";

        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

            // 将格式化后的字符串写入文件
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
     * 读取.json格式文件
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

    public static void main(String[] args) {
        UserDto userDto = new UserDto();
        userDto.setUserName("xxx");
        userDto.setPassword("2344");
        writeJsonFile(userDto, JsonFileUtil.class.getResource("/").getPath() + "config", "user");
    }

}
