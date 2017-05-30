package com.hx.blog_v2.controller.admin;

import com.hx.blog_v2.domain.form.BeanIdForm;
import com.hx.blog_v2.domain.form.MoodSaveForm;
import com.hx.blog_v2.domain.form.ResourceSaveForm;
import com.hx.blog_v2.domain.vo.AdminMoodVO;
import com.hx.blog_v2.service.interf.MoodService;
import com.hx.blog_v2.service.interf.ResourceService;
import com.hx.common.interf.common.Result;
import com.hx.common.result.SimplePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SystemController
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/20/2017 4:57 PM
 */
@RestController("adminResourceController")
@RequestMapping("/admin/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(ResourceSaveForm params) {

        return resourceService.add(params);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list(@RequestParam(defaultValue = "false") boolean spread) {

        return resourceService.adminList(spread);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(ResourceSaveForm params) {

        return resourceService.update(params);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Result remove(BeanIdForm params) {

        return resourceService.remove(params);
    }

}
