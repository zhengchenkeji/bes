package com.zc.efounder.JEnterprise.service.electricPowerTranscription;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.vo.ParamVO;

import javax.servlet.http.HttpServletResponse;

/**
 * 电参数
 *
 * @author qindehua
 * @date 2022/11/29
 */
public interface ParamService {


    /**
     * 查询电表下面的电能参数
     *
     * @param id 电表树id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/29
     **/
    AjaxResult getMeterParams(Long id);

    /**
     * 查询根据电能参数  查询参数数据
     *
     * @param paramVO
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/30
     **/
    AjaxResult getDataByParamsCode(ParamVO paramVO);

    /**
     * 导出excel
     *
     * @param response 响应
     * @param paramVO
     * @Author qindehua
     * @Date 2022/11/30
     **/
    void exportExcel(HttpServletResponse response , ParamVO paramVO);

    /**
     * 批量导出
     *
     * @param response 响应
     * @param paramVO
     * @Author qindehua
     * @Date 2022/12/01
     **/
    void exportExcelBatch(HttpServletResponse response,  ParamVO paramVO);
}
