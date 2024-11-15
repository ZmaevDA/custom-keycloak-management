FROM quay.io/keycloak/keycloak:20.0.2

WORKDIR /opt/keycloak

ENV KEYCLOAK_USER=admin
ENV KEYCLOAK_PASSWORD=admin
ENV KC_DB=postgres

COPY /docker/plugin/*.jar /opt/keycloak/providers/

COPY /docker/script/build-pluging.sh /opt/keycloak/build-pluging.sh

USER root
RUN chmod +x /opt/keycloak/build-pluging.sh

ENTRYPOINT ["/opt/keycloak/build-pluging.sh"]

CMD ["start", "--auto-build", "--db", "postgres", "--hostname-strict-https", "false", "--hostname-strict", "false", "--proxy", "edge", "--http-enabled", "true", "--log-level=DEBUG", "--import-realm", "--spi-user-profile-legacy-user-profile-read-only-attributes", "*_RES_ACCESS_MODE"]
