package com.hx.blog_v2.test;

import com.hx.blog_v2.domain.po.BlogCommentPO;
import com.hx.blog_v2.domain.po.BlogExPO;
import com.hx.blog_v2.domain.po.BlogPO;
import com.hx.blog_v2.domain.po.BlogSensePO;
import com.hx.blog_v2.util.BlogConstants;
import com.hx.json.JSONObject;
import com.hx.log.str.MysqlSqlGenerator;
import org.junit.Test;

import static com.hx.log.util.Log.info;

/**
 * Test01GenerateTable
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/20/2017 10:27 AM
 */
public class Test01GenerateTable {


    @Test
    public void blog() throws Exception {

        BlogPO blog = new BlogPO("xx", "hx", "url", "1", "xx", "url");
        blog.setId("2");

        JSONObject blogObj = blog.encapJSON(BlogConstants.IDX_MANAGER.getDoLoad(), BlogConstants.IDX_MANAGER.getDoFilter());
        String sql = MysqlSqlGenerator.generateCreateTableSql(BlogConstants.TABLE_BLOG, blogObj);
        info(sql);

    }

    @Test
    public void blogEx() throws Exception {

        BlogExPO blog = new BlogExPO();
        blog.setId("2");
        blog.setBlogId("12");
        blog.setCommentCnt(12);
        blog.setViewCnt(12);
        blog.setGoodCnt(12);
        blog.setNotGoodCnt(12);

        JSONObject blogObj = blog.encapJSON(BlogConstants.IDX_MANAGER.getDoLoad(), BlogConstants.IDX_MANAGER.getDoFilter());
        String sql = MysqlSqlGenerator.generateCreateTableSql(BlogConstants.TABLE_BLOG_EX, blogObj);
        info(sql);

    }


    @Test
    public void blogComment() throws Exception {

        BlogCommentPO blog = new BlogCommentPO("name", "email", "url", "toUser", "role", "content");
        blog.setId("2");
        blog.setBlogId("11");
        blog.setFloorId("11");
        blog.setCommentId("11");


        JSONObject blogObj = blog.encapJSON(BlogConstants.IDX_MANAGER.getDoLoad(), BlogConstants.IDX_MANAGER.getDoFilter());
        String sql = MysqlSqlGenerator.generateCreateTableSql(BlogConstants.TABLE_BLOG_COMMENT, blogObj);
        info(sql);

    }



}
