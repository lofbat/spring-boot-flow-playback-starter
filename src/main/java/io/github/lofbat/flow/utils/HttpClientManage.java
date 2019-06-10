package io.github.lofbat.flow.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.dubbo.common.utils.StringUtils;

/**
 * OkHttp官方文档并不建议我们创建多个OkHttpClient，因此全局使用一个。
 * 支持自动读取配置文件中的过期时间配置以及CA导入，或采用默认配置
 * 支持实现同步请求（普通+快速）,异步请求（普通+快速）
 * 支持头文件设置
 * 支持https
 * 支持代理ip port 若未设置代理ip等信息则采用本机ip常规方式进行请求
 * @date 2017年6月10日 下午5:07:14
 */
@Slf4j
public class HttpClientManage {
    private static volatile HttpClientManage instance;

    private static int CONNECT_TIMEOUT = 60;
    private static int READ_TIMEOUT = 100;
    private static int WRITE_TIMEOUT = 60;
    private static int CONNECT_TIMEOUT_QUICK = 3;
    private static int READ_TIMEOUT_QUICK = 5;
    private static int WRITE_TIMEOUT_QUICK = 3;
    /**
     * ca证书存放路径
     */
    private static String CA_PATH = null;
    /**
     * 代理域名或者IP
     */
    private static String PROXY_IP = null;
    /**
     * 代理端口
     */
    private static int PROXY_PORT = 80;
    /**
     * 是否忽略正式
     */
    private static boolean IS_INORE_CA = true;

    private static String PARAM_SPLIT_FIRST = "?";
    private static String PARAM_SPLIT_VAL = "=";
    private static String PARAM_SPLIT = "&";
    private static String DEFAULT_ENCODE = "UTF-8";
    /**
     * ca证书别名
     */
    private static String SSL_KEY_ALIAS = "DIY";

    /**
     * 证书工厂
     */
    private static SSLSocketFactory sslSocketFactory = null;
    /**
     * 证书类型
     */
    private static X509TrustManager trustManager = null;

    /**
     * 正常请求
     */
    private static OkHttpClient mOkHttpClient;

    /**
     * 局域网内调用的快速请求 响应时间要求比普通正常请求快20倍
     */
    private static OkHttpClient mOkHttpClientQuick;

    /**
     * 代理请求
     */
    private static OkHttpClient mOkHttpClientProxy;

    /**
     * 单体实例类
     * @return
     * @exception/throws [异常类型] [异常说明](可选)
     * @date 2017年6月10日 下午5:00:57
     * @since 1.0.0
     */
    public static HttpClientManage getInstance()
    {
        if (instance == null)
        {
            synchronized (HttpClientManage.class)
            {
                if (instance == null)
                {
                    instance = new HttpClientManage();
                }
            }
        }
        return instance;
    }

    /**
     * 简单调用: get(url)
     * 加参数调用: get(url, param)
     * 加参数同时加头文件调用: get(url, param, header)
     * 仅加头文件调用: get(url, null, header)
     * @param url 请求地址
     * @param params 如果有多个map传入, 则默认第一个为参数param, 第二个为header param
     * @return
     * @exception/throws [异常类型] [异常说明](可选)
     * @date 2017年6月10日 下午5:01:50
     * @since 1.0.0
     */
    public String get(String url, Map<String, String>... params)
    {
        return getCommon(url, mOkHttpClient, params);
    }

    public String getQuick(String url, Map<String, String>... params)
    {
        return getCommon(url, mOkHttpClientQuick, params);
    }

    public String getProxy(String url, Map<String, String>... params)
    {
        if (null != mOkHttpClientProxy)
        {
            return getCommon(url, mOkHttpClientProxy, params);
        }
        return get(url, params);
    }

    public String post(String url, Map<String, String>... params)
    {
        return postCommon(url, mOkHttpClient, params);
    }

    public String postQuick(String url, Map<String, String>... params)
    {
        return postCommon(url, mOkHttpClientQuick, params);
    }

    public String postProxy(String url, Map<String, String>... params)
    {
        if (null != mOkHttpClientProxy)
        {
            return postCommon(url, mOkHttpClientProxy, params);
        }
        return post(url, params);
    }

    /**
     * 异步调用
     * 简单调用: get(url, callback)
     * 加参数调用: get(url, callbackparam)
     * 加参数同时加头文件调用: get(url, callback, param, header)
     * 仅加头文件调用: get(url, callback, null, header)
     * @param url 请求地址
     * @param callback 回调函数
     * @param params 如果有多个map传入, 则默认第一个为参数param, 第二个为header param
     * @exception/throws [异常类型] [异常说明](可选)
     * @date 2017年6月10日 下午5:02:59
     * @since 1.0.0
     */
    public void getAsyn(String url, Callback callback, Map<String, String>... params)
    {
        getCommonAsyn(url, callback, mOkHttpClient, params);
    }

    public void getQuickAsyn(String url, Callback callback, Map<String, String>... params)
    {
        getCommonAsyn(url, callback, mOkHttpClientQuick, params);
    }

    public void getProxyAsyn(String url, Callback callback, Map<String, String>... params)
    {
        if (null != mOkHttpClientProxy)
        {
            getCommonAsyn(url, callback, mOkHttpClientProxy, params);
            return;
        }
        getAsyn(url, callback, params);
    }

    public void postAsyn(String url, Callback callback, Map<String, String>... params)
    {
        postCommonAsyn(url, callback, mOkHttpClient, params);
    }

    public void postQuickAsyn(String url, Callback callback, Map<String, String>... params)
    {
        postCommonAsyn(url, callback, mOkHttpClientQuick, params);
    }

    public void postProxyAsyn(String url, Callback callback, Map<String, String>... params)
    {
        if (null != mOkHttpClientProxy)
        {
            postCommonAsyn(url, callback, mOkHttpClientProxy, params);
            return;
        }
        postAsyn(url, callback, params);
    }

    private HttpClientManage()
    {
        init();
    }

    private void init()
    {
        // 初始化静态 如果不需要配置文件来进行参数配置，可以将此方法去掉
        initProperty();
        // 初始化CA
        initCa();
        initNormalClient();
        initQuickClient();
        initProxyClient();
    }

    /**
     * 初始化参数
     * @exception/throws [异常类型] [异常说明](可选)
     * @date 2017年6月10日 下午5:05:34
     * @since 1.0.0
     */
    private void initProperty()
    {
        //IS_INORE_CA = true;
    }

    private void initCa()
    {
        try
        {
            if (IS_INORE_CA)
            {
                trustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                    {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                    {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers()
                    {
                        X509Certificate[] x509Certificates = new X509Certificate[0];
                        return x509Certificates;
                    }
                };
            }
            else
            {
                if (StringUtils.isEmpty(CA_PATH))
                {
                    return;
                }
                // 读取key
                KeyStore keyStore = readKeyStore();
                if (null == keyStore)
                {
                    return;
                }
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                        .getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager))
                {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            sslSocketFactory = sslContext.getSocketFactory();
        }
        catch (Exception e)
        {
            log.error("initCa error.", e);
        }
    }

    private KeyStore readKeyStore()
    {
        File file = new File(CA_PATH);
        if (!file.exists())
        {
            return null;
        }

        KeyStore keyStore = null;
        CertificateFactory certificateFactory = null;
        try
        {
            certificateFactory = CertificateFactory.getInstance("X.509");
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
        }
        catch (Exception e1)
        {
            log.error(null, e1);
        }

        if (null == keyStore || null == certificateFactory)
        {
            return null;
        }

        InputStream inputStream = null;
        if (!file.isDirectory())
        {
            try
            {
                inputStream = new FileInputStream(file);
                addOneCer(certificateFactory, keyStore, SSL_KEY_ALIAS, inputStream);
            }
            catch (Exception e)
            {
                log.error(null, e);
            }
            finally
            {
                try
                {
                    if (inputStream != null)
                        inputStream.close();
                }
                catch (IOException e)
                {
                    log.error(null, e);
                }
            }
        }
        else
        {
            int index = 0;
            String certificateAlias;
            for (File cerFile : file.listFiles())
            {
                certificateAlias = SSL_KEY_ALIAS + Integer.toString(index++);
                try
                {
                    inputStream = new FileInputStream(cerFile);
                    addOneCer(certificateFactory, keyStore, certificateAlias, inputStream);
                }
                catch (Exception e)
                {
                    log.error(null, e);
                }
                finally
                {
                    try
                    {
                        if (inputStream != null)
                            inputStream.close();
                    }
                    catch (IOException e)
                    {
                        log.error(null, e);
                    }
                }
            }
        }

        return keyStore;
    }

    private void addOneCer(CertificateFactory certificateFactory, KeyStore keyStore, String CerAlias,
                           InputStream certificate) throws KeyStoreException, CertificateException
    {
        keyStore.setCertificateEntry(CerAlias, certificateFactory.generateCertificate(certificate));
        try
        {
            if (certificate != null)
                certificate.close();
        }
        catch (IOException e)
        {
            log.error(null, e);
        }
    }

    // 初始化普通请求
    private void initNormalClient()
    {
        // 进行数据初始化
        okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)// 设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)// 设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);// 设置连接超时时间
        // 有证书需要引入
        if (null != sslSocketFactory && null != trustManager)
        {
            builder.sslSocketFactory(sslSocketFactory, trustManager);
        }
        if (IS_INORE_CA)
        {
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        mOkHttpClient = builder.build();
    }

    // 初始化快速请求
    private void initQuickClient()
    {
        okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT_QUICK, TimeUnit.SECONDS)// 设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT_QUICK, TimeUnit.SECONDS)// 设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT_QUICK, TimeUnit.SECONDS);// 设置连接超时时间

        // 有证书需要引入
        if (null != sslSocketFactory && null != trustManager)
        {
            builder.sslSocketFactory(sslSocketFactory, trustManager);
        }
        if (IS_INORE_CA)
        {
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        mOkHttpClientQuick = builder.build();
    }

    // 初始化代理请求
    private void initProxyClient()
    {
        if (StringUtils.isEmpty(PROXY_IP))
        {
            return;
        }
        okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)// 设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)// 设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);// 设置连接超时时间

        // 有证书需要引入
        if (null != sslSocketFactory && null != trustManager)
        {
            builder.sslSocketFactory(sslSocketFactory, trustManager);
        }
        builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_IP, PROXY_PORT)));
        mOkHttpClientProxy = builder.build();
    }

    private String getCommon(String url, OkHttpClient okHttpClient, Map<String, String>[] params)
    {
        okhttp3.Request.Builder requestBuilder = createGetBuilder(url, params);
        return doSync(url, okHttpClient, requestBuilder, params);
    }

    /**
     * 异步请求
     * @param url
     * @param callback
     * @param mOkHttpClient
     * @param params
     * @exception/throws [异常类型] [异常说明](可选)
     * @date 2017年6月10日 下午5:05:06
     * @since 1.0.0
     */
    private void getCommonAsyn(String url, Callback callback, OkHttpClient mOkHttpClient, Map<String, String>[] params)
    {
        okhttp3.Request.Builder requestBuilder = createGetBuilder(url, params);
        doAsyn(url, callback, mOkHttpClient, requestBuilder, params);
    }

    private okhttp3.Request.Builder createGetBuilder(String url, Map<String, String>[] params)
    {
        String realUrl = url;
        Map<String, String> headers = null;
        if (null != params)
        {
            if (params.length > 0)
            {
                realUrl = buildUrl(url, params[0]);
            }
            if (params.length > 1)
            {
                headers = params[1];
            }
        }

        okhttp3.Request.Builder requestBuilder = createBuilder(realUrl, headers);
        return requestBuilder;
    }

    /**
     * 同步请求
     * @param url
     * @param okHttpClient
     * @param requestBuilder
     * @param params
     * @return
     * @exception/throws [异常类型] [异常说明](可选)
     * @date 2017年6月10日 下午5:04:29
     * @since 1.0.0
     */
    private String doSync(String url, OkHttpClient okHttpClient, okhttp3.Request.Builder requestBuilder,
                          Map<String, String>[] params)
    {
        try
        {
            printRequestInfo(url, params);

            String response = okHttpClient.newCall(requestBuilder.build()).execute().body().string();

            printResponseInfo(response);
            return response;
        }
        catch (IOException e)
        {
            log.error("url:" + url + ",params:" + JSONArray.toJSONString(params), e);
        }
        return null;
    }

    private void printRequestInfo(String url, Map<String, String>[] params)
    {
        log.debug("url:" + url + "\n params:" + JSONArray.toJSONString(params).replace("\\", ""));
    }

    private void printResponseInfo(String response)
    {
        log.debug("response:" + response);
    }

    /**
     * 异步请求
     * @param url
     * @param callback
     * @param okHttpClient
     * @param requestBuilder
     * @param params
     * @exception/throws [异常类型] [异常说明](可选)
     * @since 1.0.0
     */
    private void doAsyn(String url, Callback callback, OkHttpClient okHttpClient,
                        okhttp3.Request.Builder requestBuilder, Map<String, String>[] params)
    {
        printRequestInfo(url, params);
        okHttpClient.newCall(requestBuilder.build()).enqueue(callback);
        // printResponseInfo(response);
    }

    private okhttp3.Request.Builder createBuilder(String url, Map<String, String> headers)
    {
        okhttp3.Request.Builder builder = new Request.Builder().url(url);
        if (null == headers)
        {
            return builder;
        }

        for (Map.Entry<String, String> en : headers.entrySet())
        {
            builder.addHeader(en.getKey(), en.getValue());
        }
        return builder;
    }

    private String buildUrl(String url, Map<String, String> param)
    {
        if (null == param)
        {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        try
        {
            for (Map.Entry<String, String> en : param.entrySet())
            {
                if (!StringUtils.isEmpty(en.getKey()) && !StringUtils.isEmpty(en.getValue()))
                {
                    sb.append(PARAM_SPLIT);
                    sb.append(URLEncoder.encode(en.getKey(), DEFAULT_ENCODE));
                    sb.append(PARAM_SPLIT_VAL);
                    sb.append(URLEncoder.encode(en.getValue(), DEFAULT_ENCODE));
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("url=" + url, e);
        }
        if (sb.length() > 0)
        {
            if (!url.contains(PARAM_SPLIT_FIRST))
            {
                url = url + PARAM_SPLIT_FIRST;
            }
            url = url + sb.substring(1);
        }
        return url;
    }

    private String postCommon(String url, OkHttpClient okHttpClient, Map<String, String>[] params)
    {
        okhttp3.Request.Builder requestBuilder = createPostBuilder(url, params);
        return doSync(url, okHttpClient, requestBuilder, params);
    }

    private void postCommonAsyn(String url, Callback callback, OkHttpClient okHttpClient, Map<String, String>[] params)
    {
        okhttp3.Request.Builder requestBuilder = createPostBuilder(url, params);
        doAsyn(url, callback, okHttpClient, requestBuilder, params);
    }

    private okhttp3.Request.Builder createPostBuilder(String url, Map<String, String>[] params)
    {
        Map<String, String> headers = null;
        Builder formBuilder = new FormBody.Builder();
        if (null != params)
        {
            if (params.length > 0 && null != params[0])
            {
                for (Map.Entry<String, String> en : params[0].entrySet())
                {
                    formBuilder.add(en.getKey(), en.getValue());
                }
            }

            if (params.length > 1)
            {
                headers = params[1];
            }
        }
        okhttp3.Request.Builder requestBuilder = createBuilder(url, headers).post(formBuilder.build());
        return requestBuilder;
    }

    /**
     * 文件上传
     * @param urlStore   上传服务地址
     * @param file       本地文件
     * @param callback   回调函数，若无则为同步，同步则直接返回result，异步则在callback中返回上传结果
     * @param params     第一个map为body中需要传的参数，第二个map为头文件中需要传的参数
     * @return
     * @exception/throws [异常类型] [异常说明](可选)
     * @date 2018年5月11日 上午8:47:24
     */
    public String upload(String urlStore, File file, Callback callback, Map<String, String>... params)
    {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream") , file);
        okhttp3.MultipartBody.Builder mBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("desc" , file.getName())
                .addFormDataPart("file" , file.getName(), fileBody);
        Map<String, String> paramMap = null;
        if (null != params && params.length > 0)
        {
            paramMap = params[1];
        }                                                ;
        if (null != paramMap)
        {
            for (Map.Entry<String, String> en : paramMap.entrySet())
            {
                mBodyBuilder.addFormDataPart(en.getKey(), en.getValue());
            }
        }
        Map<String, String> headMap = null;
        if (null != params && params.length > 1)
        {
            headMap = params[1];
        }

        okhttp3.Request.Builder requestBuilder = createBuilder(urlStore, headMap);
        requestBuilder.post(mBodyBuilder.build());

        if (null == callback)
        {
            //同步
            return doSync(urlStore, mOkHttpClient, requestBuilder, params);
        }
        else
        {
            //异步
            doAsyn(urlStore, callback, mOkHttpClient, requestBuilder, params);
            return null;
        }
    }



}