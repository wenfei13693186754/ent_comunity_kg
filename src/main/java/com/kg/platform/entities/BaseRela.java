package com.kg.platform.entities;


import java.util.Map;

public class BaseRela {

    private String source;
    private String target;
    private String relaType;
    private String relaName;
    private String relDirection;
    private Map<String,Object> properties;

    public String getRelaType() {
        return relaType;
    }

    public void setRelaType(String relaType) {
        this.relaType = relaType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getRelaName() {
        return relaName;
    }

    public void setRelaName(String relaName) {
        this.relaName = relaName;
    }

    public String getRelDirection() {
        return relDirection;
    }

    public void setRelDirection(String relDirection) {
        this.relDirection = relDirection;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
