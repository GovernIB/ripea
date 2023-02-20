-- Changeset db/changelog/changes/0.9.97/1122.yaml::1629371481-2::limit
INSERT INTO IPA_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (6, 'PLUGINS', 'DADES_EXT_PINBAL', 'Configuració del plugin de dades externes per consultes a PINBAL' );

Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE, CONFIGURABLE) values ('es.caib.ripea.plugin.dadesextpinbal.class','es.caib.ripea.plugin.caib.dadesext.DadesExternesPluginCaib','Classe del plugin','DADES_EXT_PINBAL','1','0','DADESEXT_CLASS',null,null, 1);

Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE, CONFIGURABLE) values ('es.caib.ripea.plugin.dadesextcaib.service.url',null,'Url del plugin','DADES_EXT_PINBAL','2','1','TEXT',null,null, 1);

-- Changeset db/changelog/changes/0.9.97/1156.yaml::1671191152106-1::limit
ALTER TABLE ipa_organ_gestor ADD cif VARCHAR(10);

-- Changeset db/changelog/changes/0.9.97/1157.yaml::1673273997173-1::limit
ALTER TABLE ipa_metadocument ADD pinbal_utilitzar_cif_organ BOOLEAN DEFAULT FALSE;

-- Changeset db/changelog/changes/0.9.97/1164.yaml::1673857608869-1::limit
ALTER TABLE ipa_config ADD configurable_organ BOOLEAN DEFAULT FALSE;

ALTER TABLE ipa_config ADD organ_codi VARCHAR(64);

ALTER TABLE ipa_config ADD configurable_entitat_actiu BOOLEAN DEFAULT FALSE;

ALTER TABLE ipa_config ADD configurable_organ_actiu BOOLEAN DEFAULT FALSE;

update ipa_config set configurable_entitat_actiu = configurable;

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.creacio.carpetes.activa';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.mostrar.carpetes.anotacions';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.carpetes.logiques';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.ordenacio.contingut.habilitada';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.creacio.importacio.activa';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.creacio.documents.copiarMoure.activa';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.creacio.documents.vincular.activa';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.descarregar.imprimible.nofirmats';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.expedient.permetre.reobrir';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.document.modificar.custodiats';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.creacio.documents.publicar.activa';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.creacio.documents.moure.mateix.expedient';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.base.url';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.usuari';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.contrasenya';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.url';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.usuari';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.conversio.imprimible.contrasenya';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.aplicacio.codi';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.csv.definicio';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.timeout.connect';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.arxiu.caib.timeout.read';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.arxiu.metadades.addicionals.actiu';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.arxiu.firma.detalls.actiu';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.notificacio.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.notificacio.url';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.notificacio.username';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.notificacio.password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.notificacio.retard.num.dies';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.notificacio.caducitat.num.dies';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.notificacio.enviament.postal.actiu';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.notificacio.enviament.deh.activa';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.notificacio.forsar.entitat';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.notificacio.guardar.certificacio.expedient';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.conversio.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.conversio.ooffice.host';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.conversio.ooffice.port';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.pinbal.basic.auth';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.pinbal.base.url';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.pinbal.user';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.pinbal.password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.pinbal.codi.sia.peticions';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.endpoint';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_url';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_username';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.auth.username';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.plugins.signatureserver.portafib.api_passarela_password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.auth.password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.perfil';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.signerEmail';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.location';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.firmaservidor.portafib.username';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.validatesignature.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.endpoint';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.username';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.applicationID';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.ignoreservercertificates';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.TransformersTemplatesPath';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.ks.path';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.ks.type';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.ks.password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugins.validatesignature.afirmacxf.authorization.ks.cert.password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.tasca.preavisDataLimitEnDies';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.propagar.relacio.expedients';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.incorporacio.anotacions.duplicada';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.document.nou.escanejar.actiu';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.creacio.documents.publicar.activa';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.estat.elaboracio.identificador.origen.obligat';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.document.propagar.modificacio.arxiu';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.desactivar.comprovacio.duplicat.nom.arxiu';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.versio.antiga';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.ids';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.ignorar.modal.ids';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.1.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.1.nom';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.1.desc';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.1.plugins.signatureweb.portafib.api_passarela_url';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.1.plugins.signatureweb.portafib.api_passarela_username';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.1.plugins.signatureweb.portafib.api_passarela_password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.2.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.2.nom';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.2.desc';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.3.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.3.nom';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.3.desc';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.3.plugins.signatureweb.miniappletinserver.base_dir';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.passarelafirma.3.plugins.signatureweb.miniappletinserver.ignore_certificate_filter';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.portafirmes.class';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.portafirmes.portafib.base.url';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.portafirmes.portafib.username';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.portafirmes.portafib.password';

update ipa_config set configurable_organ = true where key = 'es.caib.ripea.plugin.portafirmes.flux.filtrar.usuari.descripcio';

update ipa_config set configurable = false, configurable_entitat_actiu = false where key = 'es.caib.ripea.segonpla.guardar.arxiu.max.reintents.expedients';

delete from ipa_config where entitat_codi is not null and key like '%'||'segonpla.guardar.arxiu.max.reintents.expedients';

update ipa_config set configurable = false, configurable_entitat_actiu = false where key = 'es.caib.ripea.segonpla.guardar.arxiu.max.reintents.documents';

delete from ipa_config where entitat_codi is not null and key like '%'||'guardar.arxiu.max.reintents.documents';

update ipa_config set configurable = false, configurable_entitat_actiu = false where key = 'es.caib.ripea.segonpla.guardar.arxiu.max.reintents.interessats';

delete from ipa_config where entitat_codi is not null and key like '%'||'segonpla.guardar.arxiu.max.reintents.interessats';

-- Changeset db/changelog/changes/0.9.97/1208.yaml::1670837833869-1::limit
ALTER TABLE ipa_metadada ADD no_aplica BOOLEAN DEFAULT FALSE;

