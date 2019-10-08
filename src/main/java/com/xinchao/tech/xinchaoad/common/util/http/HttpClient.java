package com.xinchao.tech.xinchaoad.common.util.http;


import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpClient {
    private Map<Integer, Integer> registerPortList = new HashMap();

    public HttpClient() {
        Protocol.registerProtocol("https", new Protocol("https", new HttpClient.SimpleHttpsSocketFactory(), 443));
        this.registerPort(Integer.valueOf(443));
    }

    public HttpResult post(String url, Map<String, Object> params, String charset) {
        HttpResult result = new HttpResult();
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Connection", "close");
        postMethod.addRequestHeader("Content-Type", "application/json;charset=" + charset);
        NameValuePair[] data = this.createNameValuePair(params);
        postMethod.setRequestBody(data);
        Integer port = this.getPort(url);
        if (this.isRegisterPort(port)) {
            Protocol client = new Protocol("https", new HttpClient.SimpleHttpsSocketFactory(), port.intValue());
            Protocol.registerProtocol("https ", client);
            this.registerPort(port);
        }
        org.apache.commons.httpclient.HttpClient client1 = new org.apache.commons.httpclient.HttpClient();
        try {
            int ex = client1.executeMethod(postMethod);
            InputStream is = postMethod.getResponseBodyAsStream();
            String responseBody = IOUtils.toString(is, charset);
            result.setStatus(ex);
            result.setResponseBody(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return result;
    }

    public  BufferedInputStream httpPostWithJSON(String url,Map<String,String> params) throws Exception {

        try {
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数


            printWriter.write(JSON.toJSONString(params));
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
//            OutputStream os = new FileOutputStream(new File("D:/abc.png"));
//            int len;
//            byte[] arr = new byte[1024];
//            while ((len = bis.read(arr)) != -1) {
//                os.write(arr, 0, len);
//                os.flush();
//            }
//            os.close();
            return bis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResult get(String url, Map<String, String> params, String charset) {

        HttpResult result = new HttpResult();
        Integer port = this.getPort(url);
        if (this.isRegisterPort(port)) {
            Protocol httpclient = new Protocol("https", new HttpClient.SimpleHttpsSocketFactory(), port.intValue());
            Protocol.registerProtocol("https ", httpclient);
            this.registerPort(port);
        }
        if (params != null && !params.isEmpty()) {
            url = this.appendUrlParam(url, params);
        }
        org.apache.commons.httpclient.HttpClient httpclient1 = new org.apache.commons.httpclient.HttpClient();
        GetMethod httpget = new GetMethod(url);
        try {
            int ex = httpclient1.executeMethod(httpget);
            InputStream is = httpget.getResponseBodyAsStream();
            String responseBody = IOUtils.toString(is, charset);
            result.setStatus(ex);
            result.setResponseBody(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpget.releaseConnection();
        }

        return result;
    }

    public String download(String url, Callback<String, InputStream> callback) {
        Integer port = this.getPort(url);
        if (this.isRegisterPort(port)) {
            Protocol httpclient = new Protocol("https", new HttpClient.SimpleHttpsSocketFactory(), port.intValue());
            Protocol.registerProtocol("https ", httpclient);
            this.registerPort(port);
        }
        org.apache.commons.httpclient.HttpClient httpclient1 = new org.apache.commons.httpclient.HttpClient();
        GetMethod httpget = new GetMethod(url);
        try {
            httpclient1.executeMethod(httpget);
            InputStream is = httpget.getResponseBodyAsStream();
            return callback.invoke(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpget.releaseConnection();
        }
        return null;
    }


    private boolean isRegisterPort(Integer port) {
        return this.registerPortList.get(port) != null;
    }

    private void registerPort(Integer port) {
        this.registerPortList.put(port, port);
    }

    private Integer getPort(String uri) {
        try {
            URL e = new URL(uri);
            int port = e.getPort();
            if (port == -1) {
                if (uri.indexOf("https://") == 0) {
                    port = 443;
                } else {
                    port = 80;
                }
            }

            return Integer.valueOf(port);
        } catch (MalformedURLException var4) {
            throw new RuntimeException(var4);
        }
    }

    private NameValuePair[] createNameValuePair(Map<String, Object> params) {
        NameValuePair[] pairs = new NameValuePair[params.size()];
        int index = 0;

        String key;
        for (Iterator var5 = params.keySet().iterator(); var5.hasNext(); pairs[index++] = new NameValuePair(key, (String) params.get(key))) {
            key = (String) var5.next();
        }

        return pairs;
    }

    private String appendUrlParam(String url, Map<String, String> params) {
        String result = "";
        if (url.contains("?") && url.contains("=")) {
            result = url + "&";
        } else {
            result = url + "?";
        }

        String key;
        for (Iterator var5 = params.keySet().iterator(); var5.hasNext(); result = result + key + "=" + (String) params.get(key) + "&") {
            key = (String) var5.next();
        }

        if (result.charAt(result.length() - 1) == 38) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public class SimpleHttpsSocketFactory implements ProtocolSocketFactory {
        private SSLContext sslcontext = null;

        public SimpleHttpsSocketFactory() {
        }

        private SSLContext createEasySSLContext() {
            try {
                X509TrustManager e = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                TrustManager[] trustMgrs = new TrustManager[]{e};
                SSLContext context = SSLContext.getInstance("SSL");
                context.init((KeyManager[]) null, trustMgrs, (SecureRandom) null);
                return context;
            } catch (Exception var4) {
                var4.printStackTrace();
                throw new HttpClientError(var4.toString());
            }
        }

        private SSLContext getSSLContext() {
            if (this.sslcontext == null) {
                this.sslcontext = this.createEasySSLContext();
            }

            return this.sslcontext;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
            return this.getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
            if (params == null) {
                throw new IllegalArgumentException("Parameters may not be null");
            } else {
                int timeout = params.getConnectionTimeout();
                SSLSocketFactory socketfactory = this.getSSLContext().getSocketFactory();
                if (timeout == 0) {
                    return socketfactory.createSocket(host, port, localAddress, localPort);
                } else {
                    Socket socket = socketfactory.createSocket();
                    InetSocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
                    InetSocketAddress remoteaddr = new InetSocketAddress(host, port);
                    socket.bind(localaddr);
                    socket.connect(remoteaddr, timeout);
                    return socket;
                }
            }
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return this.getSSLContext().getSocketFactory().createSocket(host, port);
        }

        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return this.getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass().equals(SSLSocketFactory.class);
        }

        @Override
        public int hashCode() {
            return HttpClient.SimpleHttpsSocketFactory.class.hashCode();
        }
    }
}
