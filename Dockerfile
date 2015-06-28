FROM tomcat:8.0
MAINTAINER sqshq

# Add WAR to webapps
RUN rm -rf /usr/local/tomcat/webapps/ROOT
ADD target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

WORKDIR /usr/local/tomcat/

EXPOSE 8080