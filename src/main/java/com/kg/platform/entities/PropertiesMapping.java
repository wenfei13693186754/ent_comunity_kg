package com.kg.platform.entities;

import java.util.LinkedHashMap;

public class PropertiesMapping {

    public static LinkedHashMap entProperties = new LinkedHashMap<String,String>(){
        {
            put("name","公司名称");
            put("createTime","公司创立时间");
            put("introduce","公司简介");
            put("history","公司历程");
            put("culture","企业文化");
        }
    };

    public static LinkedHashMap personProperties = new LinkedHashMap<String,String>(){
        {
            put("name","员工姓名");
            put("personIntroduce","个人介绍");
            put("personEntryTime","员工入职时间");
            put("personJob","岗位");
            put("personJobChange","岗位变动历程");
            put("personJobDuty","岗位职责");
            put("personJobContribute","岗位杰出贡献");
        }
    };
    public static LinkedHashMap industryProperties = new LinkedHashMap<String,String>(){
        {
            put("name","行业名称");
            put("industryIntroduce","行业介绍");
        }
    };
    public static LinkedHashMap productProperties = new LinkedHashMap<String,String>(){
        {
            put("name","产品名称");
            put("introduce","产品简介");
            put("productVersion","版本");
            put("maintainOrNot","是否在维护");
            put("maintainIntro","维护说明");
            put("releaseTime","上线时间");
            put("versionReleaseTime","版本发布时间线");
            put("versionUpdateContent","每个发布版本的更新内容");
            put("usedNum","在用客户数");
            put("enterprise","产品所属公司");
        }
    };
    public static LinkedHashMap categoryProperties = new LinkedHashMap<String,String>(){
        {
            put("name","分类节点名称");
            put("introduce","分类节点介绍");
        }
    };
    public static LinkedHashMap productLineProperties = new LinkedHashMap<String,String>(){
        {
            put("name","产品线名称");
            put("introduce","产品线介绍");
        }
    };

    public static LinkedHashMap areaProperties = new LinkedHashMap<String,String>(){
        {
            put("name","地域名称");
            put("introduce","地域介绍");
        }
    };
}