package searchengine.model;

import searchengine.repositories.Repositories;
import searchengine.config.ConnectionConfig;
import searchengine.config.Site;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinTask;

public class SiteIndexer extends Thread {

    private final Site site;
    private final Repositories repositories;
    private final ConnectionConfig connectionConfig;

    public SiteIndexer(Site site, Repositories repositories, ConnectionConfig connectionConfig) {
        this.site = site;
        this.repositories = repositories;
        this.connectionConfig = connectionConfig;
    }

    @Override
    public void run() {
        searchengine.model.Site siteInDB = repositories.getSiteRepository().siteByUrl(site.getUrl());
        searchengine.model.Site newSiteInDB;
        // удалить сайт в БД, если есть, вместе со страницами (каскадом):
        if (siteInDB != null) {
            newSiteInDB = siteInDB.clone();
            repositories.getSiteRepository().delete(siteInDB);
        } else {
            newSiteInDB = new searchengine.model.Site();
            newSiteInDB.setName(site.getName());
            newSiteInDB.setUrl(site.getUrl());
        }
        newSiteInDB.setStatus(Status.INDEXING);
        newSiteInDB.setStatusTime(LocalDateTime.now());
        newSiteInDB.setLastError("");
        repositories.getSiteRepository().save(newSiteInDB);

        Node node = new Node(site.getUrl(), connectionConfig);

//        ConcurrentHashMap<Integer, String> mapLinks = new ConcurrentHashMap<Integer, String>();
//        mapLinks.put(newSiteInDB.getId(), newSiteInDB.getUrl());
//        mapLinks.putAll(new ForkJoinPool().invoke(new NodeListWriter(node)));
        String error = "";
        try {
            ForkJoinTask.invokeAll(new NodeListWriter(node, newSiteInDB, repositories, connectionConfig));
        } catch (Exception e) {
            error = e.getMessage();
        }
        newSiteInDB.setStatus(error.length() == 0 ? Status.INDEXED : Status.FAILED);
        newSiteInDB.setLastError(error);
        newSiteInDB.setStatusTime(LocalDateTime.now());
        repositories.getSiteRepository().save(newSiteInDB);
    }
}
