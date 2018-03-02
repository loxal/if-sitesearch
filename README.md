<img src="https://api.sitesearch.cloud/theme/logo.png" alt="Site Search" width="600" style="max-width:100%;">

[![Instant Feedback](https://badges.gitter.im/gitterHQ/gitter.svg)](https://gitter.im/sitesearch/Lobby)
[![Travis CI Build Status](https://travis-ci.org/intrafind/if-sitesearch.svg)](https://travis-ci.org/intrafind/if-sitesearch)
[![TeamCity CI Build Status](https://ci.sitesearch.cloud/app/rest/builds/buildType(id:IntraFind_Oss_Build)/statusIcon)](https://ci.sitesearch.cloud/viewType.html?buildTypeId=IntraFind_Oss_Build)


Site Search Dispatcher
=
Site Search Dispatcher is an upstream service of [Site Search](https://api.sitesearch.cloud), 
an on-demand SaaS offering for providing websites with an externally managed search capability. 

# About

* [API](https://api.sitesearch.cloud/swagger-ui.html)
* [Documentation](https://github.com/intrafind/if-sitesearch/blob/master/docs/faq.md)
* [Integration](https://github.com/intrafind/if-sitesearch/blob/master/docs/Site%20Search%20Product%20Overview.pdf)
    
***> > > [Try out!](https://sitesearch.cloud/getting-started) < < <***    
    
# Local configuration

Add a [configuration profile](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-profile-specific-properties) 
to the `config` folder inside this project.

Required environment variables:

    * ADMIN_SITE_SECRET
    * RECAPTCHA_SITE_SECRET
    * SECURITY_OAUTH2_CLIENT_CLIENT_SECRET
    * SPRING_SECURITY_USER_PASSWORD # for iFinder Core access 
    * GITHUB_PUBLIC_ACCESS_TOKEN="your long living GitHub token"
    * DEV_SKIP_FLAG="true" # skip some oAuth2 checks in integration tests to enable additional test scenarios   

# OSS community contributions are appreciated 🤓☺️ 

* **WordPress Connector** wanted
    * ...to index exported WordPress dumps (XML) via Site Search REST API  
* **Google Drive Connector** wanted
    * ...to find things better in this new kind of "share"

# Operations

## Run 
    ./gradlew build -x test # just upon initial execution 
    ./run.sh
    http://localhost:8001
    
## Test
    ./test.sh

## Test throughput
    ./load-test.sh

## Build browser clients
    ./gradlew build -x test # just upon initial execution
    ./js-app-build.sh

## Release
    ./release.sh
    
## CI configuration
Required environment variables:

    * SECURITY_OAUTH2_CLIENT_CLIENT_SECRET
    * BUILD_NUMBER="extracted from CI"
    * GITHUB_PUBLIC_ACCESS_TOKEN="your long living GitHub token"
    * DEV_SKIP_FLAG="true" # skip some oAuth2 checks in integration tests to enable additional test scenarios
    * SCM_HASH="Git commit hash, extracted from CI"
    * SPRING_SECURITY_USER_PASSWORD="Basic Auth password" # for iFinder Core access   
    
# Attribution
* Made with ♥, Skill & Power in Munich
