package com.lxc.community.entity;

import lombok.Data;

@Data
public class Page {

    //当前页码
    private int current=1;
    //显示上限
    private int limit = 10;
    //数据总数（计算总页数）
    private int rows;
    //查询路径（用于复用分页链接）
    private String path;

    public void setCurrent(int current) {
        if (current>=1){
            this.current = current;
        }
    }

    public void setLimit(int limit) {
        if (limit>=1 && limit<=100) {
            this.limit = limit;
        }
    }

    public void setRows(int rows) {
        if (rows>=0) {
            this.rows = rows;
        }
    }

    /**
     * 计算当前页的起始行,因为查询时是用的起始行
     */
    public int getOffset(){
        //current当前页数 * limit每页显示数据行 - limit当前页数的数据量
        return (current -1)*limit;
    }

    /**
     * 获取总页数
     */
    public int getTotal(){
        //rows/limit(+1)
        if (rows % limit == 0){
            return rows/limit;
        }else {//有余数情况下，不论余多少都要再放一页
            return rows/limit + 1;
        }
    }

    /**
     * 获取分页码显示的初页码
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }


    /**
     * 获取分页码显示的终页码
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        //如果当前页就是最后一页，那就只显示当前页
        return to > total ? total : to;
    }
}
