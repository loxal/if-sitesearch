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

package com.intrafind.sitesearch.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Threads;

@Threads(1000)
@BenchmarkMode(Mode.Throughput)
public class Load1000Users {
    private static final LoadTest LOAD_10_USERS = new LoadTest();

    @Benchmark
    public void searchComplex() {
        LOAD_10_USERS.search();
    }

    @Benchmark
    public void autocomplete() {
        LOAD_10_USERS.autocomplete();
    }
}
