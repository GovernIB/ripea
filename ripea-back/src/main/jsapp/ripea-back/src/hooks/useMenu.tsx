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
                icon: 'account_balance',
                to: '/entitat',
            },
            {
                id: 'integracions',
                title: t('page.user.menu.integracions'),
                icon: 'build',
                to: '/integracio',
            },
            {
                id: 'excepcions',
                title: t('page.user.menu.excepcions'),
                icon: 'warning',
                to: '/excepcio',
            },
        ];
        const entries = [
            {
                id: 'entitat',
                title: t('page.user.menu.entitat'),
                icon: 'account_balance',
                to: '/entitat',
            },
            {
                id: 'avisos',
                title: t('page.user.menu.avisos'),
                icon: 'campaign',
                to: '/avis',
            },
            {
                id: 'monitoritzar',
                title: t('page.user.menu.monitoritzar'),
                // description: '',
                icon: 'monitor',
                children: [
                    {
                        id: 'integracions',
                        title: t('page.user.menu.integracions'),
                        // icon: '',
                        to: '/integracio',
                    },
                    {
                        id: 'excepcions',
                        title: t('page.user.menu.excepcions'),
                        // icon: '',
                        to: 'excepcio',
                    },
                    {
                        id: 'monitor',
                        title: t('page.user.menu.monitor'),
                        // icon: '',
                        onClick: handleOpen,
                    },
                ],
            },
            {
                id: 'config',
                title: t('page.user.menu.config'),
                icon: 'settings',
                children: [
                    {
                        id: 'props',
                        title: t('page.user.menu.props'),
                        // icon: '',
                        to: '/config',
                    },
                    {
                        id: 'pinbal',
                        title: t('page.user.menu.pinbal'),
                        // icon: '',
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
                icon: 'integration_instructions',
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
                icon: 'settings',
                children: [
                    {
                        id: 'procediments',
                        title: t('page.user.menu.procediments'),
                        badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                        // icon: '',
                        to: '/metaExpedient',
                    },
                    {
                        id: 'documents',
                        title: t('page.user.menu.documents'),
                        // icon: '',
                        to: '/metaDocument',
                        hidden: !user?.sessionScope?.isDocumentsGeneralsEnabled,
                    },
                    {
                        id: 'nti',
                        title: t('page.user.menu.nti'),
                        // icon: '',
                        to: '/tipusDocumental',
                        hidden: !user?.sessionScope?.isTipusDocumentsEnabled,
                    },
                    {
                        id: 'dominis',
                        title: t('page.user.menu.dominis'),
                        // icon: '',
                        to: '/domini',
                        hidden: !user?.sessionScope?.isDominisEnabled,
                    },
                    {
                        id: 'grups',
                        title: t('page.user.menu.grups'),
                        // icon: '',
                        to: '/grup',
                    },
                    {
                        id: 'organs',
                        title: t('page.user.menu.organs'),
                        // icon: '',
                        to: '/organgestor',
                    },
                    {
                        id: 'url',
                        title: t('page.user.menu.url'),
                        // icon: '',
                        to: '/urlInstruccio',
                        hidden: !user?.sessionScope?.isUrlInstruccioEnabled,
                    },
                    {
                        id: 'permisos',
                        title: t('page.user.menu.permisos'),
                        // icon: '',
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
                        // icon: '',
                        to: '/contingutAdmin',
                    },
                    /*{
                        id: 'dadesEstadistiques',
                        title: t('page.user.menu.dadesEstadistiques'),
                        // icon: '',
                        onClick: () => toProgramaAntic('historic'),
                    },
                    {
                        id: 'revisar',
                        title: t('page.user.menu.revisar'),
                        // icon: '',
                        to: '/metaExpedientRevisio',
                        hidden: !user?.sessionScope?.isRevisioActiva,
                    },*/
                    {
                        id: 'portafib',
                        title: t('page.user.menu.portafib'),
                        // icon: '',
                        to: '/seguimentPortafirmes',
                    },
                    {
                        id: 'notib',
                        title: t('page.user.menu.notib'),
                        // icon: '',
                        to: '/seguimentNotificacions',
                    },
                    {
                        id: 'pinbalEnviades',
                        title: t('page.user.menu.pinbalEnviades'),
                        // icon: '',
                        to: '/seguimentPinbal',
                    },
                    {
                        id: 'assignacio',
                        title: t('page.user.menu.assignacio'),
                        // icon: '',
                        to: '/seguimentTasques',
                    },
                    {
                        id: 'pendents',
                        title: t('page.user.menu.pendents'),
                        // icon: '',
                        to: '/seguimentExpedientsPendents',
                    },
                    {
                        id: 'comunicades',
                        title: t('page.user.menu.comunicades'),
                        // icon: '',
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
                icon: 'integration_instructions',
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
                icon: 'integration_instructions',
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
                icon: 'integration_instructions',
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
                icon: 'settings',
                children: [
                    {
                        id: 'procediments',
                        title: t('page.user.menu.procediments'),
                        badgeProps: { badgeContent: user?.sessionScope?.organsNoSincronitzats, color: 'secondary' },
                        hover: t('page.user.menu.procedimentsTitle'),
                        // icon: '',
                        to: '/metaExpedient',
                    },
                    {
                        id: 'grups',
                        title: t('page.user.menu.grups'),
                        // icon: '',
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
                icon: 'integration_instructions',
                iconVariant: 'material-icons-outlined',
                to: '/metaExpedient',
            },
            {
                id: 'grups',
                title: t('page.user.menu.grups'),
                icon: 'groups',
                to: '/grup',
            },
        ];
        const entries: any[] = [];
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
                onClick: () => toProgramaAntic('fluxusuari'),
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
                        // icon: '',
                        onClick: () => toProgramaAntic('historic'),
                        hidden: !user?.sessionScope?.teAccesEstadistiques,
                    },
                    {
                        id: 'portafib',
                        title: t('page.user.menu.portafib'),
                        // icon: '',
                        to: '/seguimentPortafirmes',
                        hidden: !user?.sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu,
                    },
                    {
                        id: 'notib',
                        title: t('page.user.menu.notib'),
                        // icon: '',
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
                icon: 'list_alt',
                children: [
                    {
                        id: 'portafirmes',
                        title: t('page.user.massive.portafirmes'),
                        // icon: '',
                        to: '/massiu/portafirmes',
                    },
                    {
                        id: 'firmar',
                        title: t('page.user.massive.firmar'),
                        // icon: '',
                        to: '/massiu/firmasimpleweb',
                    },
                    {
                        id: 'marcar',
                        title: t('page.user.massive.marcar'),
                        // icon: '',
                        to: '/massiu/definitiu',
                        hidden: !user?.sessionScope?.isConvertirDefinitiuActiu,
                    },
                    {
                        id: 'estat',
                        title: t('page.user.massive.estat'),
                        // icon: '',
                        to: '/massiu/canviEstat',
                    },
                    {
                        id: 'tancar',
                        title: t('page.user.massive.tancar'),
                        // icon: '',
                        to: '/massiu/tancament',
                    },
                    {
                        id: 'custodiar',
                        title: t('page.user.massive.custodiar'),
                        // icon: '',
                        to: '/seguimentArxiuPendents',
                    },
                    {
                        id: 'csv',
                        title: t('page.user.massive.csv'),
                        // icon: '',
                        to: '/massiu/csv',
                        hidden: !user?.sessionScope?.isUrlValidacioDefinida,
                    },
                    {
                        id: 'anexos',
                        title: t('page.user.massive.anexos'),
                        // icon: '',
                        to: '/massiu/procesarAnnexosPendents',
                    },
                    {
                        id: 'anotacio',
                        title: t('page.user.massive.anotacio'),
                        // icon: '',
                        to: '/massiu/expedientPeticioCanviEstatDistribucio',
                        hidden: !rol?.isAdmin,
                    },
                    {
                        id: 'prioritat',
                        title: t('page.user.massive.prioritat'),
                        // icon: '',
                        to: '/massiu/canviPrioritats',
                    },
                    {
                        id: 'masives',
                        title: t('page.user.action.massives.label'),
                        // icon: '',
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
                // icon: '',
                to: '/metaExpedient',
            },
        ];
        const entries: any[] = [];
        const content = <></>;

        return { appEntries, entries, content };
    }, [t]);
};
