package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();

        if (user == null) {
            return CommunityUtil.getJsonString(403, "你还没登陆！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况将来统一处理

        return CommunityUtil.getJsonString(0,"发贴成功了！");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 根据帖子id查到帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 根据帖子userId，然后用UserService查到帖子作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        //  评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：帖子的评论
        // 回复：评论的评论

        // 评论列表
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST,post.getId(),page.getOffset(), page.getLimit());

        // 评论Vo列表，commentVoList：存commentVo的list
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 实例化每个评论的commentVo
                Map<String, Object> commentVo = new HashMap<>();
                // 放入该评论
                commentVo.put("comment", comment);
                // 放入该评论作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(), 0, Integer.MAX_VALUE);

                // 回复Vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 该回复
                        replyVo.put("reply",reply);
                        // 该回复作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));

                        // 该回复目标target（回复的谁）如果是0就说明是普通回复（回复的是一个评论不是某个回复）
                        // 不然就通过userService和reply的targetId查到回复的是谁的回复
                        User target = reply.getTargetId() == 0 ?
                                null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        // 记得把replyVo放进replyVoList里
                        replyVoList.add(replyVo);
                    }
                }

                // 还要把回复Vo列表装进当前的评论Vo：commentVo里
                // 评论Vo：评论，该评论的用户，该评论下的所有回复
                commentVo.put("replys", replyVoList);

                // 评论的回复的数量也要存，因为要在评论的右下角显示有几个赞，几个回复
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());

                commentVo.put("replyCount", replyCount);

                // 记得把commentVo放进commentVoList里
                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentVoList);

        return "/site/discuss-detail";
    }
}
