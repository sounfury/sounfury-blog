package org.sounfury.system.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpUtils {
/**
 * 获取公网、外网IP地址
 *
 */
public static String getExternalIp(HttpServletRequest request) {
    try {
        // 先尝试从请求头获取IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (CharSequenceUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (CharSequenceUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (CharSequenceUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (CharSequenceUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (CharSequenceUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 本地IP或无法获取时，尝试从外部服务获取
        if (CharSequenceUtil.isBlank(ip) || "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            // 发送 GET 请求
            String url = "https://www.ip.cn/api/index?ip&type=0";
            HttpResponse httpResponse = HttpRequest.get(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(3000)
                .execute();
            String response = httpResponse.body();

            // 解析返回的 JSON 数据
            JSONObject jsonResponse = JSONUtil.parseObj(response);

            // 提取 IP 地址
            String externalIp = jsonResponse.getStr("ip");

            if (CharSequenceUtil.isNotBlank(externalIp)) {
                return externalIp;
            }
        }

        // 处理多个IP的情况，取第一个非unknown的IP
        if (ip != null && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
    }
}

}
