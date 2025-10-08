package com.browserstack.MediaUpload;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.nio.file.Paths;
import java.util.Base64;

public class TestMediaUpload {

        public static void main(String[] args) throws Exception {
            // ---- CONFIGURE THESE ----
            final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
            final String ACCESSKEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
            final String FILE_PATH = "src/main/resources/QR-image-1.png"; // update
            final String CUSTOM_ID = "SampleMedia";

            // truststore (contains proxy CA or server CA). Use full path.
            //final String TRUSTSTORE_PATH = System.getProperty("javax.net.ssl.trustStore", "C:\\Users\\digitalqasvc\\cacerts");
            //final String TRUSTSTORE_PASS = System.getProperty("javax.net.ssl.trustStorePassword", "changeit");

            // Proxy (can be set via JVM -Dhttp.proxyHost and -Dhttp.proxyPort)
            final String proxyHost = System.getProperty("http.proxyHost", "your-proxy-host");
            final String proxyPortStr = System.getProperty("http.proxyPort", "your-proxy-port");
            final int proxyPort = Integer.parseInt(proxyPortStr);

            // Endpoint
            final String url = "https://api-cloud.browserstack.com/app-automate/upload-media";

            // ---- Build SSL context that trusts the provided truststore ----

            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();

            // Allow TLSv1.3 and TLSv1.2 (will negotiate with server)
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1.3", "TLSv1.2"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier()
            );

            // ---- Configure proxy (use "http" scheme for CONNECT tunneling) ----
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");

            RequestConfig requestConfig = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();

            // ---- Build HttpClient with custom SSL + proxy ----
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslSocketFactory)
                    .setDefaultRequestConfig(requestConfig)
                    .build()) {

                // Prepare POST
                HttpPost post = new HttpPost(url);

                // Basic Auth header (username:accesskey)
                String creds = USERNAME + ":" + ACCESSKEY;
                System.out.println("Creds => "+ creds);
                String basicAuth = "Basic " + Base64.getEncoder().encodeToString(creds.getBytes("UTF-8"));
                System.out.println("Auth header => "+ basicAuth);
                post.setHeader("Authorization", basicAuth);

                // Multipart body
                File file = Paths.get(FILE_PATH).toFile();
                if (!file.exists() || !file.isFile()) {
                    throw new IllegalArgumentException("File not found: " + FILE_PATH);
                }

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
                builder.addTextBody("custom_id", CUSTOM_ID, ContentType.TEXT_PLAIN);

                post.setEntity(builder.build());

                // Execute
                System.out.println("Executing upload to: " + url + " via proxy " + proxy);
                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    int status = response.getStatusLine().getStatusCode();
                    String respBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                    System.out.println("Status: " + status);
                    System.out.println("Response: " + respBody);
                }
            }
        }


}
