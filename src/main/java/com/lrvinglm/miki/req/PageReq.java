package com.lrvinglm.miki.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class PageReq {

    @NotNull(message="[页码]不能为空")
    private int page;//当前页

    @NotNull(message="[每页条数]不能为空")
    @Max(value=500,message = "[每页条数]最大不超过500条")
    private int size;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    @Override
    public String toString() {
        return "PageReq{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }

}