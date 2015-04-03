package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GfmPreviewFX extends ModernGfmPreview {

    private final JPanel jPanel;
    private WebView webView;
    private final JFXPanelRetina jfxPanelRetina;

    public GfmPreviewFX(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);
        jPanel = new JPanel(new BorderLayout(), true);
        jfxPanelRetina = new JFXPanelRetina(); // initializing javafx
        jPanel.add(jfxPanelRetina, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView = new WebView();
                webView.getEngine().setUserStyleSheetLocation(getClass().getResource("/com/github/shyykoserhiy/gfm/stylesheet/javafxstylesheet.css").toExternalForm());
                AnchorPane anchorPane = new AnchorPane();
                AnchorPane.setTopAnchor(webView, 0.0);
                AnchorPane.setBottomAnchor(webView, 0.0);
                AnchorPane.setLeftAnchor(webView, 0.0);
                AnchorPane.setRightAnchor(webView, 0.0);
                anchorPane.getChildren().add(webView);
                jfxPanelRetina.setScene(new Scene(anchorPane));
            }
        });
    }

    @Override
    public boolean isImmediateUpdate() {
        return true;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return jPanel;
    }

    @NotNull
    @Override
    public String getName() {
        return GfmBundle.message("gfm.editor.preview.tab-name-fx");
    }

    @Override
    public void dispose() {
        super.dispose();
        jfxPanelRetina.removeNotify(); //fixme @see com.github.shyykoserhiy.gfm.editor.GfmPreviewFX.JFXPanelRetina.removeNotify()
    }

    @Override
    protected GfmRequestDoneListener getRequestDoneListener() {
        return new RequestDoneListener();
    }

    private void loadFile(final File file) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().load("file:" + file.getAbsolutePath());
                onceUpdated = true;
            }
        });
    }

    private void loadContent(final String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().loadContent(content);
            }
        });
    }

    /**
     * Fix for rendering bug on retina
     *
     * @see <http://cr.openjdk.java.net/~ant/RT-38915/webrev.0/>
     */
    private class JFXPanelRetina extends JFXPanel {
        @Override
        public void removeNotify() {
            /*try { //fixme? significantly increases performance(but probably can lead to errors:))
                Field scaleFactor = JFXPanel.class.getDeclaredField("scaleFactor");
                scaleFactor.setAccessible(true);
                scaleFactor.setInt(this, 1);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            super.removeNotify();*/
        }
    }

    private class RequestDoneListener implements GfmRequestDoneListener {
        @Override
        public void onRequestDone(final File result) {
            loadFile(result);
        }

        @Override
        public void onRequestDone(final String title, final String markdown) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    final WebEngine webEngine = webView.getEngine();
                    final String script = "document.getElementById('title').innerHTML = window.java.getTitle();" +
                            "document.querySelector('.markdown-body.entry-content').innerHTML = window.java.getMarkdown();";
                    JSObject jsobj = (JSObject) webEngine.executeScript("window");
                    jsobj.setMember("java", new JSMarkdownBridge(markdown, title));
                    if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                        webEngine.executeScript(script);
                        webView.requestLayout();
                    } else {
                        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                            @Override
                            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                                if (newValue == Worker.State.SUCCEEDED) {
                                    webEngine.executeScript(script);
                                    webView.requestLayout();
                                }
                            }
                        });
                    }

                }
            });
        }

        @Override
        public void onRequestFail(String error) {
            previewIsUpToDate = false;
            loadContent(error);
        }
    }

    public static class JSMarkdownBridge {
        private String markdown;
        private String title;

        public JSMarkdownBridge(String markdown, String title) {
            this.markdown = markdown;
            this.title = title;
        }

        public String getMarkdown() {
            return markdown;
        }

        public String getTitle() {
            return title;
        }
    }
}
