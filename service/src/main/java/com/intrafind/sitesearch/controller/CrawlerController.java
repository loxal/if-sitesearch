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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.intrafind.sitesearch.dto.CaptchaVerification;
import com.intrafind.sitesearch.dto.CrawlerJobResult;
import com.intrafind.sitesearch.dto.SitesCrawlStatus;
import com.intrafind.sitesearch.service.CrawlerService;
import com.intrafind.sitesearch.service.SiteCrawler;
import com.intrafind.sitesearch.service.SiteService;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping(SiteController.ENDPOINT)
public class CrawlerController {
    private static final Logger LOG = LoggerFactory.getLogger(CrawlerController.class);
    private static final String PROSPECTS_EMAIL_ADDRESS = "Support - Site Search <f518c8ec.intrafind.de@emea.teams.ms>";
    private final SiteService siteService;
    private final CrawlerService crawlerService;

    @Autowired
    private CrawlerController(SiteService siteService, CrawlerService crawlerService) {
        this.siteService = siteService;
        this.crawlerService = crawlerService;
    }

    static final ObjectMapper MAPPER = new ObjectMapper();
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String SERVICE_CONFIG_PATH = "config/";
    private static final File DATA_STORE_DIR = new File(SERVICE_CONFIG_PATH + "gmail-api");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private static MimeMessage createEmail(String to, String subject, String bodyText) throws Exception {
        final Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        final MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress("team@sitesearch.cloud"));
        email.setReplyTo(new InternetAddress[]{new InternetAddress("feedback@sitesearch.cloud")});
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(PROSPECTS_EMAIL_ADDRESS));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    private static com.google.api.services.gmail.model.Message createMessageWithEmail(MimeMessage emailContent) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private static com.google.api.services.gmail.model.Message sendMessage(Gmail service, String userId, MimeMessage emailContent) throws Exception {
        com.google.api.services.gmail.model.Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        LOG.debug(message.toPrettyString());
        return message;
    }

    public static Credential authorize() throws IOException {
        final InputStream resourceAsStream = new FileInputStream(new File(SERVICE_CONFIG_PATH + "gmail-api-client_secret.json"));
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(resourceAsStream));

        final GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        final Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        LOG.debug("Credentials saved to: " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    private static Gmail initGmailService() throws IOException {
        final Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Site Search")
                .build();
    }

    private static void sendSetupInfoEmail(UUID siteId, UUID siteSecret, URI url, String email, int pageCount) throws Exception {
        final Gmail service = initGmailService();
        LOG.debug("servicePath: " + service.getServicePath());

        final Message message = sendMessage(service, "me",
                createEmail(
                        email,
                        "Evaluation Information - Site Search",
                        "\nHello," +
                                "\n\nyou are just a few steps away from adding Site Search to your website." +
                                "\nBelow you should find everything you need to evaluate Site Search for your website." +
                                "\n\tWebsite URL: " + url +
                                "\n\t\tPages crawled: " + pageCount +
                                "\n\tSite ID: " + siteId +
                                "\n\tSite Secret: " + siteSecret +
                                "\n\tSite Search Evaluation URL: https://sitesearch.cloud/getting-started/?siteId=" + siteId + "&siteSecret=" + siteSecret + "&url=" + url +
                                "\n\nPlease do not hesitate to ask us any questions you should encounter during your 14-day evaluation period!" +
                                "\n\nCheers," +
                                "\nSite Search Team"
                )
        );
    }

    public static void main(String[] args) throws Exception {
        sendSetupInfoEmail(UUID.randomUUID(), UUID.randomUUID(), URI.create("https://example.com"), PROSPECTS_EMAIL_ADDRESS, 10);
    }

    @RequestMapping(path = "crawl", method = RequestMethod.POST)
    ResponseEntity<SitesCrawlStatus> recrawlSites(
            @RequestParam(value = "serviceSecret") UUID serviceSecret,
            @RequestBody SitesCrawlStatus sitesCrawlStatusUpdate,
            @RequestParam(required = false, value = "allSitesCrawl", defaultValue = "false") boolean allSitesCrawl
    ) {
        // TODO refactor code so `crawlerService` does not need to be passed as argument
        final Optional<SitesCrawlStatus> sitesCrawlStatus = siteService.recrawlSites(serviceSecret, crawlerService, sitesCrawlStatusUpdate, allSitesCrawl);
        if (sitesCrawlStatus.isPresent()) {
            return ResponseEntity.ok(sitesCrawlStatus.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(path = "crawl/status", method = RequestMethod.PUT)
    ResponseEntity<SitesCrawlStatus> updateCrawlStatus(
            @RequestParam(value = "serviceSecret") UUID serviceSecret,
            @RequestBody SitesCrawlStatus sitesCrawlStatusUpdate
    ) {
        final Optional<SitesCrawlStatus> sitesCrawlStatus = siteService.storeCrawlStatus(serviceSecret, sitesCrawlStatusUpdate);
        if (sitesCrawlStatus.isPresent()) {
            return ResponseEntity.ok(sitesCrawlStatus.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(path = "crawl/status", method = RequestMethod.GET)
    ResponseEntity<SitesCrawlStatus> fetchCrawlStatus(
            @RequestParam(value = "serviceSecret") UUID serviceSecret
    ) {
        final Optional<SitesCrawlStatus> sitesCrawlStatus = siteService.fetchCrawlStatus(serviceSecret);
        if (sitesCrawlStatus.isPresent()) {
            return ResponseEntity.ok(sitesCrawlStatus.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @RequestMapping(path = "{siteId}/crawl", method = RequestMethod.POST)
    ResponseEntity<CrawlerJobResult> crawl(
            @PathVariable(value = "siteId") UUID siteId,
            @RequestParam(value = "siteSecret") UUID siteSecret,
            @RequestParam(value = "url") URI url,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "token") String captchaToken
    ) {
        if (!siteService.isAllowedToModify(siteId, siteSecret)) {
            return ResponseEntity.notFound().build();
        }

        boolean captchaPassed = false;
        try {
            Request request = new Request.Builder()
                    .url("https://www.google.com/recaptcha/api/siteverify?secret=" + System.getenv("RECAPTCHA_SITE_SECRET") + "&response=" + captchaToken)
                    .post(okhttp3.RequestBody.create(MediaType.parse("applications/json"), ""))
                    .build();
            final Response response = SiteCrawler.HTTP_CLIENT.newCall(request).execute();
            final CaptchaVerification captchaVerification = MAPPER.readValue(response.body().bytes(), CaptchaVerification.class);

            if (captchaVerification.getSuccess() || "true".equals(System.getenv("DEV_SKIP_FLAG"))) {
                captchaPassed = true;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (captchaPassed) {
            final CrawlerJobResult crawlerJobResult = crawlerService.crawl(url.toString(), siteId, siteSecret);
            final String emailAddress = determineEmailAddress(email);
            try {
                sendSetupInfoEmail(siteId, siteSecret, url, emailAddress, crawlerJobResult.getPageCount());
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
            LOG.info("siteId: " + siteId + " - siteSecret: " + siteSecret + " - siteUrl: " + url + " - pageCount: " + crawlerJobResult.getPageCount() + " - email: " + email);
            return ResponseEntity.ok(crawlerJobResult);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    private String determineEmailAddress(String email) {
        if (email == null || email.isEmpty() || !email.contains("@")) {
            return PROSPECTS_EMAIL_ADDRESS;
        } else {
            return email;
        }
    }
}
