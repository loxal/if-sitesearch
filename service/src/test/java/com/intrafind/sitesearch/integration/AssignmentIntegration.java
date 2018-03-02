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

package com.intrafind.sitesearch.integration;

import com.intrafind.sitesearch.controller.AssignmentController;
import com.intrafind.sitesearch.controller.SiteController;
import com.intrafind.sitesearch.dto.SiteCreation;
import com.intrafind.sitesearch.dto.TenantOverview;
import com.intrafind.sitesearch.dto.TenantSiteAssignment;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Ignore
public class AssignmentIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(AssignmentIntegration.class);
    @Autowired
    private TestRestTemplate caller;

    private SiteCreation newSite() {
        ResponseEntity<SiteCreation> site = caller.exchange(SiteController.ENDPOINT, HttpMethod.POST, HttpEntity.EMPTY, SiteCreation.class);

        assertEquals(HttpStatus.CREATED, site.getStatusCode());
        assertNotNull(site.getBody());
        assertNotNull(site.getBody().getSiteId());
        assertNotNull(site.getBody().getSiteSecret());
        assertEquals("https://api.sitesearch.cloud/sites/" + site.getBody().getSiteId(), site.getHeaders().get(HttpHeaders.LOCATION).get(0));
        return site.getBody();
    }

    @Test
    public void assignSiteToTenant() {
        SiteCreation site = newSite();

        final UUID tenantId = UUID.randomUUID();
        final UUID siteId = site.getSiteId();
        final UUID siteSecret = site.getSiteSecret();
        final String authProvider = "testProvider";
        final String authProviderId = UUID.randomUUID().toString();

        LOG.info("tenantId: " + tenantId);
        LOG.info("siteId: " + siteId);
        LOG.info("siteSecret: " + siteSecret);

        final ResponseEntity createTenantAssignmentInitially = assignTenantAndSiteToAuthProvider(tenantId, siteId, siteSecret, "Site Name1", authProvider, authProviderId);
        assertEquals(HttpStatus.CREATED, createTenantAssignmentInitially.getStatusCode());
        // execute the above call again which should not change the resource state
        final ResponseEntity createdTenantAssignmentSubsequently = assignTenantAndSiteToAuthProvider(tenantId, siteId, siteSecret, "Site Name2", authProvider, authProviderId);
        assertEquals(HttpStatus.CREATED, createdTenantAssignmentSubsequently.getStatusCode());
        assertNull(createdTenantAssignmentSubsequently.getBody());

        final ResponseEntity<TenantOverview> fetchedTenantOverview = obtainAuthProvidersAssignments(authProvider, authProviderId);
        assureSingleSimpleAssignment(fetchedTenantOverview, 1, 1);

        // add additional auth provider
        final String additionalAuthProvider = "testProvider-" + UUID.randomUUID();
        final ResponseEntity addAuthProvider = assignTenantAndSiteToAuthProvider(tenantId, siteId, siteSecret, "Site Name3", additionalAuthProvider, authProviderId);
        assertEquals(HttpStatus.CREATED, addAuthProvider.getStatusCode());
        final ResponseEntity<TenantOverview> addedAuthProvider = obtainAuthProvidersAssignments(authProvider, authProviderId);
        assureSingleSimpleAssignment(addedAuthProvider, 1, 1);

        // add additional Site
        SiteCreation additionalSiteViaPageCreation = newSite();
        final ResponseEntity additionalSiteAddition = assignTenantAndSiteToAuthProvider(tenantId, additionalSiteViaPageCreation.getSiteId(), additionalSiteViaPageCreation.getSiteSecret(), "Site Name4", authProvider, authProviderId);
        assertEquals(HttpStatus.CREATED, additionalSiteAddition.getStatusCode());
        final ResponseEntity<TenantOverview> additionalSiteAdded = obtainAuthProvidersAssignments(authProvider, authProviderId);
        assureSingleSimpleAssignment(additionalSiteAdded, 2, 1);

        // add additional tenant
        final ResponseEntity additionalTenantAddition = assignTenantAndSiteToAuthProvider(UUID.randomUUID(), additionalSiteViaPageCreation.getSiteId(), additionalSiteViaPageCreation.getSiteSecret(), "Site Name5", authProvider, authProviderId);
        assertEquals(HttpStatus.CREATED, additionalTenantAddition.getStatusCode());
        final ResponseEntity<TenantOverview> additionalTenantAdded = obtainAuthProvidersAssignments(authProvider, authProviderId);
        assureSingleSimpleAssignment(additionalTenantAdded, 2, 1);
//        assureSingleSimpleAssignment(additionalTenantAdded, 2, 2);
//        assureSingleSimpleAssignment(additionalTenantAdded, 3, 2);

        // add additional tenant based on new site
        final SiteCreation anotherAdditionalSiteViaPageCreation = newSite();
        final ResponseEntity additionalTenantAdditionWithAdditionalSite = assignTenantAndSiteToAuthProvider(UUID.randomUUID(), anotherAdditionalSiteViaPageCreation.getSiteId(), anotherAdditionalSiteViaPageCreation.getSiteSecret(), "Site Name6", authProvider, authProviderId);
        assertEquals(HttpStatus.CREATED, additionalTenantAdditionWithAdditionalSite.getStatusCode());
        final ResponseEntity<TenantOverview> additionalTenantAddedWithAdditionalSite = obtainAuthProvidersAssignments(authProvider, authProviderId);
//        assureSingleSimpleAssignment(additionalTenantAddedWithAdditionalSite, 3, 3);
//        assureSingleSimpleAssignment(additionalTenantAddedWithAdditionalSite, 4, 3);
//        assureSingleSimpleAssignment(additionalTenantAddedWithAdditionalSite, 2, 3);
        assureSingleSimpleAssignment(additionalTenantAddedWithAdditionalSite, 3, 1);

        // siteSecret does not match siteId's secret
        UUID invalidSiteSecret = UUID.randomUUID();
        final ResponseEntity invalidSiteSecretSubmission = assignTenantAndSiteToAuthProvider(tenantId, anotherAdditionalSiteViaPageCreation.getSiteId(), invalidSiteSecret, "Site Name7", authProvider, authProviderId);
        assertEquals(HttpStatus.FORBIDDEN, invalidSiteSecretSubmission.getStatusCode());
        assertNull(invalidSiteSecretSubmission.getBody());
    }

    private ResponseEntity<TenantOverview> obtainAuthProvidersAssignments(String authProvider, String authProviderId) {
        final ResponseEntity<TenantOverview> tenantOverview = caller.getForEntity(
                "/authentication-providers/" + authProvider + "/" + authProviderId
                        + "?accessToken=" + System.getenv("GITHUB_PUBLIC_ACCESS_TOKEN"),
                TenantOverview.class
        );
        assertEquals(HttpStatus.OK, tenantOverview.getStatusCode());

        return tenantOverview;
    }

    private void assureSingleSimpleAssignment(ResponseEntity<TenantOverview> fetchedTenantOverview, int siteSize, int tenantSize) {
        assertEquals(HttpStatus.OK, fetchedTenantOverview.getStatusCode());
        final TenantOverview.TenantInfo firstTenant = fetchedTenantOverview.getBody().getTenants().get(0);
        assertEquals(siteSize, firstTenant.getSites().size());
        firstTenant.getSites().forEach(site -> {
            assertNotNull(site.getId());
            assertNotNull(site.getSecret());
            assertTrue(site.getName().startsWith("Site Name"));
        });
        assertEquals(tenantSize, fetchedTenantOverview.getBody().getTenants().size());
        fetchedTenantOverview.getBody().getTenants().forEach(tenantInfo -> {
            assertNotNull(tenantInfo.getId());
            assertFalse(tenantInfo.getCompany().isEmpty());
            assertFalse(tenantInfo.getCompany().contains("@"));
            assertFalse(tenantInfo.getContactEmail().isEmpty());
            assertTrue(tenantInfo.getContactEmail().contains("@"));
        });
        assertEquals(1, fetchedTenantOverview.getBody().getAuthProviders().size());
    }

    private ResponseEntity assignTenantAndSiteToAuthProvider(UUID tenantId, UUID siteId, UUID siteSecret, String siteName, String authProvider, String authProviderId) {
        return caller.exchange(
                AssignmentController.ENDPOINT + "/sites/" + siteId
                        + "?siteSecret=" + siteSecret
                        + "&siteName=" + siteName,
                HttpMethod.PUT,
                new HttpEntity<>(new TenantSiteAssignment(
                        "IntraFind Software AG",
                        "alexander.orlov@intrafind.de",
                        authProvider,
                        authProviderId,
                        System.getenv("GITHUB_PUBLIC_ACCESS_TOKEN"))),
                TenantSiteAssignment.class
        );
    }
}
