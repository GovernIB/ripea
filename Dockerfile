# Imatge base equivalent a la configurada al docker-maven-plugin.
ARG BASE_IMAGE=quaypre.caib.es/caib/jboss:eap72-openjdk11-rhel8
FROM ${BASE_IMAGE}

# El plugin construeix la imatge sobre JBoss EAP; feim servir el mateix directori de treball.
WORKDIR /home/jboss

# Necessitam permisos elevats per copiar artefactes al JBoss base i executar les passes d'instal·lació.
USER root

# Copiam la configuració principal del servidor
COPY ripea-ear/src/main/docker/jboss/standalone-openshift.xml \
    $JBOSS_HOME/standalone/configuration/standalone-openshift.xml

# Copiam els transformers de RIPEA
COPY ripea-ear/src/main/docker/transformers/ $JBOSS_HOME/apps/ripea/

# Copiam el mòdul Oracle JDBC
COPY ripea-ear/src/main/docker/oracle/ \
    $JBOSS_HOME/modules/system/layers/base/com/oracle/main/

# Copiam l'EAR generat per Maven al directori de desplegament de JBoss.
COPY ripea-ear/target/ripea.ear $JBOSS_HOME/standalone/deployments/

# Reproduïm els runCmds del docker-maven-plugin:
# 1) instal·lar l'adaptador Keycloak,
# 2) aplicar-lo offline sobre standalone-openshift.xml,
# 3) preparar permisos i directoris d'execució.
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
    && mkdir -p /opt/webapps/ripea \
    && chmod -R g=u /opt/webapps/ripea

# Tornam a l'usuari d'execució del contenidor després de preparar la imatge.
USER jboss

# Recuperam el directori de treball habitual de JBoss per a l'arrencada del contenidor.
WORKDIR /home/jboss

# Exposam el port HTTP i el port de debug definit també al plugin.
EXPOSE 8080 8787

# Arrencam JBoss en mode standalone escoltant a totes les interfícies.
CMD ["sh", "-c", "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0"]
