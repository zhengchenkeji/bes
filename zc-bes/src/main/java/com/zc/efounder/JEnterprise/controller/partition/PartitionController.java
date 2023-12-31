package com.zc.efounder.JEnterprise.controller.partition;

import com.zc.efounder.JEnterprise.domain.partition.PartitionInfoModel;
import com.zc.efounder.JEnterprise.domain.partition.PartitionParamModel;
import com.zc.efounder.JEnterprise.domain.partition.PartitionSingleModel;
import com.zc.efounder.JEnterprise.service.partition.PartitionService;
import com.zc.common.core.model.DataReception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Controller
public class PartitionController {

    @Resource
    public PartitionService partitionService;

    @Value("${table.partition.enable}")
    private Boolean partitionEnable;



    @Scheduled(cron = "0 0 0 1 * ?")
    public void tablePartition() {

        if (!partitionEnable) {
            return;
        }

        String rangeField = "F_CJSJ";

        setTablePartition("athena_bes_original_data", rangeField);
        setTablePartition("athena_bes_calculate_data", rangeField);
        setTablePartition("athena_bes_monitoring_data", rangeField);
        setTablePartition("athena_bes_branch_data", rangeField);
        setTablePartition("athena_bes_energy_data", rangeField);
        setTablePartition("athena_bes_household_data", "collect_time");
        setTablePartition("athena_bes_subitem_data", "collect_time");
        setTablePartition("athena_bes_household_subitem_data", "collect_time");
        //数据项历史记录表
        setTablePartition("athena_bes_product_item_data_history", "create_time");
        //数据项报警记录
        setTablePartition("athena_bes_product_item_data_warn", "happen_time");

    }

    public void setTablePartition(String tableName, String rangeField) {

        if (tableName == null || rangeField == null) {
            return;
        }

        DataReception queryPartitionResult = partitionService.queryPartition(tableName, null);

        if (!"1".equals(queryPartitionResult.getCode())) {
            return;
        }

        List<PartitionInfoModel> partitionInfoModels = (List<PartitionInfoModel>) queryPartitionResult.getData();

        if (partitionInfoModels == null) {
            return;
        }

        Calendar calendar= Calendar.getInstance();

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");

        if (partitionInfoModels.size() == 1) {

            PartitionParamModel partitionParamModel = new PartitionParamModel();

            partitionParamModel.setTableName(tableName);
            partitionParamModel.setRangeField(rangeField);

            List<PartitionSingleModel> partitionSingleModelList = new ArrayList<>();

            PartitionSingleModel partitionSingleModel1 = new PartitionSingleModel();

            partitionSingleModel1.setPartitionName("p" + dateFormat1.format(calendar.getTime()));
            partitionSingleModel1.setFieldValue(dateFormat2.format(calendar.getTime()));

            PartitionSingleModel partitionSingleModel2 = new PartitionSingleModel();

            calendar.add(Calendar.MONTH, 1);

            partitionSingleModel2.setPartitionName("p" + dateFormat1.format(calendar.getTime()));
            partitionSingleModel2.setFieldValue(dateFormat2.format(calendar.getTime()));

            calendar.add(Calendar.MONTH, 1);

            PartitionSingleModel partitionSingleModel3 = new PartitionSingleModel();

            partitionSingleModel3.setPartitionName("p" + dateFormat1.format(calendar.getTime()));
            partitionSingleModel3.setFieldValue(dateFormat2.format(calendar.getTime()));

            partitionSingleModelList.add(partitionSingleModel1);
            partitionSingleModelList.add(partitionSingleModel2);
            partitionSingleModelList.add(partitionSingleModel3);

            partitionParamModel.setPartitionSingleList(partitionSingleModelList);

            partitionService.createPartition(partitionParamModel);

            return;
        }

        calendar.add(Calendar.MONTH, 2);

        String partitionName = "p" + dateFormat1.format(calendar.getTime());

        for (PartitionInfoModel partitionInfoModel : partitionInfoModels) {
            if (partitionName.equals(partitionInfoModel.getPartitionName())) {
                return;
            }
        }

        PartitionParamModel partitionParamModel = new PartitionParamModel();

        partitionParamModel.setTableName(tableName);

        PartitionSingleModel partitionSingleModel = new PartitionSingleModel();

        partitionSingleModel.setPartitionName("p" + dateFormat1.format(calendar.getTime()));
        partitionSingleModel.setFieldValue(dateFormat2.format(calendar.getTime()));

        List<PartitionSingleModel> partitionSingleModelList = new ArrayList<>();
        partitionSingleModelList.add(partitionSingleModel);

        partitionParamModel.setPartitionSingleList(partitionSingleModelList);

        partitionService.addPartition(partitionParamModel);

    }

}
