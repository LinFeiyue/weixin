package com.pache.weixin.servlet;

import com.pache.weixin.util.Constants;
import com.pache.weixin.util.NetUtils;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetUserInfoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    /**
     * 当用户点击登陆时，已关注用户直接跳转到这里，未关注的用户授权后跳转到这里，跳转到此处时，可以获取用户信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("GetUserInfo");
        //1、用户确认授权后，获取code
        String code = request.getParameter("code");
        //2、通过code换取access_token
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url = url.replace("APPID", Constants.APPID).replace("SECRET",Constants.APPSECRET);
        String json = NetUtils.get(url);
        JSONObject jsonObject = JSONObject.fromObject(json);
        //3、刷新access_token【忽略，请参考文档】
        //4、拉取用户信息
        String access_token = jsonObject.getString("access_token");
        String openId = jsonObject.getString("openid");
        url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        url = url.replace("ACCESS_TOKEN", access_token).replace("OPENID", openId);
        json = NetUtils.get(url);

        //输出的就是用户信息
        System.out.println(json);

    }
}
