package com.kg.platform.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AssociatedNode extends BaseNode {

    private String industry;//企业所属行业
    private String address;//地址
    private String regcap;//注册资金
    private Date esdate;//成立日期
    private String introduce;
    private String maintainIntro;
    private boolean maintainOrNot;
    private String onlineTime;
    private String userNum;
    private String bugversion;
    private String versionReleaseTime;
    private String versionUpdateContent;
    private String contentUrl;
    private Map<Long, Map<String, Object>> relaInfo = new HashMap<>();//存储以子节点id为key,以子节点和当前节点关系属性为value的映射

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegcap() {
        return regcap;
    }

    public void setRegcap(String regcap) {
        this.regcap = regcap;
    }

    public Date getEsdate() {
        return esdate;
    }

    public void setEsdate(Date esdate) {
        this.esdate = esdate;
    }

    public Map<Long, Map<String, Object>> getRelaInfo() {
        return relaInfo;
    }

    public void setRelaInfo(Map<Long, Map<String, Object>> relaInfo) {
        this.relaInfo = relaInfo;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getMaintainIntro() {
        return maintainIntro;
    }

    public void setMaintainIntro(String maintainIntro) {
        this.maintainIntro = maintainIntro;
    }

    public boolean isMaintainOrNot() {
        return maintainOrNot;
    }

    public void setMaintainOrNot(boolean maintainOrNot) {
        this.maintainOrNot = maintainOrNot;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getBugversion() {
        return bugversion;
    }

    public void setBugversion(String bugversion) {
        this.bugversion = bugversion;
    }

    public String getVersionReleaseTime() {
        return versionReleaseTime;
    }

    public void setVersionReleaseTime(String versionReleaseTime) {
        this.versionReleaseTime = versionReleaseTime;
    }

    public String getVersionUpdateContent() {
        return versionUpdateContent;
    }

    public void setVersionUpdateContent(String versionUpdateContent) {
        this.versionUpdateContent = versionUpdateContent;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
