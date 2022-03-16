package com.nowcoder.community.community.service;

import com.nowcoder.community.community.dao.AlphaDao;
import com.nowcoder.community.community.dao.DiscussPostMapper;
import com.nowcoder.community.community.dao.UserMapper;
import com.nowcoder.community.community.entity.DiscussPost;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.util.Communityutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.crypto.Data;
import java.sql.SQLOutput;
import java.util.Date;

@Service
//@Scope("siingelton")默认单例
@Scope("prototype")//多个实例
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService(){
        System.out.println("实例化");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化");
    }

    @PreDestroy
    public void destory(){
        System.out.println("销毁");
    }

    public String find(){
        return alphaDao.select();
    }


    //事务隔离管理
    //REQUIRED:支持当前事务（外部事务，调用者），如果不存在外部事务，那就创建新事务
    //REQUIRED_NEW：创建一个新的事务并且暂停外部事务
    //NESTED：如果当前存在事务（外部事务）则嵌套在事务中执行（独立的提交和回滚），否则就和required一样
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user = new User();
        user.setUsername("aplha");
        user.setSalt(Communityutil.generateUUID().substring(0,5));
        user.setPassword(Communityutil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head.99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //新增帖子

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报到");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        //建错，看能否回滚
        Integer.valueOf("abc");

        return "ok";
    }

    //一般使用上面的注解进行事务管理，也可以使用下面的编程式事务管理
    //需要注入bean TranscationTemplate
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                //新增用户
                User user = new User();
                user.setUsername("aplha");
                user.setSalt(Communityutil.generateUUID().substring(0,5));
                user.setPassword(Communityutil.md5("123"+user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head.99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                //新增帖子

                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("hello");
                post.setContent("新人报到");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                //建错，看能否回滚
                Integer.valueOf("abc");

                return "ok";

            }
        });
    }

}
