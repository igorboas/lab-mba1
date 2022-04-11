FROM quay.io/quarkus/quarkus-micro-image:1.0
WORKDIR /work/

COPY target/*-runner /work/application/
COPY target/rest-json-quickstart-1.0.0-SNAPSHOT.jar /work/application/
COPY startup.sh /work/application/

RUN ls /work/application/
RUN chmod 755 /work/application/startup.sh
RUN chmod 775 /work
EXPOSE 8087
CMD ["./startup.sh"]
