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
import java.util.List;
import java.util.UUID;

public class TenantOverview implements Serializable {
    private List<TenantInfo> tenants;
    private List<String> authProviders;

    private TenantOverview() {
    }

    public TenantOverview(List<TenantInfo> tenants, List<String> authProviders) {
        this.tenants = tenants;
        this.authProviders = authProviders;
    }

    public List<TenantInfo> getTenants() {
        return tenants;
    }

    public void setTenants(List<TenantInfo> tenants) {
        this.tenants = tenants;
    }

    public List<String> getAuthProviders() {
        return authProviders;
    }

    public void setAuthProviders(List<String> authProviders) {
        this.authProviders = authProviders;
    }

    public static class TenantInfo {
        private UUID id;
        private String company;
        private String contactEmail;
        private List<Site> sites;

        private TenantInfo() {
        }

        public TenantInfo(UUID id, String company, String contactEmail, List<Site> sites) {
            this.id = id;
            this.company = company;
            this.contactEmail = contactEmail;
            this.sites = sites;
        }

        public List<Site> getSites() {
            return sites;
        }

        public void setSites(List<Site> sites) {
            this.sites = sites;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
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
}
