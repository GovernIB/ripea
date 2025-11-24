--1732 Migrar accions massives a REACT
UPDATE ipa_document doc
SET meta_document_id = metaDoc.id
FROM ipa_metadocument metaDoc
JOIN ipa_metanode metaNode ON metaDoc.id = metaNode.id
JOIN ipa_node node ON node.metanode_id = metaNode.id
WHERE node.id = doc.id;