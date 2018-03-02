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
import java.util.Objects;
import java.util.UUID;

public class FetchedPage implements Serializable {
    private String id;
    private UUID siteId;
    private String title;
    private String body;
    private String url;

    private FetchedPage() {
    }

    public FetchedPage(UUID siteId, String id, String title, String body, String url) {
        this.title = title;
        this.body = body;
        this.url = url;
        this.siteId = siteId;
        this.id = id;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FetchedPage site = (FetchedPage) o;
        return Objects.equals(title, site.title) &&
                Objects.equals(body, site.body) &&
                Objects.equals(url, site.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body, url);
    }
}
