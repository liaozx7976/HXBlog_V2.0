package com.hx.blog_v2.domain.validator;

import com.hx.blog_v2.domain.ErrorCode;
import com.hx.blog_v2.domain.form.BeanIdForm;
import com.hx.blog_v2.util.ConstantsContext;
import com.hx.common.interf.common.Result;
import com.hx.common.interf.validator.Validator;
import com.hx.common.util.ResultUtils;
import com.hx.log.str.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AdminCommentSearchValidator
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 6/15/2017 7:18 PM
 */
@Component
public class BeanIdValidator extends ConfigRefreshableValidator<BeanIdForm> implements Validator<BeanIdForm> {

    /**
     * 最小长度, 最大长度
     */
    private int minLen = -1;
    private int maxLen = -1;

    @Override
    public Result doValidate(BeanIdForm form, Object extra) {
        if (!StringUtils.isNumeric(form.getId())) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " id 不是数字 ! ");
        }

        int lenOfId = form.getId().length();
        if ((lenOfId < minLen) || (lenOfId > maxLen)) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " id 长度不在范围内 ! ");
        }

        return ResultUtils.success();

    }

    @Override
    public boolean needRefresh() {
        return minLen < 0;
    }

    @Override
    public void refreshConfig() {
        minLen = Integer.parseInt(constantsContext.ruleConfig("id.min.length", "0"));
        maxLen = Integer.parseInt(constantsContext.ruleConfig("id.max.length", "11"));
    }

}