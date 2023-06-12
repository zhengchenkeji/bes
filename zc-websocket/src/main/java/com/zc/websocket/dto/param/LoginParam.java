package com.zc.websocket.dto.param;

/**
 * 登录参数定义
 */
public class LoginParam extends AbstractCmdParam
{
    /**
     * 登录websocket密码
     */
    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

}
