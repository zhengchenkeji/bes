package com.zc.efounder.JEnterprise.commhandler;

import com.zc.connect.business.constant.ChannelType;
import com.zc.connect.business.dto.TimeData;
import com.zc.connect.client.util.DataUtil;
import com.zc.connect.business.handler.SendMsgHandler;

import java.util.TimerTask;

import static com.zc.efounder.JEnterprise.commhandler.ClientEventCallback.clientStateStore;

/**
 * @author xiepufeng
 * @date 2020/7/17 11:33
 */
public class SyncTimeBatchHandler extends TimerTask
{

    public final static int INTERVAL = 24 * 60 * 60 * 1000;

    @Override
    public void run()
    {

        try
        {
            TimeData timeData = DataUtil.getTimeDataObject();

            clientStateStore.forEach((ip, channelTypeState) ->
            {

                Boolean state = channelTypeState.getState();

                if (!state)
                {
                    return;
                }

                String type = channelTypeState.getType();

                switch (type)
                {
                    case ChannelType.EDC:
                        SendMsgHandler.setControllerTimeEDC(ip, timeData);
                        break;
                    case ChannelType.DDC:
                        SendMsgHandler.setControllerTimeDDC(ip, timeData);
                        break;
                    case ChannelType.LDC:
                        SendMsgHandler.setControllerTimeLDC(ip, timeData);
                        break;
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
