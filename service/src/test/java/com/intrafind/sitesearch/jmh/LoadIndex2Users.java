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

package com.intrafind.sitesearch.jmh;

import com.intrafind.sitesearch.controller.SiteController;
import com.intrafind.sitesearch.dto.FetchedPage;
import com.intrafind.sitesearch.dto.Page;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.util.UUID;

import static org.junit.Assert.*;

@Threads(2)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class LoadIndex2Users {
    public static final UUID SEARCH_SITE_ID = UUID.fromString("a620f72b-b02c-4b57-8730-8edf72debb05");
    public static final UUID SEARCH_SITE_SECRET = UUID.fromString("ac2618a6-c48b-4702-892b-6450ad501b92");
    private final static Logger LOG = LoggerFactory.getLogger(LoadIndex2Users.class);

    private static String generateLoremIpsum() {
        final StringBuilder loremIpsumText = new StringBuilder();
        for (String word : LoadTest.LOREM_IPSUM) {
            final int wordIndex = LoadTest.PSEUDO_ENTROPY.nextInt(LoadTest.LOREM_IPSUM.length);
            loremIpsumText.append(LoadTest.LOREM_IPSUM[wordIndex]).append(" ");
        }
        return loremIpsumText.toString();
    }

    @Benchmark
    public void createNewSiteViaNewPage() {
        final String loremIpsumText = generateLoremIpsum();
        final Page pageToIndex = buildPage(loremIpsumText);

        final ResponseEntity<FetchedPage> actual = LoadTest.CALLER.exchange(
                LoadTest.LOAD_TARGET + SiteController.ENDPOINT + "/" + SEARCH_SITE_ID + "/pages?siteSecret=" + SEARCH_SITE_SECRET,
                HttpMethod.PUT,
                new HttpEntity<>(pageToIndex),
                FetchedPage.class
        );

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertNotNull(actual.getHeaders().getLocation());
        assertNotNull(actual.getBody().getSiteId());
        assertNotNull(actual.getBody().getId());
        assertNotNull(actual.getBody().getTitle());
        assertNotNull(actual.getBody().getBody());
        assertNotNull(actual.getBody().getUrl());
    }

    @Benchmark
    public void updatePageViaUrl() throws Exception {
        final String loremIpsumText = generateLoremIpsum();
        final Page pageToIndex = buildPage(loremIpsumText);
        pageToIndex.setUrl("https://example.com/0fe5463c-a134-495d-bee1-8e2e0044e57e");

        final ResponseEntity<Page> actual = LoadTest.CALLER.exchange(
                LoadTest.LOAD_TARGET + SiteController.ENDPOINT
                        + "/c281b015-09af-4868-8185-3fd8db41d6cb/pages/url/" + URLEncoder.encode(pageToIndex.getUrl(), "UTF-8")
                        + "?siteSecret=92d957f6-956e-4ee9-8f48-de434a728ab3",
                HttpMethod.PUT,
                new HttpEntity<>(pageToIndex),
                Page.class
        );

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNull(actual.getHeaders().getLocation());
    }

    private Page buildPage(String loremIpsumText) {
        return new Page(
                loremIpsumText.substring(0, 42),
                loremIpsumText,
                "https://example.com/" + UUID.randomUUID()
        );
    }

    @Benchmark
    public void updatePageViaId() {
        final String loremIpsumText = generateLoremIpsum();
        final Page pageToIndex = buildPage(loremIpsumText);

        final ResponseEntity<Page> actual = LoadTest.CALLER.exchange(
                LoadTest.LOAD_TARGET + SiteController.ENDPOINT
                        + "/cdcfdeef-86a2-4672-890f-e952e465fe01/pages/7ca861451227886cd575cda73ae4f1255a3039ee2cef5868444144e5be4bd32a"
                        + "?siteSecret=4e8afa49-490f-4b0a-a7b7-958405b30c73",
                HttpMethod.PUT,
                new HttpEntity<>(pageToIndex),
                Page.class
        );

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNull(actual.getHeaders().getLocation());
    }
}
