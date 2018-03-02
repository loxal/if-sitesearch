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

public class TenantSiteAssignment implements Serializable {
    private String company;
    private String contactEmail;
    private String authProvider;
    private String authProviderId;
    private String authProviderToken;

    private TenantSiteAssignment() {
    }

    public TenantSiteAssignment(String company, String contactEmail, String authProvider, String authProviderId, String authProviderToken) {
        this.company = company;
        this.contactEmail = contactEmail;
        this.authProvider = authProvider;
        this.authProviderId = authProviderId;
        this.authProviderToken = authProviderToken;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getAuthProviderToken() {
        return authProviderToken;
    }

    public void setAuthProviderToken(String authProviderToken) {
        this.authProviderToken = authProviderToken;
    }

    public String getAuthProviderId() {
        return authProviderId;
    }

    public void setAuthProviderId(String authProviderId) {
        this.authProviderId = authProviderId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
