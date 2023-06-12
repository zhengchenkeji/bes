package com.zc.efounder.JEnterprise.controller.designer;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.zc.efounder.JEnterprise.domain.designer.DesignerArea;
import com.zc.efounder.JEnterprise.domain.designer.DesignerAreaPage;
import com.zc.efounder.JEnterprise.service.designer.DesignerAreaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: wanghongjie
 * @Description:设计器区域配置
 * @Date: Created in 8:32 2022/10/20
 * @Modified By:
 */
@RestController
@RequestMapping("/designerConf/designerArea")
public class DesignerAreaController {

    @Resource
    private DesignerAreaService designerAreaService;
    /**
     * 获取所有的区域信息
     * @param event
     * @return
     */
    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:designerAreaListInfo')")
    @PostMapping("/designerAreaListInfo")
    public AjaxResult listInfo(DesignerArea designerArea)
    {
        AjaxResult ajaxResult = designerAreaService.listInfo(designerArea);
        return ajaxResult;
    }

    /**
     * 添加区域
     * @param event
     * @return
     */
    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:addDesignerArea')")
    @GetMapping("/addDesignerArea")
    public AjaxResult addDesignerArea(DesignerArea designerArea)
    {
        AjaxResult ajaxResult = designerAreaService.addDesignerArea(designerArea);
        return ajaxResult;
    }

    /**
     * 修改区域
     * @param event
     * @return
     */
    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:updateDesignerArea')")
    @GetMapping("/updateDesignerArea")
    public AjaxResult updateDesignerArea(DesignerArea designerArea)
    {
        AjaxResult ajaxResult = designerAreaService.updateDesignerArea(designerArea);
        return ajaxResult;
    }

    /**
     * 删除区域
     * @param event
     * @return
     */


    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:deleteDesignerArea')")
    @Log(title = "删除设计器区域节点", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult deleteDesignerArea(@PathVariable("ids") Long[] ids)
    {
        AjaxResult ajaxResult = designerAreaService.deleteDesignerArea(ids);
        return ajaxResult;
    }

    /**
     *
     * @Description: 根据区域id获取设计器页面信息
     *
     * @auther: wanghongjie
     * @date: 9:32 2022/10/22
     * @param:
     * @return:
     *
     */
    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:seleteDesignerAreaPage')")
    @GetMapping("/seleteDesignerAreaPage")
    public AjaxResult seleteDesignerAreaPage(DesignerAreaPage designerAreaPage)
    {
        AjaxResult ajaxResult = designerAreaService.seleteDesignerAreaPage(designerAreaPage);
        return ajaxResult;
    }

    /**
     * 保存设计器区域信息(根据区域id)
     * @param event
     * @return
     */
    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:addDesignerAreaPage')")
    @RequestMapping(value = "/addDesignerAreaPage",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addDesignerAreaPage(@RequestBody DesignerAreaPage designerAreaPage)
    {
        AjaxResult ajaxResult = designerAreaService.addDesignerAreaPage(designerAreaPage);
        return ajaxResult;
    }

    /**
     * 修改设计器区域信息(根据区域id和页面id)
     * @param event
     * @return
     */
    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:updateDesignerAreaPage')")
    @RequestMapping(value = "/updateDesignerAreaPage",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updateDesignerAreaPage(@RequestBody DesignerAreaPage designerAreaPage)
    {
        AjaxResult ajaxResult = designerAreaService.updateDesignerAreaPage(designerAreaPage);
        return ajaxResult;
    }

    /**
     *
     * @Description: 删除设计器页面信息(根据区域id和页面id)
     *
     * @auther: wanghongjie
     * @date: 9:32 2022/11/2
     * @param:
     * @return:
     *
     */
    @PreAuthorize("@ss.hasPermi('designerConf:designerArea:deleteDesignerAreaPage')")
    @GetMapping("/deleteDesignerAreaPage")
    public AjaxResult deleteDesignerAreaPage(DesignerAreaPage designerAreaPage)
    {
        AjaxResult ajaxResult = designerAreaService.deleteDesignerAreaPage(designerAreaPage);
        return ajaxResult;
    }

    /**
     * 设计器图片上传
     */
    @Log(title = "设计器图片上传", businessType = BusinessType.UPDATE)
    @PostMapping("/designerPicture")
    public AjaxResult designerPicture(@RequestParam("picturefile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            String designerPicture = FileUploadUtils.upload(RuoYiConfig.getDesignerPicturePath(), file, MimeTypeUtils.IMAGE_EXTENSION);

            System.out.println(designerPicture);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("imgUrl", designerPicture);
            return ajax;

        }
        return AjaxResult.error("上传图片异常，请联系管理员");
    }

    /**
     * 设计器截图上传
     */
    @Log(title = "设计器截图上传", businessType = BusinessType.UPDATE)
    @PostMapping("/uploadDesignerScreenshot")
    public AjaxResult uploadDesignerScreenshot(@RequestParam("picturefile") MultipartFile file,
                                               @RequestParam("vue_app_base_api") String vue_app_base_api,
                                               @RequestParam("areaId") Long areaId,
                                               @RequestParam("pageId") Long pageId) throws Exception
    {
        if (!file.isEmpty())
        {
            AjaxResult ajaxResult = designerAreaService.uploadDesignerScreenshot(file,vue_app_base_api,areaId,pageId);
            return ajaxResult;

        }
        return AjaxResult.error("上传图片异常，请联系管理员");
    }

    /**
     * 设计器截图上传
     */
    @Log(title = "设计器复制")
    @PostMapping("/CopyDesignerAreaPage")
    public AjaxResult CopyDesignerAreaPage( @RequestBody DesignerAreaPage designerAreaPage) throws Exception
    {
        return designerAreaService.copyDesignerAreaPage(designerAreaPage);
    }
}
