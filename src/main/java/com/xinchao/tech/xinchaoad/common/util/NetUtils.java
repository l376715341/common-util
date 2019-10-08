package com.xinchao.tech.xinchaoad.common.util;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NetUtils {

    public static final String LOCALHOST = "127.0.0.1";
    public static final String ANYHOST = "0.0.0.0";
    public static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    protected static InetAddress local = null;


    public static InetAddress getLocalIp(String prefix) {
        if (local == null) {
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                if (interfaces != null) {
                    while (interfaces.hasMoreElements()) {
                        try {
                            NetworkInterface network = interfaces.nextElement();
                            if (network.isLoopback() || network.isVirtual() || !network.isUp()) {
                                continue;
                            }
                            Enumeration<InetAddress> addresses = network.getInetAddresses();
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address, prefix)) {
                                        local = address;
                                    }
                                } catch (Exception e) {
//                                logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        } catch (Exception e) {
//                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                        }
                    }
                }
            } catch (Exception e) {
//            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
            }
        }
        if (local == null) {
            throw new BaseException(ResultCode.FAIL_DEPENDENCY_CHECK.getCode(), "get local ip address false");
        }
        return local;
    }


    private static boolean isValidAddress(InetAddress address, String prefix) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches()
                && (StringUtils.isBlank(prefix) || name.startsWith(prefix)));
    }
}
