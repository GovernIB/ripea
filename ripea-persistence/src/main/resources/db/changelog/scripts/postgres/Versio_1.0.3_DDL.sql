--1684
-- Crear tabla IPA_METADOCUMENTFLUX
CREATE TABLE ipa_metadocumentflux (
    id BIGINT NOT NULL,
    metadocument_id BIGINT,
    portafirmes_flux_id VARCHAR(64),
    portafirmes_flux_desc VARCHAR(256),
    createdby_codi VARCHAR(64),
    createddate TIMESTAMP(6),
    lastmodifiedby_codi VARCHAR(64),
    lastmodifieddate TIMESTAMP(6),
    CONSTRAINT ipa_metadocumentflux_pk PRIMARY KEY (id)
);

-- Clave foránea hacia IPA_METADOCUMENT
ALTER TABLE ipa_metadocumentflux ADD CONSTRAINT ipa_metadocument_fk FOREIGN KEY (metadocument_id) REFERENCES ipa_metadocument(id);

-- Restricción de unicidad compuesta
ALTER TABLE ipa_metadocumentflux ADD CONSTRAINT ipa_metadocument_flux_uk UNIQUE (metadocument_id, portafirmes_flux_id);

-- Índice para la clave foránea
CREATE INDEX ipa_metadocumentflux_fk_i ON ipa_metadocumentflux (metadocument_id);

-- Eliminar columna de la tabla IPA_METADOCUMENT
ALTER TABLE ipa_metadocument DROP COLUMN portafirmes_fluxid;