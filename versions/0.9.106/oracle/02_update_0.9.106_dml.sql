-- Changeset db/changelog/changes/0.9.106/1321.yaml::1704906653342-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable, configurable_organ) VALUES ('es.caib.ripea.plugin.firmasimpleweb.debug', 'false', 'Debug', 'FIRMA_SIMPLE_WEB', '6', '0', 'BOOL', '0', '0');

-- Changeset db/changelog/changes/0.9.106/1346.yaml::1705333214600-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (15, 'SVDRRCCMATRIMONIOWS01', 0, 0, 0, 0, 0);

-- Changeset db/changelog/changes/0.9.106/1345.yaml::1705400143333-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (16, 'SVDRRCCDEFUNCIONWS01', 0, 0, 0, 0, 0);

-- Changeset db/changelog/changes/0.9.106/1381.yaml::1705668944465-1::limit
delete from ipa_config where key like '%escaneig%';

delete from ipa_config_group where description like '%escaneig%';

-- Changeset db/changelog/changes/0.9.106/1387.yaml::1703236956442-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (1, 'SVDDGPCIWS02', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (2, 'SVDDGPVIWS02', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (3, 'SVDCCAACPASWS01', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (4, 'SVDSCDDWS01', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (5, 'SCDCPAJU', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (6, 'SVDSCTFNWS01', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (7, 'SVDCCAACPCWS01', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (8, 'Q2827003ATGSS001', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (9, 'SVDDELSEXWS01', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (10, 'SCDHPAJU', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (11, 'NIVRENTI', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (12, 'ECOT103', 0, 0, 0, 0, 0);

insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (13, 'SVDDGPRESIDENCIALEGALDOCWS01', 0, 0, 0, 0, 0);

-- Changeset db/changelog/changes/0.9.106/1289.yaml::1705400143333-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (17, 'SVDBECAWS01', 0, 0, 0, 0, 0);

-- Changeset db/changelog/changes/0.9.106/1347.yaml::1703236956442-1::limit
insert into ipa_pinbal_servei (id, codi, doc_permes_dni, doc_permes_nif, doc_permes_cif, doc_permes_nie, doc_permes_pas) values (14, 'SVDRRCCNACIMIENTOWS01', 0, 0, 0, 0, 0);

-- Changeset db/changelog/changes/0.9.106/1395.yaml::1702392949975-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.anotacions.annexos.save', 'false', 'Guardar annexos de les anotacions en FileSystem (instalÂ·laciÃ³ de Ripea i DistribuciÃ³ en servidors separats)', 'GENERAL', '5', '0', 'BOOL', '1');

INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.anotacions.registre.expedient.serie.documental', NULL, 'Serie documental del contenidor dels annexos de DistribuciÃ³', 'GENERAL', '6', '0', 'TEXT', '0');

-- Changeset db/changelog/changes/0.9.106/1410.yaml::1705567798214-1::limit
INSERT INTO ipa_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.ripea.carpetes.mantenir.estat', 'false', 'Mantenir estat carpetes (oberta o tancada) fins que es tanqui el navegador', 'CONTINGUT', '41', '0', 'BOOL', '1');
