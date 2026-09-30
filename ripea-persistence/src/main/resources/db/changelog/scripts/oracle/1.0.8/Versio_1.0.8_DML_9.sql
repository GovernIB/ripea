-- RIPEA 1.0.8
-- Alinea IPA_DOCUMENT.METADOCUMENT_ID amb IPA_NODE.METANODE_ID.
-- El tipus de document es guarda a les dues columnes, pero en modificar un document
-- (DocumentHelper.updateDocument -> DocumentEntity.update) nomes s'actualitzava METANODE_ID.
-- METADOCUMENT_ID quedava amb el tipus anterior i les accions de REACT (firma en navegador, portafirmes,
-- viaFirma), les validacions de documents obligatoris i l'enviament a portafirmes treballaven amb el tipus antic.
-- Es pren METANODE_ID com a valor correcte: es el tipus triat per l'usuari i el que mostren el JSP i la graella.
-- Es idempotent: nomes tracta els documents amb les dues columnes diferents.
UPDATE ipa_document d
SET d.metadocument_id = (
    SELECT n.metanode_id
    FROM ipa_node n
    WHERE n.id = d.id)
WHERE EXISTS (
    SELECT 1
    FROM ipa_node n
    JOIN ipa_metadocument md ON md.id = n.metanode_id
    WHERE n.id = d.id
      AND n.metanode_id <> d.metadocument_id);
