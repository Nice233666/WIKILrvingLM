package com.lrvinglm.miki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lrvinglm.miki.aspect.LogAspect;
import com.lrvinglm.miki.domain.User;
import com.lrvinglm.miki.domain.UserExample;
import com.lrvinglm.miki.exception.BusinessException;
import com.lrvinglm.miki.exception.BusinessExceptionCode;
import com.lrvinglm.miki.mapper.UserMapper;
import com.lrvinglm.miki.req.UserQueryReq;
import com.lrvinglm.miki.req.UserSaveReq;
import com.lrvinglm.miki.resp.PageResp;
import com.lrvinglm.miki.resp.UserQueryResp;
import com.lrvinglm.miki.utils.CopyUtil;
import com.lrvinglm.miki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;
    private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // List<UserResp> respList = new ArrayList<>();
        // for (User user : userList) {
        //     // UserResp userResp = new UserResp();
        //     // BeanUtils.copyProperties(user, userResp);
        //     // 对象复制
        //     UserResp userResp = CopyUtil.copy(user, UserResp.class);
        //
        //     respList.add(userResp);
        // }

        // 列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }


    /**
     * 保存
     */
    public void save(UserSaveReq req){
        User user=CopyUtil.copy(req,User.class);
        if(ObjectUtils.isEmpty(user.getId())){
            if(ObjectUtils.isEmpty(selectByLoginName(user.getLoginName()))){
                //新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else{
                //用户名已存在  自定义异常
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }

        }else{
            //更新 前端的校验会给绕过，这个操作就是把LoginName清空,而带Selective这个的方法就是在数据有值的时候才更新，不然不更新
            user.setLoginName(null);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    /**
     * 删除
     */
    public void delete(Long id){
        //删除指定id的数据
        userMapper.deleteByPrimaryKey(id);

    }

    /**
     * 通过loginName查询用户
     */
    public User selectByLoginName(String loginName){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if(CollectionUtils.isEmpty(userList)){
            return null;
        }else{
            return userList.get(0);
        }

    }

}
