package com.hap.common.center.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtUser implements Serializable {

    //用户iD
    private Long userId;
    //角色
    private String roles;
    //状态
    private Integer status;

    //员工Id
    private Long empId;
    //权限
    private String permission;
    //供货商ID
    private Long supplierId;

    //过期时间
    private Long cookieDuration;
}
