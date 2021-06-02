import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

public class TestRestEasy {
    public void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("passphrase","storepass");
        params.put("keypass","serverpass");
        params.put("url","https://localhost:443/");
        params.put("pathToJks","/Users/user/Documents/nginx_dockerfile/nginx_http_test/clienttrust.jks");
        params.put("pathClient","/Users/user/Documents/nginx_dockerfile/nginx_http_test/client.jks");

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
            inputStream = con.getInputStream();
        } else {
            inputStream = con.getErrorStream();
        }
        inputStream.close();
    }
}
