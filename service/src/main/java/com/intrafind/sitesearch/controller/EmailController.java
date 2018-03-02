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

package com.intrafind.sitesearch.controller;

import com.intrafind.sitesearch.dto.CaptchaVerification;
import com.intrafind.sitesearch.service.SiteCrawler;
import com.intrafind.sitesearch.service.SiteService;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(SiteController.ENDPOINT)
public class EmailController {
    private static final Logger LOG = LoggerFactory.getLogger(EmailController.class);
    private static final AtomicInteger HARD_ABUSE_LIMIT = new AtomicInteger(0);
    private final SiteService siteService;

    @Autowired
    private EmailController(SiteService siteService) {
        this.siteService = siteService;
    }

    @RequestMapping(path = "{siteId}/email/setup-info", method = RequestMethod.POST)
    ResponseEntity<Object> email(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "siteSecret") UUID siteSecret,
            @RequestParam(value = "url") URI url
    ) {
        if (HARD_ABUSE_LIMIT.incrementAndGet() > 3) {
            return ResponseEntity.badRequest().body("E-mail limit exceeded.");
        }

        if (!siteService.isAllowedToModify(siteId, siteSecret)) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok().build();
    }


    //    @RequestMapping(path = "verify", method = RequestMethod.POST)
    ResponseEntity<Object> verify(
            @RequestParam(value = "data-callback", required = false) String dataCallback,
            @RequestBody(required = false) String payload
    ) {

        LOG.info("dataCallback: " + dataCallback);
        LOG.info("payload: " + payload);

        try {
            Request request = new Request.Builder()
                    .url("https://www.google.com/recaptcha/api/siteverify?secret=" + System.getenv("RECAPTCHA_SITE_SECRET") + "&response=" + payload)
                    .post(okhttp3.RequestBody.create(MediaType.parse("applications/json"), ""))
                    .build();
            final Response response = SiteCrawler.HTTP_CLIENT.newCall(request).execute();
            final CaptchaVerification captchaVerification = CrawlerController.MAPPER.readValue(response.body().bytes(), CaptchaVerification.class);

            return ResponseEntity.ok(dataCallback);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
