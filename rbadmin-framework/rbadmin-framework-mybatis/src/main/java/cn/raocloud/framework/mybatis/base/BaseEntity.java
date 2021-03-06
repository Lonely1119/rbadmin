package cn.raocloud.framework.mybatis.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: BaseDTO
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/4 18:27
 */
@Setter
@Getter
public class BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
