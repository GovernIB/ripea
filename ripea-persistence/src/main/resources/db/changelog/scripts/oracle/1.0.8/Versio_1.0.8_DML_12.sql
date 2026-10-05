-- RIPEA 1.0.8
-- Data i hora d'inici (dd/MM/yyyy HH:mm) dels processos en segon pla que incorporen als expedients els certificats
-- de les remeses i els justificants de registre de les anotacions. Buides: no s'executen.
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','SCHEDULLED','es.caib.ripea.segonpla.certificats.remeses.inici',null,'Data i hora (dd/MM/yyyy HH:mm) d''inici del procés que incorpora als expedients els certificats de les remeses.',13,'TEXT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','SCHEDULLED','es.caib.ripea.segonpla.justificants.registre.inici',null,'Data i hora (dd/MM/yyyy HH:mm) d''inici del procés que incorpora als expedients els justificants de registre de les anotacions acceptades.',14,'TEXT','0','0','0','0','0');