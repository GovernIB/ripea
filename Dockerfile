# 📦 Imatge base: JBoss EAP 7.2 (personalitzada per CAIB)
FROM 280421/jboss-eap-caib-7.2

# 👤 Usuari JBoss
USER jboss

# 📁 Directori de treball de JBoss
WORKDIR /home/jboss

# Variables d'entorn opcional per a depuració o versionat
ARG VITE_APP_VERSION
ENV JAVA_OPTS="-Xms512m -Xmx2048m"

# 📂 Copiar fitxers de configuració
COPY ripea-ear/src/main/docker/jboss /home/jboss/config

# Propietats de l'aplicació
COPY ripea-ear/src/main/docker/properties /home/jboss/apps

# Keystores i certificats
COPY ripea-ear/src/main/docker/keystores /home/jboss/keystores

# Transformers (si n'hi ha)
COPY ripea-ear/src/main/docker/transformers /opt/transformers

# 📦 Copiar EAR compilat
COPY ripea-ear/target/ripea.ear $JBOSS_HOME/standalone/deployments/

# 🔨 Permisos i scripts
RUN chmod 755 /opt/webapps/ripea \
    && chmod a+x /home/jboss/config/jboss_config.sh \
    && dos2unix /home/jboss/config/jboss_config.sh \
    && sed -i 's~#!/bin/sh~#!/bin/sh\n$JBOSS_USERHOME/config/jboss_config.sh~' $JBOSS_HOME/bin/standalone.sh

# 🔌 Ports exposats
EXPOSE 8080 8787

# 🚀 Comandament d'inici
CMD ["$JBOSS_HOME/bin/standalone.sh", "-b", "0.0.0.0"]
