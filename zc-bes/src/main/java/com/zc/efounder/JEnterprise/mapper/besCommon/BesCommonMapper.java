package com.zc.efounder.JEnterprise.mapper.besCommon;

import com.zc.efounder.JEnterprise.domain.besCommon.BESEnergyType;

import java.util.List;

public interface BesCommonMapper {

    /**
     * 查询所有能耗类型
     */
    List<BESEnergyType> getEnergyTypeList(String yqbh);

}
