package cn.lzh.baby.http2_rx;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import cn.lzh.baby.APP;
import cn.lzh.baby.http2_rx.Api.BaseApi;
import cn.lzh.baby.http2_rx.cookie.CacheInterceptor;
import cn.lzh.baby.http2_rx.exception.RetryWhenNetworkException;
import cn.lzh.baby.http2_rx.listener.HttpOnNextListener;
import cn.lzh.baby.http2_rx.subscribers.ProgressSubscriber;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * http交互处理类
 * Created by WZG on 2016/7/16.
 */
public class HttpManager {
    /*弱引用對象*/
    private SoftReference<HttpOnNextListener>  onNextListener;
    private SoftReference<RxAppCompatActivity> appCompatActivity;

    public HttpManager(HttpOnNextListener onNextListener, RxAppCompatActivity appCompatActivity) {
        this.onNextListener=new SoftReference(onNextListener);
        this.appCompatActivity=new SoftReference(appCompatActivity);
    }

    /**
     * 处理http请求
     * @param basePar 封装的请求数据
     */
    public void doHttpDeal(BaseApi basePar) {
        //log拦截器
        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CacheInterceptor());
        builder.connectTimeout(basePar.getConnectionTime(), TimeUnit.SECONDS);
        builder.addNetworkInterceptor(new CacheInterceptor());
        /*缓存位置和大小*/
        builder.cache(new Cache(APP.app.getCacheDir(),10*1024*1024));


        /*创建retrofit对象*/
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(basePar.getBaseUrl())
                .build();
        HttpService  httpService = retrofit.create(HttpService.class);
        /*rx处理*/
        ProgressSubscriber subscriber=new ProgressSubscriber(basePar,onNextListener,appCompatActivity);
        Observable observable = basePar.getObservable(httpService)
                /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*生命周期管理*/
                .compose(appCompatActivity.get().bindToLifecycle())
                /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                .map(basePar);
        /*数据回调*/
        observable.subscribe(subscriber);
    }
}
