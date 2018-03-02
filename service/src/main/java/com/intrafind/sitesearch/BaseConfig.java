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

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.ServletContext;
import java.util.Collections;

@Configuration
public class BaseConfig {

//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
//        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
//
//        return factory;
//    }

    @Bean
    public Docket hideApi(ServletContext servletContext) {
        return new Docket(DocumentationType.SWAGGER_2)
                .host("api.sitesearch.cloud")
                .apiInfo(new ApiInfo(
                        "Site Search API",
                        "A simple website search solution",
                        "v1",
                        "https://sitesearch.cloud/terms",
                        new Contact("IntraFind Software AG", "https://sitesearch.cloud", "feedback@sitesearch.cloud"),
                        "Apache License, Version 2.0",
                        "https://github.com/intrafind/if-sitesearch/blob/master/LICENSE",
                        Collections.emptyList()
                ))
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        return "/";
                    }
                })
                .select()
                .paths(Predicates.not(PathSelectors.regex("/sites/crawl.*")))
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .paths(Predicates.not(PathSelectors.regex("/login.*")))
                .paths(Predicates.not(PathSelectors.regex("/subscriptions")))
                .paths(Predicates.not(PathSelectors.regex("/assignments.*")))
                .paths(Predicates.not(PathSelectors.regex("/pages/.*")))
                .paths(Predicates.not(PathSelectors.regex("/search")))
                .paths(Predicates.not(PathSelectors.regex("/autocomplete")))
                .paths(Predicates.not(PathSelectors.regex("/sites/rss")))
                .paths(Predicates.not(PathSelectors.regex("/sites/.+/pages/.*")))
                .paths(Predicates.not(PathSelectors.regex("/sites/.+/email.*")))
                .paths(Predicates.not(PathSelectors.regex("/sites/.+/crawl")))
                .paths(Predicates.not(PathSelectors.regex("/sites/.+/crawling")))
                .paths(Predicates.not(PathSelectors.regex("/authentication-providers.*")))
                .paths(Predicates.not(PathSelectors.regex("/user")))
                .paths(Predicates.not(PathSelectors.regex("/stats")))
                .build();
    }
}
