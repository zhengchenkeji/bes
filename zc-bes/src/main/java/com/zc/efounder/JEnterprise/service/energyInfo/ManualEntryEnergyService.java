package com.zc.efounder.JEnterprise.service.energyInfo;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualEntryEnergy;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualentryenergyCollection;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public interface ManualEntryEnergyService {


    List<ManualEntryEnergy> getmanualEntryEnergyData(ManualEntryEnergy manualentryenergy);

    AjaxResult insertManualentryenergy(ManualEntryEnergy manualentryenergy);

    AjaxResult treeSelect();

    AjaxResult getElectricParams(String meterid);

    AjaxResult impExcel(HttpServletRequest request, MultipartFile file) throws  Exception;


    List<ManualentryenergyCollection>  getEnergyDetailData(String entryEnergyId);

    List<ManualEntryEnergy> exportManualEntryEnergyData(ManualEntryEnergy manualentryenergy);


    AjaxResult deleteManualentryenergy(Long []ids) throws Exception;

    Collection<EnergyType> allEnergyTypeList();



}
