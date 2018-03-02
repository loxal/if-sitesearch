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

import java.util.UUID;

public class SiteCreation {
    private UUID siteId;
    private UUID siteSecret;

    private SiteCreation() {
    }

    public SiteCreation(UUID siteId, UUID siteSecret) {
        this.siteId = siteId;
        this.siteSecret = siteSecret;
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
