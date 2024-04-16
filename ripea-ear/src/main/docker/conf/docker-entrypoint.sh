#!/bin/bash

#################################################################
# Crear fitxer de propietats a partir de les variables d'entorn #
#################################################################
echo "Creant fitxer de propietats"

if [[ ! -e $FITXER_PROPIETATS ]]; then
    mkdir -p "$(dirname "$FITXER_PROPIETATS")" && touch "$FITXER_PROPIETATS"
fi

echo "Copiant variables entorn al fitxer de propietats"

printenv | grep "^es.caib.ripea" > $FITXER_PROPIETATS

echo "Fet!"

###################
# Start Jboss 5.2 #
###################
echo "Iniciant el jboss"

exec /opt/jboss-eap-5.2-caib/bin/run.sh -b 0.0.0.0
