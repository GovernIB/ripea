-- RIPEA 1.1.0
-- Pantalla d'inici (issue #1510): dies sense activitat d'una tasca pendent per considerar-la inactiva
-- (indicador i llista "Tasques sense activitat" de l'administrador).
Insert into IPA_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE_ORGAN,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) values
    ('0','ALTRES','es.caib.ripea.dies.tasca.sense.activitat','7','Dies sense modificacions ni comentaris a partir dels quals una tasca pendent es considera sense activitat (pantalla d''inici)',0,'INT','0','1','0','1','0');
-- La mateixa propietat de dies dels expedients antics la fa servir ara també la pantalla d'inici
UPDATE IPA_CONFIG SET DESCRIPTION='Dies a partir dels quals es considera un expedient com a antic (avís de documents obligatoris complets i indicador de la pantalla d''inici)' WHERE KEY = 'es.caib.ripea.dies.avis.expedient.antic';
