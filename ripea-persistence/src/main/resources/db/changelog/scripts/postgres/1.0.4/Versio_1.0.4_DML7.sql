UPDATE ipa_config SET description='Habilita l''opció de menú de publicar document', type_code='BOOL'
WHERE key LIKE '%.creacio.documents.publicar.activa';

UPDATE ipa_config SET description='Activar firma biomètrica', type_code='BOOL'
WHERE key LIKE '%.firma.biometrica.activa';

UPDATE ipa_config SET description='Serie documental del contenidor dels annexos de Distribució'
WHERE key LIKE '%.anotacions.registre.expedient.serie.documental';

UPDATE ipa_config SET description='Interval de temps (ms) en que s’executa la tasca periòdica de consultar i guardar anotacions per peticions pedents de creacio del expedients'
WHERE key LIKE '%.tasca.consulta.anotacio.temps.espera.execucio';

UPDATE ipa_config SET description='Interval de temps (ms) en que s’executa la tasca periòdica que comprova les execucions massives.'
WHERE key LIKE '%.segonpla.massives.periode.comprovacio';

UPDATE ipa_config SET description='Interval de temps (ms) en que s’executa la tasca periodica de guardar documents i expedient pendents en arxiu'
WHERE key LIKE '%.segonpla.guardar.arxiu.continguts.pendents';

UPDATE ipa_config SET description='Interval de temps (ms) en que s’executa la tasca periodica de guardar interessats pendents en arxiu'
WHERE key LIKE '%.segonpla.guardar.arxiu.interessats';

UPDATE ipa_config SET description='Url de l’arxiu'
WHERE key LIKE '%.plugin.arxiu.caib.base.url';

UPDATE ipa_config SET description='Usuari de l’arxiu'
WHERE key LIKE '%.plugin.arxiu.caib.usuari';

UPDATE ipa_config SET description='Contrasenya de l’arxiu'
WHERE key LIKE '%.plugin.arxiu.caib.contrasenya';

UPDATE ipa_config SET description='Codi de l’aplicació'
WHERE key LIKE '%.plugin.arxiu.caib.aplicacio.codi';

UPDATE ipa_config SET description='Nom d’usuari de DigitalIB'
WHERE key LIKE '%.plugin.digitalitzacio.digitalib.username';

UPDATE ipa_config SET description='Codi de l’aplicació (informar si la propietat anterior es false)'
WHERE key LIKE '%.plugin.viafirma.caib.group.codi';

UPDATE ipa_config SET description='Nom d’usuari per a la firma de servidor emprant PortaFIB'
WHERE key LIKE '%.plugin.firmaservidor.portafib.username';

UPDATE ipa_config SET description='Color del fons per defecte de l’aplicació'
WHERE key LIKE '%.capsalera.color.fons';

UPDATE ipa_config SET description='Mostrar camps adicionals a l’índex de l’expedient'
WHERE key LIKE '%.index.expedient.camps.addicionals';

UPDATE ipa_config SET description='Obtenir informació de firma utilitzant les metadades del document'
WHERE key LIKE '%.obtenir.data.firma.atributs.document';

UPDATE ipa_config SET description='Separador del número d’expedient'
WHERE key LIKE '%.numero.expedient.separador';

UPDATE ipa_config SET description='Propagar el número d’expedient a l’arxiu'
WHERE key LIKE '%.numero.expedient.propagar.arxiu';