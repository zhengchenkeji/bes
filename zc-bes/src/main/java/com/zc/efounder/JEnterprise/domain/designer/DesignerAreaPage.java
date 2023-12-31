package com.zc.efounder.JEnterprise.domain.designer;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * @Author: wanghongjie
 * @Description:设计器区域页面对象
 * @Date: Created in 8:43 2022/10/22
 * @Modified By:
 */
public class DesignerAreaPage extends BaseEntity {

    /**
     *主键id
     */
    private Long id;

    /**
     *区域id
     */
    private Long areaId;

    /**
     *区域名称
     */
    private String name;

    /**
     *画布数据
     */
    private String canvasData;

    /**
     *画布样式
     */
    private String canvasStyle;

    /**
     *截图路径
     */
    private String uploadDesignerScreenshot;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getCanvasData() {
        return canvasData;
    }

    public void setCanvasData(String canvasData) {
        this.canvasData = canvasData;
    }

    public String getCanvasStyle() {
        return canvasStyle;
    }

    public void setCanvasStyle(String canvasStyle) {
        this.canvasStyle = canvasStyle;
    }

    public String getUploadDesignerScreenshot() {
        return uploadDesignerScreenshot;
    }

    public void setUploadDesignerScreenshot(String uploadDesignerScreenshot) {
        this.uploadDesignerScreenshot = uploadDesignerScreenshot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
