package com.nowcoder.community.entity;

import lombok.Data;

/**
 * 封装分页相关信息
 */
@Data
public class Page {

    // 当前页码
    private int current = 1; //默认页码
    // 显示上限
    private int limit = 10;
    // 数据总数（用于计算总页数）
    private int rows;
    // 查询路径（用于复用分页链接）
    private String path;

    public void setCurrent(int current) {
        if(current >= 1) //防止用户跳转页数的时候传入非正整数
            this.current = current;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public void setRows(int totalRows) {
        if(totalRows >= 0)
            this.rows = totalRows;
    }

    /**
     * 获取当前页面的起始行
     * @return
     */
    public int getOffset() {
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal(){
        if (rows % limit == 0)
            return rows / limit;
        else
            return rows / limit + 1; // 不管余几都单独多出来一页
    }

    /**
     * 获取起始页码
     * 假如当前页面是6，设置一下用户可以获取的页码是4，5，6，7，8
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        if(from < 1)
            return 1; //有可能当前页是第1页或者第2页
        else
            return from;
    }
    /**
     * 获取可以到哪页
     * 假如当前页面是6，设置一下用户可以获取的页码是4，5，6，7，8
     * @return
     */
    public int getTo(){
        int to = current + 2;
        if (to > getTotal()) // 如果加上两页大于最后一页
            return getTotal();
        else
            return to;
    }
}
