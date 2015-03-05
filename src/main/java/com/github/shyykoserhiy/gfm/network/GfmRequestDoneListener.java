package com.github.shyykoserhiy.gfm.network;

import java.io.File;

public interface GfmRequestDoneListener {
    void onRequestDone(File result);

    void onRequestFail(String error);
}
