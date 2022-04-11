FROM quay.io/quarkus/quarkus-micro-image:1.0
WORKDIR /work/
COPY target/config/ /work/config/
COPY target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8086
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
