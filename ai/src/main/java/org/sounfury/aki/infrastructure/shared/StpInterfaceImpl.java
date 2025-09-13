package org.sounfury.aki.infrastructure.shared;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Component
@Primary // 如果你有多个 StpInterface 实现，确保用这一版
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    // 缓存键前缀（和主系统写入时保持一致）
    private static final String ROLE_BY_UID_KEY_FMT  = "token:loginId-find-role:%s";
    private static final String PERM_BY_ROLE_KEY_FMT = "token:roleCode-find-permission:%s";

    private final SaTokenDao saTokenDao = SaManager.getSaTokenDao();


    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String key = String.format(ROLE_BY_UID_KEY_FMT, String.valueOf(loginId));

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) saTokenDao.getObject(key);

        // 仅缓存可读：缓存缺失时返回空，避免触发 DB
        if (roles == null) {
            // 也可以选择抛异常：throw new SaTokenException("role cache miss");
            return Collections.emptyList();
        }

        // 可选：读后续期（确保你的 DAO 支持 updateTimeout，否则删掉下面这行）
        // saTokenDao.updateTimeout(key, timeout);

        // 返回防御性拷贝，避免外部修改
        return new ArrayList<>(roles);
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> roleList = getRoleList(loginId, loginType);

        if (roleList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> permSet = new LinkedHashSet<>();
        for (String roleCode : roleList) {
            String key = String.format(PERM_BY_ROLE_KEY_FMT, roleCode);

            @SuppressWarnings("unchecked")
            List<String> perms = (List<String>) saTokenDao.getObject(key);

            if (perms != null && !perms.isEmpty()) {
                permSet.addAll(perms);

                // 可选：读后续期
                // saTokenDao.updateTimeout(key, timeout);
            }
        }
        return new ArrayList<>(permSet);
    }
}
