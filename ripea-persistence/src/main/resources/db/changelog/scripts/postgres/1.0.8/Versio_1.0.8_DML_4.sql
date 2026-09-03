-- RIPEA 1.0.8
-- Es retira la funcionalitat de tipus de document generals (els que no depenien de cap
-- procediment): cada procediment ja te els seus tipus per defecte. S'esborra la propietat
-- que l'habilitava, incloses les possibles sobreescriptures per entitat o per organ.
DELETE FROM IPA_CONFIG WHERE KEY = 'es.caib.ripea.habilitar.documentsgenerals';
