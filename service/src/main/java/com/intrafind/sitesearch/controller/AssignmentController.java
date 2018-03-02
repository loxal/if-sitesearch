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

package com.intrafind.sitesearch.controller;

//@RestController
public class AssignmentController {
    public static final String ENDPOINT = "/assignments";
//    private static final Logger LOG = LoggerFactory.getLogger(AssignmentController.class);
//    private static final PersistentEntityStore ACID_PERSISTENCE_ENTITY = PersistentEntityStores.newInstance(SearchController.ACID_PERSISTENCE_ENVIRONMENT, "administration");
//    private static final RestTemplate CALLER = new RestTemplate();
//
//    @RequestMapping(path = ENDPOINT + "/sites/{siteId}", method = RequestMethod.PUT)
//    ResponseEntity<TenantSiteAssignment> assignSite(
//            @PathVariable(value = "siteId") UUID siteId,
//            @RequestParam(value = "siteSecret") UUID siteSecret,
//            @RequestParam(value = "siteName") String siteName,
//            @RequestBody TenantSiteAssignment tenantSiteAssignment
//    ) {
//        final String providerId = tenantSiteAssignment.getAuthProvider() + "-" + tenantSiteAssignment.getAuthProviderId();
//
//        if (PageService.fetchSiteSecret(siteId).isPresent()) {
//            if (!PageService.fetchSiteSecret(siteId).get().equals(siteSecret)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//            }
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        if (isAuthenticated(tenantSiteAssignment.getAuthProvider(), tenantSiteAssignment.getAuthProviderId(), tenantSiteAssignment.getAuthProviderToken())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        ACID_PERSISTENCE_ENTITY.executeInTransaction(entityTxn -> {
//            if (entityTxn.find("AuthProvider", "id", providerId).size() > 1) {
//                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Duplicate AuthProvider ID found: " + providerId);
//            }
//            Entity authProvider = entityTxn.find("AuthProvider", "id", providerId).getFirst();
//            Entity tenant;
//            if (authProvider == null) {
//                authProvider = entityTxn.newEntity("AuthProvider");
//                authProvider.setProperty("id", providerId);
//
//                tenant = assignTenantToUser(tenantSiteAssignment, entityTxn, authProvider);
//            } else {
//                tenant = authProvider.getLinks("tenant").getFirst();
//                if (tenant == null) {
//                    throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "User should have at least one tenant assigned: " + providerId);
//                }
//            }
//
//            if (entityTxn.find("Site", "id", siteId.toString()).size() > 1) {
//                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Duplicate Site ID found: " + siteId.toString());
//            }
//            Entity site = entityTxn.find("Site", "id", siteId.toString()).getFirst();
//            if (site == null) {
//                site = entityTxn.newEntity("Site");
//            }
//            site.setProperty("id", siteId.toString());
//            site.setProperty("secret", siteSecret.toString());
//            site.setProperty("name", siteName);
//            tenant.addLink("site", site);
//            site.addLink("tenant", tenant);
//        });
//
//        return ResponseEntity
//                .created(URI.create("https://api.sitesearch.cloud/assignments/authentication-providers/").resolve(tenantSiteAssignment.getAuthProvider()).resolve(tenantSiteAssignment.getAuthProviderId()))
//                .build();
//    }
//
//    private Entity assignTenantToUser(TenantSiteAssignment tenantSiteAssignment, @NotNull StoreTransaction entityTxn, Entity authProvider) {
//        UUID userTenant = UUID.randomUUID();
//        Entity tenant = entityTxn.find("Tenant", "id", userTenant.toString()).getFirst();
//        if (tenant == null) {
//            tenant = entityTxn.newEntity("Tenant");
//            tenant.setProperty("id", userTenant.toString());
//        }
//        tenant.setProperty("company", tenantSiteAssignment.getCompany());
//        tenant.setProperty("contactEmail", tenantSiteAssignment.getContactEmail());
//
//        tenant.addLink("authProvider", authProvider);
//        authProvider.addLink("tenant", tenant);
//
//        return tenant;
//    }
//
//    @RequestMapping(path = "/authentication-providers/{provider}/{providerId}", method = RequestMethod.GET)
//    ResponseEntity<TenantOverview> obtainTenantOverview(
//            @PathVariable(value = "provider") String provider,
//            @PathVariable(value = "providerId") String providerId,
//            @RequestParam(value = "accessToken") String accessToken
//    ) {
//        TenantOverview tenantOverview = new TenantOverview(
//                Lists.newArrayList(),
//                Lists.newArrayList()
//        );
//        LOG.info("provider: " + provider);
//        LOG.info("providerId: " + providerId);
//        LOG.info("accessToken: " + accessToken);
//
//        if (isAuthenticated(provider, providerId, accessToken)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        final StoreTransaction findTxn = ACID_PERSISTENCE_ENTITY.beginReadonlyTransaction();
//        final EntityIterable authProviders = findTxn.find("AuthProvider", "id", provider + "-" + providerId);
//        if (authProviders.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // TODO add integration test
//        }
//        authProviders.forEach(authProvider -> {
//            tenantOverview.getAuthProviders().add(authProvider.getProperty("id").toString());
//            authProvider.getLinks("tenant").forEach(tenant -> {
//                List<Site> sites = new ArrayList<>();
//                tenant.getLinks("site").forEach(site -> {
//                    sites.add(new Site(
//                            UUID.fromString(site.getProperty("id").toString()),
//                            UUID.fromString(site.getProperty("secret").toString()),
//                            site.getProperty("name") == null ? "" : site.getProperty("name").toString())
//                    );
//                });
//                LOG.warn("tenant: " + tenant.getProperty("id"));
//
//                TenantOverview.TenantInfo tenantInfo = new TenantOverview.TenantInfo(
//                        UUID.fromString(tenant.getProperty("id").toString()),
//                        tenant.getProperty("company").toString(),
//                        tenant.getProperty("contactEmail").toString(),
//                        sites
//                );
//                tenantOverview.getTenants().add(tenantInfo);
//            });
//        });
//        findTxn.commit();
//
//        return ResponseEntity.ok(tenantOverview);
//    }
//
//    private boolean isAuthenticated(String provider, String providerId, String accessToken) {
//        try {
//            final ResponseEntity<GitHubUser> githubUser = CALLER
//                    .getForEntity(URI.create("https://api.github.com/user?access_token=" + accessToken), GitHubUser.class);
//
//            if (System.getenv("DEV_SKIP_FLAG") == null) { // skip accessToken checks when running locally or on CI
//                if (!HttpStatus.OK.equals(githubUser.getStatusCode())
//                        || !providerId.equals(githubUser.getBody().getId())) {
//                    LOG.warn("Invalid oAuth2 accessToken {} for given authProvider {} & authProviderId {}", accessToken, provider, providerId);
//                    return true;
//                }
//            }
//            return false;
//        } catch (RestClientException e) {
//            LOG.warn(e.getMessage());
//            return false;
//        }
//    }
}
