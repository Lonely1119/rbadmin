package cn.raocloud.utils;

import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName: IpUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/4 17:28
 */
public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取IP
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        if  ("127.0.0.1".equals(ip))  {
            // 获取本机真正的ip地址
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return ip;
    }

    /**
     * IP地址定位库 文件路径
     */
    private static final String filepath = "ip2region/ip2region.db";
    public static String getCityInfo(String ip) {
        String cityInfo = "";
        try {
            String absolutePath = IpUtils.class.getClassLoader().getResource(filepath).getPath();

            DbConfig config = new DbConfig();
            DbSearcher searcher = new DbSearcher(config, absolutePath);
            DataBlock dataBlock = searcher.btreeSearch(ip);
            String address = dataBlock.getRegion().replace("0|","");
            if(address.charAt(address.length()-1) == '|'){
                address = address.substring(0,address.length() - 1);
            }
            final String intranet = "内网IP|内网IP";
            cityInfo = address.equals(intranet) ? "内网IP" : address;
        } catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
        }
        return cityInfo;
    }

    public static void main(String[] args) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();;
            String absolutePath = IpUtils.class.getClassLoader().getResource(filepath).getPath();

            DbConfig config = new DbConfig();
            DbSearcher searcher = new DbSearcher(config, absolutePath);
            DataBlock dataBlock = searcher.btreeSearch(ip);

            System.out.println(dataBlock.getCityId());
            System.out.println(dataBlock.getRegion());
            System.out.println(dataBlock.getDataPtr());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
