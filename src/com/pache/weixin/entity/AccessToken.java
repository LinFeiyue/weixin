package com.pache.weixin.entity;

/**
 * 微信Token信息保存对象
 */
public class AccessToken {

    private String  accessToken;
    private long expiresTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;
    }

    public AccessToken(String accessToken, String expiresIn) {
        this.accessToken = accessToken;
        this.expiresTime = System.currentTimeMillis()+Integer.parseInt(expiresIn)*1000;
    }

    /**
     * 判断TOKEN是否过期
     * @return
     */
    public boolean isExpire(){
        return System.currentTimeMillis() > this.expiresTime;
    }
}
