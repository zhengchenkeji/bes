package com.zc.common.core.foreignkeyservice;

/**
 * 外键服务类
 * 应用场景:当一个实体通过外键关联另外一个实体时,则这个实体对应的服务类可以实现该接口。
 *
 * @author Athena-xiepufeng
 *
 */
public interface ForeignKeyService
{

    /**
     * 通过外键值查找本实体，如果查找到则说明本实体关联了外键对应的实体。
     * @param foreignKey
     *            ： 本实体外键属性
     * @param foreignKeyValue
     *            ： 本实体的外键值
     * @return
     */
    LinkModel findByForeignKey(String foreignKey, Long foreignKeyValue);

    /**
     * 根据外键删除所有关联数据
     * @param foreignKey
     *            ： 本实体外键属性
     * @param foreignKeyValue
     *         ： 本实体的外键值
     * @return
     */
    boolean deleteAllByForeignKey(String foreignKey, Long foreignKeyValue);


    /**
     * 通过外键值计算本实体总数，如果总数大于0则说明本实体关联了外键对应的实体。
     * @param foreignKey
     *            ： 本实体外键属性
     * @param foreignKeyValue
     *           ： 本实体的外键值
     * @return
     */
    default int countByForeignKey(String foreignKey, Long foreignKeyValue)
    {

        LinkModel linkModel = findByForeignKey(foreignKey, foreignKeyValue);

        if (linkModel == null)
        {
            return 0;
        }

        return linkModel.getCount();
    }

    /**
     * 如果实体存在外键关联，则向外该键所指的实体服务注册外键，从而建立关联链关系。
     */
    void registerForeignKey();
}
