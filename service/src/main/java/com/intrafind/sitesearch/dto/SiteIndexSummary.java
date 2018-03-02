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
import java.util.UUID;

public class SiteIndexSummary implements Serializable {
    private UUID siteId;
    private UUID siteSecret;
    // TODO remove successCount as it's an implicit part of documents(.size)
    private int successCount;
    private List<String> documents = Collections.emptyList();
    private List<String> failed = Collections.emptyList();

    private SiteIndexSummary() {
    }

    public SiteIndexSummary(UUID siteId, UUID siteSecret, int successCount, List<String> documents, List<String> failed) {
        this.siteId = siteId;
        this.siteSecret = siteSecret;
        this.successCount = successCount;
        this.documents = documents;
        this.failed = failed;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public List<String> getFailed() {
        return failed;
    }

    public void setFailed(List<String> failed) {
        this.failed = failed;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public void setSiteId(UUID siteId) {
        this.siteId = siteId;
    }

    public UUID getSiteSecret() {
        return siteSecret;
    }

    public void setSiteSecret(UUID siteSecret) {
        this.siteSecret = siteSecret;
    }
}
