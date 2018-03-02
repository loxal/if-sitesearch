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

package com.intrafind.sitesearch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTest {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityTest.class);
    private static final String SEARCH_SERVICE_DOMAIN = "@main.sitesearch.cloud/";
    private static final String INVALID_CREDENTIALS = "https://" + System.getenv("SPRING_SECURITY_USER_PASSWORD") + "invalid:" + System.getenv("SPRING_SECURITY_USER_PASSWORD");

    @Autowired
    private TestRestTemplate caller;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    // TODO add CORS checks

    @Test
    public void fetchIndexHtml() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
        ;
    }

//    @Test
//    public void assureSiteSearchServiceBasicAuthProtectionForHessianPost() throws Exception {
//        final ResponseEntity<String> secureEndpointHessian = caller.postForEntity(URI.create(INVALID_CREDENTIALS + SEARCH_SERVICE_DOMAIN + "hessian/index?method=index"), HttpEntity.EMPTY, String.class);
//        assertEquals(HttpStatus.UNAUTHORIZED, secureEndpointHessian.getStatusCode());
//        assertNull(secureEndpointHessian.getBody());
//    }

//    @Test
//    public void assureSiteSearchServiceBasicAuthProtectionForHessianGet() throws Exception {
//        final ResponseEntity<String> secureEndpointHessianGet = caller.exchange(INVALID_CREDENTIALS + SEARCH_SERVICE_DOMAIN + "hessian/index?method=index", HttpMethod.GET, HttpEntity.EMPTY, String.class);
//        assertEquals(HttpStatus.UNAUTHORIZED, secureEndpointHessianGet.getStatusCode());
//        assertNull(secureEndpointHessianGet.getBody());
//    }

    @Test
    public void assureSiteSearchServiceBasicAuthProtectionForJsonPost() {
        final ResponseEntity<String> secureEndpointJson = caller.postForEntity(URI.create(INVALID_CREDENTIALS + SEARCH_SERVICE_DOMAIN + "json/index?method=index"), HttpEntity.EMPTY, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, secureEndpointJson.getStatusCode());
        assertNull(secureEndpointJson.getBody());
    }

//    @Test
//    public void assureSiteSearchServiceBasicAuthProtectionForJsonGet() throws Exception {
//        final ResponseEntity<String> secureEndpointJsonGet = caller.getForEntity(URI.create(INVALID_CREDENTIALS + SEARCH_SERVICE_DOMAIN + "json/index?method=index"), String.class);
//        assertEquals(HttpStatus.UNAUTHORIZED, secureEndpointJsonGet.getStatusCode());
//        assertNull(secureEndpointJsonGet.getBody());
//    }
}