package com.hx.blog_v2.dao;

import com.hx.blog_v2.dao.interf.RltUserRoleDao;
import com.hx.blog_v2.domain.po.RltUserRolePO;
import com.hx.blog_v2.util.BlogConstants;
import com.hx.blog_v2.util.MyMysqlConnectionProvider;
import com.hx.mongo.config.MysqlDbConfig;
import com.hx.mongo.config.interf.DbConfig;
import com.hx.mongo.connection.interf.ConnectionProvider;
import com.hx.mongo.dao.MysqlBaseDaoImpl;
import org.springframework.stereotype.Repository;

import java.sql.Connection;

/**
 * BlogDaoImpl
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 5/20/2017 10:38 AM
 */
@Repository
public class RltUserRoleDaoImpl extends MysqlBaseDaoImpl<RltUserRolePO> implements RltUserRoleDao {

    public RltUserRoleDaoImpl(RltUserRolePO bean) {
        super(bean);
    }

    public RltUserRoleDaoImpl(RltUserRolePO bean, DbConfig config) {
        super(bean, config);
    }

    public RltUserRoleDaoImpl(RltUserRolePO bean, ConnectionProvider<Connection> connectionProvider) {
        super(bean, connectionProvider);
    }

    public RltUserRoleDaoImpl(RltUserRolePO bean, DbConfig config, ConnectionProvider<Connection> connectionProvider) {
        super(bean, config, connectionProvider);
    }

    public RltUserRoleDaoImpl() {
        super(RltUserRolePO.PROTO_BEAN,
                new MysqlDbConfig(BlogConstants.MYSQL_DB_CONFIG).table(BlogConstants.TABLE_RLT_USER_ROLE).id(BlogConstants.TABLE_ID),
                new MyMysqlConnectionProvider()
        );
    }


}