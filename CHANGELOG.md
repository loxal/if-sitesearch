Change Log
=

# 29-11-2017
* **Zero-downtime deployment** enables us to deploy new releases of Site Search several times a day, without a single second downtime.
* We are evaluating the option to offer our crawler as a self-service. 
    This would enable our customers to crawl sites directly and evaluate results without the need to contact us. 

# 22-11-2017
* Log monitoring with ELK (Elasticsearch, Logstash, Kibana) for insights into the usage of Site Search
* API simplifications without breaking any clients
* Introduced GZIP compression for higher throughput and faster responses  
* Split FAQ into two parts, a search bar related part and a service related part
* Reduce search bar's log noise in browser console 

# 07-11-2017
* improved **monitoring resilience**, i.e. monitoring configurations in physically separated locations
* the new, reworked **product frontpage** went live 

# 20-10-2017
* Search bar updated
    * improved layout & styling isolation for both the search bar itself and the website where its embedded
* CORS header monitoring to further improve quality of service
* Content feeds can be stripped of HTML before being added to search index 
         