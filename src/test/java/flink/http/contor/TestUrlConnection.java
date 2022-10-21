package flink.http.contor;

import org.junit.Test;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TestUrlConnection {

    @Test
    public void test4Connection() throws Exception {
        URL url = new URL("http://localhost:8000/post");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
        writer.write("this is a test get requests");
        writer.close();

        int status = conn.getResponseCode();
        System.out.println(status);

        InputStreamReader input = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(input);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();

        System.out.println("Response: status = " + status);
        System.out.println("Response: = " + conn.getResponseMessage());
        System.out.println("Response: = " + buffer.toString());

    }

    @Test
    public void test4HttpsUrlConnection() throws Exception {
        URL url = new URL("https://www.javaroad.cn/questions/108623");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        System.out.println(url.getProtocol());

        if ("https".equals(url.getProtocol())) {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String urlHostName, SSLSession sslSession) {
                    System.out.println("waring: " + urlHostName + ":" + sslSession.getPeerHost() + " skip verify!");
                    return true;
                }
            });
            trustAll();
            System.out.println("Ignot https verify!");
        }

        conn.setDoInput(true);
        int status = conn.getResponseCode();
        System.out.println(status);

        InputStreamReader input = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(input);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();

        System.out.println("Response: status = " + status);
        System.out.println("Response: = " + conn.getResponseMessage());
        System.out.println("Response: = " + buffer.toString());
    }

    private static void trustAll() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[]  managers = new TrustManager[1];
        TrustManager myTrustManager = new MyTrustManager();
        managers[0] = myTrustManager;
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, managers, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }
    static class MyTrustManager implements TrustManager, X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }


    }
}
