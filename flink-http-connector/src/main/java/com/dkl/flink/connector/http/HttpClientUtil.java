package com.dkl.flink.connector.http;

import com.dkl.flink.utils.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClientUtil {
    public static String post(String uri, String json) throws Exception {
        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpClient client = HttpClients.createDefault();
        StringEntity entity = StringUtils.isNullOrEmpty(json) ? new StringEntity("utf-8") : new StringEntity(json, "utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        HttpResponse resp = client.execute(httpPost);
        String respContent = EntityUtils.toString(resp.getEntity(), "UTF-8");
        if (resp.getStatusLine().getStatusCode() == 200) {
            return respContent;
        } else {
            throw new RuntimeException(String.format("调用接口%s失败：%s", uri, respContent));
        }
    }

    public static String get(String uri) throws Exception {
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse resp = client.execute(httpGet);
        String respContent = EntityUtils.toString(resp.getEntity(), "UTF-8");
        if (resp.getStatusLine().getStatusCode() == 200) {
            return respContent;
        } else {
            throw new RuntimeException(String.format("调用接口%s失败：%s", uri, respContent));
        }
    }

    public static String doGet(String httpUrl) throws IOException {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        // 返回结果字符串
        String result = null;
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(HttpClientUtil.post("http://mock.apifox.cn/m1/2518376-0-default/test/flink/post/order", ""));
        System.out.println(HttpClientUtil.get("http://mock.apifox.cn/m1/2518376-0-default/test/flink/get/order"));
    }
}
