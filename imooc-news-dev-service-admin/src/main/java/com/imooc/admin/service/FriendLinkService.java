package com.imooc.admin.service;

import com.imooc.mo.FriendLinkMO;


import java.util.List;

public interface FriendLinkService {

    /**
     * 新增或者更新友情链接
     */
    void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO);

    /**
     * 查询友情链接
     */
    List<FriendLinkMO> queryAllFriendLinkList();

    /**
     * 删除友情链接
     */
    void delete(String linkId);

    /**
     * 首页查询友情链接
     */
    List<FriendLinkMO> queryPortalAllFriendLinkList();
}
