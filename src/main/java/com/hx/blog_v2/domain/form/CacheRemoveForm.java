package com.hx.blog_v2.domain.form;

/**
 * CacheRemoveForm
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/31/2017 8:36 PM
 */
public class CacheRemoveForm {

    private String id;
    private String type;

    public CacheRemoveForm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
