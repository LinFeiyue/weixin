package com.pache.weixin.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *  接口访问工具类
 */
public class NetUtils {

    /**
     * 获取
     * @return
     */
    public static String getJSONStrFromUrl(String getUrl) {
        InputStream is = null;
        try {
            URL url = new URL(getUrl);
            URLConnection connection = url.openConnection();
            is = connection.getInputStream();
            byte[] b = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = is.read(b)) != -1){
                sb.append(new String(b,0,len,"UTF-8"));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null){
                        is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
