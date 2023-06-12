package com.zc.efounder.JEnterprise.mapper.commhandler;

import com.zc.efounder.JEnterprise.domain.commhandler.HouseholdSubitemData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分户分项数据mapper接口
 * @author LvSihan
 *
 */
@Mapper
public interface HouseholdSubitemDataMapper {


	Boolean updateHouseholdGradeData(HouseholdSubitemData besHouseholdSubitemData);

	public void saveHouseholdGradeData(HouseholdSubitemData besHouseholdSubitemData);

}