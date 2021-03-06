package com.hx.blog_v2.domain.mapper;

import com.hx.blog_v2.domain.POVOTransferUtils;
import com.hx.blog_v2.domain.po.BlogCommentPO;
import com.hx.blog_v2.domain.po.LinkPO;
import com.hx.blog_v2.domain.vo.AdminCommentVO;
import com.hx.blog_v2.domain.vo.AdminLinkVO;
import com.hx.blog_v2.util.BlogConstants;
import com.hx.mongo.util.ResultSet2MapAdapter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AdminBlogVOMapper
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/21/2017 8:42 PM
 */
public class AdminCommentVOMapper implements RowMapper<AdminCommentVO> {

    @Override
    public AdminCommentVO mapRow(ResultSet resultSet, int i) throws SQLException {
        BlogCommentPO po = new BlogCommentPO();
        po.loadFromJSON(new ResultSet2MapAdapter(resultSet), BlogConstants.LOAD_ALL_CONFIG);
        AdminCommentVO vo = POVOTransferUtils.blogCommentPO2AdminCommentVO(po);
        vo.setBlogName(resultSet.getString("blog_name"));
        return vo;
    }

}
