package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("我被实例化啦！");
    }

    @PostConstruct
    public void init(){
        System.out.println("我被初始化啦！");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("我被销毁了超！");
    }

    public String find() {
        return alphaDao.select();
    }
}
