package com.zc.efounder.JEnterprise.service.designer.Impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.zc.common.constant.RedisChannelConstants;
import com.zc.common.core.rabbitMQ.MessagingService;
import com.zc.common.core.redis.pubsub.RedisPubSub;
import com.zc.efounder.JEnterprise.domain.designer.DesignerArea;
import com.zc.efounder.JEnterprise.domain.designer.DesignerAreaPage;
import com.zc.efounder.JEnterprise.mapper.designer.DesignerAreaMapper;
import com.zc.efounder.JEnterprise.service.designer.DesignerAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 8:37 2022/10/20
 * @Modified By:
 */
@Service
public class DesignerAreaServiceImpl implements DesignerAreaService {

    @Resource
    private DesignerAreaMapper designerAreaMapper;

    private static final RedisPubSub redisPubSub = SpringUtils.getBean(RedisPubSub.class);

    /**
     * @Description: 获取所有的区域信息
     * @auther: wanghongjie
     * @date: 9:03 2022/10/20
     * @param: [DesignerArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult listInfo(DesignerArea designerArea) {
        List<DesignerArea> designerAreaList = designerAreaMapper.listInfo(designerArea);

        return AjaxResult.success("获取区域信息成功", designerAreaList);
    }

    /**
     * @Description: 添加区域
     * @auther: wanghongjie
     * @date: 9:19 2022/10/20
     * @param: [designerArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult addDesignerArea(DesignerArea designerArea) {

        designerArea.setCreateBy(getUsername());
        designerArea.setCreateTime(DateUtils.getNowDate());
        int addDesignerArea = designerAreaMapper.addDesignerArea(designerArea);

        if (addDesignerArea > 0) {
            return AjaxResult.success("添加成功", designerArea);
        }
        return AjaxResult.error("添加失败");
    }

    /**
     * @Description: 修改区域
     * @auther: wanghongjie
     * @date: 8:55 2022/10/22
     * @param: [designerArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult updateDesignerArea(DesignerArea designerArea) {

        Long id = designerArea.getId();
        String name = designerArea.getName();

        designerArea.setUpdateBy(getUsername());
        designerArea.setUpdateTime(DateUtils.getNowDate());

        if (id == null || name == null || name.isEmpty()) {
            return AjaxResult.error("参数错误");
        }
        int addDesignerArea = designerAreaMapper.updateDesignerArea(designerArea);

        Gson gson = new Gson();
        Type type = new TypeToken<DesignerArea>() {
        }.getType();

        String msg = gson.toJson(designerArea, type);

        redisPubSub.publish(RedisChannelConstants.TEST_SUB_DEMO, msg);

        if (addDesignerArea > 0) {
            return AjaxResult.success("修改成功", designerArea);
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * @param id
     * @Description: 删除设计器区域节点
     * @auther: wanghongjie
     * @date: 9:00 2022/10/22
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult deleteDesignerArea(Long[] id) {


        if (id == null || id.length == 0) {
            return AjaxResult.error("参数错误");
        }
        for (int i = 0; i < id.length; i++) {
            int addDesignerArea = designerAreaMapper.deleteDesignerArea(id[i]);
            if (addDesignerArea > 0) {
                //删除区域关联的页面
                designerAreaMapper.deleteDesignerAreaPageByAreaId(id[i]);

            }
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * @Description: 根据区域id获取设计器页面信息
     * @auther: wanghongjie
     * @date: 9:35 2022/10/22
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult seleteDesignerAreaPage(DesignerAreaPage designerAreaPage) {
        Long areaId = designerAreaPage.getAreaId();
        if (areaId == null) {
            return AjaxResult.error("获取设计器页面信息失败!");
        }

        List<DesignerAreaPage> areaPage = designerAreaMapper.seleteDesignerAreaPage(designerAreaPage);

        return AjaxResult.success("获取设计器页面信息成功", areaPage);
    }


    /**
     * @Description: 保存设计器区域信息(根据区域id)
     * @auther: wanghongjie
     * @date: 8:55 2022/10/22
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult addDesignerAreaPage(@RequestBody DesignerAreaPage designerAreaPage) {

        Long areaId = designerAreaPage.getAreaId();
        String name = designerAreaPage.getName();
        String canvasData = designerAreaPage.getCanvasData();
        String canvasStyle = designerAreaPage.getCanvasStyle();

        designerAreaPage.setCreateBy(getUsername());
        designerAreaPage.setCreateTime(DateUtils.getNowDate());

        if (areaId == null) {
            return AjaxResult.error("保存失败,区域节点id不存在,请检查!");
        }
        if (name == null || name.isEmpty()) {
            return AjaxResult.error("修改失败,名称不存在,请检查!");
        }

//        if (canvasData == null || canvasData.isEmpty() ||
//                canvasStyle == null || canvasStyle.isEmpty()) {
//            return AjaxResult.error("保存失败,页面信息不存在,请检查!");
//        }

        designerAreaPage.setCreateBy(getUsername());
        designerAreaPage.setCreateTime(DateUtils.getNowDate());

        int addDesignerAreaPage = designerAreaMapper.addDesignerAreaPage(designerAreaPage);

        if (addDesignerAreaPage > 0) {
            return AjaxResult.success("保存成功", designerAreaPage);
        }

        return AjaxResult.error("保存失败");
    }

    /**
     * @Description: 修改设计器区域信息(根据区域id和页面id)
     * @auther: wanghongjie
     * @date: 11:47 2022/10/22
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult updateDesignerAreaPage(DesignerAreaPage designerAreaPage) {
        Long id = designerAreaPage.getId();
        Long areaId = designerAreaPage.getAreaId();
        String name = designerAreaPage.getName();
        String canvasData = designerAreaPage.getCanvasData();
        String canvasStyle = designerAreaPage.getCanvasStyle();

        if (areaId == null || id == null) {
            return AjaxResult.error("修改失败,请检查!");
        }
        if (name == null || name.isEmpty()) {
            return AjaxResult.error("修改失败,名称不存在,请检查!");
        }

//        if (canvasData == null || canvasData.isEmpty() ||
//                canvasStyle == null || canvasStyle.isEmpty()) {
//            return AjaxResult.error("修改失败,页面信息不存在,请检查!");
//        }

        designerAreaPage.setUpdateBy(getUsername());
        designerAreaPage.setUpdateTime(DateUtils.getNowDate());

        int addDesignerAreaPage = designerAreaMapper.updateDesignerAreaPage(designerAreaPage);

        if (addDesignerAreaPage > 0) {
            return AjaxResult.success("修改成功", designerAreaPage);
        }

        return AjaxResult.error("修改失败");
    }

    /**
     * @Description: 设计器截图上传
     * @auther: wanghongjie
     * @date: 10:00 2022/10/29
     * @param: [file, areaId, pageId]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult uploadDesignerScreenshot(MultipartFile file, String vue_app_base_api, Long areaId, Long pageId) throws IOException, InvalidExtensionException {


        DesignerAreaPage designerAreaPage = new DesignerAreaPage();

        if (areaId == null ||
                pageId == null) {
            return AjaxResult.error("上传图片异常，请联系管理员");
        }
        designerAreaPage.setAreaId(areaId);
        designerAreaPage.setId(pageId);

        String designerPicture = FileUploadUtils.upload(RuoYiConfig.getDesignerPicturePath(), file, MimeTypeUtils.IMAGE_EXTENSION);

        String filePath = vue_app_base_api + designerPicture;

        designerAreaPage.setUploadDesignerScreenshot(filePath);

        designerAreaPage.setUpdateBy(getUsername());
        designerAreaPage.setUpdateTime(DateUtils.getNowDate());

        int addDesignerAreaPage = designerAreaMapper.updateDesignerAreaPage(designerAreaPage);

        if (addDesignerAreaPage > 0) {
            return AjaxResult.success("上传成功", designerAreaPage);
        }

        return AjaxResult.error("上传图片异常，请联系管理员");
    }

    /**
     * @Description: 删除设计器页面信息(根据区域id和页面id)
     * @auther: wanghongjie
     * @date: 9:10 2022/11/2
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult deleteDesignerAreaPage(DesignerAreaPage designerAreaPage) {
        Long id = designerAreaPage.getId();
        Long areaId = designerAreaPage.getAreaId();

        if (id == null || areaId == null) {
            return AjaxResult.error("参数错误,请检查!");
        }
        int delete = designerAreaMapper.deleteDesignerAreaPage(designerAreaPage);

        if (delete > 0) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败");
    }

    @Autowired
    private MessagingService messagingService;

    @Override
    public void sendRegistrationMessage(String s) {
        messagingService.sendRegistrationMessage(s);
    }

    /***
     * @description:复制设计器页面
     * @author: sunshangeng
     * @date: 2023/5/16 17:26
     * @param: [designerAreaPage]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult copyDesignerAreaPage(DesignerAreaPage designerAreaPage) {

        Long areaId=designerAreaPage.getAreaId();
        designerAreaPage.setAreaId(null);

        if(designerAreaPage==null
                ||StringUtils.isNull(designerAreaPage.getId())
                ||StringUtils.isNull(designerAreaPage.getName())
        ){
            return  AjaxResult.error("传入的参数有误！");
        }
        DesignerAreaPage oldDesignerAreaPage = designerAreaMapper.seleteDesignerAreaPage(designerAreaPage).get(0);
        oldDesignerAreaPage.setName(designerAreaPage.getName());
        oldDesignerAreaPage.setAreaId(areaId);
        oldDesignerAreaPage.setCreateBy(getUsername());
        oldDesignerAreaPage.setCreateTime(new Date());
        int updaterow = designerAreaMapper.addDesignerAreaPage(oldDesignerAreaPage);
        if(updaterow>0){
            return AjaxResult.success("复制成功");
        }else{
            return AjaxResult.error("复制失败");
        }

    }
}
