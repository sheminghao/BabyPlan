package cn.lzh.baby.http2_rx.Api;


import cn.lzh.baby.http2_rx.HttpService;
import cn.lzh.baby.http2_rx.exception.HttpTimeException;
import cn.lzh.baby.utils.json.GsonKit;
import cn.lzh.baby.utils.tools.L;
import rx.Observable;
import rx.functions.Func1;

/**
 * 请求数据统一封装类
 */
public abstract class BaseApi<T> implements Func1<T, String> {
    /*是否能取消加载框*/
    private boolean cancel=false;
    /*是否显示加载框*/
    private boolean showProgress=true;
    /*是否需要缓存处理*/
    private boolean cache=true;
    /*基础url*/
    private  String baseUrl="http://120.76.234.53:1111/grow/api/";
    /*方法-如果需要缓存必须设置这个参数；不需要不用設置*/
    private String mothed;
    /*超时时间-默认6秒*/
    private int connectionTime = 6;
    /*有网情况下的本地缓存时间默认60秒*/
    private int cookieNetWorkTime=60;
    /*无网络的情况下本地缓存时间默认30天*/
    private int cookieNoNetWorkTime=24*60*60*30;

    /**
     * 设置参数
     *
     * @param methods
     * @return
     */
    public abstract Observable getObservable(HttpService methods);



    public int getCookieNoNetWorkTime() {
        return cookieNoNetWorkTime;
    }

    public void setCookieNoNetWorkTime(int cookieNoNetWorkTime) {
        this.cookieNoNetWorkTime = cookieNoNetWorkTime;
    }

    public int getCookieNetWorkTime() {
        return cookieNetWorkTime;
    }

    public void setCookieNetWorkTime(int cookieNetWorkTime) {
        this.cookieNetWorkTime = cookieNetWorkTime;
    }

    public String getMothed() {
        return mothed;
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public void setMothed(String mothed) {
        this.mothed = mothed;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUrl() {
        return baseUrl+mothed;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isCancel() {
         return cancel;
     }

     public void setCancel(boolean cancel) {
         this.cancel = cancel;
     }

    @Override
    public String call(T httpResult) {
        L.i("httpResult=---->" + httpResult.toString());
        BaseResultEntity baseResulte= (BaseResultEntity) GsonKit.jsonToBean(httpResult.toString(),BaseResultEntity.class);
        if (baseResulte.getCode() == 0) {
            throw new HttpTimeException(baseResulte.getMsg());
        }
        if (baseResulte.getCode()==1){
            return httpResult.toString();
        }
        return "";

    }
}
