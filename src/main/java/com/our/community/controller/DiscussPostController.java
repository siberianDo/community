package com.our.community.controller;

import com.our.community.entity.Comment;
import com.our.community.entity.DiscussPost;
import com.our.community.entity.User;
import com.our.community.service.CommentService;
import com.our.community.service.DiscussPostService;
import com.our.community.service.UserService;
import com.our.community.util.CommunityConstant;
import com.our.community.util.CommunityUtil;
import com.our.community.util.HostHolder;
import com.our.community.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.Oneway;
import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
//        判断是否为空
        if (user == null) {
            return CommunityUtil.getJSONString(403, "用户未登录！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        //帖子类型
        discussPost.setType(0);
        //帖子状态
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        //评论数量
        discussPost.setCommentCount(0);
        //帖子评分
        discussPost.setScore(0);

        discussPostService.addDiscussPost(discussPost);

        return CommunityUtil.getJSONString(200, "发布成功！");
    }

    //    评论详情界面
    @Autowired
    private CommentService commentService;

    /**
     * 帖子详情路径
     *
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDetail(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //获取帖子数据
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        //填装到模板
        model.addAttribute("post", discussPost);

        //数据处理,根据用户Id获取用户信息
        User user = userService.findUserById(discussPost.getUserId());
        //将用户信息数据封装到模板
        model.addAttribute("user", user);


        //分页处理
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        //评论：给帖子的评论----------回复，给评论的评论

        //评论列表
        List<Comment> commentList = commentService.findCommentEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit()
        );

        //遍历集合，用评论表的用户ID，获取用户信息，显示用户的头像等信息 评论VO列表
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVO = new HashMap<>();
                //评论
                commentVO.put("comment", comment);
                //填装评论所需的用户信息
                commentVO.put("user", userService.findUserById(comment.getUserId()));

                //回复列表
                List<Comment> replyList = commentService.findCommentEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复VO列表
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        //回复
                        replyVO.put("reply", reply);
                        //用户信息
                        replyVO.put("user", userService.findUserById(reply.getUserId()));
                        //处理回复目标问题----查询回复的目标用户
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target", target);
                        replyVOList.add(replyVO);
                    }
                }
                //将回复装载到评论中
                commentVO.put("replys", replyVOList);

                //计算回复数量并装到Map中
                int replyCount = commentService.selectCommentRowsByEntityTypeAndId(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);
                commentVOList.add(commentVO);
            }
            model.addAttribute("comments", commentVOList);
        }
        return "/site/discuss-detail";
    }


}
