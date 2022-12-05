-- Changeset db/changelog/changes/0.9.96/1131.yaml::1669215787742-1::limit
INSERT INTO IPA_PROCESSOS_INICIALS (codi, init, id) VALUES ('GENERAR_MISSING_HISTORICS', 1, 4);

-- Changeset db/changelog/changes/0.9.96/1159.yaml::1667905124779-1::limit
update ipa_document set arxiu_estat = 'ESBORRANY' where estat not in (3, 5) and id in (select id from ipa_contingut where arxiu_uuid is not null);

update ipa_document set arxiu_estat = 'DEFINITIU' where estat in (3, 5) and id in (select id from ipa_contingut where arxiu_uuid is not null);

update ipa_document set arxiu_estat = null where id in (select document_id from ipa_registre_annex where error is not null);

update ipa_document set doc_firma_tipus = 'SENSE_FIRMA' where nti_tipfir is null;

update ipa_document set doc_firma_tipus = 'FIRMA_ADJUNTA' where nti_tipfir !='TF04';

update ipa_document set doc_firma_tipus = 'FIRMA_SEPARADA' where nti_tipfir ='TF04';

-- Changeset db/changelog/changes/0.9.96/1191.yaml::1666869630834-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.filtre.data.creacio.actiu', 'true', 'Filtrar llistat d''expedients amb data creació inicial per defecte', 'CONTINGUT', '25', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.96/1195.yaml::1667474333258-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.document.deteccio.firma.automatica', 'true', 'Detectar de forma automátia la firma dels documents', 'CONTINGUT', '26', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.96/1202.yaml::1669384545265-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.desactivar.comprovacio.duplicat.nom.arxiu', 'false', 'Desactivar la comprovació de noms duplicats a l''arxiu', 'CONTINGUT', '27', '0', 'BOOL', '1');

-- Changeset db/changelog/changes/0.9.96/1204.yaml::1669819816681-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.document.guardar.definitiu.arxiu', 'false', 'Fer els documents firmats definitius a l''arxiu quan es creen', 'CONTINGUT', '28', '0', 'BOOL', '1');

