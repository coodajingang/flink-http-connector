package flink.http.contor;

import flink.http.contor.internal.ssl.IgnoreTrustManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
public class HttpSinkFunction<IN> extends RichSinkFunction<IN> {
    private final HttpConnectorConfig config;
    private final SerializationSchema<IN> serializer;

    public HttpSinkFunction(HttpConnectorConfig config, SerializationSchema<IN> serializer) {
        log.info("new HttpSinkFunction");
        this.config = config;
        this.serializer = serializer;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        log.info("open HttpSinkFunction");
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(IN value, Context context) throws Exception {
        boolean isGet = "GET".equals(this.config.getMethod()) ? true : false;
        URL url;
        if (isGet) {
            String ps = connectGetParameters(this.serializer.serialize(value));
            String urlString = this.config.getUrl() + "?" + ps;
            if (log.isDebugEnabled()) {
                log.debug("Get URL: " + urlString);
            }
            url = new URL(urlString);
        } else {
            url = new URL(this.config.getUrl());
        }

        HttpURLConnection conn = this.config.getUseHttps() == Boolean.TRUE ? (HttpsURLConnection) url.openConnection() : (HttpURLConnection) url.openConnection();

        if (this.config.getUseHttps()) {
            ignoreSSLVerify();
        }

        conn.setDoInput(true);
        conn.setRequestMethod(this.config.getMethod());
        conn.setConnectTimeout(this.config.getConnectTimeout());
        conn.setReadTimeout(this.config.getReadTimeout());

        if (this.config.getHeaders() != null) {
            this.config.getHeaders().forEach(conn::setRequestProperty);
        }

        if (!isGet) {
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            writer.write(new String(this.serializer.serialize(value)));
            writer.close();
        }

        int status = conn.getResponseCode();
        if (status != 200 && this.config.getLogFail()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuffer error = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                error.append(inputLine);
            }
            in.close();
            log.error("HTTP Response code: " + status
                    + ", " + conn.getResponseMessage() + ", " + error
                    + ", Submitted payload: " + value.toString()
                    + ", url:" + this.config.getUrl());
        }

        if (status == 200 && this.config.getLogSuccess()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer data = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                data.append(inputLine);
            }
            in.close();
            log.info("HTTP Response code: " + status
                    + ", RESPONSE: " + data.toString()
                    + ", url:" + this.config.getUrl());
        }

        conn.disconnect();
    }

    private String connectGetParameters(byte[] serialize) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(serialize, Map.class);
        StringBuffer sb = new StringBuffer();
        map.forEach((key, value) -> sb.append(key + "=" + value.toString() + "&"));
        return sb.toString();
    }

    private void ignoreSSLVerify() throws NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession sslSession) {
                return true;
            }
        });
        TrustManager[]  managers = new TrustManager[1];
        TrustManager myTrustManager = new IgnoreTrustManager();
        managers[0] = myTrustManager;
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, managers, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }
}
