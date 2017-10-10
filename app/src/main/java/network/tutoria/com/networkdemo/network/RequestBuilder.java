package network.tutoria.com.networkdemo.network;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import network.tutoria.com.networkdemo.network.api.CustomParser;
import network.tutoria.com.networkdemo.network.api.HeaderUtil;
import network.tutoria.com.networkdemo.network.api.NetworkRequestProcessor;
import network.tutoria.com.networkdemo.network.api.NetworkResultHandler;
import network.tutoria.com.networkdemo.network.retorfit.NetworkRequestRetrofitProcessor;

/**
 * 包含了所有的请求的数据信息
 * 包含get post upload download的方法
 * <p>
 * RequestBuilder.get(“url")
 * RequestBuilder.post(“url")
 * RequestBuilder.download(“url",file)
 * <p>
 * RequestBuilder.upload(“url",filePart，“application/octet-stream")
 * HashMap<String, File> filePart 上传文件对应的键值对
 * <p>
 * <p>
 * addParam添加请求参数
 * addHead添加请求头
 * <p>
 * RequestBuilder.get("http://gank.io/api/data/休息视频/1/1").execute(resultHandler,returnType)
 */

public class RequestBuilder {

    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_UPLOAD_FILE = 3;
    public static final int METHOD_DOWNLOAD_FILE = 4;

    @IntDef({METHOD_GET, METHOD_POST, METHOD_UPLOAD_FILE, METHOD_DOWNLOAD_FILE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface RequestType {

    }

    //get请求
    public static RequestBuilder get(@NonNull String url) {
        return new RequestBuilder(METHOD_GET).url(url);
    }

    //post请求
    public static RequestBuilder post(@NonNull String url) {
        return new RequestBuilder(METHOD_GET).url(url);
    }

    //文件上传
    public static RequestBuilder upload(@NonNull String url, @NonNull HashMap<String, File> filePart) {
        return new RequestBuilder(METHOD_UPLOAD_FILE).url(url).uploadFilePart(filePart);
    }

    public static RequestBuilder download(@NonNull String url, File downloadTargetFile) {
        return new RequestBuilder(METHOD_DOWNLOAD_FILE).url(url).downloadFile(downloadTargetFile);
    }

    private String url; //url 肯定不能为空

    private HashMap<String, File> filePart;//上传文件的路径  METHOD_UPLOAD时才有用

    private String uploadFileMediaType = "application/octet-stream"; //上传文件的类型 image/*

    private final int requestMethod;

    private HashMap<String, String> requestParams = new HashMap<>();
    private HashMap<String, String> headers = new HashMap<>();

    private File downloadTargetFile;


    //文件类型 text/plain
    public RequestBuilder uploadFileMediaType(String fileMediaType) {
        this.uploadFileMediaType = fileMediaType;
        return this;
    }

    public String getUploadFileMediaType() {
        return uploadFileMediaType;
    }

    private RequestBuilder downloadFile(@NonNull File downloadTargetFile) {
        this.downloadTargetFile = downloadTargetFile;
        return this;
    }

    private RequestBuilder() {
        requestMethod = METHOD_GET;
    }

    private RequestBuilder(@RequestType int methodType) {
        requestMethod = methodType;
    }

    private RequestBuilder url(@NonNull String url) {
        this.url = url;
        return this;
    }

    //Upload类型时才有用
    private RequestBuilder uploadFilePart(HashMap<String, File> filePart) {
        this.filePart = filePart;
        return this;
    }

    public HashMap<String, File> getFilePart() {
        return filePart;
    }

    public RequestBuilder addParam(@NonNull String key, String value) {
        requestParams.put(key, value);
        return this;
    }

    public RequestBuilder addParams(Map<String, String> params) {
        requestParams.putAll(params);
        return this;
    }

    public RequestBuilder addHeader(@NonNull String key, String value) {
        headers.put(key, value);
        return this;
    }

    public RequestBuilder addHeaders(Map<String, String> params) {
        headers.putAll(params);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getGetMethodUrlWithParam() {
        String urlResult = url;
        if (requestParams != null) {
            Set<String> keys = requestParams.keySet();
            if (!keys.isEmpty()) {
                StringBuilder paramBuilder = new StringBuilder("?");
                for (String key : keys) {
                    paramBuilder.append(key);
                    paramBuilder.append("=");
                    paramBuilder.append(requestParams.get(key));
                    paramBuilder.append("&");
                }
                paramBuilder.deleteCharAt(paramBuilder.length() - 1);
                urlResult += paramBuilder.toString();
            }
        }
        return urlResult;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRequestMethod() {
        return requestMethod;
    }

    public HashMap<String, String> getRequestParams() {
        return requestParams;
    }

    public File getDownloadTargetFile() {
        return downloadTargetFile;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    //自定义方法解析请求结果
    private CustomParser customParser;

    public CustomParser getCustomParser() {
        return customParser;
    }

    public RequestBuilder setCustomParser(CustomParser customParser) {
        this.customParser = customParser;
        return this;
    }

    public <T> void execute(Type type, @NonNull NetworkResultHandler<T> networkResultHandler) {
        //设置全局请求头
        HeaderUtil.addGlobalHeader(this);
        //执行请求
        NetworkRequestProcessor requestProcessor = NetworkRequestRetrofitProcessor.getInstance();
        switch (requestMethod) {
            case METHOD_GET:
                requestProcessor.startGetRequest(this, networkResultHandler, type);
                break;
            case METHOD_POST:
                requestProcessor.startPostRequest(this, networkResultHandler, type);
                break;
            case METHOD_DOWNLOAD_FILE:
                requestProcessor.startDownloadRequest(this, networkResultHandler);
                break;
            case METHOD_UPLOAD_FILE:
                requestProcessor.startUploadRequest(this, networkResultHandler, type);
                break;
        }
    }


}
