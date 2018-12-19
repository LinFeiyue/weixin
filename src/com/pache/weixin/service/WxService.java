package com.pache.weixin.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author LinLiangjia
 * @date 2018-12-16 下午9:28:06
 * @description:
 * 
 */
public class WxService {
	
	private static final String TOKEN = "dev_token";
	
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
		String[] strs = new String[]{TOKEN,timestamp,nonce};
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
