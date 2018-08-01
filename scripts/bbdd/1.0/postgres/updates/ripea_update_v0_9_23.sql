
------
--- #109 Mostrar número expedient com un identificador únic
--- Guarda el codi del tipus d'expedient en la taula d'expedients per mostrar el número concatenant codi/seqüència/any
------

-- Afegeix la columna codi que guardarà el codi del meta expedient
ALTER TABLE IPA_EXPEDIENT ADD COLUMN CODI character varying(256);

-- Actualitza el valor pels expedients del valor del codi de tipus d'expedient
UPDATE IPA_EXPEDIENT AS e SET CODI = 
    (
        SELECT m.codi
        FROM IPA_METANODE m
                INNER JOIN IPA_NODE n ON m.ID = n.METANODE_ID
        WHERE
            n.id = e.id
    );

-- Posa la columna com a no nulable
ALTER TABLE IPA_EXPEDIENT ALTER COLUMN CODI SET NOT NULL;