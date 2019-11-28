package cn.raocloud.page;

import cn.raocloud.constant.Constants;
import cn.raocloud.framework.tool.utils.ConverterUtils;
import cn.raocloud.framework.tool.utils.WebUtils;

/**
 * @ClassName: TableSupport
 * @Description: TODO 表格数据处理
 * @Author: raobin
 * @Date: 2019/11/28 15:03
 * @Version 1.0
 */
public class TableSupport {

    /**
     * 构建分页对象
     * @return
     */
    public static PageDomain buildPageDomain(){
        PageDomain pageDomain = new PageDomain();
        String pageNum = WebUtils.getRequestParameter(Constants.PAGE_NUM);
        pageDomain.setPageNum(ConverterUtils.toInt(pageNum));
        String pageSize = WebUtils.getRequestParameter(Constants.PAGE_SIZE);
        pageDomain.setPageSize(ConverterUtils.toInt(pageSize));
        String orderByColumn = WebUtils.getRequestParameter(Constants.ORDER_BY_COLUMN);
        pageDomain.setOrderByColumn(orderByColumn);
        String orderByWay = WebUtils.getRequestParameter(Constants.ORDER_BY_WAY);
        pageDomain.setOrderByWay(orderByWay);
        return pageDomain;
    }
}
