package com.lrvinglm.miki.controller;

import com.lrvinglm.miki.req.UserQueryReq;
import com.lrvinglm.miki.req.UserSaveReq;
import com.lrvinglm.miki.resp.CommonResp;
import com.lrvinglm.miki.resp.UserQueryResp;
import com.lrvinglm.miki.resp.PageResp;
import com.lrvinglm.miki.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/user")
public class UserControl {
    @Resource
    private UserService userService;

    /**
     * 查询所有用户
     * @param req
     * @return
     */
    @GetMapping("/list")  //接口支持所有的请求方式
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list= userService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
     * 保存用户
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        CommonResp resp = new CommonResp();
        userService.save(req);
        return resp;
    }

    /**
     * id删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp();
        userService.delete(id);
        return resp;
    }
}
