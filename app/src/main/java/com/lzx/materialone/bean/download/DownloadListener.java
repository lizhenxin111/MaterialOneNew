package com.lzx.materialone.bean.download;

/**
 * Created by lizhe on 2017/6/3.
 */

public interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}
