package com.imooc.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pojo.Fans;
import com.imooc.user.mapper.FansMapper;
import com.imooc.user.service.FansService;
import org.springframework.stereotype.Service;

/**
* @author qianzhikang
* @description 针对表【fans(粉丝表，用户与粉丝的关联关系，粉丝本质也是用户。
关联关系保存到es中，粉丝数方式和用户点赞收藏文章一样。累加累减都用redis来做。
字段与用户表有些冗余，主要用于数据可视化，数据一旦有了之后，用户修改性别和省份无法影响此表，只认第一次的数据。

)】的数据库操作Service实现
* @createDate 2023-05-08 15:08:35
*/
@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans>
    implements FansService {

}




