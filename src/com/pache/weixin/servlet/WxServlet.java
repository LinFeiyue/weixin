package com.pache.weixin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pache.weixin.service.WxService;

/**
 * @author LinLiangjia
 * @date 2018-12-16 下午12:13:18
 * @description:
 * 
 */
@SuppressWarnings("serial")
public class WxServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
		/*
		 * 开发者提交信息后，微信服务器将发送GET请求到填写的服务器地址URL上
		 *  signature
			微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
			timestamp
			时间戳
			nonce
			随机数
			echostr
			随机字符串
		 */
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		System.out.println(echostr);
		/*
		System.out.println(signature);
		System.out.println(timestamp);
		System.out.println(nonce);
		System.out.println(echostr);
		*/
		//校验请求
		if(WxService.check(timestamp, nonce, signature)){
			System.out.println("接入成功！");
			//原样返回echostr参数
			out.write(echostr);
			out.flush();
			out.close();
		}else{
			System.out.println("接入失败！");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("post");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		//查看用户发送到服务器得普通消息
//		lookMsgInfo(request,response);

		Map<String,String> requestMap = WxService.parseRequest(request.getInputStream());

		System.out.println(requestMap);
	}

	/**
	 * 查看用户发送得普通消息
	 * @param request
	 * @param response
	 */
	private void lookMsgInfo(HttpServletRequest request, HttpServletResponse response){
		try{
			ServletInputStream ss = request.getInputStream();
			byte[] b = new byte[1024];
			int len;
			StringBuilder sb = new StringBuilder();
			while((len = ss.read(b)) != -1){
				sb.append(new String(b,0,len,"UTF-8"));    //要加上编码，否则会出现乱码情况
			}
			System.out.println(sb.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
