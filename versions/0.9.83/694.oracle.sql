-- #694 Nou perfil Revisor de procediment
ALTER TABLE IPA_METAEXPEDIENT
ADD (
    REVISIO_ESTAT VARCHAR2(8 CHAR),
    REVISIO_COMENTARI VARCHAR2(1024 CHAR)
);