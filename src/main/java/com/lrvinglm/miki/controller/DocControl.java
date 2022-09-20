package com.lrvinglm.miki.controller;

import com.lrvinglm.miki.req.DocQueryReq;
import com.lrvinglm.miki.req.DocSaveReq;
import com.lrvinglm.miki.resp.CommonResp;
import com.lrvinglm.miki.resp.DocQueryResp;
import com.lrvinglm.miki.resp.PageResp;
import com.lrvinglm.miki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/doc")
public class DocControl {
    @Resource
    private DocService docService;

    @RequestMapping("/all/{ebookId}")  //接口支持所有的请求方式
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list= docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }
    /**
     * 查询所有电子书
     * @param req
     * @return
     */
    @RequestMapping("/list")  //接口支持所有的请求方式
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list= docService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
     * 保存电子书
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp();
        docService.save(req);
        return resp;
    }

    /**
     * id删除电子书
     * @param idsStr
     * @return
     */
    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp();
        if(!" ".equals(idsStr)){
            List<String> list = Arrays.asList(idsStr.split(","));
            docService.delete(list);
        }
        return resp;
    }

    /**
     * 查询内容
     * @param id
     * @return
     */
    @RequestMapping("/find-content/{id}")  //接口支持所有的请求方式
    public CommonResp findContent(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content= docService.findContent(id);
        resp.setContent(content);
        return resp;
    }
}
