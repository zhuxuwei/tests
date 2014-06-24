package spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Spider implements Runnable {

    private File splitFile = null;
    private Util util;

    public Spider(File file, String proxy) {
        splitFile = file;
        this.util = new Util(proxy);
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(splitFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                String url = line.trim();
                String key = url.substring(url.lastIndexOf("/"));
                String html = util.getHTML(url);
                FileUtils.writeStringToFile(new File("/home/zhuxuwei/var/sh/"
                        + key), html);
                System.out.println("OK " + url + "\t" + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        List<String> proxys = FileUtils.readLines(new File(
                "/home/zhuxuwei/var/proxy"));
        Collections.shuffle(proxys);
        File splitPath = new File("/home/zhuxuwei/var/sh.urls");
        File[] files = splitPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith("x")) {
                    return true;
                }
                return false;
            }
        });
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName() + "===" + proxys.get(i));
            new Thread(new Spider(files[i], proxys.get(i))).start();
        }

    }

}
