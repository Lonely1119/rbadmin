package cn.raocloud.page;

import cn.raocloud.framework.tool.utils.StringUtils;
import lombok.Data;

/**
 * @ClassName: PageDomain
 * @Description: TODO 分页数据
 * @Author: raobin
 * @Date: 2019/11/28 14:53
 * @Version 1.0
 */
@Data
public class PageDomain {
    /**
     * 当前起始索引
     */
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    private Integer pageSize;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序方式
     */
    private String orderByWay;

    public String getOrderBy(){
        if(StringUtils.isBlank(orderByColumn)){
            return "";
        }
        return StringUtils.toUnderlineCase(orderByColumn) + " " + orderByWay;
    }
}
