package cn.katool.security.starter.gatway.utils;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.InetAddress;
import java.util.regex.Matcher;

/**
 * 网络工具类
 *
*/
public class NetUtils {

    /**
     * 获取客户端 IP 地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                // 根据网卡取本机配置的 IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (inet != null) {
                    ip = inet.getHostAddress();
                }
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        if (ip == null) {
            return "127.0.0.1";
        }
        return ip;
    }

    // 正则化文件路径
    public static String normalizePath(String path) {
        String result = path.replaceAll("/+", Matcher.quoteReplacement(File.separator));
        return result.replaceAll("\\\\+", Matcher.quoteReplacement(File.separator));
    }
    // 正则化URL路径
    public static String normalizeUrl(String path) {
        String placeholder = "6445BF55-95C4-41F9-8235-34CE3ED9146D";
        String result = path.replaceAll("\\\\+", "/");
        result = result.replaceAll("/+", "/");
        result = result.replaceAll("http:/+", placeholder);
        return result.replace(placeholder, "http://");
    }

}
