package com.pache.weixin.util;

import com.pache.weixin.entity.*;
import com.pache.weixin.service.WxService;
import net.sf.json.JSONObject;

public class CreateMenu {

    public static void main(String[] arg0){
        Button btn = new Button();
        btn.getButton().add(new ClickButton("菜单一","11111"));
        btn.getButton().add(new ViewButton("菜单二","http://www.baidu.com"));
        SubButton sb = new SubButton("有子菜单");
        sb.getSub_button().add(new PhotoOrAlbumButton("传图","2222"));
        sb.getSub_button().add(new ClickButton("点击","333"));
        sb.getSub_button().add(new ViewButton("网易新闻","http://www.163.com"));
        btn.getButton().add(sb);
        JSONObject jsonObject = JSONObject.fromObject(btn);
        //准备url
        String url = Constants.CUSTOM_MENU_URL.replaceAll("ACCESS_TOKEN", WxService.getAccessToken());
        //发送请求并返回数据
        String result = NetUtils.post(url,jsonObject.toString());
        System.out.println(result);
    }
}
