-- RIPEA 1.0.8
-- Dies que es conserven les dades dels monitors d'integracions (IPA_INTEGRACIO_ACCIO) i d'excepcions (IPA_EXCEPCIO_LOG).
-- Les tasques setmanals que esborren les dades més antigues en conservaven 3 mesos, fixos al codi.
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','SCHEDULLED','es.caib.ripea.segonpla.monitor.integracions.dies','30','Dies que es conserven les accions del monitor d''integracions.',17,'INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','SCHEDULLED','es.caib.ripea.segonpla.monitor.excepcions.dies','30','Dies que es conserven les excepcions del monitor d''excepcions.',18,'INT','0','0','0','0','0');
