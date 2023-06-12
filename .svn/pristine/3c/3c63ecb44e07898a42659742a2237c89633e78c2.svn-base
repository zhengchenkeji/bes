package com.ruoyi.common.utils.poi;


import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 *
 * @Description: excel错误提示
 *
 * @auther: wanghongjie
 * @date: 10:50 2022/11/10
 * @param:
 * @return:
 *
 */
public class ExcelError extends BaseEntity {
	@ExcelVOAttribute(name = "行数", column = "A")
	private String row;
	@ExcelVOAttribute(name = "错误信息", column = "B")
	private String errorMsg;

	public String getRow() {
		return row;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setRow(String row) {
		this.row = row;
	}

	@Override
	public String toString() {
		return "ExcelError{" +
				"row='" + row + '\'' +
				", errorMsg='" + errorMsg + '\'' +
				'}';
	}
}