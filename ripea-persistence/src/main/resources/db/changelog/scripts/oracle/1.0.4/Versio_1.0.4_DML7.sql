UPDATE IPA_CONFIG SET DESCRIPTION='Habilita l''opció de menú de publicar document', TYPE_CODE='BOOL' WHERE KEY LIKE '%.creacio.documents.publicar.activa';
UPDATE IPA_CONFIG SET DESCRIPTION='Activar firma biomètrica', TYPE_CODE='BOOL' WHERE KEY LIKE '%.firma.biometrica.activa';

UPDATE IPA_CONFIG SET DESCRIPTION='Serie documental del contenidor dels annexos de Distribució' WHERE KEY LIKE '%.anotacions.registre.expedient.serie.documental';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en què s''executa la tasca periòdica de consultar i guardar anotacions per peticions pendents de creació dels expedients' WHERE KEY LIKE '%.tasca.consulta.anotacio.temps.espera.execucio';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en què s''executa la tasca periòdica que comprova les execucions massives.' WHERE KEY LIKE '%.segonpla.massives.periode.comprovacio';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en què s''executa la tasca periòdica de guardar documents i expedients pendents a l''arxiu' WHERE KEY LIKE '%.segonpla.guardar.arxiu.continguts.pendents';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en què s''executa la tasca periòdica de guardar interessats pendents a l''arxiu' WHERE KEY LIKE '%.segonpla.guardar.arxiu.interessats';
UPDATE IPA_CONFIG SET DESCRIPTION='Url de l''arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.base.url';
UPDATE IPA_CONFIG SET DESCRIPTION='Usuari de l''arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.usuari';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya de l''arxiu' WHERE KEY LIKE '%.plugin.arxiu.caib.contrasenya';
UPDATE IPA_CONFIG SET DESCRIPTION='Codi de l''aplicació' WHERE KEY LIKE '%.plugin.arxiu.caib.aplicacio.codi';
UPDATE IPA_CONFIG SET DESCRIPTION='Nom d''usuari de DigitalIB' WHERE KEY LIKE '%.plugin.digitalitzacio.digitalib.username';
UPDATE IPA_CONFIG SET DESCRIPTION='Codi de l''aplicació (informar si la propietat anterior és falsa)' WHERE KEY LIKE '%.plugin.viafirma.caib.group.codi';
UPDATE IPA_CONFIG SET DESCRIPTION='Nom d''usuari per a la firma de servidor emprant PortaFIB' WHERE KEY LIKE '%.plugin.firmaservidor.portafib.username';
UPDATE IPA_CONFIG SET DESCRIPTION='Color del fons per defecte de l''aplicació' WHERE KEY LIKE '%.capsalera.color.fons';
UPDATE IPA_CONFIG SET DESCRIPTION='Mostrar camps adicionals a l''índex de l''expedient' WHERE KEY LIKE '%.index.expedient.camps.addicionals';
UPDATE IPA_CONFIG SET DESCRIPTION='Obtenir informació de la firma utilitzant les metadades del document' WHERE KEY LIKE '%.obtenir.data.firma.atributs.document';
UPDATE IPA_CONFIG SET DESCRIPTION='Separador del número d''expedient' WHERE KEY LIKE '%.numero.expedient.separador';
UPDATE IPA_CONFIG SET DESCRIPTION='Propagar el número d''expedient a l''arxiu' WHERE KEY LIKE '%.numero.expedient.propagar.arxiu';

UPDATE IPA_CONFIG SET DESCRIPTION='Màxim temps en minuts que pot tardar el procés de generar l''arxiu zip que conté els documents dels expedients seleccionats.' WHERE KEY LIKE '%.segonpla.arxiu.maxTempsExec';
UPDATE IPA_CONFIG SET DESCRIPTION='Màxim tamany en Mb que pot ocupar l''arxiu zip que conté els documents dels expedients seleccionats.' WHERE KEY LIKE '%.segonpla.arxiu.maxMb';

UPDATE IPA_CONFIG SET DESCRIPTION='Usuari d''accés al servei si escau' WHERE KEY LIKE '%.plugin.summarize.usuari';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya d''accés al servei si escau' WHERE KEY LIKE '%.plugin.summarize.password';
UPDATE IPA_CONFIG SET DESCRIPTION='ApiKey d''accés al servei de resum GPT' WHERE KEY LIKE '%.summarize.gpt.apiKey';
UPDATE IPA_CONFIG SET DESCRIPTION='Timeout de connexió amb el servei de resum' WHERE KEY LIKE '%.summarize.service.timeout';
UPDATE IPA_CONFIG SET DESCRIPTION='Període d''actualització del contador d''anotacions pendents en segons' WHERE KEY LIKE '%.contador.anotacions.pendents';

UPDATE IPA_CONFIG SET DESCRIPTION='Guardar annexos de les anotacions en FileSystem (instal·lació de Ripea i Distribució en servidors separats)' WHERE KEY LIKE '%.anotacions.annexos.save';

UPDATE IPA_CONFIG SET DESCRIPTION='Remitent dels correus electrònics (correu electrònic)' WHERE KEY LIKE '%email.remitent';
UPDATE IPA_CONFIG SET DESCRIPTION='Interval de temps (ms) en què s''executa la tasca periòdica de reintentar canviar l''estat de les anotacions a Distribució' WHERE KEY LIKE '%.segonpla.reintentar.anotacions.pendents.enviar.distribucio';
UPDATE IPA_CONFIG SET DESCRIPTION='Expressió cron per indicar quan executar la tasca periòdica per enviar els correus electrònics pendents agrupats.' WHERE KEY LIKE '%.enviament.agrupat.cron';
UPDATE IPA_CONFIG SET DESCRIPTION='Nombre de reintents per canviar l''estat d''anotacions a Distribució' WHERE KEY LIKE '%.max.reintents.anotacions.pendents.enviar.distribucio';

UPDATE IPA_CONFIG SET DESCRIPTION='Estats d''elaboració on és obligatori el camp "Identificador ENI del document origen", separats per coma (EE02, EE03...)' WHERE KEY LIKE '%.estat.elaboracio.identificador.origen.obligat';
UPDATE IPA_CONFIG SET DESCRIPTION='Permetre enviar les metadades marcades com "Enviables arxiu" a l''arxiu' WHERE KEY LIKE '%.expedient.propagar.metadades';

UPDATE IPA_CONFIG SET DESCRIPTION='Duració per defecte d''una tasca, sempre especificada en dies.' WHERE KEY LIKE '%.duracio.tasca';
UPDATE IPA_CONFIG SET DESCRIPTION='Nombre de dies límit en què s''ha d''enviar el preavís' WHERE KEY LIKE '%.tasca.preavisDataLimitEnDies';
UPDATE IPA_CONFIG SET DESCRIPTION='Expressió cron per indicar quan executar la tasca periòdica per enviar els correus electrònics avisant que s''ha afegit un comentari al procediment' WHERE KEY LIKE '%.segonpla.email.enviament.procediment.comentari.cron';
UPDATE IPA_CONFIG SET DESCRIPTION='Cron per a l''actualització de procediments' WHERE KEY LIKE '%.procediment.actualitzar.cron';
UPDATE IPA_CONFIG SET DESCRIPTION='Consulta diària dels expedients pendents de tancar i si ha arribat la data programada' WHERE KEY LIKE '%.expedient.tancament.logic.cron';
UPDATE IPA_CONFIG SET DESCRIPTION='Indica si l''autenticació és de tipus bàsic' WHERE KEY LIKE '%.pinbal.basic.auth';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya del plugin' WHERE KEY LIKE '%.pinbal.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya de l''usuari per a accedir a DIR3' WHERE KEY LIKE '%.plugin.unitats.organitzatives.dir3.service.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya del plugin' WHERE KEY LIKE '%.plugin.notificacio.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya per accedir al Portafirmes' WHERE KEY LIKE '%.plugin.firmaservidor.portafib.auth.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya de l''API de firma simple asíncrona' WHERE KEY LIKE '%.plugin.portafirmes.firmasimpleasync.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Mostrar la persona relacionada amb el càrrec' WHERE KEY LIKE '%.plugin.portafirmes.carrer.mostrar.persona';
UPDATE IPA_CONFIG SET DESCRIPTION='Enviar URL de l''expedient en enviar a firmar' WHERE KEY LIKE '%.plugin.portafirmes.portafib.enviar.url.expedient';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya de l''API de firma simple per flux' WHERE KEY LIKE '%.plugin.portafirmes.firmasimpleflux.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya del webservice UsuariEntitat' WHERE KEY LIKE '%.plugin.portafirmes.usuarientitatws.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya del plugin' WHERE KEY LIKE '%.plugin.firmasimpleweb.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Localització on es realitza la firma' WHERE KEY LIKE '%.plugin.firmasimpleweb.location';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya per a generar imprimible' WHERE KEY LIKE '%.plugin.arxiu.caib.conversio.imprimible.contrasenya';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya del plugin' WHERE KEY LIKE '%.distribucio.regla.ws.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Client REST per a la creació de regles amb tipus d''autenticació BASIC' WHERE KEY LIKE '%.distribucio.regla.autenticacio.basic';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya de AfirmaCxf' WHERE KEY LIKE '%.plugins.validatesignature.afirmacxf.authorization.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya del plugin' WHERE KEY LIKE '%.plugin.procediment.rolsac.service.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Codi de l''aplicació CAIB' WHERE KEY LIKE '%.plugin.viafirma.caib.app.codi';
UPDATE IPA_CONFIG SET DESCRIPTION='Contrasenya del plugin' WHERE KEY LIKE '%.distribucio.backofficeIntegracio.ws.password';
UPDATE IPA_CONFIG SET DESCRIPTION='Resultat de l''escaneig fictici' WHERE KEY LIKE '%.plugin.digitalitzacio.scanner.mock';
UPDATE IPA_CONFIG SET DESCRIPTION='Màxim de tokens (típicament paraules, però no sempre) que suporta el model en la petició i resposta combinades.' WHERE KEY LIKE '%.plugin.summarize.model.maxTokens';
UPDATE IPA_CONFIG SET DESCRIPTION='Activar la conversió a definitiu' WHERE KEY LIKE '%.conversio.definitiu';
UPDATE IPA_CONFIG SET DESCRIPTION='Tipus de document específic per a certificació de NOTIB' WHERE KEY LIKE '%.notificacio.guardar.certificacio.expedient';
UPDATE IPA_CONFIG SET DESCRIPTION='Activar la incorporació del justificant' WHERE KEY LIKE '%.incorporar.justificant';
UPDATE IPA_CONFIG SET DESCRIPTION='Permetre canviar el tipus dels interessats en importar anotacions' WHERE KEY LIKE '%.interessats.permet.canvi.tipus';
UPDATE IPA_CONFIG SET DESCRIPTION='Habilita l''opció de menú per publicar documents.' WHERE KEY LIKE '%.creacio.documents.publicar.activa';
UPDATE IPA_CONFIG SET DESCRIPTION='URL de validació de documents imprimibles' WHERE KEY LIKE '%.documents.validacio.url';
UPDATE IPA_CONFIG SET DESCRIPTION='Filtrar llistat d''expedients amb data de creació inicial per defecte' WHERE KEY LIKE '%.filtre.data.creacio.actiu';
UPDATE IPA_CONFIG SET DESCRIPTION='Detectar de forma automàtica la firma dels documents' WHERE KEY LIKE '%.document.deteccio.firma.automatica';
UPDATE IPA_CONFIG SET DESCRIPTION='Enviar el contingut del document a l''arxiu sense modificar' WHERE KEY LIKE '%.document.enviar.contingut.existent';
UPDATE IPA_CONFIG SET DESCRIPTION='En notificar documents múltiples, concatenar versions imprimibles de documents si tots els documents seleccionats per notificar són PDF' WHERE KEY LIKE '%.notificacio.multiple.pdf.concatenar';
UPDATE IPA_CONFIG SET DESCRIPTION='En notificar documents múltiples, guardar el document generat com el document visible a l''usuari' WHERE KEY LIKE '%.notificacio.multiple.document.generat.visible';
UPDATE IPA_CONFIG SET DESCRIPTION='Permetre l''exportació de l''índex de l''expedient a EXCEL' WHERE KEY LIKE '%.expedient.exportacio.excel';
UPDATE IPA_CONFIG SET DESCRIPTION='Permetre l''exportació ENI per importar a INSIDE' WHERE KEY LIKE '%.expedient.exportar.inside';
UPDATE IPA_CONFIG SET DESCRIPTION='Validar la firma en adjuntar un contingut' WHERE KEY LIKE '%.firma.detectar.attached.validate.signature';
UPDATE IPA_CONFIG SET DESCRIPTION='Mantenir l''estat de les carpetes (oberta o tancada) fins que es tanqui el navegador' WHERE KEY LIKE '%.carpetes.mantenir.estat';

UPDATE IPA_CONFIG_GROUP SET DESCRIPTION='Configuració del plugin de Resums' WHERE CODE = 'SUMMARIZE';
UPDATE IPA_CONFIG_GROUP SET DESCRIPTION='Configuració del plugin de dades externes' WHERE CODE='DADES_EXT';
UPDATE IPA_CONFIG_GROUP SET DESCRIPTION='Plugin de creació de regles en DISTRIBUCIO' WHERE CODE='DISTRIBUCIO_REGLA';
UPDATE IPA_CONFIG_GROUP SET DESCRIPTION='Configuració del plugin de validació de firmes' WHERE CODE='VALIDATE_SIGNATURE';

UPDATE IPA_PINBAL_SERVEI SET NOM='Consulta d''inexistència de delictes sexuals per dades de filiació' WHERE CODI = 'SVDDELSEXWS01';