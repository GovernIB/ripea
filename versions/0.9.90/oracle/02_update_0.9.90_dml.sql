-- 915
UPDATE IPA_INTERESSAT SET NOT_AUTORITZAT = 1 WHERE NOT_AUTORITZAT = 0;
UPDATE IPA_METAEXPEDIENT SET NOT_ACTIVA = 1 WHERE NOT_ACTIVA = 0;

--885
UPDATE IPA_DOCUMENT_ENVIAMENT SET NOT_EMISOR_ID = (SELECT ORGAN_GESTOR_ID FROM IPA_EXPEDIENT WHERE IPA_DOCUMENT_ENVIAMENT.EXPEDIENT_ID = IPA_EXPEDIENT.ID);

-- 948
Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.ripea.mostrar.carpetes.anotacions','true','Mostrar carpetes per anotacions','CONTINGUT','2','0','BOOL',null,null);