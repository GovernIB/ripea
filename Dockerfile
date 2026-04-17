ARG BASE_IMAGE=${{ secrets.URL_REGISTRY }}/${{ secrets.ORGANIZATION }}/jboss:eap72-openjdk11-rhel8
FROM ${BASE_IMAGE}

WORKDIR /home/jboss
USER root

COPY ripea-ear/src/main/docker/jboss/standalone-openshift.xml \
    $JBOSS_HOME/standalone/configuration/standalone-openshift.xml

COPY ripea-ear/src/main/docker/transformers/ \
    $JBOSS_HOME/apps/ripea/

COPY ripea-ear/src/main/docker/oracle/ \
    $JBOSS_HOME/modules/system/layers/base/com/oracle/main/

COPY ripea-ear/target/ripea.ear \
    $JBOSS_HOME/standalone/deployments/

RUN curl -fL https://github.com/keycloak/keycloak/releases/download/12.0.4/keycloak-oidc-wildfly-adapter-12.0.4.tar.gz \
        -o $JBOSS_HOME/keycloak-adapter.tar.gz \
    && tar xzf $JBOSS_HOME/keycloak-adapter.tar.gz -C $JBOSS_HOME --no-same-owner --no-same-permissions --touch \
    && rm $JBOSS_HOME/keycloak-adapter.tar.gz \
    && $JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/bin/adapter-install-offline.cli \
        -Djboss.server.config.file=standalone-openshift.xml \
    && rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history \
    && mkdir -p $JBOSS_HOME/standalone/configuration/standalone_xml_history/current \
    && mkdir -p $JBOSS_HOME/standalone/data/content \
    && mkdir -p $JBOSS_HOME/standalone/tmp \
    && mkdir -p $JBOSS_HOME/standalone/log \
    && chmod -R g=u $JBOSS_HOME/standalone/data \
    && chmod -R g=u $JBOSS_HOME/standalone/tmp \
    && chmod -R g=u $JBOSS_HOME/standalone/log \
    && chmod -R g=u $JBOSS_HOME/standalone/configuration/standalone_xml_history \
    && test ! -d $JBOSS_HOME/config || chmod -R g=u $JBOSS_HOME/config \
    && mkdir -p /opt/eap/apps/ripea \
    && chmod -R g=u /opt/eap/apps/ripea \
    && mkdir -p /opt/webapps/ripea \
    && chmod -R g=u /opt/webapps/ripea

USER jboss
WORKDIR /home/jboss

EXPOSE 8080 8787

ENV JAVA_OPTS_APPEND=""

CMD ["sh", "-c", "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 $JAVA_OPTS_APPEND"]
