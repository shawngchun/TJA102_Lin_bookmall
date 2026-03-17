package com.bookmall.service;

import com.bookmall.entity.BkmlUser;
import java.util.List;

public interface AdminUserService {
    /**
     * 取得系統內所有使用者
     */
    List<BkmlUser> getAllUsers();

    /**
     * 更新指定使用者的權限角色
     */
    BkmlUser updateUserRole(Integer userId, String newRole);

    /**
     * 刪除指定使用者
     */
    void deleteUser(Integer userId);
    
    /**
     * 根據使用者名稱進行模糊搜尋 (預留功能)
     */
    List<BkmlUser> searchUsersByUsername(String username);
}