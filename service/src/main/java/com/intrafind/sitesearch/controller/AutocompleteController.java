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

import com.intrafind.sitesearch.dto.Autocomplete;
import com.intrafind.sitesearch.service.AutocompleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class AutocompleteController {
    public static final String ENDPOINT = "/autocomplete";
    private static final Logger LOG = LoggerFactory.getLogger(AutocompleteController.class);
    private final AutocompleteService service;

    @Autowired
    private AutocompleteController(AutocompleteService service) {
        this.service = service;
    }

    @RequestMapping(path = ENDPOINT, method = RequestMethod.GET)
    ResponseEntity<Autocomplete> autocompleteSuggestionDeprecatedAPI(
            @CookieValue(value = "override-site", required = false) UUID cookieSite,
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "siteId") UUID siteId
    ) {
        final var start = Instant.now();
        if (query.isEmpty()) return ResponseEntity.badRequest().build();

        // override siteId with cookie value for debugging & speed up the getting started experience
        if (cookieSite != null) siteId = cookieSite;

        final var result = service.autocomplete(query, siteId);
        if (result.isPresent()) {
            final Autocomplete autocomplete = result.get();
            final Instant stop = Instant.now();
            final Instant autocompleteDuration = stop.minusMillis(start.toEpochMilli());
            LOG.info("siteId: " + siteId + " - query-fragment: " + query + " - autocompletes: " + autocomplete.getResults().size() + " - autocompleteDurationInMs: " + autocompleteDuration.toEpochMilli());
            return ResponseEntity.ok(autocomplete);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
