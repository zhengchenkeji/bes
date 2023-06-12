package com.zc.common.core.validation;

import com.alibaba.fastjson.JSONValidator;
import com.ruoyi.common.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * json字符串校验器
 * @author xiepufeng
 */
public class JsonValidator  implements ConstraintValidator<ValidJson, String> {

    @Override
    public void initialize(ValidJson constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // 不对空字符串进行校验，应该由其他规则校验
        if (StringUtils.isEmpty(value)) {
            return true;
        }

        // 不允许是个数字
        if(value.matches("-?\\d+(\\.\\d+)?")) {
            return false;
        }

        return JSONValidator.from(value).validate();
    }
}
