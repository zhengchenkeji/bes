package com.zc.common.core.msg;

/**
 * @author Athena-gongfanfei
 * 短信服务
 * @date 2021/11/16
 */

public class SmsModel {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 区域ID
	 */
	private String regionId;
	
	/**
	 * 接收短信的手机号码
	 */
	private String phoneNumbers;
	
	/**
	 * 接收短信的姓名
	 */
	private String phoneName;
	
	/**
	 * 短信签名名称
	 */
	private String signName;
	
	/**
	 * 短信模板ID
	 */
	private String templateCode;
	
	/**
	 * 主账号AccessKey的ID
	 */
	private String accessKeyId;
	
	/**
	 * 系统规定参数
	 */
	private String action;
	
	/**
	 * 外部流水扩展字段
	 */
	private String outId;
	
	/**
	 * 上行短信扩展码，无特殊需要此字段的用户请忽略此字段
	 */
	private String smsUpExtendCode;
	
	/**
	 * 短信模板变量对应的实际值，JSON格式
	 */
	private String templateParam;
	
	/**
	 * 返回数据发送回执ID
	 */
	private String bizId;
	
	/**
	 * 请求状态码
	 */
	private String code;
	
	/**
	 * 状态码的描述
	 */
	private String message;
	
	/**
	 * 请求ID
	 */
	private String requestId;
	
	/**
	 * Access Key Secret
	 */
	private String accessSecret;

	/**
	 * @return the regionId
	 */
	public String getRegionId() {
		return regionId;
	}

	/**
	 * @param regionId the regionId to set
	 */
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	/**
	 * @return the phoneNumbers
	 */
	public String getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(String phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	/**
	 * @return the signName
	 */
	public String getSignName() {
		return signName;
	}

	/**
	 * @param signName the signName to set
	 */
	public void setSignName(String signName) {
		this.signName = signName;
	}

	/**
	 * @return the templateCode
	 */
	public String getTemplateCode() {
		return templateCode;
	}

	/**
	 * @param templateCode the templateCode to set
	 */
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return accessKeyId;
	}

	/**
	 * @param accessKeyId the accessKeyId to set
	 */
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the outId
	 */
	public String getOutId() {
		return outId;
	}

	/**
	 * @param outId the outId to set
	 */
	public void setOutId(String outId) {
		this.outId = outId;
	}

	/**
	 * @return the smsUpExtendCode
	 */
	public String getSmsUpExtendCode() {
		return smsUpExtendCode;
	}

	/**
	 * @param smsUpExtendCode the smsUpExtendCode to set
	 */
	public void setSmsUpExtendCode(String smsUpExtendCode) {
		this.smsUpExtendCode = smsUpExtendCode;
	}

	/**
	 * @return the templateParam
	 */
	public String getTemplateParam() {
		return templateParam;
	}

	/**
	 * @param templateParam the templateParam to set
	 */
	public void setTemplateParam(String templateParam) {
		this.templateParam = templateParam;
	}

	/**
	 * @return the bizId
	 */
	public String getBizId() {
		return bizId;
	}

	/**
	 * @param bizId the bizId to set
	 */
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}

	/**
	 * @param accessSecret the accessSecret to set
	 */
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}
	
	
}
