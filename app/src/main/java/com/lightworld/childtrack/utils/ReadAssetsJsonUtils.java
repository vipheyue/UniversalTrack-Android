package com.lightworld.childtrack.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by heyue on 2017/1/16.
 */

public class ReadAssetsJsonUtils {
    public static  String read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        return new String(data);
    }

}
