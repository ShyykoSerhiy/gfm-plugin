package com.github.shyykoserhiy.gfm.markdown;

import java.io.File;

public interface GfmRequestDoneListener {
    void onRequestDone(File result);

    void onRequestFail(String error);
}
