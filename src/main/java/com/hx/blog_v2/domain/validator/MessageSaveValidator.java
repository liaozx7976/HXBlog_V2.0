package com.hx.blog_v2.domain.validator;

import com.hx.blog_v2.context.ConstantsContext;
import com.hx.blog_v2.domain.ErrorCode;
import com.hx.blog_v2.domain.form.MessageSaveForm;
import com.hx.blog_v2.util.BizUtils;
import com.hx.blog_v2.util.ResultUtils;
import com.hx.common.interf.common.Result;
import com.hx.common.interf.validator.Validator;
import com.hx.log.util.Tools;
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
public class MessageSaveValidator implements Validator<MessageSaveForm> {

    @Autowired
    private SummaryValidator summaryValidator;
    @Autowired
    private CommentContentValidator commentContentValidator;
    @Autowired
    private MessageTypeValidator messageTypeValidator;
    @Autowired
    private BeanIdsStrValidator beanIdsStrValidator;
    @Autowired
    private BeanIdStrValidator beanIdStrValidator;
    @Autowired
    private ConstantsContext constantsContext;

    @Override
    public Result validate(MessageSaveForm form, Object extra) {
        if (!Tools.isEmpty(form.getId())) {
            if (!beanIdStrValidator.validate(form.getId(), extra).isSuccess()) {
                return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " id 非数字 ! ");
            }
        }

        if (!Tools.isEmpty(form.getSenderId())) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " ! ");
        }
        if (!Tools.isEmpty(form.getUserNames())) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " ! ");
        }
        boolean withTarget = false;
        if (!Tools.isEmpty(form.getUserIds())) {
            Result errResult = beanIdsStrValidator.validate(form.getUserIds(), extra);
            if (!errResult.isSuccess()) {
                return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " 指定的 userId 列表格式不正确 ! ");
            }
            withTarget = true;
        }
        if (!Tools.isEmpty(form.getRoleIds())) {
            Result errResult = beanIdsStrValidator.validate(form.getRoleIds(), extra);
            if (!errResult.isSuccess()) {
                return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " 指定的 roleIds 列表格式不正确 ! ");
            }
            withTarget = true;
        }
        if (!withTarget) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " 请指定发送消息的对象 ! ");
        }
        Result errResult = messageTypeValidator.validate(form.getType(), extra);
        if (!errResult.isSuccess()) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " type 不合法 ! ");
        }
        errResult = summaryValidator.validate(form.getSubject(), extra);
        if (!errResult.isSuccess()) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " 主题不合法 ! ");
        }
        errResult = commentContentValidator.validate(form.getContent(), extra);
        if (!errResult.isSuccess()) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " 消息内容不合法 ! ");
        }

        String subjectFormatted = BizUtils.transferTags(form.getUserIds() + "-" + form.getRoleIds() + "," + form.getUserIds(),
                form.getSubject(), constantsContext.forbiddenTagFormatMap);
        form.setSubject(subjectFormatted);
        String contentFormatted = BizUtils.prepareContent(form.getUserIds() + "-" + form.getRoleIds() + "," + form.getUserIds(),
                form.getContent(), constantsContext.allowTagSensetiveTags, constantsContext.allowTagSensetiveTag2Attr,
                constantsContext.allowTagSensetiveAttrs, null);
        form.setContent(contentFormatted);
        return ResultUtils.success();

    }
}
