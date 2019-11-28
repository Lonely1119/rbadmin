package cn.raocloud.framework.tool.utils;

import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.*;
//import org.elasticsearch.client.indices.GetIndexRequest;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;

@Slf4j
public class EsUtil {

//    private static volatile RestHighLevelClient client;
//
//    /**
//     * 获取es客户端
//     *
//     * @return
//     */
//    public static RestHighLevelClient getClient() {
//        if (null == client) {
//            synchronized (EsUtil.class){
//                if(null == client){
//                    // 获取并校验Elasticsearch配置信息
//                    String esHosts = "";
//                    if(StringUtils.isEmpty(esHosts) || StringUtils.isEmpty(esHosts.trim())){
//                        String msg = "Elasticsearch server connection parameter information is not configured, " +
//                                "please configure the parameter{ParamName: IP/PORT, " +
//                                "ParamKey: es_hosts, Remarks: Multiple IP/PORT are separated by commas, " +
//                                "but the format does not match will be ignored} in System Management -> Parameter Settings.";
//                        if(log.isErrorEnabled()){
//                            log.error(msg);
//                        }
//                        throw new RuntimeException(msg);
//                    }
//                    if(log.isInfoEnabled()){
//                        log.info("Elasticsearch server connection parameter information: hosts=[{}]", esHosts);
//                    }
//                    client = buildRestClient(esHosts);
//                }
//            }
//        }
//        return client;
//    }
//
//    /**
//     * 构建Elasticsearch连接客户端
//     * @param hosts 多个逗号分隔的主机地址
//     * @param port 端口
//     * @return
//     */
//    private static final String HOST_SEPARATOR = ",";
//    private static RestHighLevelClient buildRestClient(String hosts){
//        List<HttpHost> httpHostList = new ArrayList<>();
//
//        String[] hostArray = StringUtils.split(hosts, HOST_SEPARATOR);
//        for(String host : hostArray){
//            if(StringUtils.isNotEmpty(host) && StringUtils.isNotEmpty(host.trim())){
//                String[] info = StringUtils.split(host, "/");
//                if(info.length == 2){
//                    int port = Integer.valueOf(info[1]);
//                    HttpHost httpHost = new HttpHost(info[0], port);
//                    httpHostList.add(httpHost);
//                }
//            }
//        }
//        if(httpHostList.isEmpty()){
//            String msg = "Elasticsearch server connection parameter value is incorrect or empty，" +
//                    "please configure the parameter{ParamName: IP/PORT, " +
//                    "ParamKey: es_hosts, Remarks: Multiple IP/PORT are separated by commas, " +
//                    "but the format does not match will be ignored} in System Management -> Parameter Settings.";
//            if(log.isErrorEnabled()){
//                log.error(msg);
//            }
//            throw new RuntimeException(msg);
//        }
//        RestClientBuilder builder = RestClient.builder(httpHostList.toArray(new HttpHost[0]));
//        return new RestHighLevelClient(builder);
//    }
//
//    /**
//     * 判断索引是否存在
//     *
//     * @param index 索引
//     * @return
//     * @throws IOException
//     */
//    public static boolean exist(String index) throws IOException {
//        if(StringUtils.isEmpty(index)){
//            return false;
//        }
//        IndicesClient adminClient = getClient().indices();
//        GetIndexRequest request = new GetIndexRequest(index);
//        return adminClient.exists(request, RequestOptions.DEFAULT);
//    }
//
//    /**
//     * 根据索引集合返回存在的索引
//     * @param indices 索引列表
//     * @return
//     */
//    public static String[] getExistIndex(String... indices) throws IOException {
//        if(indices == null || indices.length == 0){
//            return new String[0];
//        }
//        List<String> indexList = new ArrayList<>();
//        for(String index : indices){
//            if(exist(index)){
//                indexList.add(index);
//            }
//        }
//        return indexList.toArray(new String[0]);
//    }
//
//    /**
//     * 根据时间范围获取存在索引
//     * @param indexPrefix 索引前缀
//     * @param beginDate 开始时间
//     * @param endDate 结束时间
//     * @return
//     */
//    public static String[] getExistIndex(String indexPrefix, Date beginDate, Date endDate) throws IOException {
//        return getExistIndex(indexPrefix, beginDate.getTime(), endDate.getTime());
//    }
//    public static String[] getExistIndex(String indexPrefix, long beginTimestamp, long endTimestamp) throws IOException {
//        List<String> list = new ArrayList<>();
//
//        Calendar beginCalendar = Calendar.getInstance();
//        beginCalendar.setTimeInMillis(beginTimestamp);
//        Calendar endCalendar = Calendar.getInstance();
//        endCalendar.setTimeInMillis(endTimestamp);
//        endCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        while(beginCalendar.before(endCalendar)){
//            list.add(formatIndex(indexPrefix, beginCalendar.getTime()));
//            beginCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        }
//
//        return getExistIndex(list.toArray(new String[0]));
//    }
//
//    /**
//     * 根据时间获取索引字符串
//     * @return
//     */
//    private static final String INDEX_SUFFIX_FORMAT = "yyyyMMdd";
//    public static String formatIndex(String indexPrefix, long timestamp){
//        return formatIndex(indexPrefix, new Date(timestamp));
//    }
//    public static String formatIndex(String indexPrefix, Date date){
//        SimpleDateFormat format = new SimpleDateFormat(INDEX_SUFFIX_FORMAT);
//        String indexSuffix = format.format(date);
//        return indexPrefix + indexSuffix;
//    }
}
