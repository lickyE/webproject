package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.LikeService;
import com.nowcoder.community.community.util.Communityutil;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId){
        User user = hostHolder.getUser();

        //点赞
        likeService.like(user.getId(), entityType,entityId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        return Communityutil.getJSONString(0,null,map);
    }
}
