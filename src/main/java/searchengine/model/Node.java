package searchengine.model;

import searchengine.config.ConnectionConfig;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Node {
    private String url;
    private final ConnectionConfig connectionConfig;
    private Collection<Node> children;
    public Node(String url, ConnectionConfig connectionConfig) {
        this.url = url;
        this.connectionConfig = connectionConfig;
        children = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }
    public Collection<Node> getChildren() {
        Document document = null;
        try {
            Connection connection = Jsoup.connect(url);
            Connection.Response response =  connection.execute();

            int statusCode = response.statusCode();
            if (!response.contentType().toLowerCase().contains("text/")) {
                return children;
            }
            // если страница не открывается, значит и ссылок нет
            if (statusCode != 200) {
                return  children;
            }

            document = connection.userAgent(connectionConfig.getUserAgent()).referrer(connectionConfig.getReferrer()).get();
            Elements links = document.select("a");
            for (Element link : links) {
                String newUrl = link.attr("abs:href");
                // проверить ссылку:
                if (!checkUrl(url, newUrl)) {
                    continue;
                }
                children.add(new Node(newUrl, connectionConfig));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return children;
    }

    private boolean checkUrl(String url, String newUrl) {
        boolean result = true;

        // Исключить:
        // то же самое:
        if (url.equals(newUrl)) {
            return false;
        }

        // пустые ссылки, такое тоже бывает:
        if (newUrl.length() == 0) {
            return false;
        }

        // внутренние ссылки:
        if (newUrl.contains("#")) {
            return false;
        }

        // ссылки на другие сайты:
        int urlLength = url.length() > newUrl.length() ? newUrl.length() : url.length();
        try {
            if (!newUrl.substring(0, urlLength).equals(url)) {
                return false;
            }
        }
        catch (StringIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        return result;
    }

}
