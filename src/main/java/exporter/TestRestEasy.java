package exporter;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class TestRestEasy {
    public void main(String[] args) throws Exception {
        URL url = new URL("https://localhost:443/");
        char[] passphrase = "storepass".toCharArray();
        char[]  keypass = "serverpass".toCharArray();

        String pathToJks = "/Users/user/Documents/nginx_dockerfile/nginx_http_test/clienttrust.jks";
        String pathClient = "/Users/user/Documents/nginx_dockerfile/nginx_http_test/client.jks";

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(pathClient), passphrase);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keypass);

        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(new FileInputStream(pathToJks), passphrase);

        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod( "GET" );
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);
//        TrustManager[] trustManagers = new TrustManager[] {
//                new X509TrustManager() {
//
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return null;
//                    }
//
//                    public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
//
//                    public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
//
//                }
//        };
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

        // Process the response
        BufferedReader reader;
        String line = null;
        reader = new BufferedReader( new InputStreamReader( inputStream ) );
       // while( ( line = reader.readLine() ) != null )
       // {
       //     System.out.println( line );
       // }

        inputStream.close();
    }
}
