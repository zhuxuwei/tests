package spider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Util {

    private HttpClient client = new HttpClient();

    public Util() {
        this(null);
    }

    public Util(String proxyStr) {
        ProxyHost proxy = null;
        if (proxyStr != null && !"".equals(proxyStr)) {
            String[] tokens = proxyStr.split(":");
            proxy = new ProxyHost(tokens[0], Integer.valueOf(tokens[1]));
        }
        // client.getHttpConnectionManager().getParams().setSoTimeout(1000);
        if (proxy != null) {
            client.getHostConfiguration().setProxyHost(proxy);
        }

    }

    public String getHTML(String url) throws IOException {
        GetMethod method = new GetMethod(url);
        method.getParams().setContentCharset("GB2312");
        // method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 1000);

        String ret = null;
        while (true) {
            try {
                int status = client.executeMethod(method);
                if (status == HttpStatus.SC_OK) {
                    String responseString = "";
                    String acceptEncoding = "";
                    if (method.getResponseHeader("Content-Encoding") != null)
                        acceptEncoding = method.getResponseHeader(
                                "Content-Encoding").getValue();
                    StringBuffer sb = new StringBuffer();
                    // System.out.println("acceptEncoding:" + acceptEncoding);
                    if (acceptEncoding.toLowerCase().indexOf("gzip") > -1) {
                        InputStream is = method.getResponseBodyAsStream();
                        GZIPInputStream gzin = new GZIPInputStream(is);
                        InputStreamReader isr = new InputStreamReader(gzin);
                        java.io.BufferedReader br = new java.io.BufferedReader(
                                isr);
                        String tempbf;
                        while ((tempbf = br.readLine()) != null) {
                            sb.append(tempbf);
                            sb.append("\r\n");
                        }
                        responseString = sb.toString();
                        isr.close();
                        gzin.close();
                    } else {
                        responseString = method.getResponseBodyAsString();
                    }
                    // System.out.println(responseString);
                    ret = responseString;
                    // System.out.println(new
                    // String(responseString.getBytes("GBK"), "UTF-8"));
                    // doc = Jsoup.parse(responseString);
                    break;
                } else {
                    System.out.println("status:" + status);
                    throw new Exception("status:" + status);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception sleep 5s");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } finally {
                method.releaseConnection();
            }
        }
        return ret;
    }

    public Document getDoc(String url) throws IOException {
        return Jsoup.parse(getHTML(url));
    }

    ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
        byte[] buffer = new byte[0x20000];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(0x20000);
        int read;
        while (true) {
            read = inStream.read(buffer);
            if (read == -1)
                break;
            outStream.write(buffer, 0, read);
        }
        ByteBuffer byteData = ByteBuffer.wrap(outStream.toByteArray());
        return byteData;
    }

    public static Document getDocument(String url, Map<String, String> params)
            throws IOException {
        Document doc = null;
        while (true) {
            try {
                if (params == null) {
                    doc = Jsoup.connect(url).get();
                } else {
                    doc = Jsoup.connect(url).data(params).get();
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception sleep 5s");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return doc;
    }

    public static void main(String[] args) throws IOException {
        Util obj = new Util();
        // obj.getDoc("http://www.jb51.net/article/32559.htm");
        Document doc = obj
                .getDoc("http://esf.soufun.com/house-a011-b02726/c21-d21000-g22-i32-l3100/");
        // System.out.println(doc.html());
        // Document doc =
        // Jsoup.connect("http://esf.soufun.com/house-a011-b02726/c21-d21000-g22-i32-l3100/").get();
        // System.out.println(doc.html());
        // obj.getDoc("http://www.baidu.com");
    }
}
