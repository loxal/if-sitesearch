/*
 * Copyright 2018 IntraFind Software AG. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intrafind.sitesearch.service;

import com.intrafind.sitesearch.CrawlerControllerFactory;
import com.intrafind.sitesearch.dto.CrawlerJobResult;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Service
public class CrawlerService {
    private static final Logger LOG = LoggerFactory.getLogger(CrawlerService.class);
    private static final String CRAWLER_STORAGE = "data/crawler";
    private static final int CRAWLER_THREADS = 1;

    public CrawlerJobResult crawl(String url, UUID siteId, UUID siteSecret) {
        final CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CRAWLER_STORAGE);
        config.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        config.setPolitenessDelay(200);
        config.setMaxOutgoingLinksToFollow(1_000);
        config.setMaxPagesToFetch(500);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            LOG.error("Controller init fail: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        controller.addSeed(url);

        if (clearIndex(siteId, siteSecret)) {
            final CrawlController.WebCrawlerFactory<?> factory = new CrawlerControllerFactory(siteId, siteSecret, URI.create(url));
            controller.start(factory, CRAWLER_THREADS);

//            controller.waitUntilFinish();
//            controller.shutdown();
            return new CrawlerJobResult(
                    controller.getCrawlersLocalData().isEmpty() ? Collections.emptySet() : new HashSet(controller.getCrawlersLocalData()),
                    controller.getCustomData() == null ? 0 : (int) controller.getCustomData()
            );
        }
        return null;
    }

    private boolean clearIndex(UUID siteId, UUID siteSecret) {
        try {
            final Request request = new Request.Builder()
                    .url("https://api.sitesearch.cloud/sites/" + siteId + "?siteSecret=" + siteSecret)
                    .delete()
                    .build();
            final Response response = SiteCrawler.HTTP_CLIENT.newCall(request).execute();
            if (response.code() == 204 || response.code() == 200) {
                return true;
            } else {
                LOG.error("Clear Index result: " + response.code());
                return false;
            }
        } catch (IOException e) {
            LOG.error("Clear Index call fail: " + e.getMessage());
            return false;
        }
    }
}