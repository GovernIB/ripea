#!/bin/sh

APP_NAME=ripea
DS_FILE=/opt/jboss/server/default/deploycaib/$APP_NAME-ds.xml
SERVICE_FILE=/opt/jboss/server/default/deploycaib/$APP_NAME-service.xml
RUN_CONF=/opt/jboss/bin/run.conf

echo "Substituint variables del datasource"
if [[ -n "$DATABASE_URL" ]]; then
	echo "Substituint DATABASE_URL per $DATABASE_URL"
	sed -i "s|DATABASE_URL|$DATABASE_URL|g" $DS_FILE
fi
if [[ -n "$DATABASE_USERNAME" ]]; then
	echo "Substituint DATABASE_USERNAME per $DATABASE_USERNAME"
	sed -i "s/DATABASE_USERNAME/$DATABASE_USERNAME/g" $DS_FILE
fi
if [[ -n "$DATABASE_PASSWORD" ]]; then
	echo "Substituint DATABASE_PASSWORD per $DATABASE_PASSWORD"
	sed -i "s/DATABASE_PASSWORD/$DATABASE_PASSWORD/g" $DS_FILE
fi

echo "Substituint variables del datasource d'usuaris"
if [[ -n "$SEYCON_URL" ]]; then
	echo "Substituint SEYCON_URL per $SEYCON_URL"
	sed -i "s|SEYCON_URL|$SEYCON_URL|g" $DS_FILE
fi
if [[ -n "$SEYCON_USERNAME" ]]; then
	echo "Substituint SEYCON_USERNAME per $SEYCON_USERNAME"
	sed -i "s/SEYCON_USERNAME/$SEYCON_USERNAME/g" $DS_FILE
fi
if [[ -n "$SEYCON_PASSWORD" ]]; then
	echo "Substituint SEYCON_PASSWORD per $SEYCON_PASSWORD"
	sed -i "s/SEYCON_PASSWORD/$SEYCON_PASSWORD/g" $DS_FILE
fi

#echo "Substituint variables de properties"
if [[ -n "$PORTAFIB_URL" ]]; then
	echo "Substituint PORTAFIB_URL per $PORTAFIB_URL"
	sed -i "s!PORTAFIB_URL!$PORTAFIB_URL!g" $SERVICE_FILE
fi
if [[ -n "$PORTAFIB_USERNAME" ]]; then
	echo "Substituint PORTAFIB_USERNAME per $PORTAFIB_URL"
	sed -i "s/PORTAFIB_USERNAME/$PORTAFIB_USERNAME/g" $SERVICE_FILE
fi
if [[ -n "$PORTAFIB_PASSWORD" ]]; then
	echo "Substituint PORTAFIB_PASSWORD per $PORTAFIB_PASSWORD"
	sed -i "s/PORTAFIB_PASSWORD/$PORTAFIB_PASSWORD/g" $SERVICE_FILE
fi
if [[ -n "$ARXIU_URL" ]]; then
	echo "Substituint ARXIU_URL per $ARXIU_URL"
	sed -i "s!ARXIU_URL!$ARXIU_URL!g" $SERVICE_FILE
fi
if [[ -n "$ARXIU_USERNAME" ]]; then
	echo "Substituint ARXIU_USERNAME per $ARXIU_USERNAME"
	sed -i "s/ARXIU_USERNAME/$ARXIU_USERNAME/g" $SERVICE_FILE
fi
if [[ -n "$ARXIU_PASSWORD" ]]; then
	echo "Substituint ARXIU_PASSWORD per $ARXIU_PASSWORD"
	sed -i "s/ARXIU_PASSWORD/$ARXIU_PASSWORD/g" $SERVICE_FILE
fi
if [[ -n "$CONCSV_URL" ]]; then
	echo "Substituint CONCSV_URL per $CONCSV_URL"
	sed -i "s!CONCSV_URL!$CONCSV_URL!g" $SERVICE_FILE
fi
if [[ -n "$CONCSV_USERNAME" ]]; then
	echo "Substituint CONCSV_USERNAME per $CONCSV_USERNAME"
	sed -i "s/CONCSV_USERNAME/$CONCSV_USERNAME/g" $SERVICE_FILE
fi
if [[ -n "$CONCSV_PASSWORD" ]]; then
	echo "Substituint CONCSV_PASSWORD per $CONCSV_PASSWORD"
	sed -i "s/CONCSV_PASSWORD/$CONCSV_PASSWORD/g" $SERVICE_FILE
fi
if [[ -n "$DIR3CAIB_URL" ]]; then
	echo "Substituint DIR3CAIB_URL per $DIR3CAIB_URL"
	sed -i "s!DIR3CAIB_URL!$DIR3CAIB_URL!g" $SERVICE_FILE
fi
if [[ -n "$DIR3CAIB_USERNAME" ]]; then
	echo "Substituint DIR3CAIB_USERNAME per $DIR3CAIB_USERNAME"
	sed -i "s/DIR3CAIB_USERNAME/$DIR3CAIB_USERNAME/g" $SERVICE_FILE
fi
if [[ -n "$DIR3CAIB_PASSWORD" ]]; then
	echo "Substituint DIR3CAIB_PASSWORD per $DIR3CAIB_PASSWORD"
	sed -i "s/DIR3CAIB_PASSWORD/$DIR3CAIB_PASSWORD/g" $SERVICE_FILE
fi
if [[ -n "$AFIRMA_URL" ]]; then
	echo "Substituint AFIRMA_URL per $AFIRMA_URL"
	sed -i "s!AFIRMA_URL!$AFIRMA_URL!g" $SERVICE_FILE
fi
if [[ -n "$AFIRMA_APPID" ]]; then
	echo "Substituint AFIRMA_APPID per $AFIRMA_APPID"
	sed -i "s/AFIRMA_APPID/$AFIRMA_APPID/g" $SERVICE_FILE
fi
if [[ -n "$AFIRMA_USERNAME" ]]; then
	echo "Substituint AFIRMA_USERNAME per $AFIRMA_USERNAME"
	sed -i "s/AFIRMA_USERNAME/$AFIRMA_USERNAME/g" $SERVICE_FILE
fi
if [[ -n "$AFIRMA_PASSWORD" ]]; then
	echo "Substituint AFIRMA_PASSWORD per $AFIRMA_PASSWORD"
	sed -i "s/AFIRMA_PASSWORD/$AFIRMA_PASSWORD/g" $SERVICE_FILE
fi
if [[ -n "$NOTIB_URL" ]]; then
	echo "Substituint NOTIB_URL per $NOTIB_URL"
	sed -i "s!NOTIB_URL!$NOTIB_URL!g" $SERVICE_FILE
fi
if [[ -n "$NOTIB_USERNAME" ]]; then
	echo "Substituint NOTIB_USERNAME per $NOTIB_USERNAME"
	sed -i "s/NOTIB_USERNAME/$NOTIB_USERNAME/g" $SERVICE_FILE
fi
if [[ -n "$NOTIB_PASSWORD" ]]; then
	echo "Substituint NOTIB_PASSWORD per $NOTIB_PASSWORD"
	sed -i "s/NOTIB_PASSWORD/$NOTIB_PASSWORD/g" $SERVICE_FILE
fi
if [[ -n "$PINBAL_URL" ]]; then
	echo "Substituint PINBAL_URL per $PINBAL"
	sed -i "s!PINBAL_URL!$PINBAL_URL!g" $SERVICE_FILE
fi
if [[ -n "$PINBAL_USERNAME" ]]; then
	echo "Substituint PINBAL_USERNAME per $PINBAL_USERNAME"
	sed -i "s/PINBAL_USERNAME/$PINBAL_USERNAME/g" $SERVICE_FILE
fi
if [[ -n "$PINBAL_PASSWORD" ]]; then
	echo "Substituint PINBAL_PASSWORD per $PINBAL_PASSWORD"
	sed -i "s/PINBAL_PASSWORD/$PINBAL_PASSWORD/g" $SERVICE_FILE
fi

echo "Configurant truststore"
echo 'JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=/opt/webapps/truststore.jks -Djavax.net.ssl.trustStorePassword=tecnologies"' >> $RUN_CONF