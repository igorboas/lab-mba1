#FROM quay.io/quarkus/quarkus-micro-image:1.0
FROM openjdk:11.0.13-jdk
#openjdk:11 
#registry.access.redhat.com/ubi8/openjdk-11:latest
WORKDIR /work/

COPY target/*-runner /work/application
COPY --chown=1001 target/quarkus-app/lib/ /work/lib/
COPY --chown=1001 target/quarkus-app/app/ /work/app/
COPY --chown=1001 target/quarkus-app/quarkus/ /work/quarkus/
COPY target/quarkus-app/quarkus-run.jar /work/application.jar
COPY startup.sh /work/startup.sh

RUN ls /work/
#RUN chmod 755 /work/startup.sh
#RUN chmod 775 /work
EXPOSE 8087
CMD ["bash", "startup.sh"]
