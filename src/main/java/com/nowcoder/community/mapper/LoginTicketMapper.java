package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id") // 自动生成主键，将生成的值注入给对象，利用keyProperty来指定注入的属性
    int insertLoginTicket(LoginTicket loginTicket);

    // 整张表是围绕ticket这个字段，利用这个字段来查
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    // 我们一般很少做delete操作，一般做update
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket} "
    })
    int updateStatus(String ticket, int status);
}
