package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody // 异步请求
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        return CommunityUtil.getJsonString(0, "已经关注！");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody // 异步请求
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJsonString(0, "已经取消关注！");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    // userId 查询的是谁的关注列表
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if(user == null) {
            throw  new RuntimeException("该用户不存在");
        }

        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followeesList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(followeesList != null) {
            for (Map<String, Object> map : followeesList) {
                User followee = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(followee.getId()));
                // 把当前用户是否关注了这个关注列表的某个人的状态传进去，方便他关注或取关
            }
        }
        model.addAttribute("users", followeesList);

        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    // userId 查询的是谁的粉丝列表
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if(user == null) {
            throw  new RuntimeException("该用户不存在");
        }

        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> followersList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(followersList != null) {
            for (Map<String, Object> map : followersList) {
                User followee = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(followee.getId()));
                // 把当前用户是否关注了这个关注列表的某个人的状态传进去，方便他关注或取关
            }
        }
        model.addAttribute("users", followersList);

        return "/site/follower";
    }

    // 判断当前用户是否关注了userId对应的user
    private boolean hasFollowed(int userId) {
        if(hostHolder.getUser() == null)
            return false;
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
