package com.zc.efounder.JEnterprise.mapper.commhandler;

import com.zc.efounder.JEnterprise.domain.commhandler.SubitemData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分项数据mapper接口
 * @author LvSihan
 *
 */
@Mapper
public interface SubitemDataMapper{


	Boolean updateGradeData(SubitemData besSubitemData);

	public void saveGradeData(SubitemData besSubitemData);

	/**
	 * 查询分项数据根据时间和类型
	 *
	 * @param time 时间
	 * @param type 类型
	 * @return {@code List<SubitemData> }
	 * @Author qindehua
	 * @Date 2022/11/10
	 **/
	List<SubitemData> getSubitemDataByTimeAndType(@Param("time") String time,@Param("type") String type);

}
