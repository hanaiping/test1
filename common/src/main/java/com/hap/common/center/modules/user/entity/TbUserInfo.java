package com.hap.common.center.modules.user.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author aiPing
 * @since 2019-09-25
 */
@Data
@ApiModel(value = "用户表")
@TableName("tb_user_info")
public class TbUserInfo extends Model<TbUserInfo> {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "编号")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    @ApiModelProperty(value = "codeid")
    @TableField("code_id")
    private String codeId;
    @ApiModelProperty(value = "应用号id修改状态 0 可以修改 1 已修改过")
    @TableField("codeid_status")
    private Integer codeidStatus;
    @ApiModelProperty(value = "用户角色（比如普通会员，大V等）")
    @TableField("user_type")
    private String userType;
    @ApiModelProperty(value = "会员等级")
    private String grade;
    @ApiModelProperty(value = "推荐人ID")
    @TableField("recommend_id")
    private Long recommendId;
    @ApiModelProperty(value = "注册类型(0,手机号注册,1、微信号注册，2、用户名密码注册)")
    @TableField("reg_type")
    private Integer regType;
    @ApiModelProperty(value = "用户唯一标识")
    private String uuid;
    @ApiModelProperty(value = "app类型")
    @TableField("app_id")
    private Integer appId;

    @ApiModelProperty(value = "微信用户ID")
//    @TableField("open_id")
    private String openId;

    @ApiModelProperty(value = "微信唯一ID")
    @TableField("union_id")
    private String unionId;
    @ApiModelProperty(value = "用户名")
    @TableField("user_name")
    private String userName;
    @ApiModelProperty(value = "昵称")
    @TableField("nick_name")
    private String nickName;
    @ApiModelProperty(value = "用户真实姓名")
    @TableField("real_name")
    private String realName;
    @ApiModelProperty(value = "登录密码(MD5(密码+盐))")
    private String password;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "性别(-1,保密，0女，1男)")
    private Integer gender;
    @ApiModelProperty(value = "年龄(-1,保密)")
    private Integer age;
    @ApiModelProperty(value = "身份证号")
    @TableField("id_card")
    private String idCard;
    @ApiModelProperty(value = "支付密码(MD5(密码+盐))")
    @TableField("pay_password")
    private String payPassword;
    @ApiModelProperty(value = "头像")
    @TableField("src_face")
    private String srcFace;
    @ApiModelProperty(value = "职业")
    private String job;
    @ApiModelProperty(value = "学校")
    private String school;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "星座")
    private String constellation;
    @ApiModelProperty(value = "封面")
    private String cover;
    @ApiModelProperty(value = "用户签名")
    private String signature;
    @ApiModelProperty(value = "生日")
    private String birthday;
    @ApiModelProperty(value = "登录盐")
    private String salt;
    @ApiModelProperty(value = "支付盐")
    @TableField("pay_salt")
    private String paySalt;
    @ApiModelProperty(value = "用户当前设备信息ID")
    @TableField("device_id")
    private String deviceId;
    @ApiModelProperty(value = "用户当前的位置信息ID")
    @TableField("location_id")
    private Integer locationId;
    @ApiModelProperty(value = "用户地址")
    private String address;
    @ApiModelProperty(value = "用户的钱包地址")
    @TableField("burse_id")
    private Integer burseId;
    @ApiModelProperty(value = "0：正常，1：冻结，2：拉黑")
    private Integer status;
    @ApiModelProperty(value = "0默认未删除,1删除")
    @TableField("del_flag")
    private Integer delFlag;
    @ApiModelProperty(value = "注册时间")
    @TableField("create_time")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "实名认证状态（0：未认证，1：已认证）")
    @TableField("certification_status")
    private Integer certificationStatus;

    @ApiModelProperty(value = "用户二维码")
    private String qr;

    public TbUserInfo() {
    }


    public TbUserInfo(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

    @Override
    public String toString() {
        return "TbUserInfo{" +
                ", userId=" + userId +
                ", codeId=" + codeId +
                ", codeidStatus=" + codeidStatus +
                ", userType=" + userType +
                ", grade=" + grade +
                ", recommendId=" + recommendId +
                ", regType=" + regType +
                ", uuid=" + uuid +
                ", appId=" + appId +
//                ", openId=" + openId +
                ", unionId=" + unionId +
                ", userName=" + userName +
                ", nickName=" + nickName +
                ", realName=" + realName +
                ", password=" + password +
                ", mobile=" + mobile +
                ", gender=" + gender +
                ", age=" + age +
                ", idCard=" + idCard +
                ", payPassword=" + payPassword +
                ", srcFace=" + srcFace +
                ", job=" + job +
                ", school=" + school +
                ", email=" + email +
                ", constellation=" + constellation +
                ", cover=" + cover +
                ", signature=" + signature +
                ", birthday=" + birthday +
                ", salt=" + salt +
                ", paySalt=" + paySalt +
                ", deviceId=" + deviceId +
                ", locationId=" + locationId +
                ", address=" + address +
                ", burseId=" + burseId +
                ", status=" + status +
                ", certificationStatus=" + certificationStatus +
                ", delFlag=" + delFlag +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", remark=" + remark +
                "}";
    }
}