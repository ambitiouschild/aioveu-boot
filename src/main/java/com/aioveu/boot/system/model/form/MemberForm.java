package com.aioveu.boot.system.model.form;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

/**
 * 会员信息表单对象
 *
 * @author ambitiouschild
 * @since 2025-07-11 15:13
 */
@Getter
@Setter
@Schema(description = "会员信息表单对象")
public class MemberForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "会员编号")
    @NotNull(message = "会员编号不能为空")
    private Integer id;

    @Schema(description = "会员姓名")
    @Size(max=50, message="会员姓名长度不能超过50个字符")
    private String name;

    @Schema(description = "会员手机号")
    @Size(max=20, message="会员手机号长度不能超过20个字符")
    private String mobile;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "会员年龄")
    private Integer age;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime  updateTime;

    @Schema(description = "是否删除(1:已删除;0:未删除)")
    private Integer isDeleted;


}
