package com.github.shyykoserhiy.gfm.markdown.offline;

public class PlatformNotSupported extends Exception {
    public PlatformNotSupported() {
        super("Offline markdown generation is not supported for your platform. " +
                "Please fill issue on https://github.com/ShyykoSerhiy/gfm-plugin/issues");
    }
}
