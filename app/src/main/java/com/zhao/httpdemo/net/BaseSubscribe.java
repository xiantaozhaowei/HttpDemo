package com.zhao.httpdemo.net;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Subscriber;

/**
 * Created by zhao on 2016/9/23.
 */

public abstract class BaseSubscribe<T> extends Subscriber<T> {
    private Boolean isShowLoadingDialog;
    private String msg;
    private Context context;
    private SweetAlertDialog dialog;

    public BaseSubscribe(Context context, String msg) {
        isShowLoadingDialog = true;
        this.context = context;
        this.msg = msg;
    }

    public BaseSubscribe() {
        isShowLoadingDialog = false;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isShowLoadingDialog) {
            dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText(msg);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            //点击取消的时候取消订阅
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onCompleted() {
        if (isShowLoadingDialog)
            dialog.dismiss();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (false) { //这里自行替换判断网络的代码
            _onError("网络不可用");
        } else if (e instanceof ApiException) {
            _onError(e.getMessage());
        } else {
            _onError("请求失败，请稍后再试...");
        }

        if (isShowLoadingDialog)
            dialog.dismiss();
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);
}