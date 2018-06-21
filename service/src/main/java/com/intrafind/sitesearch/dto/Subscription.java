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

import java.util.UUID;

public class Subscription {
    private String id;
    private String plan;
    private UUID siteId;
    private Object rawSubscription;

    public Subscription(String id, String plan, UUID siteId, Object rawSubscription) {
        this.id = id;
        this.plan = plan;
        this.siteId = siteId;
        this.rawSubscription = rawSubscription;
    }

    public String getPlan() {
        return plan;
    }

    public String getId() {
        return id;
    }

    public UUID getSiteId() {
        return siteId;
    }

    public Object getRawSubscription() {
        return rawSubscription;
    }
}