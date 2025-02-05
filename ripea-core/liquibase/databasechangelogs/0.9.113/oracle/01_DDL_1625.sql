
ALTER TABLE ipa_entitat ADD perm_env_postal NUMBER(1,0) DEFAULT 1;
ALTER TABLE ipa_organ_gestor ADD perm_env_postal NUMBER(1,0) DEFAULT 0;
ALTER TABLE ipa_organ_gestor ADD perm_env_postal_desc NUMBER(1,0) DEFAULT 0;
-- DELETE FROM ipa_conf WHERE KEY = 'es.caib.ripea.notificacio.enviament.postal.actiu';