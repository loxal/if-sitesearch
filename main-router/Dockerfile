FROM nginx:stable

MAINTAINER IntraFind Software AG <alexander.orlov@intrafind.de>

RUN apt-get update
RUN apt-get -y install unzip vim

#RUN mkdir -p /etc/letsencrypt/.well-known # enables Letsencrypt validation
COPY letsencrypt /etc/letsencrypt
COPY ssl.nginx /etc/nginx/snippets/ssl.conf
COPY cors-headers.nginx /etc/nginx/snippets/cors-headers.conf
COPY default.nginx /etc/nginx/nginx.conf
RUN echo 'sitesearch:$apr1$yiWHOvCy$YVz/vNHOKrH9LJGEglMOg.' > /etc/nginx/.htpasswd
RUN echo 'service:$2y$09$Tf3OrS.R4DNRg7OTH3x2pOC2ErJLRXzxRIjeTsbWoHfDxRtodmLUy' >> /etc/nginx/.htpasswd
RUN echo 'datev:$apr1$chP2o9Mf$gUd3cS8oRsgerkE03VKTg0' >> /etc/nginx/.htpasswd-datev
RUN echo 'bfst:$apr1$j.RVkqjh$DN/lCnp0LayLGxWXTURTb.' >> /etc/nginx/.htpasswd-bfst

EXPOSE 443