package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GfmPreviewFX extends AbstractGfmPreview {

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
        public void onRequestFail(String error) {
            previewIsUpToDate = false;
            loadContent(error);
        }
    }
}
