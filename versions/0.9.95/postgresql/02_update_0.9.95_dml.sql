-- Changeset db/changelog/changes/0.9.95/1008.yaml::1660919493847-1::limit
INSERT INTO ipa_metaexp_comment ( id, meta_expedient_id, text, createdby_codi, createddate ) SELECT ipa_hibernate_seq.NEXTVAL, id, revisio_comentari, 'sqlupdate', sysdate FROM ipa_metaexpedient WHERE ipa_metaexpedient.revisio_comentari IS NOT NULL;

-- Changeset db/changelog/changes/0.9.95/1063.yaml::1653978777186-1::limit
UPDATE ipa_metaxpedient SET organ_no_sinc = FALSE;

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.procediment.actualitzar.cron', NULL, 'Cron per a la actualització de procediments', 'SCHEDULLED', '6', '0', 'TEXT', '0');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.organs.consulta.canvis', NULL, 'Cron per a la consulta de canvis a l''organigrama', 'SCHEDULLED', '7', '0', 'TEXT', '0');

update ipa_metaexp_seq set valor = 0 where valor is null;

alter table ipa_metaexp_seq modify ( valor not null);

-- Changeset db/changelog/changes/0.9.95/1082.yaml::1666773113019-1::limit
ALTER TABLE ipa_expedient ADD numero VARCHAR(64);

INSERT INTO IPA_PROCESSOS_INICIALS (codi, init, id) VALUES ('GENERAR_EXPEDIENT_NUMERO', 1, 3);

-- Changeset db/changelog/changes/0.9.95/1123.yaml::1660219576096-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.document.esborrar.finals', 'true', 'Permetre esborrar documents CUSTODIATS, FIRMATS, DEFINITIUS I IMPORTATS', 'CONTINGUT', '21', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.95/1137.yaml::1662622106104-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.distribucio.regla.autenticacio.basic', 'false', 'Client REST creació regles amb tipus autenticació BASIC', 'DISTRIBUCIO_REGLA', '5', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.95/1142.yaml::1663146757135-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.identificador.origen.mascara', 'true', 'Aplicar màscara al camp "Identificador del document origen"', 'CONTINGUT', '23', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.95/1143.yaml::1663075341965-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.estat.elaboracio.identificador.origen.obligat', 'EE02, EE03, EE04', 'Estats elaboració on és obligatori el camp ''Identificador ENI del document origen'', separats per coma (EE02, EE03...)', 'CONTINGUT', '22', '0', 'TEXT', '1');

-- Changeset db/changelog/changes/0.9.95/1158.yaml::1664193915343-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.document.propagar.modificacio.arxiu', 'false', 'Propagar les modificacions d''un document a l''arxiu (també habilita la modificació de la data del document)', 'CONTINGUT', '24', '0', 'BOOL', '1');

