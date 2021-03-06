package com.hx.blog_v2.controller.admin;

import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.utils.Constants;
import com.hx.blog_v2.domain.form.UploadedFileSaveForm;
import com.hx.blog_v2.domain.form.UploadedImageSaveForm;
import com.hx.blog_v2.domain.validator.BeanIdValidator;
import com.hx.blog_v2.domain.validator.PageValidator;
import com.hx.blog_v2.domain.validator.UploadFileSaveValidator;
import com.hx.blog_v2.domain.validator.UploadImageSaveValidator;
import com.hx.blog_v2.service.interf.UploadFileService;
import com.hx.common.interf.common.Result;
import com.hx.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SystemController
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/20/2017 4:57 PM
 */
@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private UploadImageSaveValidator uploadImageSaveValidator;
    @Autowired
    private UploadFileSaveValidator uploadFileSaveValidator;
    @Autowired
    private BeanIdValidator beanIdValidator;
    @Autowired
    private PageValidator pageValidator;

    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public Result upload(UploadedImageSaveForm params) {
        Result errResult = uploadImageSaveValidator.validate(params, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }

        return uploadFileService.add(params);
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public Result file(UploadedFileSaveForm params) {
        Result errResult = uploadFileSaveValidator.validate(params, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }

        return uploadFileService.add(params);
    }

    @RequestMapping(value = "/ueditor/config")
    public JSONObject config(HttpServletRequest request, HttpServletResponse response) {
        // dispath 获取数据, 上传数据, ..
        request.setAttribute("action", request.getParameter("action"));
        String actionCode = (String) request.getAttribute("action");
        Integer actionType = ActionMap.getType(actionCode);
        if (actionType == null) {
            return new JSONObject().element(Constants.STATE, "无效的 action !");
        }

        if (!ActionMap.isRetriveAction(actionType)) {
            return uploadFileService.ueditorUpload();
        }
        return new JSONObject().element(Constants.STATE, "无效的 action !");
    }

}
