ALTER TABLE IPA_ORGAN_GESTOR
DROP (
	CODI_DIR3
);

ALTER TABLE IPA_ORGAN_GESTOR
ADD (
	PARE_CODI VARCHAR2(64)
);