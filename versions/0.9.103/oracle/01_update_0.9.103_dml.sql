-- Changeset db/changelog/changes/0.9.103/1148.yaml::1695297765581-1::limit
update ipa_document set fitxer_nom = replace(fitxer_nom, '.odt', '.pdf'), fitxer_content_type = 'application/pdf' where fitxer_nom LIKE '%.odt' and (estat = 2 or estat = 3 or estat = 7);

update ipa_document set fitxer_nom = replace(fitxer_nom, '.docx', '.pdf'), fitxer_content_type = 'application/pdf' where fitxer_nom LIKE '%.docx' and (estat = 2 or estat = 3 or estat = 7);