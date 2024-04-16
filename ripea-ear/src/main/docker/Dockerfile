FROM 280421/jb5caib
ARG APP_NAME=ripea
ENV FITXER_PROPIETATS="/opt/webapps/ripea/conf/ripea.properties"
ENV javax.net.ssl.trustStore="/opt/webapps/ripea/conf/truststore.jks"
ENV javax.net.ssl.trustStorePassword="tecnologies"
WORKDIR /opt/

COPY --chown=default:default conf/run.conf /opt/jboss-eap-5.2-caib/bin/
COPY --chown=default:default conf/seycon-ds.xml /opt/jboss-eap-5.2-caib/server/default/deploy/
ADD --chown=default:default conf/truststore.jks /opt/webapps/ripea/conf/
ADD --chown=default:default deploycaib/ /opt/jboss-eap-5.2-caib/server/default/deploycaib/
#ADD --chown=default:default lib/ /opt/jboss-eap-5.2-caib/server/default/lib/
ADD --chown=default:default conf/docker-entrypoint.sh /opt/jboss-eap-5.2-caib/tools/
RUN chmod +x /opt/jboss-eap-5.2-caib/tools/docker-entrypoint.sh
ADD --chown=default:default conf/afirma /opt/webapps/ripea/conf/afirma/

USER default
COPY --chown=default:default maven/${project.build.finalName}.ear /opt/jboss/server/default/deploycaib/${project.build.finalName}.ear
WORKDIR /opt/jboss
ENTRYPOINT ["/bin/sh","/opt/jboss-eap-5.2-caib/tools/docker-entrypoint.sh"]
