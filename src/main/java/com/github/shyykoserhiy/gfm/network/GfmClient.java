package com.github.shyykoserhiy.gfm.network;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.github.shyykoserhiy.gfm.template.TemplateManager;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GfmClient {
    private ExecutorService connectionsThreadPool = Executors.newSingleThreadExecutor();

    private GfmGlobalSettings globalSettings;
    private CloseableHttpClient httpClient;
    private GfmRequestDoneListener requestDoneListener;
    private TemplateManager templateManager;

    public GfmClient(GfmRequestDoneListener requestDoneListener) {
        this.requestDoneListener = requestDoneListener;
        httpClient = HttpClients.createDefault();
        templateManager = TemplateManager.getInstance();
        globalSettings = GfmGlobalSettings.getInstance();
    }

    public void queueMarkdownHtmlRequest(String filename, String markdown) {
        connectionsThreadPool.submit(new GfmWorker(filename, markdown));
    }

    private class GfmWorker implements Runnable {
        private String filename;
        private String markdown;

        public GfmWorker(String filename, String markdown) {
            this.filename = filename;
            this.markdown = markdown;
        }

        @Override
        public void run() {
            String githubToken = globalSettings.getGithubAccessToken();
            HttpPost httpPost = new HttpPost("https://api.github.com/markdown/raw"
                    + (githubToken != null && !githubToken.isEmpty() ? "?access_token=" + githubToken : "")
            );
            StringEntity stringEntity = new StringEntity(markdown, ContentType.create("text/plain", "UTF-8"));
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(globalSettings.getSocketTimeout())
                    .setConnectTimeout(globalSettings.getConnectionTimeout())
                    .build();
            httpPost.setEntity(stringEntity);

            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        String responseText = templateManager.getMarkdownHtml(filename, responseString);
                        File file = File.createTempFile("markdown", ".html"); //todo
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(responseText);
                        fileWriter.close();
                        requestDoneListener.onRequestDone(file);
                        break;
                    case 403:
                        fireFail(GfmBundle.message("gfm.error.github-rate-limit"), responseString);
                    default:
                        fireFail(GfmBundle.message("gfm.error.github-unknown"), responseString);
                }
            } catch (ConnectTimeoutException e) {
                fireFail(GfmBundle.message("gfm.error.request-timeout"), ExceptionUtils.getStackTrace(e));
            } catch (org.apache.http.conn.ConnectTimeoutException e) {
                fireFail(GfmBundle.message("gfm.error.request-timeout"), ExceptionUtils.getStackTrace(e));
            } catch (UnknownHostException e) {
                fireFail(GfmBundle.message("gfm.error.github-unavailable"), ExceptionUtils.getStackTrace(e));
            } catch (IOException e) {
                fireFail(GfmBundle.message("gfm.error.io-exception"), ExceptionUtils.getStackTrace(e)); // todo
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void fireFail(String message, String stackTrace) {
            requestDoneListener.onRequestFail(templateManager.getErrorHtml(message, stackTrace));
        }
    }
}
