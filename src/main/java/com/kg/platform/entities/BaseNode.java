package com.kg.platform.entities;

import java.util.Map;

/**
 * 节点的基类，用于定义节点共性的属性
 */
public class BaseNode {
    private String id;//节点id
    private String jxId;
    private String name;//节点名称
    private String nodeType;//节点类型
    private String category;//节点类型中文名
    private Map<String,Object> properties;//节点属性

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJxId() {
        return jxId;
    }

    public void setJxId(String jxId) {
        this.jxId = jxId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
