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

import com.intrafind.api.Fields;
import com.intrafind.api.search.Search;
import com.intrafind.sitesearch.Application;
import com.intrafind.sitesearch.dto.Autocomplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AutocompleteService {
    private static final Search SEARCH_AUTOCOMPLETE_SERVICE = IfinderCoreClient.newHessianClient(Search.class, Application.IFINDER_CORE + "/autocomplete");
    private static final Logger LOG = LoggerFactory.getLogger(AutocompleteService.class);

    public Optional<Autocomplete> autocomplete(String query, UUID siteId) {
        com.intrafind.api.search.Hits hits = SEARCH_AUTOCOMPLETE_SERVICE.search(
                query,
                Search.FILTER_QUERY, Fields.TENANT + ":" + siteId,
                "ac-dym.profile", "fuzzy",
                Search.HITS_LIST_SIZE, 10
        );

        final List<String> terms = hits.getMetaData().getAll("autocomplete.terms");
        if (terms == null || terms.isEmpty()) {
            return Optional.of(new Autocomplete(Collections.emptyList()));
        } else {
            return Optional.of(new Autocomplete(terms));
        }
    }
}
