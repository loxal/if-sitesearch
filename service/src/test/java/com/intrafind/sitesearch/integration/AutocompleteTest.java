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

import com.intrafind.sitesearch.controller.AutocompleteController;
import com.intrafind.sitesearch.dto.Autocomplete;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AutocompleteTest {
    private static final Logger LOG = LoggerFactory.getLogger(AutocompleteTest.class);
    @Autowired
    private TestRestTemplate caller;

    @Test
    public void referenceDeprecated() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity(AutocompleteController.ENDPOINT + "?query=Knowledge&siteId=" + SearchTest.SEARCH_SITE_ID, Autocomplete.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(1 <= actual.getBody().getResults().size());
        assertEquals("knowledge graph", actual.getBody().getResults().get(0));
    }

    @Test
    public void reference() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity("/sites/" + SearchTest.SEARCH_SITE_ID + "/autocomplete?query=Knowledge", Autocomplete.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(1 <= actual.getBody().getResults().size());
        assertEquals("knowledge graph", actual.getBody().getResults().get(0));
    }

    @Test
    public void complexPositiveDeprecated() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity(AutocompleteController.ENDPOINT + "?query=ifinder&siteId=" + SearchTest.SEARCH_SITE_ID, Autocomplete.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(1 <= actual.getBody().getResults().size());
        actual.getBody().getResults().forEach(term -> {
            LOG.info("term: " + term);
            assertTrue(term.contains("ifinder"));
        });
    }

    @Test
    public void complexPositive() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity("/sites/" + SearchTest.SEARCH_SITE_ID + "/autocomplete?query=ifinder", Autocomplete.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(1 <= actual.getBody().getResults().size());
        actual.getBody().getResults().forEach(term -> {
            LOG.info("term: " + term);
            assertTrue(term.contains("ifinder"));
        });
    }

    @Test
    public void nonExistingDeprecated() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity(AutocompleteController.ENDPOINT + "?query=not_found&siteId=" + SearchTest.SEARCH_SITE_ID, Autocomplete.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().getResults().isEmpty());
    }

    @Test
    public void nonExisting() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity("/sites/" + SearchTest.SEARCH_SITE_ID + "/autocomplete?query=not_found", Autocomplete.class);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().getResults().isEmpty());
    }

    @Test
    public void withoutSiteIdDeprecated() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity(AutocompleteController.ENDPOINT + "?query=not_found", Autocomplete.class);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertNotNull(actual.getBody());
    }

    @Test
    public void withoutSiteId() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity("/sites/" + "/autocomplete?query=not_found", Autocomplete.class);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());

        final ResponseEntity<Autocomplete> response = caller.getForEntity("/sites" + "?query=not_found", Autocomplete.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(actual.getBody());
    }

    @Test
    public void withoutInvalidSiteId() {
        final ResponseEntity<Autocomplete> actual = caller.getForEntity("/sites/invalid-siteId/autocomplete?query=not_found", Autocomplete.class);

        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertNotNull(actual.getBody());
    }
}


