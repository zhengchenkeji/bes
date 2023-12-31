package com.zc.efounder.JEnterprise.controller.besCommon;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.tree.ISSPTreeNode;
import com.zc.efounder.JEnterprise.service.besCommon.BesCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * common Controller
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@RestController
@RequestMapping("/bas/common")
public class BesCommonController extends BaseController
{
    @Autowired
    private BesCommonService besCommonService;

    /**
     * 查询采集参数定义左侧树
     */
    @GetMapping("/electricParams/leftTree")
    public AjaxResult leftTree()
    {
        List<ISSPTreeNode> tree = besCommonService.leftTree();
        return AjaxResult.success(tree);
    }

    /**
     * 园区能源配置左侧树
     */
    @GetMapping("/energyConfig/leftTree")
    public AjaxResult energyConfigLeftTree()
    {
        List<ISSPTreeNode> tree = besCommonService.energyConfigLeftTree();
        return AjaxResult.success(tree);
    }

    /**
     * 采集方案定义左侧树
     */
    @GetMapping("/collMethod/leftTree")
    public AjaxResult collMethodLeftTree()
    {
        List<ISSPTreeNode> tree = besCommonService.collMethodLeftTree();
        return AjaxResult.success(tree);
    }

}
