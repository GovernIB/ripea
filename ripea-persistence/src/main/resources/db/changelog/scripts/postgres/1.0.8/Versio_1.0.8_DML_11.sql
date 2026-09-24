-- Activacio dels tipus de document per defecte NOTIFICACIO_MULTIPLE i OTROS (configurables per entitat).
-- Si la propietat no val true, el tipus no es crea en els procediments (ni a l'alta, ni sota demanda, ni en importar-los).
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','CONTINGUT','es.caib.ripea.metadocument.defecte.notificacio.multiple.actiu','false','Crear als procediments el tipus de document NOTIFICACIO_MULTIPLE, que s''aplica per defecte al document generat en notificar documents múltiples',35,'BOOL','0','1','0','1','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','CONTINGUT','es.caib.ripea.metadocument.defecte.otros.actiu','false','Crear als procediments nous el tipus de document OTROS, marcat com a tipus de document per defecte del procediment',36,'BOOL','0','1','0','1','0');
