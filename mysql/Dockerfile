FROM mysql:5.7
MAINTAINER Lotfi EL KORCHI <elkolotfi@gmail.com>

ADD ./dump/ /
ADD init.sh /

RUN \
 chmod +x /init.sh && \
 apt-get update && apt-get dist-upgrade -y --force-yes && apt-get install dos2unix && \
 apt-get install psmisc -y -q && \
 apt-get autoremove -y && apt-get clean && \
 rm -rf /var/cache/* && rm -rf /var/lib/apt/lists/* && \
 dos2unix -n /init.sh /docker-entrypoint-initdb.d/initx.sh && \
 chmod +x /docker-entrypoint-initdb.d/initx.sh
