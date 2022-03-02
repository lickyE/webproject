package com.nowcoder.community.community.service;

import com.nowcoder.community.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLOutput;

@Service
//@Scope("siingelton")默认单例
@Scope("prototype")//多个实例
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;
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
}
