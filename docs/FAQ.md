FAQ
=

# Site Search Service

* What is Site Search?
    * Site Search is an on-demand SaaS offering from IntraFind Software AG to enable website operators 
    to provide search functionality for their websites.

* What ist the **advantage of using Site Search** vs using search backends like *Elasticsearch* directly? -
    * Site Search removes Elasticsearch as your direct dependency when introducing a search to a website. 
    In fact Site Search does not only rely on Elasticsearch but on additional components 
    from *IntraFind AG* that **improve the quality of search results**. Also Site Search is a **managed service** that 
    removes the burden of operations from your organization. Site Search provides you with a simple API
    that is **tailored** for the purpose to provide a website search with all its specifics. 

* What is the difference between **sites** and **pages**?
    * A **site** is a *website* that can contain thousands of **pages**. 
    You may have two **sites**: *en.example.com* and *de.example.com* 
    and each may contain thousands of pages in the respective language.

* What is a **tenant**?
    * A tenant is an **organizational entity** that can manage **dozens of independent sites**.
    You may have a company that is assigned to a tenant.

* What is Site Search' primary API endpoint URL?
    * https://api.sitesearch.cloud should be used for all Site Search API calls.
    
* How can I index a website and make it searchable?
  * Currently a website needs to expose an **RSS / Atom feed** to make it easily searchable.
  This is the fastest & easiest way to get up and running. Use this ***curl command***
  
        curl -X POST https://api.sitesearch.cloud/sites/rss?feedUrl=https%3A%2F%2Fintrafind.de%2Fshare%2Fenterprise-search-blog.xml
  
  ...to [call our API accordingly](https://api.sitesearch.cloud/swagger-ui.html#!/site45controller/indexNewRssFeedUsingPOST). 
  Still you can make a website searchable that does not expose any feeds by using our API and indexing a website page by page, 
  cf. "How can I index a page and make it searchable?".  
  
* How can I **create a new site** to add pages to this site and make them searchable? 
Or what should be the first thing, I need to do, to **get started** with Site Search?
    * After executing this ***curl command***, you will also obtain the *siteId* and *siteSecret* of the newly created site. 

            curl -v -X POST https://api.sitesearch.cloud/sites

* How can I **index a page**, within an **already existing site**, and make it searchable?
    * When a page with the provided URL already exists, the page will be updated with the given title & body.
    Execute this curl command with a *siteSecret* that corresponds to the provided *siteId*:
    
            curl -v -X PUT \
              'https://api.sitesearch.cloud/sites/22d7cbe6-78c3-4f54-ba40-79b881cbe568/pages?siteSecret=8a12c8db-7d49-4f03-b2e5-03870b4e1e48' \
              -H 'content-type: application/json' \
              -d '{
                "title": "Test Page",
                "body": "Test Content",
                "url": "https://example.com/page"
            }'

* How can I **update** an already indexed page?
    * When updating a page the URL in the payload assures that only the page with the provided URL is updated with a new tile and content.
    If the page with the provided URL does not exist in a site's index, the page is automatically added to the site index.
    Execute this curl command with a *siteSecret* that corresponds to the provided *siteId*:
    
            curl -v -X PUT \
              'https://api.sitesearch.cloud/sites/22d7cbe6-78c3-4f54-ba40-79b881cbe568/pages?siteSecret=8a12c8db-7d49-4f03-b2e5-03870b4e1e48' \
              -H 'content-type: application/json' \
              -d '{
                "title": "Test Page",
                "body": "Test Content",
                "url": "https://example.com/page"
            }'

* How can I **delete** an indexed page?
    * You need to provide a **siteSecret** that corresponds to the provided **siteId**,
    as well as a page's primary ID.
    
            curl -v -X DELETE \
              https://api.sitesearch.cloud/sites/d87fdcef-a84d-462d-ba84-61df9805536a/pages/0c42f3ef01536bd29510d7b4d178fc7e6cbc1d26095ac3a759bf638f80bfa3c9?siteSecret=b554a1e7-3e87-44ab-b353-f1fd8a423bbe 

* What is a **site secret**?
    * A site secret is a token that you should keep confidential and not publish it to any public repository. 
    The site secret enables you to **add new pages and update existing pages** within a site. 
    You need to provide a *siteSecret* as a query parameter to authorize operations that modify the index of a site.

* How can I make my website searchable?
    * The easiest & quickest way is to use the [Site Search Gadget](https://api.sitesearch.cloud/sitesearch-gadget.html)
    to **create an index** from an RSS / Atom feed and make it **instantly searchable** without even embedding it into a website.
    If your website does not expose any feeds, you need to use our open [API](https://api.sitesearch.cloud/swagger-ui.html#!/page45controller/indexNewSiteUsingPOST)
    to **index your website page by page** calling the API.
    
* Where can I find an API documentation and specification for Site Search?
    * [Site Search' API](https://api.sitesearch.cloud/swagger-ui.html) ist technically documented according 
    to the OpenAPI 3.0 specification, using Swagger. Not only, you can lookup the required schemas 
    but you can also, use the API right from your browser. 

* What does Site Search cost?
    * Currently we have no fixed plans as Site Search is still in BETA. 
    As soon as Site Search meets all our production requirements, we will offer a **free basic plan** 
    and additional **enterprise offerings**.  

* I do not want to use Site Search anymore. Do I have to delete my data?
    * You do not have to do anything. We will automatically delete your indexed data after a certain time of inactivity.
    We would appreciate to [learn the reasons](mailto:feedback@sitesearch.cloud) behind your decision to stop using our service. 
    
* After using the *Site Search Gadget* I get search results that do not correspond to the specified *siteId*.
    * Delete your browser's cookies for *api.sitesearch.cloud* and specifically the "override-site" cookie.
     
* Who is using Site Search?
    * The following website's search is powered by Site Search
        * [IntraFind Software AG](https://www.intrafind.de)
        * intrafind.com [coming soon]
        * analyzelaw.com [coming soon]
   
# Crawler 

* Does the crawler support **dynamic, JavaScript generated content**?
    * Like most (all?) other crawlers, the Site Search crawler cannot extract runtime-generated content.
    A common practice is to provide the crawler with **HTML snapshots** of the page content you want to be crawled.  
    
* Can the crawler access sites or pages that require **authentication**, e.g. Basic Auth protected sites?
    * **Not yet**, but if this feature is frequently requested, we might consider to implement this functionality.
        
# Search Bar

* Do I need the **search bar** to use Site Search?
    * No, not necessarily; the **search bar is a sample implementation** to quickly show you how 
    a search bar may look like and interact with the *Site Search service*.
    Still, the **search bar** is a production-grade implementation and ready for production deployments.

* Can I use my own search bar with Site Search?
    * Yes, you can use any search bar, written in any language that also runs on native platforms as long as it follows
    [Site Search' API specification](https://api.sitesearch.cloud/swagger-ui.html).

* Can I *customize* the layout and/or design of the search bar?   
    * Yes, you can *fully customize the appearance of the search bar* using HBS templates.
    * Alternative you can customize the search bar overriding its CSS classes which is appropriate for minor customizations only.

* How do I integrate Site Search' search bar into my website?
    * Embed the following HTML fragment into your website and **adjust the siteId** provided in the snippet:
    
        
            <link rel="stylesheet" href="https://cdn.sitesearch.cloud/searchbar/latest/app/css/app.css"/>
            <div id="searchbarContainer">
                <div class="container" style="width: 530px;">
                    <div id="searchbar"></div>
                    <div id="resultlist"></div>
                </div>
            
                <script src="https://cdn.sitesearch.cloud/searchbar/latest/app/js/app.js"></script>
                <script>
                    jQuery.noConflict();
                    jQuery(document).ready(function ($) {
                        IFS.initClient({
                            customConfig: {
                                overwrite: {
                                    "appLang": "de"
                                }
                            },
                            sbTarget: "#searchbar",
                            configurl: "https://cdn.sitesearch.cloud/searchbar/latest/config/sitesearch.json",
                            sitesearch: true,
                            siteId: "4bcccea2-8bcf-4280-88c7-8736e9c3d15c"
                        });
                    });
                </script>
            </div>  

* I want to change the *tooltips* provided by the search bar. Does the search bar support **i18n / l10n**?
    * Yes, you need to download the [search bar](https://api.sitesearch.cloud/integration_artifacts/searchbar.zip), adjust the translations inside `searchbar/data/language/` and replace 
    `https://api.sitesearch.cloud` inside the [configuration folder](https://api.sitesearch.cloud/integration_artifacts/searchbar-config.zip) with your new location. 
    
* How can I override search bar's *default language selection mechanism*?
    * The below example specifies German as search bar's user interface language. 
    
            <script>
                jQuery.noConflict();
                jQuery(document).ready(function ($) {
                    IFS.initClient({
                        customConfig: {
                            overwrite: {
                                "appLang": "de"
                            }
                        },
                        sbTarget: "#searchbar",
                        configurl: "https://cdn.sitesearch.cloud/searchbar/latest/config/sitesearch.json",
                        sitesearch: true,
                        siteId: "4bcccea2-8bcf-4280-88c7-8736e9c3d15c"
                    });
                });
            </script>
    
