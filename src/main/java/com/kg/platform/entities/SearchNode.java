package com.kg.platform.entities;

public class SearchNode {
    private long nodeId;
    private String name;
    private String label;
    private double score;
    private String highLightContent;
    private String urlInfo;

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getHighLightContent() {
        return highLightContent;
    }

    public void setHighLightContent(String highLightContent) {
        this.highLightContent = highLightContent;
    }

    public String getUrlInfo() {
        return urlInfo;
    }

    public void setUrlInfo(String urlInfo) {
        this.urlInfo = urlInfo;
    }
}
