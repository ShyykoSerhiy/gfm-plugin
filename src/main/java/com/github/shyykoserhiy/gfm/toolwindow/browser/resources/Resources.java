package com.github.shyykoserhiy.gfm.toolwindow.browser.resources;

import javax.swing.*;

public class Resources {
    public static ImageIcon getIcon(String fileName) {
        return new ImageIcon(Resources.class.getResource(fileName));
    }
}
