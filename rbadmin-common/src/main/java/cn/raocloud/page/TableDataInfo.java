package cn.raocloud.page;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: TableDataInfo
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/28 15:04
 * @Version 1.0
 */
@Data
public class TableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 8388477249465254079L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 列表数据
     */
    private List<T> data;

    /**
     * 消息状态码
     */
    private Integer code;

    public TableDataInfo(){}

    public TableDataInfo(List<T> data, Long total){
        this.data = data;
        this.total = total;
    }
}
