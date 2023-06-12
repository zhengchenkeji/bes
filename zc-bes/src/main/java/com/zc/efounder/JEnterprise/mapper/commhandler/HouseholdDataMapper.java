package com.zc.efounder.JEnterprise.mapper.commhandler;

import com.zc.efounder.JEnterprise.domain.commhandler.HouseholdData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分户数据mapper接口
 * @author LvSihan
 *
 */
@Mapper
public interface HouseholdDataMapper {


	Boolean updateHouseholdData(HouseholdData besHouseholdData);

	public void saveHouseholdData(HouseholdData besHouseholdData);

}