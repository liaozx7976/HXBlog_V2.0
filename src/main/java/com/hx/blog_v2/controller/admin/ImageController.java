package com.hx.blog_v2.controller.admin;

import com.hx.blog_v2.domain.ErrorCode;
import com.hx.blog_v2.domain.form.BeanIdForm;
import com.hx.blog_v2.domain.form.ImageSaveForm;
import com.hx.blog_v2.domain.form.ImageSearchForm;
import com.hx.blog_v2.domain.validator.BeanIdValidator;
import com.hx.blog_v2.domain.validator.ImageSaveValidator;
import com.hx.blog_v2.domain.validator.ImageSearchValidator;
import com.hx.blog_v2.domain.validator.PageValidator;
import com.hx.blog_v2.domain.vo.AdminImageVO;
import com.hx.blog_v2.service.interf.ImageService;
import com.hx.common.interf.common.Result;
import com.hx.common.result.SimplePage;
import com.hx.blog_v2.util.ResultUtils;
import com.hx.log.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ImageController
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/20/2017 4:57 PM
 */
@RestController("adminImageController")
@RequestMapping("/admin/image")
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageSaveValidator imageSaveValidator;
    @Autowired
    private ImageSearchValidator imageSearchValidator;
    @Autowired
    private BeanIdValidator beanIdValidator;
    @Autowired
    private PageValidator pageValidator;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(ImageSaveForm params) {
        Result errResult = imageSaveValidator.validate(params, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }
        if (!Tools.isEmpty(params.getId())) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " id 不为空 ! ");
        }

        return imageService.add(params);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list(ImageSearchForm params, SimplePage<AdminImageVO> page) {
        Result errResult = imageSearchValidator.validate(params, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }
        errResult = pageValidator.validate(page, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }

        return imageService.adminList(params, page);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(ImageSaveForm params) {
        Result errResult = imageSaveValidator.validate(params, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }
        if (Tools.isEmpty(params.getId())) {
            return ResultUtils.failed(ErrorCode.INPUT_NOT_FORMAT, " id 不为空 ! ");
        }

        return imageService.update(params);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Result remove(BeanIdForm params) {
        Result errResult = beanIdValidator.validate(params, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }

        return imageService.remove(params);
    }

    @RequestMapping(value = "/reSort", method = RequestMethod.POST)
    public Result reSort(ImageSearchForm params) {
        Result errResult = imageSearchValidator.validate(params, null);
        if (!errResult.isSuccess()) {
            return errResult;
        }

        return imageService.reSort(params);
    }

}
