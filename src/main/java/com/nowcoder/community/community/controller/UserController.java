package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.annotation.LoginRequired;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.FollweService;
import com.nowcoder.community.community.service.LikeService;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CommunityConstent;
import com.nowcoder.community.community.util.Communityutil;
import com.nowcoder.community.community.util.HostHolder;
import org.apache.catalina.Host;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstent {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollweService follweService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","??????????????????");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String sufflix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(sufflix)){
            model.addAttribute("error","??????????????????");
            return "/site/setting";
        }
        fileName = Communityutil.generateUUID() + sufflix;
        File dest = new File(uploadPath + "/" + fileName);

        try {
            //????????????
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
            throw new RuntimeException("??????????????????????????????????????????",e);
        }

        String headUrl = domain + contextPath + "/user/header/" + fileName;
        User user = hostHolder.getUser();

        userService.updateHeader(user.getId(),headUrl);
        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(path = "/resetPassword",method = RequestMethod.POST)
    public String resetPassword(String password,String newPassword1,String newPassword2,Model model){
        User user = hostHolder.getUser();
        int id = user.getId();
        Map map = userService.updatePassword(id,password,newPassword1,newPassword2);
        if(map.containsKey("wrongpassword")){
            model.addAttribute("passworderror","???????????????");
            return "/site/setting";
        }

        if(map.containsKey("wrongpasswordrepeat")){
            model.addAttribute("passwordrepeaterror","????????????????????????????????????????????????");
            return "/site/setting";
        }

        hostHolder.setUser(user);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String fileName, HttpServletResponse response){
        fileName = uploadPath + "/" + fileName;
        String sufflix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + sufflix);
        try (FileInputStream fis = new FileInputStream(fileName);
             OutputStream os = response.getOutputStream();){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("??????????????????!" + e.getMessage());
        }

    }

    //????????????
    @RequestMapping(path = "/profile/{userId}" , method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("?????????????????????");
        }

        //??????
        model.addAttribute("user",user);


        //??????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //????????????
        long followee = follweService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followee);
        //????????????
        long follower = follweService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",follower);
        //???????????????????????????
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = follweService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
