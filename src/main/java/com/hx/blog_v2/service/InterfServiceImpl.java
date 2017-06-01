package com.hx.blog_v2.service;

import com.hx.blog_v2.dao.interf.InterfDao;
import com.hx.blog_v2.dao.interf.RltResourceInterfDao;
import com.hx.blog_v2.domain.POVOTransferUtils;
import com.hx.blog_v2.domain.form.BeanIdForm;
import com.hx.blog_v2.domain.form.InterfSaveForm;
import com.hx.blog_v2.domain.form.ResourceInterfUpdateForm;
import com.hx.blog_v2.domain.mapper.RltResourceInterfPOMapper;
import com.hx.blog_v2.domain.po.InterfPO;
import com.hx.blog_v2.domain.po.ResourcePO;
import com.hx.blog_v2.domain.po.RltResourceInterfPO;
import com.hx.blog_v2.domain.vo.AdminInterfVO;
import com.hx.blog_v2.domain.vo.ResourceInterfVO;
import com.hx.blog_v2.domain.vo.UserRoleVO;
import com.hx.blog_v2.service.interf.BaseServiceImpl;
import com.hx.blog_v2.service.interf.InterfService;
import com.hx.blog_v2.util.BlogConstants;
import com.hx.blog_v2.util.CacheContext;
import com.hx.blog_v2.util.DateUtils;
import com.hx.common.interf.common.Result;
import com.hx.common.util.ResultUtils;
import com.hx.json.JSONArray;
import com.hx.log.collection.CollectionUtils;
import com.hx.log.util.Log;
import com.hx.log.util.Tools;
import com.hx.mongo.criteria.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * BlogServiceImpl
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/20/2017 11:47 AM
 */
@Service
public class InterfServiceImpl extends BaseServiceImpl<InterfPO> implements InterfService {

    @Autowired
    private InterfDao interfDao;
    @Autowired
    private RltResourceInterfDao rltResourceInterfDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CacheContext cacheContext;

    @Override
    public Result add(InterfSaveForm params) {
        Map<String, InterfPO> interfs = cacheContext.allInterfs();
        if (contains(interfs, params.getName())) {
            return ResultUtils.failed("该接口已经存在 !");
        }

        InterfPO po = new InterfPO(params.getName(), params.getDesc(), params.getEnable());
        try {
            interfDao.save(po, BlogConstants.ADD_BEAN_CONFIG);
            interfs.put(po.getId(), po);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.failed(Tools.errorMsg(e));
        }
        return ResultUtils.success(po.getId());
    }

    @Override
    public Result adminList() {
        Map<String, InterfPO> interfs = cacheContext.allInterfs();
        List<AdminInterfVO> result = new ArrayList<>(interfs.size());
        for (Map.Entry<String, InterfPO> entry : interfs.entrySet()) {
            result.add(POVOTransferUtils.interfPO2AdminInterfVO(entry.getValue()));
        }

        return ResultUtils.success(result);
    }

    @Override
    public Result resourceInterfList() {
        Map<String, InterfPO> interfsById = cacheContext.allInterfs();
        String resourceInterfSql = " select * from rlt_resource_interf where resource_id in ( %s ) ";

        List<ResourceInterfVO> roleResources = (List<ResourceInterfVO>) POVOTransferUtils.resourcePO2ResourceInterfVOList(collectAllResource());
        String roleIds = collectResourceIds(roleResources);
        List<RltResourceInterfPO> rltResourceInterfs = jdbcTemplate.query(String.format(resourceInterfSql, roleIds), new RltResourceInterfPOMapper());
        for (RltResourceInterfPO rltResourceInterf : rltResourceInterfs) {
            int idx = idxOfResource(roleResources, rltResourceInterf.getResourceId());
            if (idx >= 0) {
                ResourceInterfVO resourceInterf = roleResources.get(idx);
                resourceInterf.getInterfIds().add(rltResourceInterf.getInterfId());
                resourceInterf.getInterfNames().add(interfsById.get(rltResourceInterf.getInterfId()).getName());
            } else {
                Log.err(" there is an db query error ! ");
            }
        }

        return ResultUtils.success(roleResources);
    }

    @Override
    public Result update(InterfSaveForm params) {
        InterfPO po = cacheContext.allInterfs().get(params.getId());
        if (po == null) {
            return ResultUtils.failed("该接口不存在 !");
        }

        po.setName(params.getName());
        po.setDesc(params.getDesc());
        po.setEnable(params.getEnable());
        po.setUpdatedAt(DateUtils.formate(new Date(), BlogConstants.FORMAT_YYYY_MM_DD_HH_MM_SS));
        try {
            long modified = interfDao.updateById(po, BlogConstants.UPDATE_BEAN_CONFIG)
                    .getModifiedCount();
            if (modified == 0) {
                return ResultUtils.failed("没有找到对应的接口 !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.failed(Tools.errorMsg(e));
        }
        return ResultUtils.success(po.getId());
    }

    @Override
    public Result userRoleUpdate(ResourceInterfUpdateForm params) {
        String[] interfIds = params.getInterfIds().split(",");
        List<RltResourceInterfPO> userRoles = null;
        if (!Tools.isEmpty(interfIds)) {
            Tools.trimAllSpaces(interfIds);
            userRoles = new ArrayList<>(interfIds.length);
            for (String roleId : interfIds) {
                userRoles.add(new RltResourceInterfPO(params.getResourceId(), roleId));
            }
        }

        try {
            rltResourceInterfDao.deleteMany(Criteria.eq("resource_id", params.getResourceId()));
            if (!CollectionUtils.isEmpty(userRoles)) {
                rltResourceInterfDao.insertMany(userRoles, BlogConstants.ADD_BEAN_CONFIG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.failed(Tools.errorMsg(e));
        }
        return ResultUtils.success(params.getResourceId());
    }

    @Override
    public Result remove(BeanIdForm params) {
        InterfPO po = cacheContext.allInterfs().remove(params.getId());
        if (po == null) {
            return ResultUtils.failed("该接口不存在 !");
        }

        String updatedAt = DateUtils.formate(new Date(), BlogConstants.FORMAT_YYYY_MM_DD_HH_MM_SS);
        try {
            long deleted = interfDao.updateOne(Criteria.eq("id", params.getId()),
                    Criteria.set("deleted", "1").add("updated_at", updatedAt)
            ).getModifiedCount();
            if (deleted == 0) {
                return ResultUtils.failed("接口[" + params.getId() + "]不存在 !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.failed(Tools.errorMsg(e));
        }
        return ResultUtils.success(params.getId());
    }

    // -------------------- 辅助方法 --------------------------

    /**
     * 判断当前所有的 BlogType 中 是否有名字为 name的 BlogType
     *
     * @param blogTypes blogTypes
     * @param name      name
     * @return boolean
     * @author Jerry.X.He
     * @date 5/21/2017 6:20 PM
     * @since 1.0
     */
    private boolean contains(Map<String, InterfPO> blogTypes, String name) {
        for (Map.Entry<String, InterfPO> entry : blogTypes.entrySet()) {
            if (Tools.equalsIgnoreCase(entry.getValue().getName(), name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取 用户接口 列表中给定的用户的索引
     *
     * @param userRoles userRoles
     * @param id        id
     * @return int
     * @author Jerry.X.He
     * @date 5/30/2017 5:43 PM
     * @since 1.0
     */
    private int idxOfUser(List<UserRoleVO> userRoles, String id) {
        int idx = 0;
        for (UserRoleVO userRole : userRoles) {
            if (userRole.getId().equals(id)) {
                return idx;
            }
            idx++;
        }

        return -1;
    }

    /**
     * 获取所有的 RolePO
     *
     * @return java.util.List<com.hx.blog_v2.domain.po.RolePO>
     * @author Jerry.X.He
     * @date 5/30/2017 7:46 PM
     * @since 1.0
     */
    private List<ResourcePO> collectAllResource() {
        Map<String, ResourcePO> resourcesById = cacheContext.allResources();
        List<ResourcePO> list = new ArrayList<>(resourcesById.size());
        for (Map.Entry<String, ResourcePO> entry : resourcesById.entrySet()) {
            ResourcePO role = entry.getValue();
            if (role.getEnable() != 0) {
                list.add(role);
            }
        }
        return list;
    }

    /**
     * 收集给定的用户列表的所有用户的id
     *
     * @param allResources allResources
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 5/30/2017 5:35 PM
     * @since 1.0
     */
    private String collectResourceIds(List<ResourceInterfVO> allResources) {
        JSONArray arr = new JSONArray();
        for (ResourceInterfVO resource : allResources) {
            arr.add(resource.getId());
        }
        String idsWithArr = arr.toString();
        return idsWithArr.substring(1, idsWithArr.length() - 1);
    }


    /**
     * 获取 资源接口 列表中给定的资源的索引
     *
     * @param roleResources resourcesById
     * @param id            parentId
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 5/30/2017 9:57 PM
     * @since 1.0
     */
    private int idxOfResource(List<ResourceInterfVO> roleResources, String id) {
        int idx = 0;
        for (ResourceInterfVO resInterf : roleResources) {
            if (resInterf.getId().equals(id)) {
                return idx;
            }
            idx++;
        }

        return -1;
    }
}
