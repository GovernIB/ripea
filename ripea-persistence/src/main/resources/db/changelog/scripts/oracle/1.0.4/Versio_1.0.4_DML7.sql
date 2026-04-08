UPDATE IPA_CONFIG SET DESCRIPTION='Habilita l''opció de menú de publicar document', TYPE_CODE='BOOL' WHERE KEY LIKE '%.creacio.documents.publicar.activa';
UPDATE IPA_CONFIG SET DESCRIPTION='Activar firma biomètrica', TYPE_CODE='BOOL' WHERE KEY LIKE '%.firma.biometrica.activa';

UPDATE IPA_CONFIG SET DESCRIPTION='Serie documental del contenidor dels annexos de Distribució' WHERE KEY LIKE '%.anotacions.registre.expedient.serie.documental';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s''executa la tasca periòdica de consultar i guardar anotacions per peticions pedents de creacio dels expedients' WHERE KEY LIKE '%.tasca.consulta.anotacio.temps.espera.execucio';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s''executa la tasca periòdica que comprova les execucions massives.' WHERE KEY LIKE '%.segonpla.massives.periode.comprovacio';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s''executa la tasca periodica de guardar documents i expedient pendents en arxiu' WHERE KEY LIKE '%.segonpla.guardar.arxiu.continguts.pendents';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que s''executa la tasca periodica de guardar interessats pendents en arxiu' WHERE KEY LIKE '%.segonpla.guardar.arxiu.interessats';
UPDATE IPA_CONFIG SET DESCRIPTION='Url de l''arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.base.url';
UPDATE IPA_CONFIG SET DESCRIPTION='Usuari de l''arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.usuari';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya de l''arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.contrasenya';
UPDATE IPA_CONFIG SET DESCRIPTION='Codi de l''aplicació' WHERE KEY LIKE '%.plugin.arxiu.caib.aplicacio.codi';
UPDATE IPA_CONFIG SET DESCRIPTION='Nom d''usuari de DigitalIB' WHERE KEY LIKE '%.plugin.digitalitzacio.digitalib.username';
UPDATE IPA_CONFIG SET DESCRIPTION='Codi de l''aplicació (informar si la propietat anterior es false)' WHERE KEY LIKE '%.plugin.viafirma.caib.group.codi';
UPDATE IPA_CONFIG SET DESCRIPTION='Nom d''usuari per a la firma de servidor emprant PortaFIB' WHERE KEY LIKE '%.plugin.firmaservidor.portafib.username';
UPDATE IPA_CONFIG SET DESCRIPTION='Color del fons per defecte de l''aplicació' WHERE KEY LIKE '%.capsalera.color.fons';
UPDATE IPA_CONFIG SET DESCRIPTION='Mostrar camps adicionals a l''índex de l''expedient' WHERE KEY LIKE '%.index.expedient.camps.addicionals';
UPDATE IPA_CONFIG SET DESCRIPTION='Obtenir informació de firma utilitzant les metadades del document' WHERE KEY LIKE '%.obtenir.data.firma.atributs.document';
UPDATE IPA_CONFIG SET DESCRIPTION='Separador del número d''expedient' WHERE KEY LIKE '%.numero.expedient.separador';
UPDATE IPA_CONFIG SET DESCRIPTION='Propagar el número d''expedient a l''arxiu' WHERE KEY LIKE '%.numero.expedient.propagar.arxiu';

UPDATE IPA_CONFIG SET DESCRIPTION='Maxim temps en minuts que pot tardar el procés de  generar l''arxiu zip que conté els documents dels expedients seleccionats' WHERE KEY LIKE '%arxiu.maxTempsExec';
UPDATE IPA_CONFIG SET DESCRIPTION='Maxim tamany en Mb que pot ocupar l''arxiu zip que conté els documents dels expedients seleccionats' WHERE KEY LIKE '%segonpla.arxiu.maxMb';

UPDATE IPA_CONFIG_GROUP SET DESCRIPTION='Configuració del plugin de Resums' WHERE CODE = 'SUMMARIZE';

UPDATE IPA_CONFIG SET DESCRIPTION='Usuari d''accés al servei si escau' WHERE KEY LIKE '%plugin.summarize.usuari';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya d''accés al servei si escau' WHERE KEY LIKE '%plugin.summarize.password';
UPDATE IPA_CONFIG SET DESCRIPTION='ApiKey d''accés al servei de resum GPT' WHERE KEY LIKE '%summarize.gpt.apiKey';
UPDATE IPA_CONFIG SET DESCRIPTION='Timeout de connexió amb el servei de resum' WHERE KEY LIKE '%summarize.service.timeout';
UPDATE IPA_CONFIG SET DESCRIPTION='Perí­ode d''actualització del contador d''anotacions pendents en segons' WHERE KEY LIKE '%contador.anotacions.pendents';

UPDATE IPA_CONFIG SET DESCRIPTION='Guardar annexos de les anotacions en FileSystem (instal·lació de Ripea i Distribució en servidors separats)' WHERE KEY LIKE '%anotacions.annexos.save';

UPDATE IPA_CONFIG SET DESCRIPTION='Remitent dels correus electrònics (correu electrònic)' WHERE KEY LIKE '%email.remitent';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en que executa la tasca periòdica de reintentar canviar estat d''anotacions a Distribució' WHERE KEY LIKE '%reintentar.anotacions.pendents.enviar.distribucio';
UPDATE IPA_CONFIG SET DESCRIPTION='Expressió cron per indicar quan executar la tasca periòdica per enviar els correus electrònics pendents agrupats.' WHERE KEY LIKE '%enviament.agrupat.cron';
UPDATE IPA_CONFIG SET DESCRIPTION='Nombre de reintents per canviar l''estat d''anotacions a Distribució' WHERE KEY LIKE '%max.reintents.anotacions.pendents.enviar.distribucio';
