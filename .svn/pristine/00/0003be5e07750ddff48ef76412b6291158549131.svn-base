package com.zc.common.core.foreignkeyservice;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 被关联服务抽象类
 * @author Athena-xiepufeng
 */
public abstract class AssociatedService
{
    /*
     * ForeignKeyService :对应的service
     */
    private final Map<ForeignKeyService, String> serviceForeignKeyMap = new HashMap<>();

    /**
     * 判断实体是否能够被硬删除。如果实体被其他实体关联，则不能被硬删除。
     *
     * @param id
     *            本实体ID
     * @return
     */
    public boolean canHardDelete(Long id)
    {

        if (id == null || serviceForeignKeyMap.isEmpty())
        {
            return true;
        }

        for (ForeignKeyService foreignKeyService : serviceForeignKeyMap.keySet())
        {

            String foreignKey = serviceForeignKeyMap.get(foreignKeyService);

            if (foreignKeyService.countByForeignKey(foreignKey, id) > 0)
            {
                return false;
            }
        }

        return true;
    }
    /**
     * 判断实体是否能够被硬删除。如果实体被其他实体关联，则不能被硬删除。
     *
     * @param ids
     *            本实体ID
     * @return
     */
    public boolean canHardDelete(Long[] ids)
    {
        if (ids == null || ids.length <= 0 || serviceForeignKeyMap.isEmpty())
        {
            return true;
        }

        for (Long id : ids)
        {
            for (ForeignKeyService foreignKeyService : serviceForeignKeyMap.keySet())
            {

                String foreignKey = serviceForeignKeyMap.get(foreignKeyService);

                if (foreignKeyService.countByForeignKey(foreignKey, id) > 0)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断实体是否能够被硬删除。如果实体被其他实体关联，则不能被硬删除。
     *
     * @param id
     *            本实体ID
     * @return
     */
    public LinkModel getWhoReferenceMe(Long id)
    {

        if (id == null || serviceForeignKeyMap.isEmpty())
        {
            return null;
        }

        for (ForeignKeyService foreignKeyService : serviceForeignKeyMap.keySet())
        {
            String foreignKey = serviceForeignKeyMap.get(foreignKeyService);

            LinkModel model = foreignKeyService.findByForeignKey(foreignKey, id);

            if (model != null)
            {
                return model;
            }
        }

        return null;
    }

    /**
     * 判断实体是否能够被硬删除。如果实体被其他实体关联，则不能被硬删除。
     *
     * @param ids
     *            本实体ID
     * @return
     */
    public LinkModel getWhoReferenceMe(Long[] ids)
    {

        if (ids == null || serviceForeignKeyMap.isEmpty() || ids.length <= 0)
        {
            return null;
        }

        for (Long id : ids)
        {
            for (ForeignKeyService foreignKeyService : serviceForeignKeyMap.keySet())
            {
                String foreignKey = serviceForeignKeyMap.get(foreignKeyService);

                LinkModel model = foreignKeyService.findByForeignKey(foreignKey, id);

                if (model != null)
                {
                    return model;
                }
            }
        }

        return null;
    }

    /**
     * 删除所有关联数据
     * @param id
     *        本实体ID
     * @return
     */
    @Transactional
    public boolean deleteAssociatedDataById (Long id)
    {
        if (id == null)
        {
            return false;
        }

        if (serviceForeignKeyMap.isEmpty())
        {
            return true;
        }

        for (ForeignKeyService foreignKeyService : serviceForeignKeyMap.keySet())
        {
            String foreignKey = serviceForeignKeyMap.get(foreignKeyService);

            if (foreignKeyService.countByForeignKey(foreignKey, id) > 0)
            {
                foreignKeyService.deleteAllByForeignKey(foreignKey, id);
            }

        }

        return true;

    }

    /**
     * 批量删除所有关联数据
     * @param ids
     *        本实体ID
     * @return
     */
    @Transactional
    public boolean deleteAssociatedDataByIds (Long[] ids)
    {
        if (ids == null || ids.length <= 0)
        {
            return false;
        }

        if (serviceForeignKeyMap.isEmpty())
        {
            return true;
        }

        for (Long id : ids)
        {
            for (ForeignKeyService foreignKeyService : serviceForeignKeyMap.keySet())
            {
                String foreignKey = serviceForeignKeyMap.get(foreignKeyService);

                if (foreignKeyService.countByForeignKey(foreignKey, id) > 0)
                {
                    foreignKeyService.deleteAllByForeignKey(foreignKey, id);
                }

            }
        }

        return true;

    }

    /**
     * 注册外键服务
     *
     * @param subscriber
     *            外键服务
     * @param foreignKey
     *            外键名称(用于区分一个表中有多个外键的情况)
     */
    public void registerForeignKey(ForeignKeyService subscriber, String foreignKey)
    {
        serviceForeignKeyMap.put(subscriber, foreignKey);
    }
}
