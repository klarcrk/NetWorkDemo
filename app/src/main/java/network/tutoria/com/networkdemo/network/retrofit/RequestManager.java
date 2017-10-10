package network.tutoria.com.networkdemo.network.retrofit;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * 管理请求
 * 发起请求时 給某个请求添加tag 保存
 * 请求结束后 需要删除这个tag
 * <p>
 * 实现 取消请求
 */
public class RequestManager {

    private static RequestManager requestManager;

    public static RequestManager get() {
        if (requestManager == null) {
            requestManager = new RequestManager();
        }
        return requestManager;
    }

    private HashMap<Object, Disposable> cachedRequest = new HashMap<>();

    public void addTag(@NonNull Object tag, @NonNull Disposable disposable) {
        if (cachedRequest.containsKey(tag)) {
            Disposable preRequest = cachedRequest.get(tag);
            if (!preRequest.isDisposed()) {
                //需要取消掉之前的吗？？？
                preRequest.dispose();
            }
        }
        cachedRequest.put(tag, disposable);
    }

    public void removeTag(@NonNull Object tag) {
        removeTag(tag, false);
    }

    public void removeTag(@NonNull Object tag, boolean cancelRequest) {
        if (cancelRequest) {
            if (cachedRequest.containsKey(tag)) {
                Disposable preRequest = cachedRequest.get(tag);
                if (!preRequest.isDisposed()) {
                    //需要取消掉之前的吗？？？
                    preRequest.dispose();
                }
            }
        }
        cachedRequest.remove(tag);
    }

    public void clear() {
        Set<Object> requestTags = cachedRequest.keySet();
        if (!requestTags.isEmpty()) {
            for (Object requestTag : requestTags) {
                Disposable preRequest = cachedRequest.get(requestTag);
                if (!preRequest.isDisposed()) {
                    //需要取消掉之前的吗？？？
                    preRequest.dispose();
                }
            }
        }
        cachedRequest.clear();
    }


}
