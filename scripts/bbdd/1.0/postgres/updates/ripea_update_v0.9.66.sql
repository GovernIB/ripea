--#261-------------------------------------------------------------------------
ALTER TABLE IPA_METADADA ADD COLUMN VALOR character varying(255);
ALTER TABLE IPA_METADOCUMENT ADD COLUMN NTI_ORIGEN character varying(2);
ALTER TABLE IPA_METADOCUMENT ADD COLUMN NTI_ESTELA character varying(4);
ALTER TABLE IPA_METADOCUMENT ADD COLUMN NTI_TIPDOC character varying(4);
  
 