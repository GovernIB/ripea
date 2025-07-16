Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE,CONFIGURABLE_ORGAN,ORGAN_CODI,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,ENTITAT_CODI,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) 
values ('es.caib.ripea.encription.key',null,'Clau per encriptar i dessencriptar parametres de les URLS (firma, escaneig, flux firma).','GENERAL','10','1','TEXT',null,null,'0',null,'0','0',null,'0','0');

--Afegir la propietat es.caib.ripea.encription.key=g8J@kLp!3#xYzWv9bQnM4dF5TjZ2Rc7p al system.properties (pot ser qualsevol clau de 32 caracters)

Insert into IPA_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE,CONFIGURABLE_ORGAN,ORGAN_CODI,CONFIGURABLE_ENTITAT_ACTIU,CONFIGURABLE_ORGAN_ACTIU,ENTITAT_CODI,CONFIGURABLE,CONFIGURABLE_ORG_DESCENDENTS) 
values ('es.caib.ripea.maxUploadSize','52428800','Maxim tamany dels fitxers dels camps fileUpload dels formularis.','GENERAL','11','0','INT',null,null,'0',null,'0','0',null,'0','0');

-- Si no s'activa aquesta propietat, totes les firmes es consideren PADES, no funcionen CADES ni XADES.
-- S'hauria de comprovar per entitat, pot ser algunes ho tenguin a false perque no treballen amb validacio de firma.
-- Pero per norma general, a la CAIB, hauria de estar a TRUE.
--UPDATE IPA_CONFIG set VALUE='true' WHERE KEY LIKE '%arxiu.firma.detalls.actiu';