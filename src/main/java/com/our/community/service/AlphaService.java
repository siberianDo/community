package com.our.community.service;

import com.our.community.dao.AlphaDao;
import com.our.community.dao.mapper.DiscussPostMapper;
import com.our.community.dao.mapper.UserMapper;
import com.our.community.entity.DiscussPost;
import com.our.community.entity.User;
import com.our.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;


@Service
/**
 * 加入该注解限定实例化次数（大多数时候都是单例）
 * 默认参数singleton(单例)，prototype即每次访问bean都要实例化
 */
//@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    //编程式事务管理
    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    //在构造器之后自动调用该方法初始话数据
    @PostConstruct
    public void init() {
        System.out.println("初始化AlphaService");
    }

    //在销毁对象之前调用该方法释放资源
    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    //模拟实现查询业务
    public String find() {
        return alphaDao.select();
    }


    /**
     * 事务管理----声明式
     * eg.添加用户的同时自动添加帖子。
     *
     * @return
     * @Transactional 事务管理注解
     * isolation选择隔离级别
     * propagation事务的传播机制（解决两个不同事务管理发生冲突）
     * -->REQUIRED：支持当前事务（外部事务），如果没有外部事务，则创建新事务
     * -->REQUIRES_NEW：创建一个新的事务，并暂停当前事务
     * -->NESTED：如果当前存在外部事务，则嵌套在当前事务中执行，但拥有独立的提交和回滚操作，如果不存在，则创建新的事务
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        //新增用户
        User user = new User();
        user.setUsername("SWTest1");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("12345" + user.getSalt()));
        user.setEmail("SWTest1@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/10t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("事务管理测试1！");
        discussPost.setContent("这是事务管理测试一号！");
        discussPost.setCreateTime(new Date());

        discussPostMapper.insertDiscussPost(discussPost);

        //设置错误，将事务回滚，使得两个插入操作失效
        Integer.valueOf("abc");

        return "ok";
    }

    /**
     * 事务管理----编程式事务管理
     * @return
     */
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                //新增用户
                User user = new User();
                user.setUsername("SWTest2");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("12345" + user.getSalt()));
                user.setEmail("SWTest2@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/20t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost discussPost = new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("事务管理测试2！");
                discussPost.setContent("这是事务管理测试二号！");
                discussPost.setCreateTime(new Date());

                discussPostMapper.insertDiscussPost(discussPost);

                //设置错误，将事务回滚，使得两个插入操作失效
//                Integer.valueOf("abc");

                return null;
            }
        });
    }



}
