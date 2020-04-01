package uestc.zhangkx.zkxbatis.sqlsession;

import uestc.zhangkx.zkxbatis.sqlsession.cfg.Configuration;
import uestc.zhangkx.zkxbatis.sqlsession.impl.SqlSessionFactoryImpl;
import uestc.zhangkx.zkxbatis.utils.XMLConfigBuilder;

import java.io.InputStream;

/**
 * 用于创建一个sqlsessionFactory对象
 */
public class SqlSessionFactoryBuilder {

    /**
     * 根据参数的字节输入流来构建一个SqlSessionFactory工厂
     * @param config
     * @return
     */
    public SqlSessionFactory build(InputStream config){
        Configuration cfg = XMLConfigBuilder.loadConfiguration(config);
        return  new SqlSessionFactoryImpl(cfg);
    }
}
