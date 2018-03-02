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

package com.intrafind.sitesearch.controller;

import com.intrafind.sitesearch.dto.*;
import com.intrafind.sitesearch.service.AutocompleteService;
import com.intrafind.sitesearch.service.SearchService;
import com.intrafind.sitesearch.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(SiteController.ENDPOINT)
public class SiteController {
    public static final String ENDPOINT = "/sites";
    private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);
    private final SiteService siteService;
    private final SearchService searchService;
    private final AutocompleteService autocompleteService;

    @Autowired
    private SiteController(SiteService siteService, SearchService searchService, AutocompleteService autocompleteService) {
        this.siteService = siteService;
        this.searchService = searchService;
        this.autocompleteService = autocompleteService;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<SiteCreation> createNewSite(@RequestBody(required = false) SiteProfileUpdate siteProfileCreation) {
        final SiteCreation newlyCreatedSite;
        if (siteProfileCreation == null) {
            newlyCreatedSite = siteService.createSite();
        } else {
            newlyCreatedSite = siteService.createSite(siteProfileCreation.getUrls(), siteProfileCreation.getEmail());
        }
        return ResponseEntity
                .created(URI.create("https://api.sitesearch.cloud/sites/" + newlyCreatedSite.getSiteId()))
                .body(newlyCreatedSite);
    }

    @RequestMapping(path = "{siteId}/profile", method = RequestMethod.GET)
    ResponseEntity<SiteProfile> fetchSiteProfile(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "siteSecret") UUID siteSecret
    ) {
        final Optional<SiteProfile> siteProfileFetch = siteService.fetchSiteProfile(siteId, siteSecret);
        if (siteProfileFetch.isPresent()) {
            final SiteProfile siteProfile = siteProfileFetch.get();
            return ResponseEntity.ok(new SiteProfile(
                    siteProfile.getId(),
                    siteProfile.getSecret(),
                    siteProfile.getUrls(),
                            siteProfile.getEmail(),
                            siteProfile.getPages()
                    )
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "{siteId}/profile", method = RequestMethod.PUT)
    ResponseEntity<SiteProfile> updateSiteProfile(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "siteSecret") UUID siteSecret,
            @RequestBody SiteProfileUpdate siteProfileUpdate
    ) {
        final Optional<SiteProfile> siteProfileUpdated = siteService.updateSiteProfile(siteId, siteSecret, siteProfileUpdate);
        if (siteProfileUpdated.isPresent()) {
            final SiteProfile siteProfile = siteProfileUpdated.get();
            return ResponseEntity.ok(new SiteProfile(
                    siteProfile.getId(),
                    siteProfile.getSecret(),
                    siteProfile.getUrls(),
                            siteProfile.getEmail(),
                            siteProfile.getPages()
                    )
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "{siteId}/pages", method = RequestMethod.GET)
    ResponseEntity<FetchedPage> fetchViaUrl(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "url") String url
    ) {
        String pageId = Page.hashPageId(siteId, url);

        Optional<FetchedPage> fetched = siteService.fetchById(pageId);
        if (fetched.isPresent()) {
            return ResponseEntity.ok(fetched.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "{siteId}/pages", method = RequestMethod.PUT)
    ResponseEntity<FetchedPage> addPageToSiteIndex(
            @PathVariable(name = "siteId") UUID siteId,
            @RequestParam(name = "siteSecret") UUID siteSecret,
            @RequestBody Page page
    ) {
        String pageId = Page.hashPageId(siteId, page.getUrl());
        // TODO use SiteUpdate DTO with NO siteId & NO siteSecret provided

        // TODO make sure that an existing page is actually updated
        Optional<FetchedPage> indexed = siteService.indexExistingPage(pageId, siteId, siteSecret, page);
        if (indexed.isPresent()) {
            return ResponseEntity.ok(indexed.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "{siteId}/pages/{pageId}", method = RequestMethod.PUT)
    ResponseEntity<FetchedPage> updateExistingPageInSiteIndex(
            @PathVariable(name = "siteId") UUID siteId,
            @PathVariable("pageId") String pageId,
            @RequestParam(name = "siteSecret") UUID siteSecret,
            @RequestBody Page page
    ) {
        if (pageId.length() != 64) { // just good enough but not sufficient to guarantee a valid, collision-safe GLOBAL pageId
            return ResponseEntity.badRequest().build();
        }
        // TODO use SiteUpdate DTO with NO siteId & NO siteSecret provided

        // TODO make sure that an existing page is actually updated
        Optional<FetchedPage> indexed = siteService.indexExistingPage(pageId, siteId, siteSecret, page);
        if (indexed.isPresent()) {
            return ResponseEntity.ok(indexed.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "{siteId}", method = RequestMethod.GET)
    ResponseEntity<List<String>> fetchAll(
            @PathVariable(value = "siteId") UUID siteId
    ) {
        Optional<List<String>> allDocumentsOfTenant = siteService.fetchAllDocuments(siteId);
        if (allDocumentsOfTenant.isPresent()) {
            return ResponseEntity.ok(allDocumentsOfTenant.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "{siteId}/xml", method = RequestMethod.PUT)
    ResponseEntity<SiteIndexSummary> reimportIndex(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "siteSecret") UUID siteSecret,
            @RequestParam(value = "xmlUrl") String xmlUrl,
            @RequestParam(value = "stripHtmlTags", required = false, defaultValue = "false") Boolean stripHtmlTags,
            @RequestParam(value = "clearIndex", required = false, defaultValue = "false") Boolean clearIndex
    ) {
        return indexAsRssFeed(siteId, siteSecret, URI.create(xmlUrl), stripHtmlTags, true, clearIndex);
    }

    @RequestMapping(path = "{siteId}/rss", method = RequestMethod.PUT)
    ResponseEntity<SiteIndexSummary> indexRssFeed(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "siteSecret") UUID siteSecret,
            @RequestParam(value = "feedUrl") String feedUrl,
            @RequestParam(value = "stripHtmlTags", required = false, defaultValue = "false") Boolean stripHtmlTags
    ) {
        return indexAsRssFeed(siteId, siteSecret, URI.create(feedUrl), stripHtmlTags, false, false);
    }

    @RequestMapping(path = "rss", method = RequestMethod.POST)
    ResponseEntity<SiteIndexSummary> indexNewRssFeed(
            @RequestParam(value = "feedUrl") String feedUrl,
            @RequestParam(value = "stripHtmlTags", required = false, defaultValue = "false") Boolean stripHtmlTags
    ) {
        return indexAsRssFeed(null, null, URI.create(feedUrl), stripHtmlTags, false, false);
    }

    private ResponseEntity<SiteIndexSummary> indexAsRssFeed(UUID siteId, UUID siteSecret, URI feedUrl, Boolean stripHtmlTags, Boolean isGeneric, Boolean clearIndex) {
        Optional<SiteIndexSummary> siteCreatedInfo = siteService.indexFeed(feedUrl, siteId, siteSecret, stripHtmlTags, isGeneric, clearIndex);
        if (siteCreatedInfo.isPresent()) {
            return ResponseEntity.ok(siteCreatedInfo.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(path = "{siteId}/pages/{pageId}", method = RequestMethod.DELETE)
    ResponseEntity deleteById(
            @PathVariable(name = "siteId") UUID siteId,
            @PathVariable(name = "pageId") String pageId,
            @RequestParam(name = "siteSecret") UUID siteSecret
    ) {
        LOG.info("delete-event" + pageId);
        if (siteService.delete(siteId, siteSecret, pageId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build(); // do not return UNAUTHORIZED/FORBIDDEN as those could be miss-used for brute force attacks
        }
    }

    @RequestMapping(path = "{siteId}/pages", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteViaUrl(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "url") String url,
            @RequestParam(name = "siteSecret") UUID siteSecret
    ) {
        String pageId = Page.hashPageId(siteId, url);

        return deleteById(siteId, pageId, siteSecret);
    }

    @RequestMapping(path = "{siteId}", method = RequestMethod.DELETE)
    ResponseEntity<?> clearSiteIndex(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(name = "siteSecret") UUID siteSecret
    ) {
        if (siteService.clearSite(siteId, siteSecret)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(path = "{siteId}/autocomplete", method = RequestMethod.GET)
    ResponseEntity<Autocomplete> autocompleteSuggestion(
            @CookieValue(value = "override-site", required = false) UUID cookieSite,
            @RequestParam(value = "query", defaultValue = "") String query,
            @PathVariable(value = "siteId") UUID siteId
    ) {
        if (query.isEmpty()) return ResponseEntity.badRequest().build();

        // override siteId with cookie value for debugging & speed up the getting started experience
        if (cookieSite != null) {
            siteId = cookieSite;
        }

        Optional<Autocomplete> result = autocompleteService.autocomplete(query, siteId);
        if (result.isPresent()) {
            final Autocomplete autocomplete = result.get();
            LOG.info("siteId: " + siteId + " - query-fragment: " + query + " - autocompletes: " + autocomplete.getResults().size());
            return ResponseEntity.ok(autocomplete);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "{siteId}/search", method = RequestMethod.GET)
    ResponseEntity<Hits> search(
            @CookieValue(value = "override-site", required = false) UUID cookieSite,
            @RequestParam(value = "query", defaultValue = "") String query,
            @PathVariable(value = "siteId") UUID siteId
    ) {
        if (query.isEmpty()) return ResponseEntity.badRequest().build();

        // override siteId with cookie value for debugging & speed up the getting started experience
        if (cookieSite != null) {
            siteId = cookieSite;
        }

        Hits searchResult = searchService.search(query, siteId);
        LOG.info("siteId: " + siteId + " - query: " + query + " - results: " + searchResult.getResults().size());
        return ResponseEntity.ok(searchResult);
    }
}
