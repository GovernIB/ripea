#!/bin/sh

CONFIG_STANDALONE_FILE=$JBOSS_HOME/standalone/configuration/standalone-openshift.xml
SCRIPT_DIR=`dirname -- "$0"`
DATASOURCES_FILE=$SCRIPT_DIR/datasources.xml
SYSTEM_PROPS_FILE=$SCRIPT_DIR/system-props.xml
KEYCLOAK_FILE=$SCRIPT_DIR/keycloak.xml
KEYCLOAK_SD_FILE=$SCRIPT_DIR/keycloak_security_domain.xml
LOGGING_FILE=$SCRIPT_DIR/logging.xml
MAIL_FILE=$SCRIPT_DIR/mail.xml
TEMP_PROPS_FILE=$SCRIPT_DIR/jboss_properties.tmp
JBOSS_SYSTEM_PROPS_FILE=$JBOSS_HOME/apps/ripea/ripea_system.properties

# Eliminar possibles \r (CRLF) dels fitxers de configuració
# Garantiza que tanto el XML base como todos los ficheros inyectados estén en LF puro antes de cualquier operación
sed -i 's/\r//' $CONFIG_STANDALONE_FILE
sed -i 's/\r//' $DATASOURCES_FILE
sed -i 's/\r//' $SYSTEM_PROPS_FILE
sed -i 's/\r//' $KEYCLOAK_FILE
sed -i 's/\r//' $KEYCLOAK_SD_FILE
sed -i 's/\r//' $LOGGING_FILE
sed -i 's/\r//' $MAIL_FILE

if ! grep -q "<system-properties" $CONFIG_STANDALONE_FILE; then
	echo "Configuració inicial de JBoss..."
	sed '/<\/extensions>/a <system-properties\/>' -i $CONFIG_STANDALONE_FILE
#	sed '/<\/socket-binding-group>/i <socket-binding name="proxy-https" port="443"\/>' -i $CONFIG_STANDALONE_FILE
	sed '/<\/socket-binding-group>/i <outbound-socket-binding name="smtp-ripea" source-port="0" fixed-source-port="false"><remote-destination host="${env.JBOSS_MAIL_HOST}" port="${env.JBOSS_MAIL_PORT}"\/><\/outbound-socket-binding>' -i $CONFIG_STANDALONE_FILE
#	sed -i 's/<http-listener name="default" socket-binding="http" redirect-socket="https" enable-http2="true"\/>/<http-listener name="default" socket-binding="http" proxy-address-forwarding="true" redirect-socket="proxy-https"\/>/g' $CONFIG_STANDALONE_FILE
	echo "...configuració inicial de JBoss finalitzada"
fi

echo "Modificant fitxer de configuració de JBoss $CONFIG_STANDALONE_FILE..."
# Afegir extensió Keycloak si no existeix
if ! grep -q "keycloak-adapter-subsystem" $CONFIG_STANDALONE_FILE; then
    sed -i 's|</extensions>|    <extension module="org.keycloak.keycloak-adapter-subsystem"/>\n</extensions>|' $CONFIG_STANDALONE_FILE
fi
# Afegir subsistema Keycloak si no existeix
if ! grep -q "urn:jboss:domain:keycloak:1.1" $CONFIG_STANDALONE_FILE; then
    sed -i 's|</profile>|KEYCLOAK_PLACEHOLDER\n</profile>|' $CONFIG_STANDALONE_FILE
    sed -i "/KEYCLOAK_PLACEHOLDER/{
        r $KEYCLOAK_FILE
        d
    }" $CONFIG_STANDALONE_FILE
fi
# Afegir security-domain keycloak si no existeix (nomes dins el block security-domains)
if ! grep -q "KeycloakLoginModule" $CONFIG_STANDALONE_FILE; then
    sed -i '/subsystem xmlns="urn:jboss:domain:security:2.0"/,/\/subsystem/{
        /<\/security-domains>/{ r '"$KEYCLOAK_SD_FILE"'
d }
    }' $CONFIG_STANDALONE_FILE
fi
sed -i '/<system-properties>/,/<\/system-properties>/c \<system-properties\/>' $CONFIG_STANDALONE_FILE
sed -e '/<system-properties\/>/ {' -e "r $SYSTEM_PROPS_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:datasources:5.0">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:datasources:5.0"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:datasources:5.0"\/>/ {' -e "r $DATASOURCES_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i 's/<subsystem xmlns="urn:jboss:domain:keycloak:1.1"\/>/<subsystem xmlns="urn:jboss:domain:keycloak:1.1"><\/subsystem>/' $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:keycloak:1.1">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:keycloak:1.1"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:keycloak:1.1"\/>/ {' -e "r $KEYCLOAK_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:logging:6.0">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:logging:6.0"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:logging:6.0"\/>/ {' -e "r $LOGGING_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
sed -i '/<subsystem xmlns="urn:jboss:domain:mail:3.0">/,/<\/subsystem>/c \<subsystem xmlns="urn:jboss:domain:mail:3.0"\/>' $CONFIG_STANDALONE_FILE
sed -e '/<subsystem xmlns="urn:jboss:domain:mail:3.0"\/>/ {' -e "r $MAIL_FILE" -e 'd' -e '}' -i $CONFIG_STANDALONE_FILE
echo "...fitxer de configuració de JBoss modificat"

echo "Modificant fitxers de properties per incorporar variables d'entorn..."
awk -F '=' 'NF {if (ENVIRON[$1]) {print $1 "=" ENVIRON[$1]} else {print $1 "=" $2}}' $JBOSS_SYSTEM_PROPS_FILE > $TEMP_PROPS_FILE && mv $TEMP_PROPS_FILE $JBOSS_SYSTEM_PROPS_FILE
echo "...fitxers de properties modificats"