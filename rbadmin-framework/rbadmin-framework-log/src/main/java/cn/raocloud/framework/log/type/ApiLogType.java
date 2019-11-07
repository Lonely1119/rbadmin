package cn.raocloud.framework.log.type;

/**
 * @ClassName: ApiLogType
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/7 10:08
 * @Version 1.0
 */
public enum ApiLogType {

    /**
     * 默认类型
     */
    DEFAULT("default"),
    /**
     * 新增操作
     */
    ADD("add"),
    /**
     * 修改操作
     */
    UPDATE("update");

    private String type;

    ApiLogType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
