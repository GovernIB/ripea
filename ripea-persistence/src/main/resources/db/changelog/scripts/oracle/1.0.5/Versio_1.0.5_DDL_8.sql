--Índexs per optimitzar el llistat d'expedients amb filtre de permisos (rols no-admin).
--El rol admin s'evita aquest filtre; els altres rols apliquen el predicat de permisos sobre tota la taula.

--Filtre esborrat + entitat i ordenació per createddate del llistat (columnes a IPA_CONTINGUT).
--Creat manualment a l'entorn local el 2026-07-13; es formalitza aquí per PRE/PRO.
CREATE INDEX IPA_CONTINGUT_LLISTAT_I ON IPA_CONTINGUT (ESBORRAT, ENTITAT_ID, CREATEDDATE);

--Correlació del exists de permisos per òrgan (VIA 2): FK EXPEDIENT_ID sense indexar (causa principal de l'O(n^2)).
CREATE INDEX IPA_EXPEDIENT_ORGANPARE_EXP_I ON IPA_EXPEDIENT_ORGANPARE (EXPEDIENT_ID);

--Join intern del exists de permisos (IPA_EXPEDIENT_ORGANPARE amb IPA_METAEXP_ORGAN): FK sense indexar.
CREATE INDEX IPA_EXPEDIENT_ORGANPARE_MEO_I ON IPA_EXPEDIENT_ORGANPARE (META_EXPEDIENT_ORGAN_ID);

--Filtre AND per l'òrgan gestor directe de l'expedient: FK ORGAN_GESTOR_ID sense indexar (evita el full scan d'IPA_EXPEDIENT).
CREATE INDEX IPA_EXPEDIENT_ORGAN_FK_I ON IPA_EXPEDIENT (ORGAN_GESTOR_ID);
