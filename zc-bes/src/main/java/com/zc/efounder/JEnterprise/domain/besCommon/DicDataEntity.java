package com.zc.efounder.JEnterprise.domain.besCommon;

/**
 * description:公用字典实体
 * author: sunshangeng
 * date:2022/9/19 14:43
 */
public class DicDataEntity {

    public DicDataEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public DicDataEntity() {
    }

    /**
     * 字典键
     * */
    private String code;

    /**
     * 字典值
     * */
    private  String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
