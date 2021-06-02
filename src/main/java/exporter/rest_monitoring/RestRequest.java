package exporter.rest_monitoring;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.Map;

public class RestRequest {
    private Map<String,String> params;
    private int answer;
    public RestRequest(Map<String,String> params) {
        this.params = params;
    }
    public int getResponseStatus() throws Exception {
        URL url = new URL(params.get("url"));

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(params.get("pathClient")), params.get("passphrase").toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, params.get("keypass").toCharArray());

        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(new FileInputStream(params.get("pathToJks")), params.get("passphrase").toCharArray());

        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod( "GET" );
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return s.equals(sslSession.getPeerHost());
            }
        };
        con.setHostnameVerifier(hostnameVerifier);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        con.setSSLSocketFactory(sslContext.getSocketFactory());

        int responseCode = con.getResponseCode();
        InputStream inputStream;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println(HttpURLConnection.HTTP_OK);
            answer = HttpURLConnection.HTTP_OK;
            inputStream = con.getInputStream();
        } else {
            inputStream = con.getErrorStream();
            answer = 0;
        }
        inputStream.close();
        return answer;
    }
}
