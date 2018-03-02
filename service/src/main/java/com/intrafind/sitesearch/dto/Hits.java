/*
 * Copyright 2017 IntraFind Software AG. All rights reserved.
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

package com.intrafind.sitesearch.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Hits implements Serializable {
    private List<FoundPage> results = Collections.emptyList();
    private String query = "";

    private Hits() {
    }

    public Hits(String query, List<FoundPage> results) {
        this.query = query;
        this.results = results;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public List<FoundPage> getResults() {
        return results;
    }

    public void setResults(List<FoundPage> sites) {
        this.results = sites;
    }
}
