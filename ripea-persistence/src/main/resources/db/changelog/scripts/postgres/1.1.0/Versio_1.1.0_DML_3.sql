-- RIPEA 1.1.0
-- Pantalla d'inici (issue #1510): caché per usuari dels resultats i límit de temps de les consultes.
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
    ('0','ALTRES','es.caib.ripea.inici.cache.segons','120','Segons que es guarden a la memòria del servidor les targetes i les llistes de la pantalla d''inici de cada usuari (0 per no guardar-les)',0,'INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
    ('0','ALTRES','es.caib.ripea.inici.cache.expedients.tancar.segons','900','Segons que es guarda a la memòria del servidor la llista «Expedients més antics que es podrien tancar» de la pantalla d''inici de cada usuari (0 per no guardar-la)',0,'INT','0','0','0','0','0');
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
    ('0','ALTRES','es.caib.ripea.inici.consulta.timeout.segons','15','Segons màxims de cada consulta de la pantalla d''inici: si se superen, la consulta es cancel·la i la targeta o la llista mostra un avís (0 sense límit)',0,'INT','0','0','0','0','0');
