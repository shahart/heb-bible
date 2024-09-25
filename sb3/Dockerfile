FROM tomcat:10.1.28-jdk21
#gradle
#COPY build/libs/heb-bible-0.0.1-SNAPSHOT-plain.war /usr/local/tomcat/webapps/ROOT.war
#maven
COPY target/hebbible-0.0.1-SNAPSHOT.war  /usr/local/tomcat/webapps/hebBible.war
#ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]

#FROM jetty:jdk21
#COPY target/hebbible-0.0.1-SNAPSHOT.war /var/lib/jetty/webapps/root.war
#EXPOSE 8080
#CMD java -jar "$JETTY_HOME/start.jar"
