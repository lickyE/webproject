package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.entity.Comment;
import com.nowcoder.community.community.entity.DiscussPost;
import com.nowcoder.community.community.entity.Page;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.CommentService;
import com.nowcoder.community.community.service.DiscussPostService;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CommunityConstent;
import com.nowcoder.community.community.util.Communityutil;
import com.nowcoder.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.attribute.DosFileAttributes;
import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstent {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return Communityutil.getJSONString(403,"没有登陆！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        //报错的情况将来统一处理
        return Communityutil.getJSONString(0,"发布成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussId, Model model, Page page){
        //帖子查询
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussId);
        model.addAttribute("post",discussPost);
        //作者查询
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussId);
        page.setRows(discussPost.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论
        List<Comment> commentList =
                commentService.findCommentByEntity(ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());

        //评论的列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();

        if(commentList != null){
            for(Comment comment : commentList){
                Map<String,Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //查询回复
                List<Comment> replyList =
                        commentService.findCommentByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        Map<String ,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标
                        User target =
                                reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }
}
