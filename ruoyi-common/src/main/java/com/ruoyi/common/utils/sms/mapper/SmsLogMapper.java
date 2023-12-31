package com.ruoyi.common.utils.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息发送日志Mapper接口
 *
 * @author sunshangeng
 * @date 2022-11-21
 */
@Mapper
public interface SmsLogMapper extends BaseMapper<AthenaBesSmsLog>
{


}
