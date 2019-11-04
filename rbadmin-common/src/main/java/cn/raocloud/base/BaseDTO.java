package cn.raocloud.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: BaseDTO
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/4 18:27
 */
@Setter
@Getter
public class BaseDTO {

    private Boolean delete;

    private long createTime;

    private long updateTime;
}
