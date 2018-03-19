package com.dmsoft.firefly.sdk.utils;

import java.io.*;

/**
 * Created by GuangLi on 2018/3/2.
 */
public class DeepCopy implements Serializable {
    public static Object deepCopy(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(o);
        //然后反序列化，从流里读取出来，即完成复制
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }
}
