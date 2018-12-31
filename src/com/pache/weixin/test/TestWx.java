package com.pache.weixin.test;

import com.baidu.aip.ocr.AipOcr;
import com.pache.weixin.entity.*;
import com.pache.weixin.service.WxService;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestWx {

    //设置APPID/AK/SK
    public static final String APP_ID = "15304250";
    public static final String API_KEY = "QMt9lYDNY7qVqpnUCxMr0he1";
    public static final String SECRET_KEY = "SlBywaTct1pkfSn9LpfL8nRr5HjEdcdE";


    @Test
    public void testGetUserInfo(){
        System.out.println(WxService.getUserInfo("o_NLw0p4cQODAXW5Ybtl7PHB6-iE"));
    }

    @Test
    public void testQrCode(){
        String ticket = WxService.getQrCodeTicket();
        System.out.println(ticket);
    }

    @Test
    public void testUpload(){
        String path = "C:\\Users\\admin\\Desktop\\2.jpg";
        String resp = WxService.upload(path,"image");
        System.out.println(resp);
    }

    @Test
    public void testPic(){
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        String path = "C:\\Users\\admin\\Desktop\\2.jpg";
        org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        System.out.println(res.toString(2));
    }

    @Test
    public void testButton(){
        Button btn = new Button();
        btn.getButton().add(new ClickButton("菜单一","11111"));
        btn.getButton().add(new ViewButton("菜单二","http://www.baidu.com"));
        SubButton sb = new SubButton("有子菜单");
        sb.getSub_button().add(new PhotoOrAlbumButton("传图","2222"));
        sb.getSub_button().add(new ClickButton("点击","333"));
        sb.getSub_button().add(new ViewButton("网易新闻","http://www.163.com"));
        btn.getButton().add(sb);
        JSONObject jsonObject = JSONObject.fromObject(btn);
        System.out.println(jsonObject);
    }

    /**
     * 测试TOKEN的获取
     */
    @Test
    public void testToken(){
       String accessToken = WxService.getAccessToken();
       System.out.println(accessToken);
    }

    /**
     * 测试对象转xml
     */
    @Test
    public void testMsg(){

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("ToUserName","to");
        requestMap.put("FromUserName","from");
        requestMap.put("MsgType","text");
        TextMessage tm = new TextMessage(requestMap,"你好！");

        //将对象转换为xml
        XStream stream = new XStream();
        //为解决转换后得XML字符串的根节点不是对象名或其他节点为指定名称，需要在每个子对象或属性上添加@XStreamAlias("xml"),将对象名转为xml
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(ImageMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);

        String respXML = stream.toXML(tm);

        System.out.println(respXML);
    }
}
