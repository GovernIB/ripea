-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 24/06/21 08:21
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/initial_schema_table.yaml::init-1::limit (generated)
CREATE TABLE ipa_alerta (id NUMBER(38, 0) NOT NULL, text VARCHAR2(256) NOT NULL, error VARCHAR2(2048), llegida NUMBER(1) NOT NULL, contingut_id NUMBER(38, 0), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_alerta_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-2::limit (generated)
CREATE TABLE ipa_carpeta (id NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_carpeta_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-3::limit (generated)
CREATE TABLE ipa_contingut (id NUMBER(38, 0) NOT NULL, nom VARCHAR2(1024) NOT NULL, tipus INTEGER NOT NULL, pare_id NUMBER(38, 0), esborrat INTEGER, esborrat_data TIMESTAMP, arxiu_uuid VARCHAR2(36), arxiu_data_act TIMESTAMP, expedient_id NUMBER(38, 0), contmov_id NUMBER(38, 0), entitat_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_contingut_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-4::limit (generated)
CREATE TABLE ipa_cont_comment (id NUMBER(38, 0) NOT NULL, text VARCHAR2(1024), contingut_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_cont_comment_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-5::limit (generated)
CREATE TABLE ipa_cont_log (id NUMBER(38, 0) NOT NULL, tipus INTEGER NOT NULL, contingut_id NUMBER(38, 0) NOT NULL, pare_id NUMBER(38, 0), contmov_id NUMBER(38, 0), objecte_id VARCHAR2(256), objecte_log_tipus INTEGER, objecte_tipus INTEGER, param1 VARCHAR2(256), param2 VARCHAR2(256), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_cont_log_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-6::null
CREATE TABLE ipa_cont_mov (id NUMBER(38, 0) NOT NULL, contingut_id NUMBER(38, 0) NOT NULL, origen_id NUMBER(38, 0), desti_id NUMBER(38, 0), remitent_codi VARCHAR2(64), comentari VARCHAR2(256), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_cont_mov_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-7::limit (generated)
CREATE TABLE ipa_dada (id NUMBER(38, 0) NOT NULL, ordre INTEGER, valor VARCHAR2(256) NOT NULL, metadada_id NUMBER(38, 0) NOT NULL, node_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_dada_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-8::limit (generated)
CREATE TABLE ipa_document (id NUMBER(38, 0) NOT NULL, tipus INTEGER NOT NULL, estat INTEGER NOT NULL, ubicacio VARCHAR2(255), data TIMESTAMP NOT NULL, data_captura TIMESTAMP NOT NULL, custodia_data TIMESTAMP, custodia_id VARCHAR2(256), custodia_csv VARCHAR2(256), fitxer_nom VARCHAR2(256), fitxer_content_type VARCHAR2(256), fitxer_contingut BLOB, versio_darrera VARCHAR2(32), versio_count INTEGER NOT NULL, nti_version VARCHAR2(5) NOT NULL, nti_identif VARCHAR2(48) NOT NULL, nti_organo VARCHAR2(9) NOT NULL, nti_origen VARCHAR2(2) NOT NULL, nti_estela VARCHAR2(4) NOT NULL, nti_tipdoc VARCHAR2(4) NOT NULL, nti_idorig VARCHAR2(48), nti_tipfir VARCHAR2(4), nti_csv VARCHAR2(256), nti_csvreg VARCHAR2(512), descripcio VARCHAR2(512), ges_doc_firmat_id VARCHAR2(256), ges_doc_adjunt_id VARCHAR2(256), ges_doc_adjunt_firma_id VARCHAR2(256), nom_fitxer_firmat VARCHAR2(512), CONSTRAINT ipa_document_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-9::limit (generated)
CREATE TABLE ipa_document_enviament (id NUMBER(38, 0) NOT NULL, dtype VARCHAR2(32) NOT NULL, estat VARCHAR2(255) NOT NULL, assumpte VARCHAR2(256) NOT NULL, observacions VARCHAR2(256), enviat_data TIMESTAMP, processat_data TIMESTAMP, cancelat_data TIMESTAMP, error NUMBER(1), error_desc VARCHAR2(2048), intent_num INTEGER, intent_data TIMESTAMP, intent_proxim_data TIMESTAMP, pub_tipus INTEGER, document_id NUMBER(38, 0) NOT NULL, expedient_id NUMBER(38, 0) NOT NULL, servei_tipus VARCHAR2(10), entrega_postal NUMBER(1), not_tipus INTEGER, not_retard INTEGER, not_data_prog TIMESTAMP, not_data_caducitat TIMESTAMP, not_env_ref VARCHAR2(100), not_env_dat_estat VARCHAR2(20), not_env_dat_data TIMESTAMP, not_env_dat_orig VARCHAR2(20), not_env_cert_data TIMESTAMP, not_env_cert_orig VARCHAR2(20), not_env_cert_arxiuid VARCHAR2(50), not_env_registre_data date, not_env_registre_numero NUMBER(38, 0), not_env_registre_num_formatat VARCHAR2(50), not_env_id VARCHAR2(100), notificacio_estat VARCHAR2(255), pf_prioritat INTEGER, pf_cad_data TIMESTAMP, pf_doc_tipus VARCHAR2(64), pf_responsables VARCHAR2(1024), pf_seq_tipus INTEGER, pf_flux_id VARCHAR2(64), pf_portafirmes_id VARCHAR2(64), pf_callback_estat INTEGER, pf_flux_tipus VARCHAR2(256), pf_motiu_rebuig VARCHAR2(512), vf_codi_usuari VARCHAR2(64), vf_titol VARCHAR2(256), vf_descripcio VARCHAR2(256), vf_codi_dispositiu VARCHAR2(64), vf_message_code VARCHAR2(64), vf_callback_estat INTEGER, vf_contrasenya_usuari VARCHAR2(64), vf_lectura_obligatoria NUMBER(1), vf_viafirma_dispositiu NUMBER(38, 0), firma_parcial NUMBER(1), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_document_enviament_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-10::limit (generated)
CREATE TABLE ipa_document_enviament_dis (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64), codi_aplicacio VARCHAR2(64), codi_usuari VARCHAR2(64), descripcio VARCHAR2(255), locale VARCHAR2(10), estat VARCHAR2(64), token VARCHAR2(255), identificador VARCHAR2(64), tipus VARCHAR2(64), email_usuari VARCHAR2(64), identificador_nac VARCHAR2(64), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_document_env_dis_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-11::limit (generated)
CREATE TABLE ipa_document_enviament_doc (document_enviament_id NUMBER(38, 0) NOT NULL, document_id NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_document_env_doc_pk PRIMARY KEY (document_enviament_id, document_id));

-- Changeset db/changelog/initial_schema_table.yaml::init-12::limit (generated)
CREATE TABLE ipa_document_enviament_inter (id NUMBER(38, 0) NOT NULL, document_enviament_id NUMBER(38, 0) NOT NULL, interessat_id NUMBER(38, 0) NOT NULL, not_env_ref VARCHAR2(100), not_env_dat_estat VARCHAR2(20), not_env_dat_data TIMESTAMP, not_env_dat_orig VARCHAR2(20), not_env_cert_data TIMESTAMP, not_env_cert_orig VARCHAR2(20), error NUMBER(1), error_desc VARCHAR2(2048), not_env_registre_data date, not_env_registre_numero NUMBER(38, 0), not_env_registre_num_formatat VARCHAR2(50), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_document_env_inter_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-13::limit (generated)
CREATE TABLE ipa_domini (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(256) NOT NULL, descripcio VARCHAR2(256), consulta VARCHAR2(256) NOT NULL, cadena VARCHAR2(256) NOT NULL, contrasenya VARCHAR2(256) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_domini_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-14::limit (generated)
CREATE TABLE ipa_email_pendent_enviar (id NUMBER(38, 0) NOT NULL, remitent VARCHAR2(64) NOT NULL, destinatari VARCHAR2(64) NOT NULL, subject VARCHAR2(1024) NOT NULL, text VARCHAR2(4000) NOT NULL, event_tipus_enum VARCHAR2(64) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_email_pendent_enviar_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-15::limit (generated)
CREATE TABLE ipa_entitat (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(256) NOT NULL, descripcio VARCHAR2(1024), cif VARCHAR2(9) NOT NULL, unitat_arrel VARCHAR2(9) NOT NULL, activa NUMBER(1), logo_img BLOB, capsalera_color_fons VARCHAR2(32), capsalera_color_lletra VARCHAR2(32), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_entitat_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-16::limit (generated)
CREATE TABLE ipa_execucio_massiva (id NUMBER(38, 0) NOT NULL, tipus VARCHAR2(255) NOT NULL, data_inici TIMESTAMP, data_fi TIMESTAMP, pfirmes_motiu VARCHAR2(256), pfirmes_priori VARCHAR2(255), pfirmes_datcad TIMESTAMP, enviar_correu NUMBER(1), entitat_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_execucio_massiva_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-17::limit (generated)
CREATE TABLE ipa_expedient (id NUMBER(38, 0) NOT NULL, estat INTEGER NOT NULL, tancat_data TIMESTAMP, tancat_motiu VARCHAR2(1024), anio INTEGER NOT NULL, sequencia NUMBER(38, 0) NOT NULL, codi VARCHAR2(256) NOT NULL, nti_version VARCHAR2(5) NOT NULL, nti_identif VARCHAR2(52) NOT NULL, nti_organo VARCHAR2(9) NOT NULL, nti_fecha_ape TIMESTAMP NOT NULL, nti_clasif_sia VARCHAR2(30) NOT NULL, sistra_bantel_num VARCHAR2(16), sistra_publicat NUMBER(1), sistra_unitat_adm VARCHAR2(9), sistra_clau VARCHAR2(100), agafat_per_codi VARCHAR2(64), expedient_estat_id NUMBER(38, 0), metaexpedient_id NUMBER(38, 0) NOT NULL, documents_firmats NUMBER(1), grup_id NUMBER(38, 0), organ_gestor_id NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_expedient_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-18::limit (generated)
CREATE TABLE ipa_expedient_estat (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(256) NOT NULL, nom VARCHAR2(256) NOT NULL, ordre INTEGER NOT NULL, color VARCHAR2(256), metaexpedient_id NUMBER(38, 0), inicial NUMBER(1), responsable_codi VARCHAR2(64), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_expedient_estat_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-19::limit (generated)
CREATE TABLE ipa_expedient_filtre (id NUMBER(38, 0) NOT NULL, meta_expedient_id NUMBER(38, 0), nom VARCHAR2(256), data_creacio_inici TIMESTAMP, data_creacio_fi TIMESTAMP, numero VARCHAR2(256), estat VARCHAR2(256), data_tancat_inici TIMESTAMP, data_tancat_fi TIMESTAMP, meus_expedients NUMBER(1), filter_name VARCHAR2(256) NOT NULL, last_used_date TIMESTAMP, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_expedient_filtre_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-20::limit (generated)
CREATE TABLE ipa_expedient_interessat (expedient_id NUMBER(38, 0) NOT NULL, interessat_id NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_expedient_interessat_pk PRIMARY KEY (expedient_id, interessat_id));

-- Changeset db/changelog/initial_schema_table.yaml::init-21::limit (generated)
CREATE TABLE ipa_expedient_organpare (id NUMBER(38, 0) NOT NULL, expedient_id NUMBER(38, 0) NOT NULL, meta_expedient_organ_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_expedient_organpare_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-22::limit (generated)
CREATE TABLE ipa_expedient_peticio (id NUMBER(38, 0) NOT NULL, identificador VARCHAR2(80) NOT NULL, clau_acces VARCHAR2(200) NOT NULL, data_Alta TIMESTAMP NOT NULL, estat VARCHAR2(40) NOT NULL, meta_expedient_nom VARCHAR2(256), exp_peticio_accio VARCHAR2(20), registre_id NUMBER(38, 0), consulta_ws_error NUMBER(1), consulta_ws_error_desc VARCHAR2(4000), consulta_ws_error_date TIMESTAMP, notifica_dist_error VARCHAR2(4000), expedient_id NUMBER(38, 0), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_expedient_peticio_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-23::limit (generated)
CREATE TABLE ipa_expedient_rel (expedient_id NUMBER(38, 0) NOT NULL, expedient_rel_id NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_expedient_rel_pk PRIMARY KEY (expedient_id, expedient_rel_id));

-- Changeset db/changelog/initial_schema_table.yaml::init-24::limit (generated)
CREATE TABLE ipa_expedient_seguidor (expedient_id NUMBER(38, 0) NOT NULL, seguidor_codi VARCHAR2(64) NOT NULL, CONSTRAINT ipa_expedient_seguidor_pk PRIMARY KEY (expedient_id, seguidor_codi));

-- Changeset db/changelog/initial_schema_table.yaml::init-25::limit (generated)
CREATE TABLE ipa_expedient_tasca (id NUMBER(38, 0) NOT NULL, expedient_id NUMBER(38, 0) NOT NULL, metaexp_tasca_id NUMBER(38, 0) NOT NULL, responsable_codi VARCHAR2(64) NOT NULL, data_inici TIMESTAMP NOT NULL, data_fi TIMESTAMP, estat VARCHAR2(20), motiu_rebuig VARCHAR2(1024), data_limit TIMESTAMP, comentari VARCHAR2(1024), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_expedient_tasca_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-26::limit (generated)
CREATE TABLE ipa_exp_comment (id NUMBER(38, 0) NOT NULL, expedient_id NUMBER(38, 0) NOT NULL, text VARCHAR2(1024), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_exp_comment_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-27::limit (generated)
CREATE TABLE ipa_grup (id NUMBER(38, 0) NOT NULL, rol VARCHAR2(50) NOT NULL, descripcio VARCHAR2(512) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_grup_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-28::limit (generated)
CREATE TABLE ipa_historic (id NUMBER(38, 0) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, organ_id NUMBER(38, 0), metaexpedient_id NUMBER(38, 0) NOT NULL, tipus INTEGER NOT NULL, data TIMESTAMP NOT NULL, n_exped_creats NUMBER(38, 0) NOT NULL, n_exped_creats_acum NUMBER(38, 0) NOT NULL, n_exped_oberts NUMBER(38, 0) NOT NULL, n_exped_oberts_acum NUMBER(38, 0) NOT NULL, n_exped_tancats NUMBER(38, 0) NOT NULL, n_exped_tancats_acum NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_historic_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-29::limit (generated)
CREATE TABLE ipa_hist_expedient (id NUMBER(38, 0) NOT NULL, n_exped_amb_alertes NUMBER(38, 0) NOT NULL, n_exped_errors_valid NUMBER(38, 0) NOT NULL, n_docs_pendents_sign NUMBER(38, 0) NOT NULL, n_docs_sign NUMBER(38, 0) NOT NULL, n_docs_pendents_notif NUMBER(38, 0) NOT NULL, n_docs_notif NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_hist_expedient_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-30::limit (generated)
CREATE TABLE ipa_hist_exp_interessat (id NUMBER(38, 0) NOT NULL, interessat_doc_num VARCHAR2(17) NOT NULL, CONSTRAINT ipa_hist_exp_interessat_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-31::limit (generated)
CREATE TABLE ipa_hist_exp_usuari (id NUMBER(38, 0) NOT NULL, usuari_codi VARCHAR2(64) NOT NULL, n_tasques_tramitades NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_hist_exp_usuari_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-32::limit (generated)
CREATE TABLE ipa_interessat (id NUMBER(38, 0) NOT NULL, dtype VARCHAR2(256) NOT NULL, nom VARCHAR2(30), llinatge1 VARCHAR2(30), llinatge2 VARCHAR2(30), document_tipus VARCHAR2(40) NOT NULL, document_num VARCHAR2(17) NOT NULL, pais VARCHAR2(4), provincia VARCHAR2(2), municipi VARCHAR2(5), adresa VARCHAR2(160), codi_postal VARCHAR2(5), email VARCHAR2(160), telefon VARCHAR2(20), observacions VARCHAR2(160), organ_codi VARCHAR2(9), organ_nom VARCHAR2(80), rao_social VARCHAR2(80), not_idioma VARCHAR2(2), not_autoritzat NUMBER(1) NOT NULL, es_representant NUMBER(1) NOT NULL, representant_id NUMBER(38, 0), expedient_id NUMBER(38, 0) NOT NULL, entrega_deh NUMBER(1), entrega_deh_obligat NUMBER(1), incapacitat NUMBER(1), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_interessat_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-33::limit (generated)
CREATE TABLE ipa_massiva_contingut (id NUMBER(38, 0) NOT NULL, execucio_massiva_id NUMBER(38, 0), contingut_id NUMBER(38, 0), data_inici TIMESTAMP, data_fi TIMESTAMP, estat VARCHAR2(255), error VARCHAR2(2046), ordre NUMBER(38, 0), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_massiva_contingut_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-34::limit (generated)
CREATE TABLE ipa_metadada (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(256) NOT NULL, tipus INTEGER NOT NULL, multiplicitat INTEGER NOT NULL, activa NUMBER(1) NOT NULL, read_only NUMBER(1) NOT NULL, ordre INTEGER NOT NULL, descripcio VARCHAR2(1024), meta_node_id NUMBER(38, 0) NOT NULL, global_document NUMBER(1), global_expedient NUMBER(1), global_multiplicitat VARCHAR2(255), global_readonly NUMBER(1), global_carpeta NUMBER(1), entitat_id NUMBER(38, 0) NOT NULL, valor VARCHAR2(255), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_metadada_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-35::limit (generated)
CREATE TABLE ipa_metadocument (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, multiplicitat INTEGER NOT NULL, firma_pfirma NUMBER(1), portafirmes_doctip VARCHAR2(64), portafirmes_fluxid VARCHAR2(64), portafirmes_respons VARCHAR2(512), portafirmes_seqtip VARCHAR2(256), portafirmes_custip VARCHAR2(64), firma_passarela NUMBER(1), passarela_custip VARCHAR2(64), plantilla_nom VARCHAR2(256), plantilla_content_type VARCHAR2(256), plantilla_contingut BLOB, meta_expedient_id NUMBER(38, 0), global_expedient NUMBER(1), global_multiplicitat VARCHAR2(255), global_readonly NUMBER(1), nti_origen VARCHAR2(2), nti_estela VARCHAR2(4), nti_tipdoc VARCHAR2(4), firma_biometrica NUMBER(1), biometrica_lectura NUMBER(1), meta_document_tipus_gen VARCHAR2(256), portafirmes_fluxtip VARCHAR2(256), CONSTRAINT ipa_metadocument_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-36::limit (generated)
CREATE TABLE ipa_metaexpedient (id NUMBER(38, 0) NOT NULL, pare_id NUMBER(38, 0), clasif_sia VARCHAR2(30) NOT NULL, serie_doc VARCHAR2(30) NOT NULL, not_activa NUMBER(1) NOT NULL, expressio_numero VARCHAR2(100), entitat_id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, permet_metadocs_generals NUMBER(1) NOT NULL, organ_gestor_id NUMBER(38, 0), gestio_amb_grups_activa NUMBER(1) NOT NULL, revisio_estat VARCHAR2(8), revisio_comentari VARCHAR2(1024), CONSTRAINT ipa_metaexpedient_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-37::limit (generated)
CREATE TABLE ipa_metaexpedient_carpeta (id NUMBER(38, 0) NOT NULL, nom VARCHAR2(1024) NOT NULL, pare_id NUMBER(38, 0), meta_expedient_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_metaexpedient_carpeta_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-38::limit (generated)
CREATE TABLE ipa_metaexpedient_grup (metaexpedient_id NUMBER(38, 0) NOT NULL, grup_id NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_metaexpedient_grup_pk PRIMARY KEY (metaexpedient_id, grup_id));

-- Changeset db/changelog/initial_schema_table.yaml::init-39::limit (generated)
CREATE TABLE ipa_metaexpedient_metadocument (id NUMBER(38, 0) NOT NULL, multiplicitat VARCHAR2(255), ordre INTEGER NOT NULL, readonly NUMBER(1), metadocument_id NUMBER(38, 0) NOT NULL, metaexpedient_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_metaexp_metadoc_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-40::limit (generated)
CREATE TABLE ipa_metaexp_organ (id NUMBER(38, 0) NOT NULL, meta_expedient_id NUMBER(38, 0) NOT NULL, organ_gestor_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_metaexp_organ_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-41::limit (generated)
CREATE TABLE ipa_metaexp_seq (id NUMBER(38, 0) NOT NULL, anio INTEGER, valor NUMBER(38, 0), meta_expedient_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_metaexp_seq_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-42::limit (generated)
CREATE TABLE ipa_metaexp_tasca (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(256) NOT NULL, descripcio VARCHAR2(1024) NOT NULL, responsable VARCHAR2(64), activa NUMBER(1) NOT NULL, meta_expedient_id NUMBER(38, 0) NOT NULL, data_limit TIMESTAMP, estat_crear_tasca_id NUMBER(38, 0), estat_finalitzar_tasca_id NUMBER(38, 0), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_metaexp_tasca_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-43::limit (generated)
CREATE TABLE ipa_metanode (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(256) NOT NULL, nom VARCHAR2(256) NOT NULL, descripcio VARCHAR2(1024), tipus VARCHAR2(256) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, actiu NUMBER(1), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_metanode_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-44::limit (generated)
CREATE TABLE ipa_metanode_metadada (id NUMBER(38, 0) NOT NULL, multiplicitat VARCHAR2(256), ordre INTEGER NOT NULL, metadada_id NUMBER(38, 0) NOT NULL, metanode_id NUMBER(38, 0) NOT NULL, readonly NUMBER(1), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_metanode_metadada_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-45::limit (generated)
CREATE TABLE ipa_node (id NUMBER(38, 0) NOT NULL, metanode_id NUMBER(38, 0), CONSTRAINT ipa_node_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-46::limit (generated)
CREATE TABLE ipa_organ_gestor (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(1000) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, pare_id NUMBER(38, 0), actiu NUMBER(1) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_organ_gestor_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-47::limit (generated)
CREATE TABLE ipa_portafirmes_block (id NUMBER(38, 0) NOT NULL, blk_order NUMBER(38, 0) NOT NULL, document_enviament_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_portafirmes_block_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-48::limit (generated)
CREATE TABLE ipa_portafirmes_block_info (id NUMBER(38, 0) NOT NULL, portafirmes_block_id NUMBER(38, 0) NOT NULL, portafirmes_signer_nom VARCHAR2(50), portafirmes_signer_codi VARCHAR2(50) NOT NULL, portafirmes_signer_id VARCHAR2(9), signed NUMBER(1) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_portafirmes_block_info_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-49::limit (generated)
CREATE TABLE ipa_registre (id NUMBER(38, 0) NOT NULL, aplicacio_codi VARCHAR2(20), aplicacio_versio VARCHAR2(15), assumpte_codi_codi VARCHAR2(16), assumpte_codi_desc VARCHAR2(100), assumpte_tipus_codi VARCHAR2(16), assumpte_tipus_desc VARCHAR2(100), data TIMESTAMP NOT NULL, doc_fisica_codi VARCHAR2(1), doc_fisica_desc VARCHAR2(100), entitat_codi VARCHAR2(21) NOT NULL, entitat_desc VARCHAR2(100), expedient_numero VARCHAR2(80), exposa CLOB, extracte VARCHAR2(240), procediment_codi VARCHAR2(20), identificador VARCHAR2(100) NOT NULL, idioma_codi VARCHAR2(2) NOT NULL, idioma_desc VARCHAR2(100), llibre_codi VARCHAR2(4) NOT NULL, llibre_desc VARCHAR2(100), observacions VARCHAR2(50), oficina_codi VARCHAR2(21) NOT NULL, oficina_desc VARCHAR2(100), origen_data TIMESTAMP, origen_registre_num VARCHAR2(80), ref_Externa VARCHAR2(16), solicita CLOB, transport_num VARCHAR2(20), transport_tipus_codi VARCHAR2(2), transport_tipus_desc VARCHAR2(100), usuari_codi VARCHAR2(20), usuari_nom VARCHAR2(80), desti_codi VARCHAR2(21) NOT NULL, desti_descripcio VARCHAR2(100), entitat_id NUMBER(38, 0) NOT NULL, justificant_arxiu_uuid VARCHAR2(256), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_registre_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-50::limit (generated)
CREATE TABLE ipa_registre_annex (id NUMBER(38, 0) NOT NULL, contingut BLOB, firma_contingut BLOB, firma_perfil VARCHAR2(20), firma_tamany INTEGER, firma_tipus VARCHAR2(4), nom VARCHAR2(80) NOT NULL, nti_fecha_captura TIMESTAMP NOT NULL, nti_origen VARCHAR2(20) NOT NULL, nti_tipo_doc VARCHAR2(20) NOT NULL, observacions VARCHAR2(50), sicres_tipo_doc VARCHAR2(20) NOT NULL, sicres_validez_doc VARCHAR2(30), tamany INTEGER NOT NULL, tipus_mime VARCHAR2(30), titol VARCHAR2(200) NOT NULL, uuid VARCHAR2(100), registre_id NUMBER(38, 0) NOT NULL, estat VARCHAR2(20), error VARCHAR2(4000), nti_estado_elaboracio VARCHAR2(50) NOT NULL, firma_nom VARCHAR2(80), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_registre_annex_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-51::limit (generated)
CREATE TABLE ipa_registre_interessat (id NUMBER(38, 0) NOT NULL, adresa VARCHAR2(160), canal VARCHAR2(30), cp VARCHAR2(5), doc_numero VARCHAR2(17), doc_tipus VARCHAR2(15), email VARCHAR2(160), llinatge1 VARCHAR2(30), llinatge2 VARCHAR2(30), municipi_codi VARCHAR2(100), nom VARCHAR2(30), observacions VARCHAR2(160), pais_codi VARCHAR2(4), provincia_codi VARCHAR2(100), rao_social VARCHAR2(80), telefon VARCHAR2(20), tipus VARCHAR2(40) NOT NULL, representant_id NUMBER(38, 0), registre_id NUMBER(38, 0), pais VARCHAR2(200), provincia VARCHAR2(200), municipi VARCHAR2(200), organ_codi VARCHAR2(9), createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_registre_interessat_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-52::limit (generated)
CREATE TABLE ipa_tipus_documental (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(256) NOT NULL, entitat_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64), createddate TIMESTAMP, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, CONSTRAINT ipa_tipus_documental_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-53::limit (generated)
CREATE TABLE ipa_usuari (codi VARCHAR2(64) NOT NULL, inicialitzat NUMBER(1), nif VARCHAR2(9), nom VARCHAR2(200), email VARCHAR2(200), idioma VARCHAR2(2) NOT NULL, emails_agrupats NUMBER(1), version NUMBER(38, 0) NOT NULL, CONSTRAINT ipa_usuari_pk PRIMARY KEY (codi));

-- Changeset db/changelog/initial_schema_table.yaml::init-54::limit (generated)
CREATE TABLE ipa_usuari_viafirma_ripea (id NUMBER(38, 0) NOT NULL, viafirma_user_codi VARCHAR2(64) NOT NULL, ripea_user_codi VARCHAR2(64) NOT NULL, CONSTRAINT ipa_usuari_viafirma_ripea_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-55::limit (generated)
CREATE TABLE ipa_viafirma_usuari (codi VARCHAR2(64) NOT NULL, contrasenya VARCHAR2(64) NOT NULL, descripcio VARCHAR2(64) NOT NULL, CONSTRAINT ipa_viafirma_usuari_pk PRIMARY KEY (codi));

-- Changeset db/changelog/initial_schema_table.yaml::init-56::limit (generated)
CREATE TABLE ipa_acl_sid (id NUMBER(38, 0) NOT NULL, principal NUMBER(1) NOT NULL, sid VARCHAR2(100) NOT NULL, CONSTRAINT ipa_acl_sid_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-57::limit (generated)
CREATE TABLE ipa_acl_class (id NUMBER(38, 0) NOT NULL, class VARCHAR2(100) NOT NULL, CONSTRAINT ipa_acl_class_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-58::limit (generated)
CREATE TABLE ipa_acl_object_identity (id NUMBER(38, 0) NOT NULL, object_id_class NUMBER(38, 0) NOT NULL, object_id_identity NUMBER(38, 0) NOT NULL, parent_object NUMBER(38, 0), owner_sid NUMBER(38, 0) NOT NULL, entries_inheriting NUMBER(1) NOT NULL, CONSTRAINT ipa_acl_object_identity_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-59::limit (generated)
CREATE TABLE ipa_acl_entry (id NUMBER(38, 0) NOT NULL, acl_object_identity NUMBER(38, 0) NOT NULL, ace_order NUMBER(38, 0) NOT NULL, sid NUMBER(38, 0) NOT NULL, mask INTEGER NOT NULL, granting NUMBER(1) NOT NULL, audit_success NUMBER(1) NOT NULL, audit_failure NUMBER(1) NOT NULL, CONSTRAINT ipa_acl_entry_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-1::limit (generated)
ALTER TABLE ipa_expedient_peticio ADD CONSTRAINT ipa_exp_pet_exp_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-2::limit (generated)
ALTER TABLE ipa_exp_comment ADD CONSTRAINT ipa_exp_com_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-3::limit (generated)
ALTER TABLE ipa_cont_comment ADD CONSTRAINT ipa_cont_com_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-4::limit (generated)
ALTER TABLE ipa_cont_comment ADD CONSTRAINT ipa_cont_com_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-5::limit (generated)
ALTER TABLE ipa_cont_comment ADD CONSTRAINT ipa_cont_com_contingut_fk FOREIGN KEY (contingut_id) REFERENCES ipa_contingut (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-6::limit (generated)
ALTER TABLE ipa_metaexpedient_metadocument ADD CONSTRAINT ipa_mexp_mdoc_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-7::limit (generated)
ALTER TABLE ipa_metaexpedient_metadocument ADD CONSTRAINT ipa_mexp_mdoc_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-8::limit (generated)
ALTER TABLE ipa_expedient ADD CONSTRAINT ipa_expedient_agafatper_fk FOREIGN KEY (agafat_per_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-9::limit (generated)
ALTER TABLE ipa_alerta ADD CONSTRAINT ipa_alerta_contingut_fk FOREIGN KEY (contingut_id) REFERENCES ipa_contingut (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-10::limit (generated)
ALTER TABLE ipa_carpeta ADD CONSTRAINT ipa_carpeta_contingut_fk FOREIGN KEY (id) REFERENCES ipa_contingut (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-11::limit (generated)
ALTER TABLE ipa_massiva_contingut ADD CONSTRAINT ipa_massiva_contingut_fk FOREIGN KEY (contingut_id) REFERENCES ipa_contingut (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-12::limit (generated)
ALTER TABLE ipa_node ADD CONSTRAINT ipa_node_contingut_fk FOREIGN KEY (id) REFERENCES ipa_contingut (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-13::limit (generated)
ALTER TABLE ipa_contingut ADD CONSTRAINT ipa_contingut_contmov_fk FOREIGN KEY (contmov_id) REFERENCES ipa_cont_mov (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-14::limit (generated)
ALTER TABLE ipa_cont_log ADD CONSTRAINT ipa_cont_log_cont_mov_fk FOREIGN KEY (contmov_id) REFERENCES ipa_cont_mov (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-15::limit (generated)
ALTER TABLE ipa_document_enviament_doc ADD CONSTRAINT IPA_DOCENV_DOCENVDOC_FK FOREIGN KEY (document_enviament_id) REFERENCES ipa_document_enviament (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-16::limit (generated)
ALTER TABLE ipa_document_enviament_doc ADD CONSTRAINT IPA_DOCUMENT_DOCENVDOC_FK FOREIGN KEY (document_id) REFERENCES ipa_document (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-17::limit (generated)
ALTER TABLE ipa_document_enviament ADD CONSTRAINT ipa_doc_env_document_fk FOREIGN KEY (document_id) REFERENCES ipa_document (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-18::limit (generated)
ALTER TABLE ipa_document_enviament ADD CONSTRAINT ipa_doc_env_doc_env_dis_fk FOREIGN KEY (vf_viafirma_dispositiu) REFERENCES ipa_document_enviament_dis (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-19::limit (generated)
ALTER TABLE ipa_contingut ADD CONSTRAINT ipa_contingut_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-20::limit (generated)
ALTER TABLE ipa_domini ADD CONSTRAINT ipa_domini_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-21::limit (generated)
ALTER TABLE ipa_grup ADD CONSTRAINT ipa_grup_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-22::limit (generated)
ALTER TABLE ipa_metadada ADD CONSTRAINT ipa_metadada_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-23::limit (generated)
ALTER TABLE ipa_metanode ADD CONSTRAINT ipa_metanode_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-24::limit (generated)
ALTER TABLE ipa_organ_gestor ADD CONSTRAINT ipa_organ_gestor_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-25::limit (generated)
ALTER TABLE ipa_registre ADD CONSTRAINT ipa_registre_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-26::limit (generated)
ALTER TABLE ipa_tipus_documental ADD CONSTRAINT ipa_tipus_docmntl_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-27::limit (generated)
ALTER TABLE ipa_massiva_contingut ADD CONSTRAINT ipa_mas_con_exe_mas_fk FOREIGN KEY (execucio_massiva_id) REFERENCES ipa_execucio_massiva (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-28::limit (generated)
ALTER TABLE ipa_contingut ADD CONSTRAINT ipa_contingut_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-29::limit (generated)
ALTER TABLE ipa_document_enviament ADD CONSTRAINT ipa_doc_env_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-30::limit (generated)
ALTER TABLE ipa_expedient_interessat ADD CONSTRAINT ipa_exp_int_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-31::limit (generated)
ALTER TABLE ipa_expedient_rel ADD CONSTRAINT ipa_exp_rel_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-32::limit (generated)
ALTER TABLE ipa_expedient_seguidor ADD CONSTRAINT ipa_exp_seg_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-33::limit (generated)
ALTER TABLE ipa_interessat ADD CONSTRAINT ipa_interessat_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-34::limit (generated)
ALTER TABLE ipa_expedient ADD CONSTRAINT ipa_expedient_exp_estat_fk FOREIGN KEY (expedient_estat_id) REFERENCES ipa_expedient_estat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-35::limit (generated)
ALTER TABLE ipa_expedient_organpare ADD CONSTRAINT ipa_exp_orgpare_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-36::limit (generated)
ALTER TABLE ipa_expedient_organpare ADD CONSTRAINT ipa_exp_orgpare_mexp_org_fk FOREIGN KEY (meta_expedient_organ_id) REFERENCES ipa_metaexp_organ (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-37::limit (generated)
ALTER TABLE ipa_expedient_organpare ADD CONSTRAINT ipa_exp_orgpare_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-38::limit (generated)
ALTER TABLE ipa_expedient_organpare ADD CONSTRAINT ipa_exp_orgpare_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-39::limit (generated)
ALTER TABLE ipa_expedient_rel ADD CONSTRAINT ipa_exp_rel_exprel_fk FOREIGN KEY (expedient_rel_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-40::limit (generated)
ALTER TABLE ipa_exp_comment ADD CONSTRAINT ipa_exp_com_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-41::limit (generated)
ALTER TABLE ipa_expedient_tasca ADD CONSTRAINT ipa_exp_tasca_expedient_fk FOREIGN KEY (expedient_id) REFERENCES ipa_expedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-42::limit (generated)
ALTER TABLE ipa_expedient ADD CONSTRAINT ipa_expedient_grup_fk FOREIGN KEY (grup_id) REFERENCES ipa_grup (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-43::limit (generated)
ALTER TABLE ipa_metaexpedient_grup ADD CONSTRAINT ipa_mexp_grup_grup_fk FOREIGN KEY (grup_id) REFERENCES ipa_grup (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-44::limit (generated)
ALTER TABLE ipa_historic ADD CONSTRAINT ipa_historic_entitat_fk FOREIGN KEY (entitat_id) REFERENCES ipa_entitat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-45::limit (generated)
ALTER TABLE ipa_historic ADD CONSTRAINT ipa_historic_metaexp_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-46::limit (generated)
ALTER TABLE ipa_historic ADD CONSTRAINT ipa_historic_org_ges_fk FOREIGN KEY (organ_id) REFERENCES ipa_organ_gestor (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-47::limit (generated)
ALTER TABLE ipa_hist_exp_usuari ADD CONSTRAINT ipa_hist_exp_usu_usuari_fk FOREIGN KEY (usuari_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-48::limit (generated)
ALTER TABLE ipa_expedient_interessat ADD CONSTRAINT ipa_exp_int_interessat_fk FOREIGN KEY (interessat_id) REFERENCES ipa_interessat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-49::limit (generated)
ALTER TABLE ipa_dada ADD CONSTRAINT ipa_dada_metadada_fk FOREIGN KEY (metadada_id) REFERENCES ipa_metadada (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-50::limit (generated)
ALTER TABLE ipa_metanode_metadada ADD CONSTRAINT ipa_mnode_mdada_metadada_fk FOREIGN KEY (metadada_id) REFERENCES ipa_metadada (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-51::limit (generated)
ALTER TABLE ipa_expedient_tasca ADD CONSTRAINT ipa_exp_tasca_mext_tasca_fk FOREIGN KEY (metaexp_tasca_id) REFERENCES ipa_metaexp_tasca (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-52::limit (generated)
ALTER TABLE ipa_expedient_estat ADD CONSTRAINT ipa_exp_est_metaexp_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-53::limit (generated)
ALTER TABLE ipa_expedient ADD CONSTRAINT ipa_expedient_metaexp_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-54::limit (generated)
ALTER TABLE ipa_metadocument ADD CONSTRAINT ipa_metadoc_metaexp_fk FOREIGN KEY (meta_expedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-55::limit (generated)
ALTER TABLE ipa_metaexpedient_metadocument ADD CONSTRAINT ipa_metaexp_metadoc_metaexp_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-56::limit (generated)
ALTER TABLE ipa_metaexpedient_GRUP ADD CONSTRAINT ipa_METAEXP_METAEXPGRUP_fk FOREIGN KEY (metaexpedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-57::limit (generated)
ALTER TABLE ipa_metaexp_organ ADD CONSTRAINT ipa_mexp_org_metaexp_fk FOREIGN KEY (meta_expedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-58::limit (generated)
ALTER TABLE ipa_metaexp_seq ADD CONSTRAINT ipa_metaexp_seq_metaexp_fk FOREIGN KEY (meta_expedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-59::limit (generated)
ALTER TABLE ipa_metaexp_tasca ADD CONSTRAINT ipa_metaexp_tasca_metaexp_fk FOREIGN KEY (meta_expedient_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-60::limit (generated)
ALTER TABLE ipa_metaexp_tasca ADD CONSTRAINT ipa_mexp_tasca_exp_est_cre_fk FOREIGN KEY (estat_crear_tasca_id) REFERENCES ipa_expedient_estat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-61::limit (generated)
ALTER TABLE ipa_metaexp_tasca ADD CONSTRAINT ipa_mexp_tasca_exp_est_fi_fk FOREIGN KEY (ESTAT_FINALITZAR_TASCA_id) REFERENCES ipa_expedient_estat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-62::limit (generated)
ALTER TABLE ipa_metanode_metadada ADD CONSTRAINT ipa_mnode_mdada_metanode_fk FOREIGN KEY (metanode_id) REFERENCES ipa_metanode (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-63::limit (generated)
ALTER TABLE ipa_metadada ADD CONSTRAINT ipa_metadada_metanode_fk FOREIGN KEY (meta_node_id) REFERENCES ipa_metanode (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-64::limit (generated)
ALTER TABLE ipa_metadocument ADD CONSTRAINT ipa_metadoc_metanode_fk FOREIGN KEY (id) REFERENCES ipa_metanode (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-65::limit (generated)
ALTER TABLE ipa_metaexpedient ADD CONSTRAINT ipa_metaexp_metanode_fk FOREIGN KEY (id) REFERENCES ipa_metanode (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-66::limit (generated)
ALTER TABLE ipa_node ADD CONSTRAINT ipa_node_metanode_fk FOREIGN KEY (metanode_id) REFERENCES ipa_metanode (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-67::limit (generated)
ALTER TABLE ipa_dada ADD CONSTRAINT ipa_dada_node_fk FOREIGN KEY (node_id) REFERENCES ipa_node (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-68::limit (generated)
ALTER TABLE ipa_document ADD CONSTRAINT ipa_document_node_fk FOREIGN KEY (id) REFERENCES ipa_node (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-69::limit (generated)
ALTER TABLE ipa_expedient ADD CONSTRAINT ipa_expedient_node_fk FOREIGN KEY (id) REFERENCES ipa_node (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-70::limit (generated)
ALTER TABLE ipa_expedient ADD CONSTRAINT ipa_expedient_org_ges_fk FOREIGN KEY (organ_gestor_id) REFERENCES ipa_organ_gestor (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-71::limit (generated)
ALTER TABLE ipa_metaexpedient ADD CONSTRAINT ipa_metaexp_org_ges_fk FOREIGN KEY (organ_gestor_id) REFERENCES ipa_organ_gestor (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-72::limit (generated)
ALTER TABLE ipa_metaexp_organ ADD CONSTRAINT ipa_metaexp_organ_org_ges_fk FOREIGN KEY (organ_gestor_id) REFERENCES ipa_organ_gestor (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-73::limit (generated)
ALTER TABLE ipa_contingut ADD CONSTRAINT ipa_contingut_pare_fk FOREIGN KEY (pare_id) REFERENCES ipa_contingut (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-74::limit (generated)
ALTER TABLE ipa_cont_log ADD CONSTRAINT ipa_cont_log_pare_fk FOREIGN KEY (pare_id) REFERENCES ipa_cont_log (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-75::limit (generated)
ALTER TABLE ipa_metaexpedient ADD CONSTRAINT ipa_metaexpedient_pare_fk FOREIGN KEY (PARE_id) REFERENCES ipa_metaexpedient (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-76::limit (generated)
ALTER TABLE ipa_expedient_seguidor ADD CONSTRAINT ipa_exp_seg_usuari_fk FOREIGN KEY (seguidor_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-77::limit (generated)
ALTER TABLE ipa_registre_annex ADD CONSTRAINT ipa_reg_annex_registre_fk FOREIGN KEY (registre_id) REFERENCES ipa_registre (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-78::limit (generated)
ALTER TABLE ipa_registre_interessat ADD CONSTRAINT ipa_reg_int_registre_fk FOREIGN KEY (registre_id) REFERENCES ipa_registre (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-79::limit (generated)
ALTER TABLE ipa_expedient_peticio ADD CONSTRAINT ipa_exp_pet_registre_fk FOREIGN KEY (registre_id) REFERENCES ipa_registre (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-80::limit (generated)
ALTER TABLE ipa_cont_mov ADD CONSTRAINT ipa_cont_mov_remitent_fk FOREIGN KEY (remitent_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-81::limit (generated)
ALTER TABLE ipa_interessat ADD CONSTRAINT ipa_interessat_repres_fk FOREIGN KEY (representant_id) REFERENCES ipa_interessat (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-82::limit (generated)
ALTER TABLE ipa_usuari_viafirma_ripea ADD CONSTRAINT ipa_usu_viaf_rip_usuari_fk FOREIGN KEY (ripea_user_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-83::limit (generated)
ALTER TABLE ipa_portafirmes_block ADD CONSTRAINT ipa_port_block_doc_env_fk FOREIGN KEY (document_enviament_id) REFERENCES ipa_document_enviament (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-84::limit (generated)
ALTER TABLE ipa_portafirmes_block_INFO ADD CONSTRAINT ipa_port_blo_info_pot_blo_fk FOREIGN KEY (portafirmes_block_id) REFERENCES ipa_portafirmes_block (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-85::limit (generated)
ALTER TABLE ipa_expedient_tasca ADD CONSTRAINT ipa_exp_tasca_usuari_fk FOREIGN KEY (responsable_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-86::limit (generated)
ALTER TABLE ipa_alerta ADD CONSTRAINT ipa_alerta_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-87::limit (generated)
ALTER TABLE ipa_contingut ADD CONSTRAINT ipa_contingut_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-88::limit (generated)
ALTER TABLE ipa_cont_log ADD CONSTRAINT ipa_cont_log_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-89::limit (generated)
ALTER TABLE ipa_cont_mov ADD CONSTRAINT ipa_cont_mov_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-90::limit (generated)
ALTER TABLE ipa_dada ADD CONSTRAINT ipa_dada_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-91::limit (generated)
ALTER TABLE ipa_document_enviament ADD CONSTRAINT ipa_doc_env_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-92::limit (generated)
ALTER TABLE ipa_entitat ADD CONSTRAINT ipa_entitat_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-93::limit (generated)
ALTER TABLE ipa_massiva_contingut ADD CONSTRAINT ipa_mass_cont_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-94::limit (generated)
ALTER TABLE ipa_execucio_massiva ADD CONSTRAINT ipa_exe_mass_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-95::limit (generated)
ALTER TABLE ipa_exp_comment ADD CONSTRAINT ipa_exp_com_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-96::limit (generated)
ALTER TABLE ipa_interessat ADD CONSTRAINT ipa_interessat_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-97::limit (generated)
ALTER TABLE ipa_metanode_metadada ADD CONSTRAINT ipa_mnode_mdada_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-98::limit (generated)
ALTER TABLE ipa_metadada ADD CONSTRAINT ipa_metadada_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-99::limit (generated)
ALTER TABLE ipa_metaexp_seq ADD CONSTRAINT ipa_metaexp_seq_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-100::limit (generated)
ALTER TABLE ipa_metaexp_tasca ADD CONSTRAINT ipa_mexp_tasca_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-101::limit (generated)
ALTER TABLE ipa_metanode ADD CONSTRAINT ipa_metanode_createdby_fk FOREIGN KEY (createdby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-102::limit (generated)
ALTER TABLE ipa_alerta ADD CONSTRAINT ipa_alerta_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-103::limit (generated)
ALTER TABLE ipa_contingut ADD CONSTRAINT ipa_contingut_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-104::limit (generated)
ALTER TABLE ipa_cont_log ADD CONSTRAINT ipa_cont_log_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-105::limit (generated)
ALTER TABLE ipa_cont_mov ADD CONSTRAINT ipa_cont_mov_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-106::limit (generated)
ALTER TABLE ipa_dada ADD CONSTRAINT ipa_dada_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-107::limit (generated)
ALTER TABLE ipa_document_enviament ADD CONSTRAINT ipa_doc_env_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-108::limit (generated)
ALTER TABLE ipa_entitat ADD CONSTRAINT ipa_entitat_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-109::limit (generated)
ALTER TABLE ipa_massiva_contingut ADD CONSTRAINT ipa_mass_cont_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-110::limit (generated)
ALTER TABLE ipa_execucio_massiva ADD CONSTRAINT ipa_exe_mass_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-111::limit (generated)
ALTER TABLE ipa_interessat ADD CONSTRAINT ipa_interessat_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-112::limit (generated)
ALTER TABLE ipa_metanode_metadada ADD CONSTRAINT ipa_mnode_mdada_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-113::limit (generated)
ALTER TABLE ipa_metadada ADD CONSTRAINT ipa_metadada_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-114::limit (generated)
ALTER TABLE ipa_metaexp_seq ADD CONSTRAINT ipa_metaexp_seq_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-115::limit (generated)
ALTER TABLE ipa_metaexp_tasca ADD CONSTRAINT ipa_mexp_tasca_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-116::limit (generated)
ALTER TABLE ipa_metanode ADD CONSTRAINT ipa_metanode_lastmodby_fk FOREIGN KEY (lastmodifiedby_codi) REFERENCES ipa_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-117::limit (generated)
ALTER TABLE ipa_usuari_viafirma_ripea ADD CONSTRAINT ipa_usu_viaf_rip_lastmodby_fk FOREIGN KEY (viafirma_user_codi) REFERENCES ipa_viafirma_usuari (codi);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-118::limit (generated)
ALTER TABLE ipa_acl_sid ADD CONSTRAINT ipa_acl_sid_uk UNIQUE (sid, principal);

ALTER TABLE ipa_acl_class ADD CONSTRAINT ipa_acl_class_uk UNIQUE (class);

ALTER TABLE ipa_acl_object_identity ADD CONSTRAINT ipa_acl_oid_uk UNIQUE (object_id_class, object_id_identity);

ALTER TABLE ipa_acl_object_identity ADD CONSTRAINT ipa_acl_oid_parent_fk FOREIGN KEY (parent_object) REFERENCES ipa_acl_object_identity (id);

ALTER TABLE ipa_acl_object_identity ADD CONSTRAINT ipa_acl_oid_class_fk FOREIGN KEY (object_id_class) REFERENCES ipa_acl_class (id);

ALTER TABLE ipa_acl_object_identity ADD CONSTRAINT ipa_acl_oid_owner_fk FOREIGN KEY (owner_sid) REFERENCES ipa_acl_sid (id);

ALTER TABLE ipa_acl_entry ADD CONSTRAINT ipa_acl_entry_uk UNIQUE (acl_object_identity, ace_order);

ALTER TABLE ipa_acl_entry ADD CONSTRAINT ipa_acl_entry_object_fk FOREIGN KEY (acl_object_identity) REFERENCES ipa_acl_object_identity (id);

ALTER TABLE ipa_acl_entry ADD CONSTRAINT ipa_acl_entry_acl_fk FOREIGN KEY (sid) REFERENCES ipa_acl_sid (id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-1::limit (generated)
CREATE UNIQUE INDEX ipa_dada_mult_uk ON ipa_dada(metadada_id, node_id, ordre);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-2::limit (generated)
ALTER TABLE ipa_dada ADD CONSTRAINT ipa_dada_mult_uk UNIQUE (metadada_id, node_id, ordre) USING INDEX ipa_dada_mult_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-3::limit (generated)
CREATE UNIQUE INDEX ipa_entitat_codi_uk ON ipa_entitat(codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-4::limit (generated)
ALTER TABLE ipa_entitat ADD CONSTRAINT ipa_entitat_codi_uk UNIQUE (codi) USING INDEX ipa_entitat_codi_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-5::limit (generated)
CREATE UNIQUE INDEX ipa_expedient_filtre_uk ON ipa_expedient_filtre(lastmodifiedby_codi, filter_name);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-6::limit (generated)
ALTER TABLE ipa_expedient_filtre ADD CONSTRAINT ipa_expedient_filtre_uk UNIQUE (lastmodifiedby_codi, filter_name) USING INDEX ipa_expedient_filtre_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-7::limit (generated)
CREATE UNIQUE INDEX ipa_expedient_seq_uk ON ipa_expedient(metaexpedient_id, anio, sequencia);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-8::limit (generated)
ALTER TABLE ipa_expedient ADD CONSTRAINT ipa_expedient_seq_uk UNIQUE (metaexpedient_id, anio, sequencia) USING INDEX ipa_expedient_seq_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-9::limit (generated)
CREATE UNIQUE INDEX ipa_metadada_metanode_codi_uk ON ipa_metadada(meta_node_id, codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-10::limit (generated)
ALTER TABLE ipa_metadada ADD CONSTRAINT ipa_metadada_metanode_codi_uk UNIQUE (meta_node_id, codi) USING INDEX ipa_metadada_metanode_codi_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-11::limit (generated)
CREATE UNIQUE INDEX ipa_metadoc_metaexp_codi_uk ON ipa_metadocument(meta_expedient_id, codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-12::limit (generated)
ALTER TABLE ipa_metadocument ADD CONSTRAINT ipa_metadoc_metaexp_codi_uk UNIQUE (meta_expedient_id, codi) USING INDEX ipa_metadoc_metaexp_codi_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-13::limit (generated)
CREATE UNIQUE INDEX ipa_metaexp_entitat_codi_uk ON ipa_metaexpedient(entitat_id, codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-14::limit (generated)
ALTER TABLE ipa_metaexpedient ADD CONSTRAINT ipa_metaexp_entitat_codi_uk UNIQUE (entitat_id, codi) USING INDEX ipa_metaexp_entitat_codi_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-15::limit (generated)
CREATE UNIQUE INDEX ipa_metaexp_seq_mult_uk ON ipa_metaexp_seq(anio, meta_expedient_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-16::limit (generated)
ALTER TABLE ipa_metaexp_seq ADD CONSTRAINT ipa_metaexp_seq_mult_uk UNIQUE (anio, meta_expedient_id) USING INDEX ipa_metaexp_seq_mult_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-17::limit (generated)
CREATE UNIQUE INDEX ipa_metaexp_tasca_MULT_uk ON ipa_metaexp_tasca(codi, meta_expedient_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-18::limit (generated)
ALTER TABLE ipa_metaexp_tasca ADD CONSTRAINT ipa_metaexp_tasca_mult_uk UNIQUE (codi, meta_expedient_id) USING INDEX ipa_metaexp_tasca_mult_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-19::limit (generated)
CREATE UNIQUE INDEX ipa_mnode_mdada_mnod_mdad_uk ON ipa_metanode_metadada(metanode_id, metadada_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-20::limit (generated)
ALTER TABLE ipa_metanode_metadada ADD CONSTRAINT ipa_mnode_mdada_mnod_mdad_uk UNIQUE (metanode_id, metadada_id) USING INDEX ipa_mnode_mdada_mnod_mdad_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-21::limit (generated)
CREATE UNIQUE INDEX ipa_organ_gestor_uk ON ipa_organ_gestor(codi, entitat_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-22::limit (generated)
ALTER TABLE ipa_organ_gestor ADD CONSTRAINT ipa_organ_gestor_uk UNIQUE (codi, entitat_id) USING INDEX ipa_organ_gestor_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-23::limit (generated)
CREATE UNIQUE INDEX ipa_metaexp_metadov_uk ON ipa_metaexpedient_metadocument(metaexpedient_id, metadocument_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-24::limit (generated)
ALTER TABLE ipa_metaexpedient_metadocument ADD CONSTRAINT ipa_metaexp_metadov_uk UNIQUE (metaexpedient_id, metadocument_id) USING INDEX ipa_metaexp_metadov_uk;

-- Changeset db/changelog/initial_schema_index.yaml::init-index-25::limit (generated)
CREATE INDEX ipa_acl_class_oid_fk_i ON ipa_acl_object_identity(object_id_class);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-26::limit (generated)
CREATE INDEX ipa_acl_oid_entry_fk_i ON ipa_acl_entry(acl_object_identity);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-27::limit (generated)
CREATE INDEX ipa_acl_parent_oid_fk_i ON ipa_acl_object_identity(parent_object);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-28::limit (generated)
CREATE INDEX ipa_acl_sid_entry_fk_i ON ipa_acl_entry(sid);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-29::limit (generated)
CREATE INDEX ipa_acl_sid_oid_fk_i ON ipa_acl_object_identity(owner_sid);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-30::limit (generated)
CREATE INDEX ipa_expedient_agafatper_fk_i ON ipa_expedient(agafat_per_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-31::limit (generated)
CREATE INDEX ipa_alerta_contingut_fk_i ON ipa_alerta(contingut_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-32::limit (generated)
CREATE INDEX ipa_cont_log_contingut_fk_i ON ipa_cont_log(contingut_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-33::limit (generated)
CREATE INDEX ipa_cont_mov_contingut_fk_i ON ipa_cont_mov(contingut_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-34::limit (generated)
CREATE INDEX ipa_mass_cont_contingut_fk_i ON ipa_massiva_contingut(contingut_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-35::limit (generated)
CREATE INDEX ipa_contingut_contmov_fk_i ON ipa_contingut(contmov_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-36::limit (generated)
CREATE INDEX ipa_cont_log_contmov_fk_i ON ipa_cont_log(contmov_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-37::limit (generated)
CREATE INDEX ipa_cont_mov_desti_fk_i ON ipa_cont_mov(desti_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-38::limit (generated)
CREATE INDEX ipa_doc_env_document_fk_i ON ipa_document_enviament(document_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-39::limit (generated)
CREATE INDEX ipa_contingut_entitat_fk_i ON ipa_contingut(entitat_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-40::limit (generated)
CREATE INDEX ipa_entitat_metanode_fk_i ON ipa_metanode(entitat_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-41::limit (generated)
CREATE INDEX ipa_mass_cont_exemass_fk_i ON ipa_massiva_contingut(execucio_massiva_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-42::limit (generated)
CREATE INDEX ipa_EXPEDIENT_contingut_fk_i ON ipa_contingut(EXPEDIENT_ID);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-43::limit (generated)
CREATE INDEX ipa_doc_env_expedient_fk_i ON ipa_document_enviament(expedient_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-44::limit (generated)
CREATE INDEX ipa_exp_int_expedient_fk_i ON ipa_expedient_interessat(expedient_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-45::limit (generated)
CREATE INDEX ipa_exp_seg_expedient_fk_i ON ipa_expedient_seguidor(expedient_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-46::limit (generated)
CREATE INDEX ipa_interessat_expedient_fk_i ON ipa_interessat(expedient_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-47::limit (generated)
CREATE INDEX ipa_exp_int_interessat_fk_i ON ipa_expedient_interessat(interessat_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-48::limit (generated)
CREATE INDEX ipa_dada_metadada_fk_i ON ipa_dada(metadada_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-49::limit (generated)
CREATE INDEX ipa_mnode_mdada_metadada_fk_i ON ipa_metanode_metadada(metadada_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-50::limit (generated)
CREATE INDEX ipa_metaexp_seq_metaexp_fk_i ON ipa_metaexp_seq(meta_expedient_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-51::limit (generated)
CREATE INDEX ipa_mnode_mdada_metanode_fk_i ON ipa_metanode_metadada(metanode_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-52::limit (generated)
CREATE INDEX ipa_node_metanode_fk_i ON ipa_node(metanode_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-53::limit (generated)
CREATE INDEX ipa_dada_node_fk_i ON ipa_dada(node_id);

-- Changeset db/changelog/initial_schema_index.yaml::1620025951430-126::limit (generated)
CREATE INDEX ipa_cont_mov_origen_fk_i ON ipa_cont_mov(origen_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-54::limit (generated)
CREATE INDEX ipa_contingut_pare_fk_i ON ipa_contingut(pare_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-55::limit (generated)
CREATE INDEX ipa_cont_log_pare_fk_i ON ipa_cont_log(pare_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-56::limit (generated)
CREATE INDEX ipa_metaexp_pare_fk_i ON ipa_metaexpedient(pare_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-57::limit (generated)
CREATE INDEX ipa_expedient_seg_fk_i ON ipa_expedient_seguidor(seguidor_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-58::limit (generated)
CREATE INDEX ipa_cont_mov_remitent_fk_i ON ipa_cont_mov(remitent_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-59::limit (generated)
CREATE INDEX ipa_interessat_repres_fk_i ON ipa_interessat(representant_id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-60::limit (generated)
CREATE INDEX ipa_alerta_createdby_fk_i ON ipa_alerta(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-61::limit (generated)
CREATE INDEX ipa_contingut_createdby_fk_i ON ipa_contingut(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-62::limit (generated)
CREATE INDEX ipa_cont_log_createdby_fk_i ON ipa_cont_log(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-63::limit (generated)
CREATE INDEX ipa_cont_mov_createdby_fk_i ON ipa_cont_mov(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-64::limit (generated)
CREATE INDEX ipa_dada_createdby_fk_i ON ipa_dada(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-65::limit (generated)
CREATE INDEX ipa_doc_env_createdby_fk_i ON ipa_document_enviament(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-66::limit (generated)
CREATE INDEX ipa_entitat_createdby_fk_i ON ipa_entitat(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-67::limit (generated)
CREATE INDEX ipa_mass_cont_createdby_fk_i ON ipa_massiva_contingut(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-68::limit (generated)
CREATE INDEX ipa_exec_mass_createdby_fk_i ON ipa_execucio_massiva(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-69::limit (generated)
CREATE INDEX ipa_interessat_createdby_fk_i ON ipa_interessat(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-70::limit (generated)
CREATE INDEX ipa_mnode_mdada_createdby_fk_i ON ipa_metanode_metadada(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-71::limit (generated)
CREATE INDEX ipa_metadada_createdby_fk_i ON ipa_metadada(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-72::limit (generated)
CREATE INDEX ipa_mexp_seq_createdby_fk_i ON ipa_metaexp_seq(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-73::limit (generated)
CREATE INDEX ipa_metanode_createdby_fk_i ON ipa_metanode(createdby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-74::limit (generated)
CREATE INDEX ipa_alerta_lastmodby_fk_i ON ipa_alerta(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-75::limit (generated)
CREATE INDEX ipa_contingut_lastmodby_fk_i ON ipa_contingut(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-76::limit (generated)
CREATE INDEX ipa_cont_log_lastmodby_fk_i ON ipa_cont_log(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-77::limit (generated)
CREATE INDEX ipa_cont_mov_lastmodby_fk_i ON ipa_cont_mov(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-78::limit (generated)
CREATE INDEX ipa_dada_lastmodby_fk_i ON ipa_dada(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-79::limit (generated)
CREATE INDEX ipa_doc_env_lastmodby_fk_i ON ipa_document_enviament(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-80::limit (generated)
CREATE INDEX ipa_entitat_lastmodby_fk_i ON ipa_entitat(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-81::limit (generated)
CREATE INDEX ipa_mass_cont_lastmodby_fk_i ON ipa_massiva_contingut(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-82::limit (generated)
CREATE INDEX ipa_exe_mass_lastmodby_fk_i ON ipa_execucio_massiva(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-83::limit (generated)
CREATE INDEX ipa_nteressat_lastmodby_fk_i ON ipa_interessat(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-84::limit (generated)
CREATE INDEX ipa_mnode_mdada_lastmodby_fk_i ON ipa_metanode_metadada(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-85::limit (generated)
CREATE INDEX ipa_metadada_lastmodby_fk_i ON ipa_metadada(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-86::limit (generated)
CREATE INDEX ipa_metaexp_seq_lastmodby_fk_i ON ipa_metaexp_seq(lastmodifiedby_codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-87::null
-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-1::limit (generated)
CREATE SEQUENCE ipa_hibernate_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-2::limit (generated)
CREATE SEQUENCE ipa_acl_sid_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-3::limit (generated)
CREATE SEQUENCE ipa_acl_class_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-4::limit (generated)
CREATE SEQUENCE ipa_acl_oid_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-5::limit (generated)
CREATE SEQUENCE ipa_acl_entry_seq START WITH 1;

-- Changeset db/changelog/initial_schema_trigger.yaml::init-trigger-1::limit (generated)
CREATE OR REPLACE TRIGGER ipa_acl_sid_idgen BEFORE INSERT ON ipa_acl_sid FOR EACH ROW BEGIN SELECT ipa_acl_sid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

/ SHOW ERRORS;

CREATE OR REPLACE TRIGGER ipa_acl_class_idgen BEFORE INSERT ON ipa_acl_class FOR EACH ROW BEGIN SELECT ipa_acl_class_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

/ SHOW ERRORS;

CREATE OR REPLACE TRIGGER ipa_acl_oid_idgen BEFORE INSERT ON ipa_acl_object_identity FOR EACH ROW BEGIN SELECT ipa_acl_oid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

/ SHOW ERRORS;

CREATE OR REPLACE TRIGGER ipa_acl_entry_idgen BEFORE INSERT ON ipa_acl_entry FOR EACH ROW BEGIN SELECT ipa_acl_entry_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

/ SHOW ERRORS;

-- Changeset db/changelog/initial_schema_lob.yaml::lob-1::limit (generated)
ALTER TABLE ipa_document MOVE LOB(fitxer_contingut) STORE AS ipa_document_fitxcont_lob(TABLESPACE ripea_lob INDEX ipa_document_fitxcont_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-2::limit (generated)
ALTER TABLE ipa_entitat MOVE LOB(logo_img) STORE AS ipa_entitat_logo_lob(TABLESPACE ripea_lob INDEX ipa_entitat_logo_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-3::limit (generated)
ALTER TABLE ipa_metadocument MOVE LOB(plantilla_contingut) STORE AS ipa_metadoc_plancont_lob(TABLESPACE ripea_lob INDEX ipa_metadoc_plancont_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-4::limit (generated)
ALTER TABLE ipa_registre MOVE LOB(exposa) STORE AS ipa_registre_exposa_lob(TABLESPACE ripea_lob INDEX ipa_registre_exposa_lob_i);

ALTER TABLE ipa_registre MOVE LOB(solicita) STORE AS ipa_registre_solicita_lob(TABLESPACE ripea_lob INDEX ipa_registre_solicita_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-5::limit (generated)
ALTER TABLE ipa_registre_annex MOVE LOB(contingut) STORE AS ipa_reg_annx_cont_lob(TABLESPACE ripea_lob INDEX ipa_reg_annx_cont_lob_i);

ALTER TABLE ipa_registre_annex MOVE LOB(firma_contingut) STORE AS ipa_reg_annx_firmacont_lob(TABLESPACE ripea_lob INDEX ipa_reg_annx_firmacont_lob_i);

-- Changeset db/changelog/initial_schema_grant.yaml::init-grant-1::limit (generated)
GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_alerta TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_carpeta TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_contingut TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_cont_comment TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_cont_log TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_cont_mov TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_dada TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_document TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_document_enviament TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_document_enviament_dis TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_document_enviament_doc TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_document_enviament_inter TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_domini TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_email_pendent_enviar TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_entitat TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_execucio_massiva TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_estat TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_filtre TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_interessat TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_organpare TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_peticio TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_rel TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_seguidor TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_expedient_tasca TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_exp_comment TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_grup TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_historic TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_hist_expedient TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_hist_exp_interessat TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_hist_exp_usuari TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_interessat TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_massiva_contingut TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metadada TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metadocument TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metaexpedient TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metaexpedient_carpeta TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metaexpedient_grup TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metaexpedient_metadocument TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metaexp_organ TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metaexp_seq TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metaexp_tasca TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metanode TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_metanode_metadada TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_node TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_organ_gestor TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_portafirmes_block TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_portafirmes_block_info TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_registre TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_registre_annex TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_registre_interessat TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_tipus_documental TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_usuari TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_usuari_viafirma_ripea TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_viafirma_usuari TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_acl_sid TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_acl_class TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_acl_object_identity TO www_ripea;

GRANT SELECT, UPDATE, INSERT, DELETE ON ipa_acl_entry TO www_ripea;

GRANT SELECT ON ipa_hibernate_sequence TO www_ripea;

GRANT SELECT ON ipa_acl_sid_seq TO www_ripea;

GRANT SELECT ON ipa_acl_class_seq TO www_ripea;

GRANT SELECT ON ipa_acl_oid_seq TO www_ripea;

GRANT SELECT ON ipa_acl_entry_seq TO www_ripea;

