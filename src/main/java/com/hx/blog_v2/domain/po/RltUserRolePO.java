package com.hx.blog_v2.domain.po;

import com.hx.json.JSONObject;
import com.hx.json.config.interf.JSONConfig;
import com.hx.json.interf.JSONField;
import com.hx.log.json.interf.JSONTransferable;
import com.hx.log.util.Constants;
import com.hx.log.util.Tools;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

/**
 * user -> role
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/20/2017 10:05 AM
 */
public class RltUserRolePO implements JSONTransferable<RltUserRolePO> {

    @JSONField({"id", "id"})
    private String id;
    @JSONField({"userId", "user_id"})
    private String userId;
    @JSONField({"roleId", "role_id"})
    private String roleId;

    public RltUserRolePO(String userId, String roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public RltUserRolePO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public static final RltUserRolePO PROTO_BEAN = new RltUserRolePO();

    @Override
    public RltUserRolePO loadFromJSON(Map<String, Object> obj, JSONConfig config) {
        if (Tools.isEmpty(obj)) {
            return this;
        }

        JSONObject.fromObject(obj).toBean(RltUserRolePO.class, this, config);
        return this;
    }

    @Override
    public JSONObject encapJSON(JSONConfig config) {
        return encapJSON(config, new LinkedList<>());
    }

    @Override
    public JSONObject encapJSON(JSONConfig config, Deque<Object> cycleDectector) {
        if (cycleDectector.contains(this)) {
            return JSONObject.fromObject(Constants.OBJECT_ALREADY_EXISTS).element("id", String.valueOf(id()));
        }
        cycleDectector.push(this);

        JSONObject result = JSONObject.fromObject(this, config);
        return result;
    }

    @Override
    public RltUserRolePO newInstance(Object... args) {
        return new RltUserRolePO();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void id(String id) {
        this.id = id;
    }


}