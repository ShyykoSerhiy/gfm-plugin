package com.github.shyykoserhiy.gfm.toolwindow.browser;

import com.github.shyykoserhiy.gfm.browser.BrowserFx;
import com.github.shyykoserhiy.gfm.browser.BrowserJx;
import com.github.shyykoserhiy.gfm.browser.BrowserLobo;
import com.github.shyykoserhiy.gfm.browser.IsBrowser;
import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.io.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class BrowserToolWindow extends JPanel implements Disposable {

    private final IsBrowser browser;
    private final ToolBar toolBar;
    private final JComponent jsConsole;
    private final JComponent container;
    private final JComponent browserContainer;

    public BrowserToolWindow() {
        browser = createBrowser();

        browserContainer = createBrowserContainer();
        jsConsole = createConsole();
        toolBar = createToolBar(browser);

        container = new JPanel(new BorderLayout());
        container.add(browserContainer, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    public void dispose() {
        browser.dispose();
    }

    private ToolBar createToolBar(IsBrowser browser) {
        ToolBar toolBar = new ToolBar(browser);
        toolBar.addPropertyChangeListener("TabClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange("TabClosed", false, true);
            }
        });
        toolBar.addPropertyChangeListener("JSConsoleDisplayed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                showConsole();
            }
        });
        toolBar.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
            }
        });
        return toolBar;
    }

    private void hideConsole() {
        showComponent(browserContainer);
    }

    private void showComponent(JComponent component) {
        container.removeAll();
        container.add(component, BorderLayout.CENTER);
        validate();
    }

    private void showConsole() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(browserContainer, JSplitPane.TOP);
        splitPane.add(jsConsole, JSplitPane.BOTTOM);
        splitPane.setResizeWeight(0.8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        showComponent(splitPane);
    }

    private JComponent createConsole() {
        JSConsole result = new JSConsole(browser);
        result.addPropertyChangeListener("JSConsoleClosed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                hideConsole();
                toolBar.didJSConsoleClose();
            }
        });
        return result;
    }

    private JComponent createBrowserContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.add(browser.getComponent(), BorderLayout.CENTER);
        return container;
    }

    private IsBrowser createBrowser() {
        IsBrowser browser = null;
        switch (GfmGlobalSettings.getInstance().getRenderingEngine()) {
            case JX_BROWSER:
                browser = new BrowserJx();
                break;
            case FX_WEBVIEW:
                String javaHome = System.getProperty("java.home");
                File jfxrt = new File(FileUtil.join(javaHome, "lib", "ext", "jfxrt.jar"));
                if (jfxrt.exists()) {
                    try {
                        PluginClassLoader pluginClassLoader = (PluginClassLoader) this.getClass().getClassLoader();
                        URL url = jfxrt.toURI().toURL();
                        if (!pluginClassLoader.getUrls().contains(url)) {
                            ((PluginClassLoader) this.getClass().getClassLoader()).addURL(url);
                        }
                        browser = new BrowserFx();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LOBOEVOLUTION:
                browser = new BrowserLobo();
                break;
        }
        if (browser == null) {
            browser = new BrowserJx();
        }
        return browser;
    }
}
