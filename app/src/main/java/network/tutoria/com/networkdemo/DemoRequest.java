package network.tutoria.com.networkdemo;

import java.lang.reflect.Type;
import java.util.HashMap;

import network.tutoria.com.networkdemo.bean.LoginBean;
import network.tutoria.com.networkdemo.network.api.IRequestBaseManager;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.api.CustomParser;
import network.tutoria.com.networkdemo.network.api.NetworkResultHandler;
import network.tutoria.com.networkdemo.network.api.RequestBase;

/**
 *继承RequestBase
 * 在每个定义的请求方法中，调用父类的requestGet，requestPost，requestDownload，requestUpload，
 * 这样才能实现 通过RequestBase自动管理请求，
 * 否则需要调用addRequestBuilder(RequestBuilder requestBuilder)
 *
 */

public class DemoRequest extends RequestBase {


    public DemoRequest(IRequestBaseManager requestBaseManager) {
        super(requestBaseManager);
    }

    /**
     * @param networkResultHandler
     */
    public void doRegister(String email, String password, NetworkResultHandler<Object> networkResultHandler) {
        String url = "http://gank.io/api/data/休息视频/1/1";
        cancelPreRequest(url);
        requestGet(url).addParam("regist", email).addParam("password", password).setCustomParser(new CustomParser() {
            @Override
            public Object parseResult(Type type, String result) {
                return null;
            }
        }).doRequest(LoginBean.class, networkResultHandler);
    }


    public void doLogin(HashMap<String, String> params, NetworkResultHandler<LoginBean> networkResultHandler) {
        RequestBuilder requestBuilder = requestPost("http://gank.io/api/data/休息视频/1/1").addParams(params)
                .setCustomParser(new CustomParser<LoginBean>() {
                    @Override
                    public LoginBean parseResult(Type type, String result) {
                        //自定义解析结果
                        return null;
                    }
                }).doRequest(LoginBean.class, networkResultHandler);
    }

}
