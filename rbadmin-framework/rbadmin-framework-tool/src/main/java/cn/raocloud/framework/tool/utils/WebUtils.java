package cn.raocloud.framework.tool.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @ClassName: WebUtils
 * @Description: TODO
 * @Author: raobin
 * @Date: 2019/11/4 17:28
 */
@Slf4j
public class WebUtils extends org.springframework.web.util.WebUtils {

    public static final String USER_AGENT_HEADER = "user-agent";
    public static final String UN_KNOWN = "unknown";

    public static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes == null ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取IP
     * @return
     */
    public static String getIp(HttpServletRequest request){
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || UN_KNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        ip = StringUtils.isEmpty(ip) ? null : ip.split(",")[0];
        if  ("127.0.0.1".equals(ip))  {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
        return ip;
    }

    /**
     * IP地址定位库 文件路径
     */
    private static final String FILEPATH = "ip2region/ip2region.db";
    public static String getCityInfo(String ip) {
        String cityInfo = "";
        try {
            String absolutePath = Objects.requireNonNull(WebUtils.class.getClassLoader().getResource(FILEPATH)).getPath();

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
            log.error(e.getMessage(), e.getCause());
        }
        return cityInfo;
    }

    /**
     * 请求参数转换为字符串
     * @param request
     * @return
     */
    public static String getRequestParamString(HttpServletRequest request) {
        String paramString = "";
        try {
            // 获取地址中的参数部分
            String queryString = request.getQueryString();
            if(StringUtils.isNotBlank(queryString)){
                // 参数在地址中
                queryString = new String(queryString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                paramString = queryString.replaceAll("&amp;", "&").replaceAll("%22", "\"");
            } else {
                // 参数在请求体中
                int contentLength = request.getContentLength();
                if(contentLength > 0){
                    byte[] buffer = IOUtils.readBytes(request.getInputStream());
                    String charsetName = request.getCharacterEncoding();
                    Charset charset = StringUtils.isEmpty(charsetName) ? StandardCharsets.UTF_8 : Charset.forName(charsetName);
                    String str = new String(buffer, charset).trim();
                    if (StringUtils.isBlank(str)) {
                        StringBuilder sb = new StringBuilder();
                        Enumeration parameterNames = request.getParameterNames();

                        while(parameterNames.hasMoreElements()) {
                            String key = (String)parameterNames.nextElement();
                            String value = request.getParameter(key);
                            sb.append(key).append("=").append(value).append("&");
                        }

                        str = StringUtils.removeSuffix(sb.toString(), "&");
                    }
                    paramString = str.replaceAll("&amp;", "&");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
        return paramString;
    }
}
