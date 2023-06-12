package com.zc.efounder.JEnterprise.mapper.deviceTree;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import org.apache.ibatis.annotations.Param;

/**
 * 控制器Mapper接口
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
public interface ControllerMapper
{
    /**
     * 查询控制器
     *
     * @param id 控制器主键
     * @return 控制器
     */
    public Controller selectControllerById(Long id);

    /**
     * 查询控制器列表
     *
     * @param controller 控制器
     * @return 控制器集合
     */
    List<Controller> selectControllerList(Controller controller);

    /**
     * 根据模块树Id查询控制器
     */
    Controller selectControllerByTreeId(Integer treeId);

    /**
     * 根据虚点树Id查询控制器
     */
    Controller selectControllerByTreeIdVpoint(Integer treeId);

    /**
     * 根据模块点树Id查询控制器
     */
    Controller selectControllerByModulePointTreeId(Integer treeId);

    /**
     * 根据模块sysname查询控制器
     */
    Controller selectControllerByModule(String sysName);

    /**
     * 根据模块Id查询控制器
     */
    Controller selectControllerByModuleId(Integer treeId);

    /**
     * 新增控制器
     *
     * @param controller 控制器
     * @return 结果
     */
    Boolean insertController(Controller controller);

    /**
     * 修改控制器
     *
     * @param controller 控制器
     * @return 结果
     */
    Boolean updateController(Controller controller);

    /**
     * 删除控制器
     *
     * @param id 控制器主键
     * @return 结果
     */
    Boolean deleteControllerById(Integer id);

    /**
     * 批量删除控制器
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteControllerByIds(Long[] ids);


    /**
     * 更新同步状态
     *
     * @param controller 控制器
     * @return boolean
     * @Author qindehua
     * @Date 2022/09/24
     **/
    boolean updateSynchState(Controller controller);



    /**
     * @description:根据树节点查询所在控制器
     * @author: sunshangeng
     * @date: 2022/9/27 14:15
     * @param: [treeid, type]
     * @return: com.ruoyi.deviceManagement.deviceTree.domain.Controller
     **/
    Controller getControllerbytreeid(int treeid);

    String[] selectOnLine();

    Boolean updateOffLine(String[] ids);

    /**
     * 更新同步状态
     *
     * @param deviceTreeIds 模块树ID
     * @return boolean
     * @Author qindehua
     * @Date 2022/10/15
     **/
    boolean updateSynchStateModule(@Param("deviceTreeIds") List<Integer> deviceTreeIds);

    /**
     * 更新同步状态
     *
     * @param deviceTreeIds 点位树ID
     * @return boolean
     * @Author qindehua
     * @Date 2022/10/15
     **/
    boolean updateSynchStatePoint(@Param("deviceTreeIds") List<Integer> deviceTreeIds);


    /**
     * 更新同步状态
     *
     * @param deviceTreeIds 电表树ID
     * @return boolean
     * @Author qindehua
     * @Date 2022/10/15
     **/
    boolean updateSynchStateMeter(@Param("deviceTreeIds") List<Integer> deviceTreeIds);

    /**
     * 根据设备树ID 查询控制器
     **/
    Controller selectControllerInfoByDeviceTreeId(String deviceTreeId);

    /**
     * 根据设备树ID 查询控制器
     **/
    Controller selectControllerInfoByDeviceTreeId(Long deviceTreeId);

    /**
     * 查询控制器  根据ip
     *
     * @param ip 地址
     * @return {@code Controller }
     * @Author qindehua
     * @Date 2022/12/16
     **/
    Controller selectControllerByIp(@Param("ip") String ip);
}
