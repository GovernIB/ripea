import { useEffect, useMemo, useState } from 'react';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { Breadcrumbs, Link, Typography, Icon } from '@mui/material';
import { useResourceApiService } from 'reactlib';
import * as builder from '../../../util/springFilterUtils.ts';

export type BreadcrumbItem = {
    id: string;
    nom: string;
    current: boolean;
};

// Normalitza 'treePath' a array d'identificadors (string)
const normalizeTreePath = (treePath: unknown): string[] | null => {
    if (!Array.isArray(treePath) || treePath.length === 0) {
        return null;
    }
    return treePath.map((x) => String(x));
};

import { useUserSession } from '../../../components/Session.tsx';

// Fil d'Ariadna de carpetes (sense repetir l'expedient); enllaços a '/contingut/{id}' si l'accés al detall de carpeta està activat
const ContingutBreadcrumb = (props: { expedient: any; carpetaNode?: any | null }) => {
    const { expedient, carpetaNode } = props;
    const { id: routeId } = useParams();
    const { value: user } = useUserSession();
    const contingutCarpetaDetallAccesActiva = user?.sessionScope?.isContingutCarpetaDetallAccesActiva === true;
    const [pathLabels, setPathLabels] = useState<Record<string, string>>({});

    const {
        isReady: apiContingutReady,
        find: contingutFind,
    } = useResourceApiService('contingutResource');

    const pathIds = useMemo(
        () => normalizeTreePath(carpetaNode?.treePath),
        [carpetaNode?.treePath],
    );

    useEffect(() => {
        if (!apiContingutReady || !pathIds || pathIds.length <= 2) {
            setPathLabels({});
            return;
        }
        const mids = pathIds.slice(1, -1);
        if (mids.length === 0) {
            setPathLabels({});
            return;
        }
        let cancelled = false;
        const filter = builder.or(...mids.map((mid) => builder.eq('id', mid)));
        contingutFind({
            filter,
            unpaged: true,
            perspectives: ['AUDITORIA'],
        })
            .then((res: any) => {
                if (cancelled) {
                    return;
                }
                const m: Record<string, string> = {};
                res?.rows?.forEach((row: any) => {
                    if (row?.id != null && row?.nom != null) {
                        m[String(row.id)] = row.nom;
                    }
                });
                setPathLabels(m);
            })
            .catch(() => {
                if (!cancelled) {
                    setPathLabels({});
                }
            });
        return () => {
            cancelled = true;
        };
    }, [apiContingutReady, pathIds, contingutFind]);

    const items: BreadcrumbItem[] = useMemo(() => {
        if (!expedient?.id || !carpetaNode || !routeId) {
            return [];
        }

        if (!pathIds || pathIds.length <= 1) {
            return [
                {
                    id: String(routeId),
                    nom: carpetaNode.nom ?? String(routeId),
                    current: true,
                },
            ];
        }

        const folderIds = pathIds.slice(1);
        return folderIds.map((pid, i) => {
            const isLast = i === folderIds.length - 1;
            const nom = isLast
                ? carpetaNode.nom ?? pathLabels[pid] ?? pid
                : pathLabels[pid] ?? pid;
            return { id: pid, nom, current: isLast };
        });
    }, [expedient, carpetaNode, pathIds, pathLabels, routeId]);

    if (items.length === 0) {
        return null;
    }

    return (
        <Breadcrumbs
            aria-label="breadcrumb"
            separator={<Icon sx={{ fontSize: '1.1rem', mx: 0.25 }}>chevron_right</Icon>}
            sx={{
                ml: 0.5,
                mt: 0,
                display: 'inline-flex',
                flexWrap: 'wrap',
                alignItems: 'center',
                minWidth: 0,
            }}
        >
            {items.map((item) =>
                item.current ? (
                    <Typography key={item.id} color="text.primary" variant="body2" component="span">
                        {item.nom}
                    </Typography>
                ) : contingutCarpetaDetallAccesActiva ? (
                    <Link
                        key={item.id}
                        component={RouterLink}
                        to={`/contingut/${item.id}`}
                        underline="hover"
                        color="inherit"
                        variant="body2"
                    >
                        {item.nom}
                    </Link>
                ) : (
                    <Typography key={item.id} color="inherit" variant="body2" component="span">
                        {item.nom}
                    </Typography>
                ),
            )}
        </Breadcrumbs>
    );
};

export default ContingutBreadcrumb;
