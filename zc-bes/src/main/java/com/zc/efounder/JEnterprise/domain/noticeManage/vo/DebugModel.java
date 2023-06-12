package com.zc.efounder.JEnterprise.domain.noticeManage.vo;

/**
 * description:调试参数实体
 * author: sunshangeng
 * date:2023/2/13 10:19
 */
public class DebugModel {
    /**配置id*/
    private String configId;

    /**模板id*/
    private String templateId;

    /**接收人*/
    private String recipient;

    /**变量配置*/
    private String content;

    /**邮箱标题*/

    private String title;



    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
