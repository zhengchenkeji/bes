package com.zc.efounder.JEnterprise.service.energyAnalysis;

import com.ruoyi.common.core.domain.AjaxResult;

import java.util.Date;

public interface reportFormService {

    AjaxResult service(Long[] branchIds , Date[] times);
}
