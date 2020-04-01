package uestc.zhangkx.zkxbatis.sqlsession.impl;

import uestc.zhangkx.zkxbatis.sqlsession.SqlSession;
import uestc.zhangkx.zkxbatis.sqlsession.cfg.Configuration;
import uestc.zhangkx.zkxbatis.sqlsession.proxy.MapperProxy;
import uestc.zhangkx.zkxbatis.utils.DataSourceUtil;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlSessionImpl implements SqlSession {

    private Configuration cfg;
    private Connection connection;

    public SqlSessionImpl(Configuration cfg){
        this.cfg = cfg;
        connection = DataSourceUtil.getConnection(cfg);
    }

    /**
     * 用于创建代理对象
     * @param daoInterfaceClass dao的接口字节码
     * @param <T>
     * @return
     */
    @Override
    public <T> T getMapper(Class<T> daoInterfaceClass) {

        return (T) Proxy.newProxyInstance(daoInterfaceClass.getClassLoader(),
                new Class[]{daoInterfaceClass},
                new MapperProxy(cfg.getMappers(),
                        connection));
    }

    /**
     * 用于释放资源
     */
    @Override
    public void close() {
        if(connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
