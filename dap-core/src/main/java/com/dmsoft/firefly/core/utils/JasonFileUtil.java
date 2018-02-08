package com.dmsoft.firefly.core.utils;


import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.io.*;

/**
 * Created by Lucien.Chen on 2018/2/7.
 */
public class JasonFileUtil {

    /**
     * 生成.json格式文件
     */
    public static boolean createJsonFile(JSONObject jsonObject, String fileParentPath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;

        // 拼接文件完整路径
        String fullPath = fileParentPath + File.separator + fileName;

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
            write.write(jsonObject.toString());
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
    public static JSONObject readJsonFile(String fileParentPath, String fileName) {
        JSONObject jsonObject = null;

        // 拼接文件完整路径
        String fullPath = fileParentPath + File.separator + fileName;
        try {
            File file = new File(fullPath);
            InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String json = result.toString("UTF-8");
            jsonObject = JSONObject.fromObject(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    ;

    public static JSONObject objectToJson(Object obj) {
        JSONObject jsonObject = JSONObject.fromObject(obj);
        return jsonObject;
    }
}
