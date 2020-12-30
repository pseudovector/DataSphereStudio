package com.webank.wedatasphpere.dss.user.service;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;

import javax.ws.rs.core.Response;

public interface Command {

    final public static String SUCCESS = "success";
    /**
     * 授权开通服务
     * @param body
     * @return 成功 success  其他失败
     */
    public String authorization(AuthorizationBody body);

    /**
     * 关闭授权
     * @param body
     * @return 成功 success  其他失败
     */
    public String undoAuthorization(AuthorizationBody body);

    /**
     * 扩容
     * @param body
     * @return 成功 success  其他失败
     */
    public String capacity(AuthorizationBody body);

    /**
     * 续费
     * @param body
     * @return 成功 success  其他失败
     */
    public String renew(AuthorizationBody body);
}
