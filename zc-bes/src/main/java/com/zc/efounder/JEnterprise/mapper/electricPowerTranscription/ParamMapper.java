package com.zc.efounder.JEnterprise.mapper.electricPowerTranscription;


import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.vo.ParamVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ParamMapper {


    /**
     * 查询根据电能参数  查询参数数据
     *
     * @param paramVO
     * @param name    电表系统名称
     * @return {@code Map<String, Object> }
     * @Author qindehua
     * @Date 2022/11/30
     **/
    @MapKey("time")
    Map<String, Object> selectDataByParamsCode(@Param("paramVO") ParamVO paramVO, @Param("name") String name);

    @MapKey("time")
    Map<String, Object> selectDataByParamsCodeOther(@Param("paramsId") List<String> paramsId,
                                               @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("name") String name);
    @MapKey("time")
    Map<String, Object> selectDataByParamsCodeOtherOther(@Param("paramsId") List<String> paramsId,
                                                    @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("name") String name);

}
