--1520
INSERT INTO ipa_config (
  key, value, description, group_code, position, jboss_property, type_code,
  lastmodifiedby_codi, lastmodifieddate, configurable_organ, organ_codi,
  configurable_entitat_actiu, configurable_organ_actiu, entitat_codi,
  configurable, configurable_org_descendents
)
VALUES (
  'es.caib.ripea.segonpla.json.metriques',
  '0 0 3 * * *',
  'Cron per guardar JSON en brut de mètriques diaries.',
  'SCHEDULLED',
  10,
  '0',
  'TEXT',
  NULL,
  NULL,
  '0',
  NULL,
  '0',
  '0',
  NULL,
  '0',
  '0'
);