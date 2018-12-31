package com.pache.weixin.util;

/**
 * 常量类
 */
public class Constants {

    /**
     * 微信接口配置TOKEN值
     */
    public static final String TOKEN = "dev_token";

    /**
     * access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token，获取地址
     */
    public static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * AppID
     */
    public static final String APPID = "wx1821711211bd7fd8";

    /**
     *  APPSECRET
     */
    public static final String APPSECRET = "c063daa5d41a6f7b962f69d6c5d72941";

    /**
     * 公众号生产自定义菜单url
     */
    public static final String CUSTOM_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    /**
     * 百度AI图片识别
     */
    public static final String APP_ID = "15304250";

    public static final String API_KEY = "QMt9lYDNY7qVqpnUCxMr0he1";

    public static final String SECRET_KEY = "SlBywaTct1pkfSn9LpfL8nRr5HjEdcdE";

}
