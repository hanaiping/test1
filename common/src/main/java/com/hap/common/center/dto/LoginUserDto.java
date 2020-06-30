package com.hap.common.center.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: rrc-cloud
 * @description: 登录用户基本信息
 * @author: chenhj
 * @create: 2019-12-25 11:14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户登录基本信息")
public class LoginUserDto implements Serializable {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("用户类型")
    private String userTypes;

    @ApiModelProperty("用户角色")
    private List<String> roles;

    @ApiModelProperty("用户权限资源")
    private List<String> resourceUrl;

    @ApiModelProperty("代理商(个人,企业)店铺ID")
    private Long shopId;

    @ApiModelProperty("代理商(个人,企业)店铺名称")
    private String shopName;

    @ApiModelProperty("店铺类型")
    private Integer shopType;


    @ApiModelProperty("供应商店铺ID")
    private Long supplierShopId;

    @ApiModelProperty("供应商名称")
    private String supplierName;

    @ApiModelProperty("供应商类型")
    private Integer supplierType;


    @ApiModelProperty("登录平台")
    private String platform;

    @ApiModelProperty("登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date loginTime;


}
