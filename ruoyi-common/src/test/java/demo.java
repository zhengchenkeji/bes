import com.zc.common.core.rabbitMQ.MsgProducer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:03 2023/2/8
 * @Modified By:
 */
public class demo {

    @Autowired
    private static MsgProducer msgProducer;

    public static void main(String[] args) {
        msgProducer.sendMsg("222");
    }
}
