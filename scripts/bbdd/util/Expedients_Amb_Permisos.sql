--ORGANS GESTORS ASSOCIATS A UN PROCEDIMENT
SELECT
  MEOG.ID
, META.ID
, MEOG.ORGAN_GESTOR_ID
, og.nom
, og.pare_id
, meta.organ_gestor_id
FROM IPA_METAEXP_ORGAN MEOG
, IPA_ORGAN_GESTOR OG
, IPA_METAEXPEDIENT META
WHERE MEOG.ORGAN_GESTOR_ID=OG.ID
AND MEOG.META_EXPEDIENT_ID=META.ID
AND META.ID=24609;

--ORGANS GESTORS ASSOCIATS A UN EXPEDIENT
SELECT
  EXP.ID
, EXP.CODI||'/'||EXP.SEQUENCIA||'/'||EXP.ANIO as NUMERO
, EOGP.META_EXPEDIENT_ORGAN_ID
, MEOG.ID
, META.ID
, MEOG.ORGAN_GESTOR_ID OG_PROC_COMU
, og.nom OG_PROC_COMU_1
, og.pare_id 
, meta.organ_gestor_id OG_PROC_DIRECTE
, OGEXP.ID OG_EXPEDIENT
FROM  IPA_CONTINGUT CONT
    , IPA_EXPEDIENT EXP
    , IPA_EXPEDIENT_ORGANPARE EOGP
    , IPA_METAEXP_ORGAN MEOG
    , IPA_ORGAN_GESTOR OG
    , IPA_ORGAN_GESTOR OGEXP
    , IPA_METAEXPEDIENT META
WHERE CONT.ID=EXP.ID
AND EXP.ID=EOGP.EXPEDIENT_ID
AND EOGP.META_EXPEDIENT_ORGAN_ID=MEOG.ID(+)
AND MEOG.ORGAN_GESTOR_ID=OG.ID
AND MEOG.META_EXPEDIENT_ID=META.ID
AND EXP.ORGAN_GESTOR_ID=OGEXP.ID
AND CONT.ESBORRAT=0
AND EXP.ESTAT=0
AND META.ID=861
ORDER BY EXP.ID;

--SELECT JSP
select count(distinct expediente0_.id)
from ipa_expedient expediente0_
inner join ipa_node expediente0_1_ on expediente0_.id=expediente0_1_.id 
inner join ipa_contingut expediente0_2_ on expediente0_.id=expediente0_2_.id 
left outer join ipa_expedient_organpare metaexpedi1_ on expediente0_.id=metaexpedi1_.expedient_id 
left outer join ipa_metaexp_organ metaexpedi2_ on metaexpedi1_.meta_expedient_organ_id=metaexpedi2_.id cross 
join ipa_metaexpedient metaexpedi3_ 
where expediente0_.metaexpedient_id=metaexpedi3_.id
  AND expediente0_2_.ESBORRAT=0
  AND expediente0_2_.ENTITAT_ID=1
  AND expediente0_.ESTAT=0
  and (
        (expediente0_.metaexpedient_id in (24609)) --Meta expedients permesos
    or  (metaexpedi2_.organ_gestor_id in (74, 17268)) --Organs permesos
    )    
and (metaexpedi3_.PERMIS_DIRECTE=0 or expediente0_.metaexpedient_id in (24609))
and expediente0_.organ_gestor_id in (74, 17268);

--SELECT REACT
select distinct expedientr0_.id as id1_13_
from ipa_expedient expedientr0_ 
inner join ipa_node expedientr0_1_ on expedientr0_.id=expedientr0_1_.id 
inner join ipa_contingut expedientr0_2_ on expedientr0_.id=expedientr0_2_.id 
left outer join ipa_entitat entitatres1_ on expedientr0_2_.entitat_id=entitatres1_.id 
left outer join ipa_metaexpedient metaexpedi2_ on expedientr0_.metaexpedient_id=metaexpedi2_.id 
inner join ipa_metaexp_organ metaexpedi3_ on metaexpedi2_.id=metaexpedi3_.meta_expedient_id 
left outer join ipa_organ_gestor organgesto4_ on metaexpedi3_.organ_gestor_id=organgesto4_.id 
left outer join ipa_organ_gestor organgesto5_ on expedientr0_.organ_gestor_id=organgesto5_.id 
where
    expedientr0_.estat=0
    and entitatres1_.id=1
    and expedientr0_2_.esborrat=0    
    and (
        metaexpedi2_.id in (24609) 
        or organgesto4_.id in (74 , 17268)
        )   
    and (metaexpedi2_.PERMIS_DIRECTE=0 or metaexpedi2_.id in (24609)) 
    and (organgesto5_.id in (74 , 17268));