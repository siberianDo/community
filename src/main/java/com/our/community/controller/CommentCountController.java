package com.our.community.controller;


import com.our.community.entity.Comment;
import com.our.community.service.CommentService;
import com.our.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/commentCount")
public class CommentCountController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/insert/{discussId}", method = RequestMethod.POST)
    public String changeComment(@PathVariable("discussId") int discussId, Comment comment) {

        comment.setUserId(hostHolder.getUser().getId());

        comment.setStatus(0);

        comment.setCreateTime(new Date());

        commentService.insertComment(comment);

        return "redirect:/discuss/detail/" + discussId;
    }

}
