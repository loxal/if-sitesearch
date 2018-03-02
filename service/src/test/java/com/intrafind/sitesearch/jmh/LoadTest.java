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

package com.intrafind.sitesearch.jmh;

import com.intrafind.sitesearch.controller.AutocompleteController;
import com.intrafind.sitesearch.controller.SearchController;
import com.intrafind.sitesearch.dto.Autocomplete;
import com.intrafind.sitesearch.dto.Hits;
import com.intrafind.sitesearch.integration.SearchTest;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

@State(Scope.Benchmark)
public class LoadTest {
    private static final Logger LOG = LoggerFactory.getLogger(LoadTest.class);
    private static final UUID LOAD_SITE_ID = UUID.fromString("8464f91e-df95-4e48-bfe0-f63af81e7346");
    static final String[] LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras viverra enim vitae malesuada placerat. Nam auctor pellentesque libero, et venenatis enim molestie vel. Duis est metus, congue quis orci id, tincidunt mattis turpis. In fringilla ultricies sapien ultrices accumsan. Sed mattis tellus lacus, quis scelerisque turpis hendrerit et. In iaculis malesuada ipsum, ac rhoncus mauris auctor quis. Proin varius, ex vestibulum condimentum lacinia, ligula est finibus ligula, id consectetur nisi enim ut velit. Sed aliquet gravida justo ac condimentum. In malesuada sed elit vitae vestibulum. Mauris vitae congue lacus. Quisque vitae tincidunt orci. Donec viverra enim a lacinia pulvinar. Sed vel ullamcorper est. Vestibulum vel urna at nisl tincidunt blandit. Donec purus leo, interdum in diam in, posuere varius tellus. Quisque eleifend nulla at nulla vestibulum ullamcorper. Praesent interdum vehicula cursus. Morbi vitae nunc et urna rhoncus semper aliquam nec velit. Quisque aliquet et velit ut mollis. Sed mattis eleifend tristique. Praesent pharetra, eros eget viverra tempus, nisi turpis molestie metus, nec tristique nulla dolor a mauris. Nullam cursus finibus erat, in pretium urna fermentum ac. In hac habitasse platea dictumst. Cras id velit id nisi euismod eleifend. Duis vehicula gravida bibendum. Cras rhoncus, massa et accumsan euismod, metus arcu rutrum orci, eu porttitor lacus tellus sed quam. Morbi tincidunt est sit amet sem convallis porta in nec nisi. Sed ex enim, fringilla nec diam in, luctus pulvinar enim. Suspendisse potenti. Quisque ut pellentesque erat. In tincidunt metus id sem fringilla sagittis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Proin erat nunc, pharetra sit amet iaculis nec, malesuada eu dui. Nullam sagittis ut arcu vitae convallis. Mauris molestie gravida lectus, eu commodo quam bibendum aliquam. Donec laoreet sed dolor eu consectetur."
            .split("\\s");

    static final List<String> QUERY_LIST_AUTOCOMPLETE;
    static final String LOAD_TARGET = "https://api.sitesearch.cloud";
    static final TestRestTemplate CALLER = new TestRestTemplate();
    static final Random PSEUDO_ENTROPY = new Random();

    private static final List<UUID> TENANTS = Arrays.asList(
            SearchTest.SEARCH_SITE_ID,
            UUID.fromString("4bcccea2-8bcf-4280-88c7-8736e9c3d15c"),
            UUID.fromString("1a6715d9-119f-48d1-9329-e8763273bbea")
    );

    static final Map<String, Long> SEARCH_QUERIES = new HashMap<>();
    static {
        SEARCH_QUERIES.put("bank", 47L);
        SEARCH_QUERIES.put("fonds", 1L);
        SEARCH_QUERIES.put("finanzen", 12L);
        SEARCH_QUERIES.put("geld", 34L);
        SEARCH_QUERIES.put("\uD83E\uDD84", 0L);
    }

    static final List<String> QUERY_LIST_SEARCH = new ArrayList<>(SEARCH_QUERIES.keySet());

    static final Map<String, Long> AUTOCOMPLETE_QUERIES = new ConcurrentHashMap<>();

    static {
        AUTOCOMPLETE_QUERIES.put("hyp", 0L);
        AUTOCOMPLETE_QUERIES.put("geld", 5L);
        AUTOCOMPLETE_QUERIES.put("bank", 7L);
        AUTOCOMPLETE_QUERIES.put("fond", 10L);
        QUERY_LIST_AUTOCOMPLETE = new ArrayList<>(AUTOCOMPLETE_QUERIES.keySet());
    }

    public static void main(String... args) throws RunnerException {
        Options options = new OptionsBuilder()
//                .timeout(TimeValue.seconds(60))
//                .timeout(TimeValue.seconds(5))
//                .include(".*")
//                .include(LoadIndex2Users.class.getSimpleName())
                .include(LoadTest.class.getSimpleName())
//                .warmupIterations(5)
//                .measurementIterations(20)
                .forks(2)
                .threads(200)
                .mode(Mode.Throughput)
                .resultFormat(ResultFormatType.JSON)
                .result("build/jmh-result.json")
                .shouldFailOnError(true)
                .build();

        new Runner(options).run();
    }

    @Benchmark
    public void staticFiles() {
        final ResponseEntity<String> staticFile = CALLER.getForEntity(LOAD_TARGET, String.class);

        assertEquals(HttpStatus.OK, staticFile.getStatusCode());
        assertFalse(staticFile.getBody().isEmpty());
    }

    @Benchmark
    public void searchComplexDeprecated() {
        final int queryIndex = LoadTest.PSEUDO_ENTROPY.nextInt(LoadTest.SEARCH_QUERIES.size());
        final String query = LoadTest.QUERY_LIST_SEARCH.get(queryIndex);

        final ResponseEntity<Hits> actual = CALLER.getForEntity(
                LoadTest.LOAD_TARGET + SearchController.ENDPOINT
                        + "?query=" + query + "&siteId=" + LOAD_SITE_ID,
                Hits.class
        );

        final long queryResultCount = LoadTest.SEARCH_QUERIES.get(query);
        if (queryResultCount == 0) {
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertNotNull(actual.getBody());
        } else {
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertEquals(queryResultCount, actual.getBody().getResults().size());
        }
    }

    @Benchmark
    public void search() {
        final int queryIndex = LoadTest.PSEUDO_ENTROPY.nextInt(LoadTest.SEARCH_QUERIES.size());
        final String query = LoadTest.QUERY_LIST_SEARCH.get(queryIndex);

        final ResponseEntity<Hits> actual = CALLER.getForEntity(
                LoadTest.LOAD_TARGET + "/sites/" + LOAD_SITE_ID + SearchController.ENDPOINT
                        + "?query=" + query,
                Hits.class
        );

        final long queryResultCount = LoadTest.SEARCH_QUERIES.get(query);
        if (queryResultCount == 0) {
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertNotNull(actual.getBody());
        } else {
            assertEquals(HttpStatus.OK, actual.getStatusCode());
            assertEquals(queryResultCount, actual.getBody().getResults().size());
        }
    }

    @Benchmark
    public void autocompleteDeprecated() {
        final int queryIndex = LoadTest.PSEUDO_ENTROPY.nextInt(LoadTest.AUTOCOMPLETE_QUERIES.size());
        final String query = LoadTest.QUERY_LIST_AUTOCOMPLETE.get(queryIndex);

        final ResponseEntity<Autocomplete> actual = CALLER.getForEntity(
                LOAD_TARGET + AutocompleteController.ENDPOINT
                        + "?query=" + query + "&siteId=" + LOAD_SITE_ID,
                Autocomplete.class
        );

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        final long queryResultCount = AUTOCOMPLETE_QUERIES.get(query);
//        assertEquals(queryResultCount, actual.getBody().getResults().size());
        assertTrue(queryResultCount <= actual.getBody().getResults().size());
    }

    @Benchmark
    public void autocomplete() {
        final int queryIndex = LoadTest.PSEUDO_ENTROPY.nextInt(LoadTest.AUTOCOMPLETE_QUERIES.size());
        final String query = LoadTest.QUERY_LIST_AUTOCOMPLETE.get(queryIndex);

        final ResponseEntity<Autocomplete> actual = CALLER.getForEntity(
                LOAD_TARGET + "/sites/" + LOAD_SITE_ID + AutocompleteController.ENDPOINT
                        + "?query=" + query,
                Autocomplete.class
        );

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        final long queryResultCount = AUTOCOMPLETE_QUERIES.get(query);
//        assertEquals(queryResultCount, actual.getBody().getResults().size());
        assertTrue(queryResultCount <= actual.getBody().getResults().size());
    }
}
