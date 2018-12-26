package com.pache.weixin.test;

import com.pache.weixin.entity.*;
import com.pache.weixin.service.WxService;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestWx {

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
