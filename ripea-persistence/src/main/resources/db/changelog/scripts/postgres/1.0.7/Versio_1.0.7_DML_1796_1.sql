UPDATE IPA_METAEXP_TASCA t1
SET ordre = sub.cnt
    FROM (
    SELECT t2a.id AS tasca_id,
           count(*) AS cnt
    FROM IPA_METAEXP_TASCA t2a
    JOIN IPA_METAEXP_TASCA t2b
      ON t2b.meta_expedient_id = t2a.meta_expedient_id
     AND (t2b.createddate < t2a.createddate
          OR (t2b.createddate = t2a.createddate AND t2b.id <= t2a.id))
    GROUP BY t2a.id
) sub
WHERE t1.id = sub.tasca_id;
