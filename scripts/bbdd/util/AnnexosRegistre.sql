SELECT *
FROM IPA_REGISTRE_ANNEX ANN
   , IPA_REGISTRE REG
   , IPA_expedient_peticio PET
   WHERE REG.ID=ANN.REGISTRE_ID
   AND PET.registre_id=REG.id
   AND PET.EXPEDIENT_ID is not null; --Anotacio acceptada
   --AND ANN.DOCUMENT_ID is null; --Annex pendent de processar