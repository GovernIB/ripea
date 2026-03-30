UPDATE IPA_CONFIG SET DESCRIPTION='Habilita l''opció de menú de publicar document', TYPE_CODE='BOOL' WHERE KEY LIKE '%.creacio.documents.publicar.activa';
UPDATE IPA_CONFIG SET DESCRIPTION='Activar firma biomètrica', TYPE_CODE='BOOL' WHERE KEY LIKE '%.firma.biometrica.activa';

UPDATE IPA_CONFIG SET DESCRIPTION='Serie documental del contenidor dels annexos de Distribució' WHERE KEY LIKE '%.anotacions.registre.expedient.serie.documental';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s’executa la tasca periòdica de consultar i guardar anotacions per peticions pedents de creacio del expedients' WHERE KEY LIKE '%.tasca.consulta.anotacio.temps.espera.execucio';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s’executa la tasca periòdica que comprova les execucions massives.' WHERE KEY LIKE '%.segonpla.massives.periode.comprovacio';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s’executa la tasca periodica de guardar documents i expedient pendents en arxiu' WHERE KEY LIKE '%.segonpla.guardar.arxiu.continguts.pendents';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s’executa la tasca periodica de guardar interessats pendents en arxiu' WHERE KEY LIKE '%.segonpla.guardar.arxiu.interessats';
UPDATE IPA_CONFIG SET DESCRIPTION='Url de l’arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.base.url';
UPDATE IPA_CONFIG SET DESCRIPTION='Usuari de l’arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.usuari';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya de l’arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.contrasenya';
UPDATE IPA_CONFIG SET DESCRIPTION='Codi de l’aplicació' WHERE KEY LIKE '%.plugin.arxiu.caib.aplicacio.codi';
UPDATE IPA_CONFIG SET DESCRIPTION='Nom d’usuari de DigitalIB' WHERE KEY LIKE '%.plugin.digitalitzacio.digitalib.username';
UPDATE IPA_CONFIG SET DESCRIPTION='Codi de l’aplicació (informar si la propietat anterior es false)' WHERE KEY LIKE '%.plugin.viafirma.caib.group.codi';
UPDATE IPA_CONFIG SET DESCRIPTION='Nom d’usuari per a la firma de servidor emprant PortaFIB' WHERE KEY LIKE '%.plugin.firmaservidor.portafib.username';
UPDATE IPA_CONFIG SET DESCRIPTION='Color del fons per defecte de l’aplicació' WHERE KEY LIKE '%.capsalera.color.fons';
UPDATE IPA_CONFIG SET DESCRIPTION='Mostrar camps adicionals a l’índex de l’expedient' WHERE KEY LIKE '%.index.expedient.camps.addicionals';
UPDATE IPA_CONFIG SET DESCRIPTION='Obtenir informació firma utilitzant les metadades del document' WHERE KEY LIKE '%.obtenir.data.firma.atributs.document';