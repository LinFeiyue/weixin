package com.pache.weixin.service;

import com.baidu.aip.ocr.AipOcr;
import com.pache.weixin.entity.*;
import com.pache.weixin.robot.WxRobot;
import com.pache.weixin.util.Constants;
import com.pache.weixin.util.NetUtils;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.pache.weixin.util.Constants.*;

/**
 * @author LinLiangjia
 * @date 2018-12-16 下午9:28:06
 * @description:
 */
public class WxService {

    private static AccessToken at;

    /**
     * @param timestamp
     * @param nonce
     * @param signature
     * @return
     * @Description: 验证签名
     * @author LinLiangjia
     * @date 2018-12-16 下午9:32:32
     */
    public static boolean check(String timestamp, String nonce, String signature) {
        /*
         * 1）将token、timestamp、nonce三个参数进行字典序排序
         */
        String[] strs = new String[]{Constants.TOKEN, timestamp, nonce};
        Arrays.sort(strs);
        /* 2）将三个参数字符串拼接成一个字符串进行sha1加密
         */
        String str = strs[0] + strs[1] + strs[2];
        String mysign = sha1(str);
		/*System.out.println(mysign);
		System.out.println(signature);*/
        /* 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
         */
        return mysign.equalsIgnoreCase(signature);
    }

    /**
     * @return
     * @Description:进行sha1,加密
     * @author LinLiangjia
     * @date 2018-12-16 下午9:40:12
     */
    private static String sha1(String src) {

        try {
            //获取一个加密对象
            MessageDigest md = MessageDigest.getInstance("sha1");//md5加密直接输入md5
            //加密
            byte[] digest = md.digest(src.getBytes());
            //处理加密结果
            char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            StringBuilder sb = new StringBuilder();
            /*
             * 处理逻辑
             * 1、每个byte占8位
             * 2、让高四位右移并和00001111进行按位与操作，即可获取到0~15以内的数
             * 3、让整个byte直接和00001111进行按位与操作，则高四位位0，第四位也将是一个0~15以内的数
             */
            for (byte b : digest) {
                sb.append(chars[b >> 4 & 15]);
                sb.append(chars[b & 15]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理消息和事件推送
     *
     * @param is
     * @return
     */
    public static Map<String, String> parseRequest(InputStream is) {

        Map<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();
        try {
            //读取输入流，h获取文档对象
            Document document = reader.read(is);
            //根据文档对象获取根节点
            Element root = document.getRootElement();
            //获取根节点得所有子节点
            List<Element> elements = root.elements();
            for (Element element : elements) {
                map.put(element.getName(), element.getStringValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 用于处理所有的事件和消息的回复
     *
     * @param requestMap
     * @return
     */
    public static String getResponseXML(Map<String, String> requestMap) {
        BaseMessage msg = null;
        String msgType = requestMap.get("MsgType");
        switch (msgType) {
            //处理文本消息
            case "text":
                msg = dealTextMessage(requestMap);
                break;
            case "image":
                msg = dealImage(requestMap);
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
            case "event":
                msg = dealEvent(requestMap);
                break;
            default:
                break;
        }
        //把消息对象处理为xml数据包
        if (msg != null) {
            return beanToXML(msg);
        }
        return null;
    }

    /**
     * 该方法用来图片识别，读取图片里面的文字
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealImage(Map<String, String> requestMap) {

        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口【本地图片实现方式】
       /* String path = "C:\\Users\\admin\\Desktop\\2.jpg";
        org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());*/

        // 调用接口【远程图片实现方式】
        String path = requestMap.get("PicUrl");
        org.json.JSONObject res = client.basicGeneralUrl(path, new HashMap<String,String>());

        String json = res.toString();
        //然后转为这里对应的jsonObjec
        JSONObject jsonObject = JSONObject.fromObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("words_result");
        Iterator<JSONObject> it = jsonArray.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()){
            JSONObject next = it.next();
            sb.append(next.getString("words"));
        }
        return new TextMessage(requestMap,sb.toString());
    }

    /**
     * 处理事件推送
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealEvent(Map<String, String> requestMap) {
        String event = requestMap.get("Event");
        switch (event) {
            case "CLICK":
                return dealClick(requestMap);
            case "VIEW":
                return dealView(requestMap);
            default:
                break;
        }
        return null;
    }

    /**
     * 处理view类型的按钮菜单
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealView(Map<String, String> requestMap) {
        return null;
    }

    /**
     * 处理click类型的按钮菜单
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealClick(Map<String, String> requestMap) {

        String key = requestMap.get("EventKey");
        switch (key) {
            case "11111":
                //处理点击第一个一级菜单
                return new TextMessage(requestMap, "我使一级菜单");
            case "333":
                //处理点击第三个一级菜单的第二个子菜单
                return new TextMessage(requestMap, "我第三个一级菜单的第二个子菜单");
            default:
                break;
        }


        return null;
    }

    /**
     * 把bean对象处理为xml数据包
     *
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
     *
     * @param requestMap
     * @return
     */
    private static BaseMessage dealTextMessage(Map<String, String> requestMap) {
        //用户发来的内容
        String msg = requestMap.get("Content");
        //调用方法返回聊天的内容
        String respMsg = "";
        if (msg.indexOf("你是谁") > -1 || msg.indexOf("您是谁") > -1) {
            respMsg = "这里是致好科技工作室客服，请问有什么能帮到您？";
        } else if ("图文".equals(msg)) {
            List<Article> articles = new ArrayList<>();
            articles.add(new Article("这是图文的标题", "这是图文的描述", "http://mmbiz.qpic.cn/mmbiz_jpg/bdrib2b2UULa1ia3ibEXvuT85A4iccbib5hzHESdJ1K6KNqtIHLMePNzJAdGecBNZLDDU3vThgQE6txYHolGQMS4gDA/0", "https://www.baidu.com/"));
            NewsMessage nm = new NewsMessage(requestMap, articles);
            return nm;
        } else if("登陆".equals(msg)){
            String href = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1821711211bd7fd8&redirect_uri=http://zhihao.free.idcfengye.com/weixin/GetUserInfoServlet&response_type=code&scope=snsapi_userinfo#wechat_redirect";
            TextMessage tm = new TextMessage(requestMap,"点击<a href=\""+href+"\">这里</a>登陆");
            return tm;
        }else {
            respMsg = WxRobot.chat(msg);
        }

        TextMessage tm = new TextMessage(requestMap, respMsg);
        return tm;
    }

    /**
     * 获取TOKEN信息，并封装到对象中
     */
    private static void getToken() {
        String url = Constants.GET_ACCESS_TOKEN_URL.replaceAll("APPID", Constants.APPID).replaceAll("APPSECRET", Constants.APPSECRET);
        String jsonStr = NetUtils.get(url);
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        String access_token = jsonObject.getString("access_token");
        String expires_in = jsonObject.getString("expires_in");
        at = new AccessToken(access_token, expires_in);
    }

    /**
     * 更新并获取TOKEN信息
     *
     * @return
     */
    public static String getAccessToken() {
        if (at == null || at.isExpire()) {
            getToken();
        }
        return at.getAccessToken();
    }


    /**
     * 上传临时素材
     * @param path  上传文件路劲
     * @param type  上传文件类型
     * @return
     */
    public static String upload(String path,String type){
        File file = new File(path);
        //地址
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
        url = url.replace("ACCESS_TOKEN",getAccessToken()).replace("TYPE",type);
        try {
            URL obj = new URL(url);
            //强转为安全链接
            HttpsURLConnection connection = (HttpsURLConnection)obj.openConnection();
            //设置链接的信息
            connection.setDoOutput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            //设置请求头信息
            connection.setRequestProperty("Connection","Keep-Alive");
            connection.setRequestProperty("Charset","utf8");
            //数据边界
            String boundary = "-----"+System.currentTimeMillis();
            connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
            //获取输出流
            OutputStream out = connection.getOutputStream();
            //创建文件的输入流
            InputStream is = new FileInputStream(file);
            //准备数据
            //第一部分，头部信息
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition:form-data;name=\"media\";filename=\""+file.getName()+"\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");
            out.write(sb.toString().getBytes());
            System.out.println(sb.toString());
            //第二部分，文件内容
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1){
                out.write(b,0,len);
            }
            is.close();
            //第三部分，尾部信息
            String foot = "\r\n--"+boundary+"--\r\n";
            out.write(foot.getBytes());
            out.flush();
            out.close();
            //读取数据
            InputStream in = connection.getInputStream();
            StringBuilder resp = new StringBuilder();
            while ((len = in.read(b)) != -1){
                resp.append(new String(b,0,len,"UTF-8"));
            }
            return resp.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取带参数的二维码Ticket
     * @return
     */
    public static String getQrCodeTicket(){

        String at = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+at;
        //生成临时字符串二维码
        String data = "{\n" +
                "\t\"expire_seconds\": 600, \n" +
                "\t\"action_name\": \"QR_STR_SCENE\", \n" +
                "\t\"action_info\": {\n" +
                "\t\t\"scene\": {\n" +
                "\t\t\t\"scene_str\": \"nicai\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        String result = NetUtils.post(url, data);
        String ticket = JSONObject.fromObject(result).getString("ticket");
        return ticket;
    }

    /**
     * 获取已关注用户信息
     * @param openId
     * @return
     */
    public static String getUserInfo(String openId){
        String at = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        url = url.replace("ACCESS_TOKEN",at).replace("OPENID",openId);
        String result = NetUtils.get(url);
        return result;
    }

}
