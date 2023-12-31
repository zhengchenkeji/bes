package com.zc.efounder.JEnterprise.mapper.designer;

import com.zc.efounder.JEnterprise.domain.designer.DesignerArea;
import com.zc.efounder.JEnterprise.domain.designer.DesignerAreaPage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:设计器页面mapper
 * @Date: Created in 9:07 2022/10/20
 * @Modified By:
 */
@Mapper
public interface DesignerAreaMapper {

    /**
     *
     * @Description: 获取所有的区域信息
     *
     * @auther: wanghongjie
     * @date: 9:09 2022/10/20
     * @param: [DesignerArea]
     * @return: java.util.List<com.ruoyi.efounder.JEnterprise.domain.DesignerArea>
     *
     */
    List<DesignerArea> listInfo(DesignerArea designerArea);

    /**
     *
     * @Description: 添加区域
     *
     * @auther: wanghongjie
     * @date: 9:19 2022/10/20
     * @param: [designerArea]
     * @return: int
     *
     */
    int addDesignerArea(DesignerArea designerArea);

    /**
     *
     * @Description: 保存设计器区域信息(根据区域id)
     *
     * @auther: wanghongjie
     * @date: 9:08 2022/10/22
     * @param: [designerAreaPage]
     * @return: int
     *
     */
    int addDesignerAreaPage(DesignerAreaPage designerAreaPage);

    /**
     *
     * @Description: 根据区域id获取设计器页面信息
     *
     * @auther: wanghongjie
     * @date: 9:37 2022/10/22
     * @param: [designerAreaPage]
     * @return: com.ruoyi.efounder.JEnterprise.domain.DesignerAreaPage
     *
     */
    List<DesignerAreaPage> seleteDesignerAreaPage(DesignerAreaPage designerAreaPage);

    /**
     *
     * @Description: 修改设计器区域信息(根据区域id和页面id)
     *
     * @auther: wanghongjie
     * @date: 11:48 2022/10/22
     * @param: [designerAreaPage]
     * @return: int
     *
     */
    int updateDesignerAreaPage(DesignerAreaPage designerAreaPage);

    /**
     *
     * @Description: 修改区域
     *
     * @auther: wanghongjie
     * @date: 11:41 2022/10/31
     * @param: [designerArea]
     * @return: int
     *
     */
    int updateDesignerArea(DesignerArea designerArea);

    /**
     *
     * @Description: 删除设计器区域节点
     *
     * @auther: wanghongjie
     * @date: 11:44 2022/10/31
     * @param: [id]
     * @return: int
     *
     */
    int deleteDesignerArea(Long id);

    /**
     *
     * @Description: 删除区域关联的页面
     *
     * @auther: wanghongjie
     * @date: 11:46 2022/10/31
     * @param: [id]
     * @return: void
     *
     */
    void deleteDesignerAreaPageByAreaId(Long id);

    /**
     *
     * @Description: 删除设计器页面信息(根据区域id和页面id)
     *
     * @auther: wanghongjie
     * @date: 9:16 2022/11/2
     * @param: [designerAreaPage]
     * @return: int
     *
     */
    int deleteDesignerAreaPage(DesignerAreaPage designerAreaPage);
}
