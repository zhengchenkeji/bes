package com.zc.efounder.JEnterprise.service.designer;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.zc.efounder.JEnterprise.domain.designer.DesignerArea;
import com.zc.efounder.JEnterprise.domain.designer.DesignerAreaPage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 8:37 2022/10/20
 * @Modified By:
 */
public interface DesignerAreaService {
    /**
     *
     * @Description: 获取所有的区域信息
     *
     * @auther: wanghongjie
     * @date: 9:02 2022/10/20
     * @param: [DesignerArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult listInfo(DesignerArea designerArea);

    /**
     *
     * @Description: 添加区域
     *
     * @auther: wanghongjie
     * @date: 9:18 2022/10/20
     * @param: [designerArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult addDesignerArea(DesignerArea designerArea);

    /**
     *
     * @Description: 修改区域
     *
     * @auther: wanghongjie
     * @date: 8:54 2022/10/22
     * @param: [designerArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult updateDesignerArea(DesignerArea designerArea);


    /**
     *
     * @Description: 删除设计器区域节点
     *
     * @auther: wanghongjie
     * @date: 9:00 2022/10/22
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     * @param id
     */
    AjaxResult deleteDesignerArea(Long[] id);

    /**
     *
     * @Description: 保存设计器区域信息(根据区域id)
     *
     * @auther: wanghongjie
     * @date: 8:55 2022/10/22
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult addDesignerAreaPage(DesignerAreaPage designerAreaPage);


    /**
     *
     * @Description: 根据区域id获取设计器页面信息
     *
     * @auther: wanghongjie
     * @date: 9:34 2022/10/22
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult seleteDesignerAreaPage(DesignerAreaPage designerAreaPage);

    /**
     *
     * @Description: 修改设计器区域信息(根据区域id和页面id)
     *
     * @auther: wanghongjie
     * @date: 11:46 2022/10/22
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult updateDesignerAreaPage(DesignerAreaPage designerAreaPage);

    /**
     *
     * @Description: 设计器截图上传
     *
     * @auther: wanghongjie
     * @date: 10:00 2022/10/29
     * @param: [file, areaId, pageId]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult uploadDesignerScreenshot(MultipartFile file, String vue_app_base_api, Long areaId, Long pageId) throws IOException, InvalidExtensionException;

    /**
     *
     * @Description: 删除设计器页面信息(根据区域id和页面id)
     *
     * @auther: wanghongjie
     * @date: 9:10 2022/11/2
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult deleteDesignerAreaPage(DesignerAreaPage designerAreaPage);

    void sendRegistrationMessage(String s);

    AjaxResult copyDesignerAreaPage(DesignerAreaPage designerAreaPage);
}
