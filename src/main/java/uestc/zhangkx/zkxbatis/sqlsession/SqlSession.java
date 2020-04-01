package uestc.zhangkx.zkxbatis.sqlsession;

/**
 * zkxbatis中和数据库交互的和兴类
 * 它里面可以创建dao接口的对象
 */
public interface SqlSession {

    /**
     * 根据参数创建一个代理对象
     * @param daoInterfaceClass dao的接口字节码
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> daoInterfaceClass);

    /**
     * 释放资源
     */
    void close();
}
