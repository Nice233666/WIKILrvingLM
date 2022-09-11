package com.lrvinglm.miki.controller;

import com.lrvinglm.miki.req.EbookQueryReq;
import com.lrvinglm.miki.req.EbookSaveReq;
import com.lrvinglm.miki.resp.CommonResp;
import com.lrvinglm.miki.resp.EbookQueryResp;
import com.lrvinglm.miki.resp.PageResp;
import com.lrvinglm.miki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/ebook")
public class EbookControl {
    @Resource
    private EbookService ebookService;

    /**
     * 查询所有电子书
     * @param req
     * @return
     */
    @RequestMapping("/list")  //接口支持所有的请求方式
    public CommonResp list(@Valid EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list= ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    /**
     * 保存电子书
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp();
        ebookService.save(req);
        return resp;
    }

    /**
     * id删除电子书
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp();
        ebookService.delete(id);
        return resp;
    }
}
