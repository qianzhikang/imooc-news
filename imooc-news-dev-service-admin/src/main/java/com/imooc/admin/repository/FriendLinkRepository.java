package com.imooc.admin.repository;

import com.imooc.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Description mongodb dao层
 * @Date 2023-05-11-13-24
 * @Author qianzhikang
 */
public interface FriendLinkRepository extends MongoRepository<FriendLinkMO,String> {
    List<FriendLinkMO> getAllByIsDelete(Integer isDelete);
}
