import { useUserSession } from '@src/components/Session';
import { useNotificacionsSession, useTasquesSession } from '@src/components/SseClient';
import useExecucioMassiva from '@src/pages/user/actions/ExecucioMassivaGrid';
import { useSistemaDetail } from '@src/pages/user/monitor/SistemaDetail';
import { useToProgramaAntic } from '@src/pages/user/UserHeadToolbar';
import { icons as iconsAppMenu } from '@src/util/icons';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';

export const useMenuSupAdmin = () => {
    const { t } = useTranslation();
    const { handleOpen, dialog } = useSistemaDetail();

    return useMemo(() => {
        const appEntries: any[] = [
            {
                id: 'entitat',
                title: t('page.user.menu.entitat'),
                icon: iconsAppMenu.entitat,
                to: '/entitat',
            },
            {
                id: 'integracions',
                title: t('page.user.menu.integracions'),
                icon: iconsAppMenu.integracio,
                to: '/integracio',
            },
            {
                id: 'excepcions',
                title: t('page.user.menu.excepcions'),
                icon: iconsAppMenu.excepcio,
                to: '/excepcio',
            },
        ];
        const entries = [
            {
                id: 'entitat',
                title: t('page.user.menu.entitat'),
                icon: iconsAppMenu.entitat,
                to: '/entitat',
            },
            {
                id: 'avisos',
                title: t('page.user.menu.avisos'),
                icon: iconsAppMenu.avis,
                to: '/avis',
            },
            {
                id: 'usuaris',
                title: t('page.user.menu.usuaris'),
                icon: iconsAppMenu.usuari,
                to: '/usuari',
            },
            {
                id: 'monitoritzar',
                title: t('page.user.menu.monitoritzar'),
                // description: '',
                icon: iconsAppMenu.monitoritzacio,
                children: [
                    {
                        id: 'integracions',
                        title: t('page.user.menu.integracions'),
                        icon: iconsAppMenu.integracio,
                        to: '/integracio',
                    },
                    {
                        id: 'excepcions',
                        title: t('page.user.menu.excepcions'),
                        icon: iconsAppMenu.excepcio,
                        to: 'excepcio',
                    },
                    {
                        id: 'monitor',
                        title: t('page.user.menu.monitor'),
                        icon: iconsAppMenu.monitorSistema,
                        onClick: handleOpen,
                    },
                ],
            },
            {
                id: 'config',
                title: t('page.user.menu.config'),
                icon: iconsAppMenu.configuracio,
                children: [
                    {
                        id: 'props',
                        title: t('page.user.menu.props'),
                        icon: iconsAppMenu.propietat,
                        to: '/config',
                    },
                    {
                        id: 'pinbal',
                        title: t('page.user.menu.pinbal'),
                        icon: iconsAppMenu.pinbal,
                        to: '/pinbalServei',
                    },
                ],
            },
        ];
        const content = <>{dialog}</>;

        return { appEntries, entries, content };
    }, [t, handleOpen, dialog]);
};

export const useMenuAdmin = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession();
    const { t } = useTranslation();

    return useMemo(() => {
        const appEntries: any[] = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'anotacions',
                title: t('page.user.menu.anotacions'),
                badgeProps: { badgeContent: numNotif, color: 'secondary' },
                icon: iconsAppMenu.anotacio,
                to: '/expedientPeticio',
            },
            {
                id: 'procediments',
                title: t('page.user.menu.procediments'),
                badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                hover: t('page.user.menu.procedimentsTitle'),
                icon: iconsAppMenu.procediment,
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
        ];
        const entries = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'anotacions',
                title: t('page.user.menu.anotacions'),
                badgeProps: { badgeContent: numNotif, color: 'secondary' },
                icon: iconsAppMenu.anotacio,
                to: '/expedientPeticio',
            },
            {
                id: 'config',
                title: t('page.user.menu.config'),
                icon: iconsAppMenu.configuracio,
                children: [
                    {
                        id: 'procediments',
                        title: t('page.user.menu.procediments'),
                        badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                        icon: iconsAppMenu.procediment,
                        to: '/metaExpedient',
                    },
                    {
                        id: 'nti',
                        title: t('page.user.menu.nti'),
                        icon: iconsAppMenu.tipusDocumental,
                        to: '/tipusDocumental',
                        hidden: !user?.sessionScope?.isTipusDocumentsEnabled,
                    },
                    {
                        id: 'dominis',
                        title: t('page.user.menu.dominis'),
                        icon: iconsAppMenu.domini,
                        to: '/domini',
                        hidden: !user?.sessionScope?.isDominisEnabled,
                    },
                    {
                        id: 'grups',
                        title: t('page.user.menu.grups'),
                        icon: iconsAppMenu.grup,
                        to: '/grup',
                    },
                    {
                        id: 'organs',
                        title: t('page.user.menu.organs'),
                        icon: iconsAppMenu.organGestor,
                        to: '/organgestor',
                    },
                    {
                        id: 'url',
                        title: t('page.user.menu.url'),
                        icon: iconsAppMenu.urlInstruccio,
                        to: '/urlInstruccio',
                        hidden: !user?.sessionScope?.isUrlInstruccioEnabled,
                    },
                    {
                        id: 'permisos',
                        title: t('page.user.menu.permisos'),
                        icon: iconsAppMenu.permis,
                        to: '/permis',
                    },
                ],
            },
            {
                id: 'consultar',
                title: t('page.user.menu.consultar'),
                icon: iconsAppMenu.consulta,
                children: [
                    {
                        id: 'continguts',
                        title: t('page.user.menu.continguts'),
                        icon: iconsAppMenu.contingut,
                        to: '/contingutAdmin',
                    },
                    /*{
                        id: 'dadesEstadistiques',
                        title: t('page.user.menu.dadesEstadistiques'),
                        icon: iconsAppMenu.estadistica,
                        onClick: () => toProgramaAntic('historic'),
                    },
                    {
                        id: 'revisar',
                        title: t('page.user.menu.revisar'),
                        icon: iconsAppMenu.revisio,
                        to: '/metaExpedientRevisio',
                        hidden: !user?.sessionScope?.isRevisioActiva,
                    },*/
                    {
                        id: 'portafib',
                        title: t('page.user.menu.portafib'),
                        icon: iconsAppMenu.portafirmes,
                        to: '/seguimentPortafirmes',
                    },
                    {
                        id: 'notib',
                        title: t('page.user.menu.notib'),
                        icon: iconsAppMenu.notificacio,
                        to: '/seguimentNotificacions',
                    },
                    {
                        id: 'pinbalEnviades',
                        title: t('page.user.menu.pinbalEnviades'),
                        icon: iconsAppMenu.pinbal,
                        to: '/seguimentPinbal',
                    },
                    {
                        id: 'assignacio',
                        title: t('page.user.menu.assignacio'),
                        icon: iconsAppMenu.tasca,
                        to: '/seguimentTasques',
                    },
                    {
                        id: 'pendents',
                        title: t('page.user.menu.pendents'),
                        icon: iconsAppMenu.expedient,
                        to: '/seguimentExpedientsPendents',
                    },
                    {
                        id: 'comunicades',
                        title: t('page.user.menu.comunicades'),
                        icon: iconsAppMenu.anotacio,
                        to: '/expedientPeticioComunicades',
                    },
                ],
            },
        ];
        const content = <></>;

        return { appEntries, entries, content };
    }, [t, numNotif, user]);
};

export const useMenuAdminLectura = () => {
    const { value: user } = useUserSession();
    const { t } = useTranslation();

    return useMemo(() => {
        const appEntries: any[] = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'procediments',
                title: t('page.user.menu.procediments'),
                badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                hover: t('page.user.menu.procedimentsTitle'),
                icon: iconsAppMenu.procediment,
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
        ];
        const entries = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'procediments',
                title: t('page.user.menu.procediments'),
                badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                hover: t('page.user.menu.procedimentsTitle'),
                icon: iconsAppMenu.procediment,
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
        ];
        const content = <></>;

        return { appEntries, entries, content };
    }, [t, user]);
};

export const useMenuAdminOrgan = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession();
    const { t } = useTranslation();

    return useMemo(() => {
        const appEntries: any[] = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'anotacions',
                title: t('page.user.menu.anotacions'),
                badgeProps: { badgeContent: numNotif, color: 'secondary' },
                icon: iconsAppMenu.anotacio,
                to: '/expedientPeticio',
            },
            {
                id: 'procediments',
                title: t('page.user.menu.procediments'),
                badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                hover: t('page.user.menu.procedimentsTitle'),
                icon: iconsAppMenu.procediment,
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
        ];
        const entries = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'anotacions',
                title: t('page.user.menu.anotacions'),
                badgeProps: { badgeContent: numNotif, color: 'secondary' },
                icon: iconsAppMenu.anotacio,
                to: '/expedientPeticio',
            },
            {
                id: 'config',
                title: t('page.user.menu.config'),
                icon: iconsAppMenu.configuracio,
                children: [
                    {
                        id: 'procediments',
                        title: t('page.user.menu.procediments'),
                        badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                        hover: t('page.user.menu.procedimentsTitle'),
                        icon: iconsAppMenu.procediment,
                        to: '/metaExpedient',
                    },
                    {
                        id: 'grups',
                        title: t('page.user.menu.grups'),
                        icon: iconsAppMenu.grup,
                        to: '/grup',
                    },
                ],
            },
        ];
        const content = <></>;

        return { appEntries, entries, content };
    }, [t, numNotif, user]);
};

export const useMenuDissenyOrgan = () => {
    const { t } = useTranslation();

    return useMemo(() => {
        const appEntries: any[] = [
            {
                id: 'procediments',
                title: t('page.user.menu.procediments'),
                icon: iconsAppMenu.procediment,
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
            {
                id: 'grups',
                title: t('page.user.menu.grups'),
                icon: iconsAppMenu.grup,
                to: '/grup',
            },
        ];
        const entries: any[] = [
            {
                id: 'config',
                title: t('page.user.menu.config'),
                icon: iconsAppMenu.configuracio,
                children: [
                    {
                        id: 'procediments',
                        title: t('page.user.menu.procediments'),
                        hover: t('page.user.menu.procedimentsTitle'),
                        icon: iconsAppMenu.procediment,
                        to: '/metaExpedient',
                    },
                    {
                        id: 'grups',
                        title: t('page.user.menu.grups'),
                        icon: iconsAppMenu.grup,
                        to: '/grup',
                    },
                ],
            },
        ];
        const content = <></>;

        return { appEntries, entries, content };
    }, [t]);
};

export const useMenuUsuari = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession();
    const { value: numTasc } = useTasquesSession();
    const { t } = useTranslation();
    const { toProgramaAntic } = useToProgramaAntic();

    return useMemo(() => {
        const appEntries: any[] = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'anotacions',
                title: t('page.user.menu.anotacions'),
                badgeProps: { badgeContent: numNotif, color: 'secondary' },
                icon: iconsAppMenu.anotacio,
                to: '/expedientPeticio',
            },
            {
                id: 'tasca',
                title: t('page.user.menu.tasca'),
                badgeProps: { badgeContent: numTasc, color: 'secondary' },
                icon: iconsAppMenu.tasca,
                to: '/usuariTasca',
            },
        ];
        const entries = [
            {
                id: 'expedient',
                title: t('page.user.menu.expedient'),
                icon: iconsAppMenu.expedient,
                to: '/expedient',
            },
            {
                id: 'anotacions',
                title: t('page.user.menu.anotacions'),
                badgeProps: { badgeContent: numNotif, color: 'secondary' },
                icon: iconsAppMenu.anotacio,
                to: '/expedientPeticio',
            },
            {
                id: 'tasca',
                title: t('page.user.menu.tasca'),
                badgeProps: { badgeContent: numTasc, color: 'secondary' },
                icon: iconsAppMenu.tasca,
                to: '/usuariTasca',
            },
            {
                id: 'flux',
                title: t('page.user.menu.flux'),
                icon: iconsAppMenu.firma,
                to: '/fluxusuari',
                hidden: !user?.sessionScope?.isCreacioFluxUsuariActiu,
            },
            {
                id: 'consultar',
                title: t('page.user.menu.consultar'),
                icon: iconsAppMenu.consulta,
                hidden: !(user?.sessionScope?.teAccesEstadistiques || user?.sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu),
                children: [
                    {
                        id: 'dadesEstadistiques',
                        title: t('page.user.menu.dadesEstadistiques'),
                        icon: iconsAppMenu.estadistica,
                        onClick: () => toProgramaAntic('historic'),
                        hidden: !user?.sessionScope?.teAccesEstadistiques,
                    },
                    {
                        id: 'portafib',
                        title: t('page.user.menu.portafib'),
                        icon: iconsAppMenu.portafirmes,
                        to: '/seguimentPortafirmes',
                        hidden: !user?.sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu,
                    },
                    {
                        id: 'notib',
                        title: t('page.user.menu.notib'),
                        icon: iconsAppMenu.notificacio,
                        to: '/seguimentNotificacions',
                        hidden: !user?.sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu,
                    },
                ],
            },
        ];
        const content = <></>;

        return { appEntries, entries, content };
    }, [t, numNotif, numTasc, user, toProgramaAntic]);
};

export const useAccionesMassivas = () => {
    const { value: user, rol } = useUserSession();
    const { t } = useTranslation();
    const { handleOpen, dialog } = useExecucioMassiva();

    return useMemo(() => {
        const appEntries: any[] = [];
        const entries = [
            {
                id: 'massive',
                title: t('page.user.massive.title'),
                icon: iconsAppMenu.massiva,
                children: [
                    {
                        id: 'portafirmes',
                        title: t('page.user.massive.portafirmes'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/portafirmes',
                    },
                    {
                        id: 'firmar',
                        title: t('page.user.massive.firmar'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/firmasimpleweb',
                    },
                    {
                        id: 'marcar',
                        title: t('page.user.massive.marcar'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/definitiu',
                        hidden: !user?.sessionScope?.isConvertirDefinitiuActiu,
                    },
                    {
                        id: 'estat',
                        title: t('page.user.massive.estat'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/canviEstat',
                    },
                    {
                        id: 'tancar',
                        title: t('page.user.massive.tancar'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/tancament',
                    },
                    {
                        id: 'custodiar',
                        title: t('page.user.massive.custodiar'),
                        icon: iconsAppMenu.monitoritzacio,
                        to: '/seguimentArxiuPendents',
                    },
                    {
                        id: 'csv',
                        title: t('page.user.massive.csv'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/csv',
                        hidden: !user?.sessionScope?.isUrlValidacioDefinida,
                    },
                    {
                        id: 'anexos',
                        title: t('page.user.massive.anexos'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/procesarAnnexosPendents',
                    },
                    {
                        id: 'anotacio',
                        title: t('page.user.massive.anotacio'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/expedientPeticioCanviEstatDistribucio',
                        hidden: !rol?.isAdmin,
                    },
                    {
                        id: 'prioritat',
                        title: t('page.user.massive.prioritat'),
                        icon: iconsAppMenu.massiva,
                        to: '/massiu/canviPrioritats',
                    },
                    {
                        id: 'masives',
                        title: t('page.user.action.massives.label'),
                        icon: iconsAppMenu.massiva,
                        onClick: handleOpen,
                    },
                ],
            },
        ];
        const content = <>{dialog}</>;

        return { appEntries, entries, content };
    }, [t, user, rol, handleOpen, dialog]);
};

export const useMenuRevisor = () => {
    const { t } = useTranslation();

    return useMemo(() => {
        const appEntries: any[] = [
            {
                id: 'revisar',
                title: t('page.user.menu.revisar'),
                icon: iconsAppMenu.procediment,
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
        ];
        const entries: any[] = [
            {
                id: 'revisar',
                title: t('page.user.menu.revisar'),
                icon: iconsAppMenu.procediment,
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
        ];
        const content = <></>;

        return { appEntries, entries, content };
    }, [t]);
};
