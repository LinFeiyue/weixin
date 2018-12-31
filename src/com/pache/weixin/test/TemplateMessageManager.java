package com.pache.weixin.test;

import com.pache.weixin.service.WxService;
import com.pache.weixin.util.NetUtils;
import org.junit.Test;

public class TemplateMessageManager {

    /**
     * 设置行业
     */
    @Test
    public void set(){

        String at = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token="+at;

        String data = "{\n" +
                "          \"industry_id1\":\"1\",\n" +
                "          \"industry_id2\":\"4\"\n" +
                "       }";
        String res = NetUtils.post(url,data);

        System.out.println(res);
    }

    /**
     * 获取行业类型
     */
    @Test
    public void get(){
        String at = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token="+at;
        String res = NetUtils.get(url);
        System.out.println(res);
    }

    /**
     * 发送模板消息
     */
    @Test
    public void sendTemplateMsg(){

        String at = WxService.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+at;

        String data = "{\n" +
                "           \"touser\":\"o_NLw0p4cQODAXW5Ybtl7PHB6-iE\",\n" +
                "           \"template_id\":\"1FLnvjN-125_W32mpdOxsQedjhSNf-C_9QRU0vw_QrQ\",             \n" +
                "           \"data\":{\n" +
                "                   \"first\": {\n" +
                "                       \"value\":\"您的简历有新的反馈信息啦！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"company\":{\n" +
                "                       \"value\":\"致好科技工作室\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"time\": {\n" +
                "                       \"value\":\"2018-12-29\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"result\": {\n" +
                "                       \"value\":\"您已被本公司录用\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"remark\":{\n" +
                "                       \"value\":\"请联系本公司电话：010-5201314\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   }\n" +
                "           }\n" +
                "       }";

        String res = NetUtils.post(url,data);

        System.out.println(res);
    }
}
