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

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class SiteCrawler extends WebCrawler {
    private final static Logger LOG = LoggerFactory.getLogger(SiteCrawler.class);
    private static final Pattern BLACKLIST = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|xml))$");
    //    private static final Pattern WHITELIST= Pattern.compile(".*(\\.(html|htm|txt|pdf))$");
    private final AtomicInteger pages = new AtomicInteger(0);
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private UUID siteId;
    private UUID siteSecret;
    private URI url;

    private SiteCrawler() {
    }

    public SiteCrawler(UUID siteId, UUID siteSecret, URI url) {
        this.siteId = siteId;
        this.siteSecret = siteSecret;
        this.url = url;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL webUrl) {
        String href = webUrl.getURL().toLowerCase();
        return !BLACKLIST.matcher(href).matches()
                && href.startsWith(url.toString())
                && noQueryParameter(webUrl)
                ;
    }

    private boolean noQueryParameter(WebURL url) {
        return URI.create(url.getURL()).getQuery() == null || URI.create(url.getURL()).getQuery().isEmpty();
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            final String htmlStrippedBody = extractTextFromMixedHtml(htmlParseData.getHtml());
            final String title = htmlParseData.getTitle();
            final Set<WebURL> links = htmlParseData.getOutgoingUrls();

            com.intrafind.sitesearch.dto.Page sitePage = new com.intrafind.sitesearch.dto.Page(
                    title,
                    htmlStrippedBody,
                    url
            );

            try {
                // TODO move this to CrawlerService
                Request request = new Request.Builder()
                        .url("https://api.sitesearch.cloud/sites/" + siteId + "/pages?siteSecret=" + siteSecret)
                        .put(RequestBody.create(MediaType.parse("application/json"), MAPPER.writeValueAsBytes(sitePage)))
                        .build();
                final Response response = HTTP_CLIENT.newCall(request).execute();
                if (response.code() != 200) {
                    LOG.warn("siteId: " + siteId + " - URL: " + url + " - responseCode: " + response.code());
                    response.close();
                    this.getMyController().getCrawlersLocalData().add("FAILED: " + url);
                    throw new RuntimeException("Error while adding page to index");
                }
                response.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }

            LOG.debug("sitePage: " + sitePage);
            LOG.debug("outgoingURLs: " + links.size());
        }
        LOG.info("siteId: " + siteId + " - pageCount: " + pages.incrementAndGet());

        this.getMyController().setCustomData(pages.get());
        this.getMyController().getCrawlersLocalData().add(url);
    }

    private String extractTextFromMixedHtml(String body) {
        final Document docPage = Jsoup.parse(body);
        return docPage.body().text();
    }
}