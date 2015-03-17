package com.github.shyykoserhiy.gfm.markdown.network;

import java.io.File;

public interface GfmRequestDoneListener {
    void onRequestDone(File result);

    void onRequestFail(String error);
}
