package cn.raocloud.framework.mybatis.base;

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
public class BaseDTO implements Serializable {

    private Long id;

    private Date createTime;

    private Date updateTime;
}
