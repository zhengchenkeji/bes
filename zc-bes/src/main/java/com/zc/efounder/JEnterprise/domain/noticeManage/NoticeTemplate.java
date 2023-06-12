package com.zc.efounder.JEnterprise.domain.noticeManage;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 通知模板配置对象 athena_bes_notice_template
 *
 * @author sunshangeng
 * @date 2023-02-09
 */
public class NoticeTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 模板名称
     */
    @Excel(name = "模板名称")
    private String templatename;

    /**
     * 通知类型
     */
    @Excel(name = "通知类型")
    private String noticetype;

    /**
     * 服务厂商
     */
    @Excel(name = "服务厂商")
    private String servicefactory;

    /**
     * 短信模板编码
     */
    private String templatecode;

    /**
     * 短信模板签名
     */
    private String templatesign;

    private String filePath;
    /**
     * 邮件主题配置
     */
    private String title;
    /**
     * 邮件内容配置
     */
    private String content;


    /**是否报警模板*/
    private Integer isAlarm;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename;
    }

    public String getTemplatename() {
        return templatename;
    }

    public void setNoticetype(String noticetype) {
        this.noticetype = noticetype;
    }

    public String getNoticetype() {
        return noticetype;
    }

    public void setServicefactory(String servicefactory) {
        this.servicefactory = servicefactory;
    }

    public String getServicefactory() {
        return servicefactory;
    }

    public void setTemplatecode(String templatecode) {
        this.templatecode = templatecode;
    }

    public String getTemplatecode() {
        return templatecode;
    }

    public void setTemplatesign(String templatesign) {
        this.templatesign = templatesign;
    }

    public String getTemplatesign() {
        return templatesign;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public Integer getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(Integer isAlarm) {
        this.isAlarm = isAlarm;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("templatename", getTemplatename())
                .append("noticetype", getNoticetype())
                .append("servicefactory", getServicefactory())
                .append("templatecode", getTemplatecode())
                .append("templatesign", getTemplatesign())
                .append("filePath", getFilePath())
                .append("title", getTitle())
                .append("content", getContent())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
