package uestc.zhangkx.zkxbatis.sqlsession.impl;

import uestc.zhangkx.zkxbatis.sqlsession.SqlSession;
import uestc.zhangkx.zkxbatis.sqlsession.SqlSessionFactory;
import uestc.zhangkx.zkxbatis.sqlsession.cfg.Configuration;

public class SqlSessionFactoryImpl implements SqlSessionFactory {

    private Configuration cfg;

    public SqlSessionFactoryImpl(Configuration cfg){
        this.cfg = cfg;
    }

    /**
     * 用于创建一个新的操作数据库对象
     * @return
     */
    @Override
    public SqlSession openSession() {
        return new SqlSessionImpl(cfg);
    }
}
