package com.github.shyykoserhiy.gfm.markdown.network;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.markdown.AbstractMarkdownParser;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
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

public class GitHubApiMarkdownParser extends AbstractMarkdownParser {
    private CloseableHttpClient httpClient;

    public GitHubApiMarkdownParser(GfmRequestDoneListener requestDoneListener) {
        super(requestDoneListener);
        httpClient = HttpClients.createDefault();
    }

    @Override
    public AbstractMarkdownParser.GfmWorker getWorker(String filename, String markdown) {
        return new GfmWorker(filename, markdown);
    }

    private class GfmWorker extends AbstractMarkdownParser.GfmWorker {
        public GfmWorker(String filename, String markdown) {
            super(filename, markdown);
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
                        fireSuccess(responseString);
                        break;
                    case 403:
                        fireFail(GfmBundle.message("gfm.error.github-rate-limit"), responseString);
                        break;
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
    }
}
