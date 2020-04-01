# 源码分析

续上文中的测试类，分析其中源码，先看测试类：

```java
package uestc.zhangkx;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import uestc.zhangkx.dao.UserDao;
import uestc.zhangkx.domain.User;

import java.io.InputStream;
import java.util.List;

public class batisTest {
    public static void main(String[] args)throws Exception {
        //1.读取配置文件
        InputStream in = Resources.getResourceAsStream("sqlmap-config.xml");
        //2.创建SqlSessionFactory工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        //3.使用工厂生产SqlSession对象
        SqlSession session = factory.openSession();
        //4.使用SqlSession创建Dao接口的代理对象
        UserDao userDao = session.getMapper(UserDao.class);
        //5.使用代理对象执行方法
        List<User> users = userDao.findAll();
        for(User user : users){
            System.out.println(user);
        }
        //6.释放资源
        session.close();
        in.close();
    }
}
```

在一次简单的应用中，可看见`mybatis`最核心的几个调用是`Resources`、`SqlSession`、`SqlSessionFactory`、`SqlSessionFactoryBuilder`。明白这几个类或接口的作用，并且可以自定义简单复写，就对mybatis源码有了简单的理解。首先得明白mybatis是如何一步步运行下去的。

## 运行流程

### 1. 读取配置文件

程序运行之初，要读取`sqlmap-config.xml`和`UserDao.xml`两个xml文件，自然会用到xml解析技术，在java中解析xml常用`DOM`、`SAX`、`DOM4J`、`JDOM`（以后在谈这几个解析技术）。我们可以使用`dom4j`来解析。

除了解析技术外，还要知道在xml文件中我们能获取到那些信息（我把xml整合了）：

```xml

<!-- 配置连接数据库的4个基本信息 -->
<property name="driver" value="com.mysql.cj.jdbc.Driver"/>
<property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
<property name="username" value="root"/>
<property name="password" value="0217"/>


<!-- 指定映射配置文件的位置，映射配置文件指的是每个dao独立的配置文件 -->
<mappers>
    <mapper resource="UserDao.xml"/>
</mappers>


<mapper namespace="uestc.zhangkx.dao.UserDao">
    <!--配置查询所有-->
    <select id="findAll" resultType="uestc.zhangkx.domain.User">
        select * from user
    </select>
</mapper>
```

- `<property>`中可以得到链接数据库的信息，有了这些信息就可以创建`Connection`对象
- `<mapper>`中`resource`可以得到映射配置信息，根据这个映射配置信息可以得到`namespace`，也就可以得到`dao`（或`mapper`）的位置
- `<select>`中可以得到`id`，可以组合出`dao`（或`mapper`）的全类名，还有`resultType`可以得到封装实体的全限定类名，以及执行的sql语句

### 2. 建造者模式

注意到，读取配置文件时候，其实只获取了一个`InputStream`对象，并没有具体的处理配置文件。其实真实的处理配置文件内容实在创建工厂时对用户隐藏了。所以在使用`builder`的时候要调用xml解析技术。

### 3. 工厂模式

使用工厂生产sqlsession对象。生产的时候应该将xml解析的信息一并携带。

### 4. 代理模式

使用代理模式，对sqlsession对象调用dao时，进行增强。在代理中实现对sql的操作，当然此时应该将操作给拆分出来。

### 5. 返回封装类、释放资源

释放掉资源，结束。

# 自定义mybatis

## 设计架构

### xml解析

使用dom4j+xpath解析，解析的数据需要封装，封装成两个包：

- 连接信息：解析出有关于数据库连接的信息打包，在析出一个用于数据库连接的Util方法，专门连接数据库返回connection。
- 映射信息：它又包含两个部分，要讲这两个信息组合定义成一个对象。
  - 执行的sql语句——Mapper
  - 封装结构的实体类全限定类名——string

### 代理设计

得到上面的Mapper和connection对象，对其增强，注意析出具体查询数据库的方法。

### selectList方法

伪代码表达：

```
1.根据配置文件信息创建Connection对象
	注册驱动，获取连接
2.获取预处理对象PreparedSatement
 	conn.prepareStatement（sql）；
3.执行查询
	ResultSet resultSet = preparedStatement.executeQuery();
4.遍历结果集用于封装
	List <E> list = new ArraryList();
	while(resultSet.next()){
        E element = (E) Class.forName(配置的全限定类名).newInstance();
        进行封装，把每个rs的内容都添加到element中
        我们的实体类属性和表中的列名是一致的
        所以我们就可以把表的列名看做实体类的属性名；
        就可以使用反射的方式来根据名称获取每个属性，并且把值赋就去
        把element加入到list中
        list.add(element);
	}
5.返回list
	return list；
```

## 具体实现

查看源码