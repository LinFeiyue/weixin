package com.pache.weixin.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *  接口访问工具类
 */
public class NetUtils {

    /**
     * 向指定地址发送一个post请求，并带着data数据
     * @param url
     * @param data
     * @return
     */
    public static String post(String url,String data){
        InputStream is = null;
       try{
           final URLConnection connection = getPostConn(url, data);
           is = connection.getInputStream();
            byte[] b = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = is.read(b)) != -1){
                sb.append(new String(b,0,len,"UTF-8"));
            }
            return sb.toString();
        }catch (Exception e){
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

    private static URLConnection getPostConn(String url,String data){
        URLConnection connection = null;
        OutputStream os = null;
        try {
            URL obj = new URL(url);
            connection = obj.openConnection();
            //要发送数据出去，必须要设置为可发送数据状态
            connection.setDoOutput(true);
            //获取输出流
            os = connection.getOutputStream();
            //写出数据
            os.write(data.getBytes());
            os.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(os != null){
                    os.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * 向指定地址发送一个get请求
     * @return
     */
    public static String get(String url) {
        InputStream is = null;
        try {
            URL obj = new URL(url);
            URLConnection connection = obj.openConnection();
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
