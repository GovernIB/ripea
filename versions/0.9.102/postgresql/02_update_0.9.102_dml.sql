-- Changeset db/changelog/changes/0.9.102/1317.yaml::1695202086434-1::limit
update ipa_massiva_contingut set element_id = contingut_id;

update ipa_massiva_contingut set element_tipus = 'DOCUMENT';

update ipa_massiva_contingut set element_nom = (select nom from ipa_contingut where ipa_contingut.id = ipa_massiva_contingut.contingut_id);

update ipa_expedient e set e.registres_importats = ( select listagg(ep.identificador, ',') within group (order by ep.identificador) from ipa_expedient_peticio ep where ep.expedient_id is not null and ep.expedient_id = e.id );


-- Changeset db/changelog/changes/0.9.102/954.yaml::1694769935247-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.entorn', NULL, 'Entorn on es troba l''aplicació', 'GENERAL', '4', '0', 'TEXT', '0');

