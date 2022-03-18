package com.nowcoder.community.community.service;

import com.nowcoder.community.community.dao.LoginTicketMapper;
import com.nowcoder.community.community.dao.UserMapper;
import com.nowcoder.community.community.entity.LoginTicket;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.util.CommunityConstent;
import com.nowcoder.community.community.util.Communityutil;
import com.nowcoder.community.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstent {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private  String domain;

    @Value("${server.servlet.context-path}")
    private  String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //判断空值
        if(user == null){
            throw new IllegalArgumentException("空参数");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("username message","账号为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("password message","密码为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("email message","邮箱为空!");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("username message","已存在该账号");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("email message","已存在该邮箱");
            return map;
        }

        //注册用户
        user.setSalt(Communityutil.generateUUID().substring(0,5));
        user.setPassword(Communityutil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(Communityutil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送邮件
        Context context = new Context();
        context.setVariable("email" , user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    public int activation(int userid,String code){
        User user = userMapper.selectById(userid);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userid,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = Communityutil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(Communityutil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    public Map<String, Object> updatePassword(int userId,String password,String newPassword1,String newPassword2){
        Map<String, Object> map = new HashMap();
        User user = userMapper.selectById(userId);
        if(!newPassword1.equals(newPassword2)){
            map.put("wrongpasswordrepeat","两次密码输入不一致！");
            return map;
        }

        if(newPassword1.length()<=8){
            map.put("wrongpasswordrepeat","新密码长度不能小于8！");
            return map;
        }
        password = Communityutil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("wrongpassword","密码不正确！");
            return map;
        }
        newPassword1 = Communityutil.md5(newPassword1 + user.getSalt());
        userMapper.updatePassword(user.getId(),newPassword1);
        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
}
