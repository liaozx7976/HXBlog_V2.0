package com.hx.blog_v2.domain.form;

/**
 * ImageSearchForm
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/31/2017 8:36 PM
 */
public class SystemConfigSearchForm {

    private String type;

    public SystemConfigSearchForm(String type) {
        this.type = type;
    }

    public SystemConfigSearchForm() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
