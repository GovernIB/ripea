-- #694 Nou perfil Revisor de procediment
update ipa_metaexpedient set revisio_estat = 'REVISAT' where revisio_estat is null;

-- #767 Filtrar anotacions pendents per procediment
update ipa_expedient_peticio set metaexpedient_id = (select ipa_metaexpedient.id from ipa_metaexpedient where clasif_sia = (select procediment_codi from ipa_registre where ipa_registre.id = ipa_expedient_peticio.registre_id));
