UPDATE IPA_METAEXP_TASCA t1
SET t1.ordre = (
    SELECT count(*)
    FROM IPA_METAEXP_TASCA t2
    WHERE t2.meta_expedient_id = t1.meta_expedient_id
      AND (t2.createddate < t1.createddate
        OR (t2.createddate = t1.createddate AND t2.id <= t1.id))
);
