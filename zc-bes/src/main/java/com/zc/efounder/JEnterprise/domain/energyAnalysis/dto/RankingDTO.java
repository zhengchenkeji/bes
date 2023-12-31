package com.zc.efounder.JEnterprise.domain.energyAnalysis.dto;


/**
 * 排名返回类
 *
 * @author qindehua
 * @date 2022/11/28
 */
public class RankingDTO {

    /** 能源id */
    private Long id;


    /** 能耗值(kW·h) */
    private float value;

    public Long getId() {
        return id;
    }

    public void setId(Long branchIds) {
        this.id = branchIds;
    }


    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public RankingDTO(Long id,  float value) {
        this.id = id;
        this.value = value;
    }


    public RankingDTO() {

    }
}
