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

public class Stats implements Serializable {
    private String buildNumber;
    private String scmHash;
    private String hostname;

    private Stats() {
    }

    public Stats(String buildNumber, String scmHash, String hostname) {
        this.buildNumber = buildNumber;
        this.scmHash = scmHash;
        this.hostname = hostname;
    }

    public String getScmHash() {
        return scmHash;
    }

    public void setScmHash(String scmHash) {
        this.scmHash = scmHash;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }
}
