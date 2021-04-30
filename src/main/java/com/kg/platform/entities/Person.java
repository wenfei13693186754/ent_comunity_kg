package com.kg.platform.entities;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "Person")
public class Person {

    @Id
    private Long jx_id;
    private String identity_num;
    private String name;
    private String gender;
    private String birthday;
    private String ethnic;
    private String nation;
    private String id_card_type;
    private String id_card_num;
    private String id_card_address;
    private String create_by;
    private String create_time;
    private String update_by;
    private String update_time;

    public Long getJx_id() {
        return jx_id;
    }

    public void setJx_id(Long jx_id) {
        this.jx_id = jx_id;
    }

    public String getIdentity_num() {
        return identity_num;
    }

    public void setIdentity_num(String identity_num) {
        this.identity_num = identity_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEthnic() {
        return ethnic;
    }

    public void setEthnic(String ethnic) {
        this.ethnic = ethnic;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getId_card_type() {
        return id_card_type;
    }

    public void setId_card_type(String id_card_type) {
        this.id_card_type = id_card_type;
    }

    public String getId_card_num() {
        return id_card_num;
    }

    public void setId_card_num(String id_card_num) {
        this.id_card_num = id_card_num;
    }

    public String getId_card_address() {
        return id_card_address;
    }

    public void setId_card_address(String id_card_address) {
        this.id_card_address = id_card_address;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
