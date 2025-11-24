--1732 Migrar accions massives a REACT
UPDATE IPA_DOCUMENT doc SET doc.metaDocument_id = (SELECT metaDoc.ID 
from IPA_METADOCUMENT metaDoc
    ,IPA_METANODE metaNode
    ,IPA_NODE node
WHERE metaDoc.ID=metaNode.ID
  AND node.METANODE_ID=metaNode.ID
  AND node.ID=doc.ID);