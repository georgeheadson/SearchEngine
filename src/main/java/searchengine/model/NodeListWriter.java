package searchengine.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.repositories.Repositories;
import searchengine.config.ConnectionConfig;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class NodeListWriter extends RecursiveTask<HashMap> {

    private Node node;
    private Site site;
    private Repositories repositories;
    private ConnectionConfig connectionConfig;

    public NodeListWriter(Node node, Site site, Repositories repositories, ConnectionConfig connectionConfig) {
        this.node = node;
        this.site = site;
        this.repositories = repositories;
        this.connectionConfig = connectionConfig;
    }

    @Override
    protected HashMap<Integer, Page> compute() {

        HashMap<Integer, Page> result = new HashMap<>();
//        result.put(node.getSite().getId(), node.getUrl());

        // проверить повторы в БД:
        if (repositories.getPageRepository().pageIdByUrlAndSite(node.getUrl().substring(site.getUrl().length() - 1), site.getId()) != null) {
            return result;
        }

        Connection connection = Jsoup.connect(node.getUrl());
        Document document;
        try {
            document = connection.userAgent(connectionConfig.getUserAgent()).referrer(connectionConfig.getReferrer()).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int statusCode = connection.response().statusCode();

        // записать в бд страницу:
        Page page = new Page();
        page.setCode(statusCode);
        page.setSite(site);
        page.setPath(node.getUrl().substring(site.getUrl().length() - 1));
        if (statusCode == 200) {
            page.setContent(document.html());
        }
        else {
            page.setContent("");
        }
        repositories.getPageRepository().save(page);

        // посчитать и записать леммы:
        if (page.getContent().length() > 0) {
            Morphology morphology = null;
            try {
                morphology = Morphology.getInstance();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            HashMap<String, Integer> lemmasMap = null;
            try {
                lemmasMap = morphology.getLemmas(page.getContent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<Lemma> lemmas = new ArrayList<>();
            List<Index> indexes = new ArrayList<>();
            synchronized (repositories.getLemmaRepository()) {
                for (String key : lemmasMap.keySet()) {
                    Lemma lemma = repositories.getLemmaRepository().lemmaByLemmaAndSite(key, site.getId());
                    if (lemma == null) {
                        lemma = new Lemma();
                        lemma.setLemma(key);
                        lemma.setSite(site);
                        lemma.setFrequency(lemmasMap.get(key));
                    } else {
                        lemma.setFrequency(lemma.getFrequency() + lemmasMap.get(key));
                    }
                    lemmas.add(lemma);

                    Index index = repositories.getIndexRepository().indexByLemmaAndPage(lemma.getId(), page.getId());
                    if (index == null) {
                        index = new Index();
                        index.setPage(page);
                        index.setLemma(lemma);
                        index.setRank(lemmasMap.get(key));
                    } else {
                        index.setRank(index.getRank() + lemmasMap.get(key));
                    }
                    indexes.add(index);
                }
                repositories.getLemmaRepository().saveAll(lemmas);
                repositories.getIndexRepository().saveAll(indexes);
            }
        }
        // поменять время в таблице сайтов:
        site.setStatusTime(LocalDateTime.now());
        repositories.getSiteRepository().save(site);

        List<NodeListWriter> taskList = new ArrayList<>();
        for (Node child : node.getChildren()) {
            NodeListWriter task = new NodeListWriter(child, site, repositories, connectionConfig);
            taskList.add(task);
            Double rnd = Math.random() * 1000;
            try {
                Thread.sleep(1000 + Math.round(rnd));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            task.fork();
        }

        for (NodeListWriter task : taskList) {
            result.putAll(task.join());
        }

        return result;
    }
}
