package com.zc.efounder.JEnterprise.mapper.commhandler;

import com.zc.efounder.JEnterprise.domain.commhandler.BesBranchData;
import com.zc.efounder.JEnterprise.domain.commhandler.BesEnergyData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 能源数据Mapper接口
 * @author LvSihan
 *
 */
@Mapper
public interface EnergyDataMapper{

	BesEnergyData queryEnergyExists(BesEnergyData besEnergyData);

	public void saveEnergyData(BesEnergyData besEnergyData);

	public void updateEnergy(BesEnergyData besEnergyData);

	Boolean updateEnergyData(BesEnergyData besEnergyData);

	Boolean updateBranchData(BesBranchData besBranchData);

	public void saveBranchData(BesBranchData besBranchData);

	/**
	 * @description:查询是否有大于当前采集时间的数据项
	 * @author: sunshangeng
	 **/
	Integer queryEnergyGtCjsj(BesEnergyData besEnergyData);

	/**
	 * @description:查询是否有等于当前采集时间的数据项
	 * @author: sunshangeng
	 **/
	Integer queryEnergyEqCjsj(BesEnergyData besEnergyData);
	/**
	 * @description:查询是否有大于当前采集时间的支路数据项
	 * @author: sunshangeng
	 **/
	Integer queryBrancGtCjsj(BesBranchData besEnergyData);

	/**
	 * @description:查询是否有等于当前采集时间的支路数据项
	 * @author: sunshangeng
	 **/
	Integer queryBrancEqCjsj(BesBranchData besEnergyData);

	/**
	 * @description:查询是否有大于当前采集时间的原始数据项
	 * @author: sunshangeng
	 * @param dbbh 电表编号,cjsj 采集时间
	 **/
	Integer queryoriginalGtCjsj(@Param("dbbh") String dbbh,@Param("cjsj") String cjsj);




}
