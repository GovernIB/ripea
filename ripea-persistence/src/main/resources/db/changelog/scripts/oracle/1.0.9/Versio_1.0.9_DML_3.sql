-- RIPEA 1.0.9
-- Cron de la tasca en segon pla que actualitza els tipus de document permesos dels serveis PINBAL amb els que tenen
-- configurats a PINBAL (cada dia a les 3:30). Buida: no s'executa.
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','SCHEDULLED','es.caib.ripea.segonpla.pinbal.documents.permesos.cron','0 30 3 * * *','Cron de la tasca periòdica que actualitza els tipus de document permesos dels serveis PINBAL',17,'TEXT','0','0','0','0','0');
