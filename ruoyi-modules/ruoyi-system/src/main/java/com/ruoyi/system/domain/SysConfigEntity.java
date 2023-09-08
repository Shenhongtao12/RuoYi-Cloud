package com.ruoyi.system.domain;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.Table;
import lombok.Data;

import java.util.Date;

@Data
@Table("sys_config")
public class SysConfigEntity {

    @Column(primaryKey = true, generatedKey = true)
    private Long configId;

    private String configName;

    private String configKey;

    private String configValue;

    private String configType;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String remark;
}
