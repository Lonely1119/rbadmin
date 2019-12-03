package cn.raocloud.base;

import cn.raocloud.framework.tool.utils.DateUtils;
import cn.raocloud.framework.tool.utils.SqlUtils;
import cn.raocloud.page.PageDomain;
import cn.raocloud.page.TableDataInfo;
import cn.raocloud.page.TableSupport;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: BaseController
 * @Description: TODO Web层通用数据处理
 * @Author: raobin
 * @Date: 2019/11/28 14:42
 * @Version 1.0
 */
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder){
        // 当往Model中Set值的时候，如果属性是对象，Spring就会去找对应的editor进行类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport(){
            @Override
            public void setAsText(String text){
                // 支持一下时间格式等常用的时间格式
                // yyyyMMddHHmmss、yyyyMMdd、yyyy-MM-dd HH:mm:ss、yyyy-MM-dd、yyyy-MM-dd HH:mm
                setValue(DateUtils.parse(text));
            }
        });
    }

    /**
     * 设置分页
     */
    protected void startPage(){
        PageDomain pageDomain = TableSupport.buildPageDomain();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if(pageNum != null && pageSize != null){
            String orderBy = SqlUtils.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     * @param data
     * @param <T>
     * @return
     */
    protected <T> TableDataInfo<T> getDataTable(List<T> data){
        TableDataInfo<T> responseData = new TableDataInfo<>();
        responseData.setCode(200);
        responseData.setData(data);
        responseData.setTotal(new PageInfo<>(data).getTotal());
        return responseData;
    }
}
