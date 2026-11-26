-- RIPEA 1.0.8
-- Registre d'accessos a la consulta d'estadístiques (taules IPA_HIST*): pantalla JSP /historic i API interna /api/historic.
-- Serveix per saber si algú en fa ús abans de decidir si es deixen de generar els històrics.
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
('0','LOGS','es.caib.ripea.historic.registre.acces.actiu','true','Desa cada petició a la consulta d''estadístiques (pantalla de dades estadístiques i API interna /api/historic) en un CSV.',12,'BOOL','0','0','0','0','0');
