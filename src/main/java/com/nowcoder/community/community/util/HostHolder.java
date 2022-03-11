package com.nowcoder.community.community.util;

import com.nowcoder.community.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 容器作用，用于持有用户信息，线程隔离，代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
