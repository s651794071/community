package com.nowcoder.community.dao.mapper;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status); // 更新激活状态

    int updateHeader(int id, String headerUrl); // 更新头像

    int updatePassword(int id, String password);
}