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

package com.intrafind.sitesearch;

import com.intrafind.sitesearch.service.SiteCrawler;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

import java.net.URI;
import java.util.UUID;

public class CrawlerControllerFactory<T extends WebCrawler> implements CrawlController.WebCrawlerFactory {

    private UUID siteId;
    private UUID siteSecret;
    private URI url;

    public CrawlerControllerFactory(UUID siteId, UUID siteSecret, URI url) {
        this.siteId = siteId;
        this.siteSecret = siteSecret;
        this.url = url;
    }

    @Override
    public SiteCrawler newInstance() {
        return new SiteCrawler(siteId, siteSecret, url);
    }
}