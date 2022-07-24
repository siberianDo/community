package com.our.community.dao.mapper;

import com.our.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //    查询帖子的数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);//用户ID，起始行号，每页显示数据

    //    @Param()用于给参数取别名
//    如果只有一个参数，并且参数在if中使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //发布帖子
    int insertDiscussPost(DiscussPost discussPost);

    //帖子详情
    DiscussPost selectDiscussPostById(int id);

    //增加帖子的评论数
    int updateCommentCountById(int id, int commentCount);

}
