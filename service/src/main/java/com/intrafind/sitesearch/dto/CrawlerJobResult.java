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

package com.intrafind.sitesearch.dto;

import java.net.URI;
import java.util.Set;

public class CrawlerJobResult {
    private Set<URI> urls;
    private int pageCount;

    public CrawlerJobResult(Set<URI> urls, int pageCount) {
        this.urls = urls;
        this.pageCount = pageCount;
    }

    private CrawlerJobResult() {
    }

    public int getPageCount() {
        return pageCount;
    }

    public Set<URI> getUrls() {
        return urls;
    }

    public void setUrls(Set<URI> urls) {
        this.urls = urls;
    }
}
