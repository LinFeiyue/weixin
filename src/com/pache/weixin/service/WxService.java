package com.pache.weixin.service;

import com.pache.weixin.entity.*;
import com.pache.weixin.robot.WxRobot;
import com.pache.weixin.util.Constants;
import com.pache.weixin.util.NetUtils;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author LinLiangjia
 * @date 2018-12-16 下午9:28:06
 * @description:
 * 
 */
public class WxService {

    private static AccessToken at;
	
	/**
	 * @Description: 验证签名
	 * @author LinLiangjia 
	 * @date 2018-12-16 下午9:32:32
	 * @param timestamp
	 * @param nonce
	 * @param signature
	 * @return
	 */
	public static boolean check(String timestamp,String nonce,String signature){
		/*
		 * 1）将token、timestamp、nonce三个参数进行字典序排序
		 */
		String[] strs = new String[]{Constants.TOKEN,timestamp,nonce};
		Arrays.sort(strs);
		/* 2）将三个参数字符串拼接成一个字符串进行sha1加密 
		 */
		String str = strs[0]+strs[1]+strs[2];
		String mysign = sha1(str);
		/*System.out.println(mysign);
		System.out.println(signature);*/
		/* 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
		 */
		return mysign.equalsIgnoreCase(signature);
	}

	/**
	 * @Description:进行sha1,加密 
	 * @author LinLiangjia 
	 * @date 2018-12-16 下午9:40:12
	 * @return
	 */
	private static String sha1(String src) {
		
		try {
			//获取一个加密对象
			MessageDigest md = MessageDigest.getInstance("sha1");//md5加密直接输入md5
			//加密
			byte[] digest = md.digest(src.getBytes());
			//处理加密结果
			char[] chars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
			StringBuilder sb = new StringBuilder();
			/*
			 * 处理逻辑
			 * 1、每个byte占8位
			 * 2、让高四位右移并和00001111进行按位与操作，即可获取到0~15以内的数
			 * 3、让整个byte直接和00001111进行按位与操作，则高四位位0，第四位也将是一个0~15以内的数
			 */
			for(byte b : digest){
				sb.append(chars[b>>4&15]);
				sb.append(chars[b&15]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 处理消息和事件推送
	 * @param is
	 * @return
	 */
	public static Map<String, String> parseRequest(InputStream is) {

		Map<String,String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		try{
			//读取输入流，h获取文档对象
			Document document = reader.read(is);
			//根据文档对象获取根节点
			Element root = document.getRootElement();
			//获取根节点得所有子节点
			List<Element> elements =  root.elements();
			for(Element element : elements){
				map.put(element.getName(),element.getStringValue());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 用于处理所有的事件和消息的回复
	 * @param requestMap
	 * @return
	 */
	public static String getResponseXML(Map<String, String> requestMap) {
        BaseMessage msg = null;
	    String msgType = requestMap.get("MsgType");
		switch (msgType){
		    //处理文本消息
            case "text":
                msg = dealTextMessage(requestMap);
                break;
            case "image":
                break;
            case "voice":
                break;
            case "video":
                break;
            case "shortvideo":
                break;
            case "location":
                break;
            case "link":
                break;
            default:
                break;
        }
        //把消息对象处理为xml数据包
        if(msg != null){
            return beanToXML(msg);
        }
        return null;
	}

    /**
     * 把bean对象处理为xml数据包
     * @param msg
     * @return
     */
    private static String beanToXML(BaseMessage msg) {
        //将对象转换为xml
        XStream stream = new XStream();
        //设置需要处理的XStreamAlias("xml")注解类
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(ImageMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);
        stream.processAnnotations(VoiceMessage.class);

        String respXML = stream.toXML(msg);

        return respXML;
    }

    /**
     * 处理文本消息
     * @param requestMap
     * @return
     */
    private static BaseMessage dealTextMessage(Map<String, String> requestMap) {
        //用户发来的内容
        String msg = requestMap.get("Content");
        //调用方法返回聊天的内容
        String respMsg = "";
        if(msg.indexOf("你是谁") > -1 || msg.indexOf("您是谁") > -1){
            respMsg = "这里是致好科技工作室客服，请问有什么能帮到您？";
        }else if("图文".equals(msg)){
            List<Article> articles = new ArrayList<>();
            articles.add(new Article("这是图文的标题","这是图文的描述","http://mmbiz.qpic.cn/mmbiz_jpg/bdrib2b2UULa1ia3ibEXvuT85A4iccbib5hzHESdJ1K6KNqtIHLMePNzJAdGecBNZLDDU3vThgQE6txYHolGQMS4gDA/0","https://www.baidu.com/"));
            NewsMessage nm = new NewsMessage(requestMap,articles);
            return  nm;
        }else{
            respMsg = WxRobot.chat(msg);
        }

        TextMessage tm = new TextMessage(requestMap,respMsg);
        return  tm;
    }

    /**
     * 获取TOKEN信息，并封装到对象中
     */
    private static void getToken(){
        String url = Constants.GET_ACCESS_TOKEN_URL.replaceAll("APPID",Constants.APPID).replaceAll("APPSECRET",Constants.APPSECRET);
        String jsonStr = NetUtils.getJSONStrFromUrl(url);
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        String access_token = jsonObject.getString("access_token");
        String expires_in = jsonObject.getString("expires_in");
        at = new AccessToken(access_token,expires_in);
    }

    /**
     * 更新并获取TOKEN信息
     * @return
     */
    public static String getAccessToken(){
        if(at == null || at.isExpire() ){
            getToken();
        }
        return at.getAccessToken();
    }

}
