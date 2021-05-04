FROM 280421/jb5caib
ENV DATABASE_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV DATABASE_USERNAME ripeadk
ENV DATABASE_PASSWORD ripeadk
ENV SEYCON_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV SEYCON_USERNAME seycon
ENV SEYCON_PASSWORD seycon
ENV PORTAFIB_URL https://proves.caib.es/portafib
ENV PORTAFIB_USERNAME \$ripea_portafib
ENV PORTAFIB_PASSWORD ripea_portafib
ENV ARXIU_URL https:\/\/afirmades.caib.es:4430/esb
ENV ARXIU_USERNAME app1
ENV ARXIU_PASSWORD app1
ENV CONCSV_URL http://dev.caib.es/concsv
ENV CONCSV_USERNAME \$concsv_ripea
ENV CONCSV_PASSWORD concsv_ripea
ENV DIR3CAIB_URL https://proves.caib.es/dir3caib
ENV DIR3CAIB_USERNAME \$ripea_dir3caib
ENV DIR3CAIB_PASSWORD ripea_dir3caib
ENV AFIRMA_URL https://afirmades.caib.es:4430/afirmaws
ENV AFIRMA_APPID CAIBDEV.RIPEA
ENV AFIRMA_USERNAME RIPEA
ENV AFIRMA_PASSWORD RIPEA
ENV NOTIB_URL https://proves.caib.es/notib
ENV NOTIB_USERNAME \$ripea_notib
ENV NOTIB_PASSWORD ripea_notib
EXPOSE 8080
EXPOSE 8787
ADD --chown=default:default deploycaib/ /opt/jboss/server/default/deploycaib/
ADD --chown=default:default lib/ /opt/jboss/server/default/lib/
ADD --chown=default:default config/ /opt/webapps/
RUN chmod +x /opt/webapps/env_init.sh
COPY --chown=default:default maven/${project.build.finalName}.ear /opt/jboss/server/default/deploycaib/${project.build.finalName}.ear
WORKDIR /opt/jboss
USER default
ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh" ]
