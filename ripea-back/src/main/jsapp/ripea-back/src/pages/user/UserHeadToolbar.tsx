import {Grid} from "@mui/material";
import {StyledBadge} from "../../components/StyledBadge.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {useTranslation} from "react-i18next";
import useExecucioMassiva from "./actions/ExecucioMassivaGrid.tsx";
import {useNotificacionsSession, useTasquesSession} from "../../components/SseClient.tsx";
import {MenuEntry} from "reactlib";
import AppMenu from "../../components/AppMenu.tsx";

const toProgramaAntic = (ref:string) => {
    window.location.href = (`${import.meta.env.VITE_BASE_URL}${ref}`)
}

const generateAppMenu = (menuEntries: MenuEntry[] | undefined) => {
    return menuEntries?.length
        ? [<AppMenu key="app_menu" menuEntries={menuEntries} />]
        : [];
}

const MenuBadge = (props:any) => {
    return <StyledBadge sx={{pl: 0}} textcolor={'primary'} badgecolor={'white'} {...props}/>
}

const UserHeadToolbar = () => {
    const { value: user } = useUserSession();
    const isRolActualSupAdmin = user?.rolActual == 'IPA_SUPER';
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';
    const isRolActualDissenyOrgan = user?.rolActual == 'IPA_DISSENY';
    const isRolActualRevisor = user?.rolActual == 'IPA_REVISIO';
    const isRolActualUser = user?.rolActual == 'tothom';

    const menuEntries:any[] =[]
    const contents:any[] = []

    const menus = [
        { condition: isRolActualSupAdmin, hook: useMenuSupAdmin },
        { condition: isRolActualAdmin, hook: useMenuAdmin },
        { condition: isRolActualOrganAdmin, hook: useMenuAdminOrgan },
        { condition: isRolActualDissenyOrgan, hook: useMenuDissenyOrgan },
        { condition: isRolActualUser, hook: useMenuUsuari },
        { condition: (isRolActualAdmin || isRolActualOrganAdmin || isRolActualUser), hook: useAccionesMassivas },
        { condition: isRolActualRevisor, hook: useMenuRevisor },
    ];

    menus.forEach(({ condition, hook }) => {
        const { entries, content } = hook();
        if (condition) {
            menuEntries.push(...entries);
            contents.push(content);
        }
    });

    return <Grid container rowSpacing={1} columnSpacing={1} item xs={8} justifyContent={'space-between'}>
        <Grid item xs={12} display={'flex'} alignContent={'center'} justifyContent={'end'}>
            {...generateAppMenu(menuEntries)}
            {...contents}
        </Grid>
    </Grid>
}

const useMenuSupAdmin = () => {
    const { t } = useTranslation();

    const entries = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            // icon: '',
            to: '/expedient',
        },
        {
            id: 'monitoritzar',
            title: t('page.user.menu.monitoritzar'),
            // icon: '',
            children: [
                {
                    id: 'integracions',
                    title: t('page.user.menu.integracions'),
                    // icon: '',
                    onClick: () => toProgramaAntic('integracio'),
                },
                {
                    id: 'excepcions',
                    title: t('page.user.menu.excepcions'),
                    // icon: '',
                    onClick: () => toProgramaAntic('excepcio'),
                },
                {
                    id: 'monitor',
                    title: t('page.user.menu.monitor'),
                    // icon: '',
                    onClick: () => toProgramaAntic('monitor'),
                },
            ],
        },
        {
            id: 'config',
            title: t('page.user.menu.config'),
            // icon: '',
            children: [
                {
                    id: 'props',
                    title: t('page.user.menu.props'),
                    // icon: '',
                    onClick: () => toProgramaAntic('config'),
                },
                {
                    id: 'pinbal',
                    title: t('page.user.menu.pinbal'),
                    // icon: '',
                    onClick: () => toProgramaAntic('pinbalServei'),
                },
                {
                    id: 'segonPla',
                    title: t('page.user.menu.segonPla'),
                    // icon: '',
                    onClick: () => toProgramaAntic('scheduled'),
                },
                {
                    id: 'plugins',
                    title: t('page.user.menu.plugins'),
                    // icon: '',
                    onClick: () => toProgramaAntic('plugin'),
                },
            ],
        },
        {
            id: 'avisos',
            title: t('page.user.menu.avisos'),
            // icon: '',
            onClick: () => toProgramaAntic('avis'),
        },
    ]
    const content = <>
    </>

    return {
        entries,
        content,
    }
}
const useMenuAdmin = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession()
    const { t } = useTranslation();

    const entries = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            // icon: '',
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: <MenuBadge badgeContent={numNotif}>{t('page.user.menu.anotacions')}</MenuBadge>,
            // icon: '',
            onClick: () => toProgramaAntic('expedientPeticio'),
        },
        {
            id: 'config',
            title: t('page.user.menu.config'),
            // icon: '',
            children: [
                {
                    id: 'procediments',
                    title: <MenuBadge badgeContent={user?.sessionScope?.organsNoSincronitzats} title={t('page.user.menu.procedimentsTitle')} sx={{pl: 0}}>
                        {t('page.user.menu.procediments')}
                    </MenuBadge>,
                    // icon: '',
                    onClick: () => toProgramaAntic('metaExpedient'),
                },
                {
                    id: 'documents',
                    title: t('page.user.menu.documents'),
                    // icon: '',
                    onClick: () => toProgramaAntic('metaDocument'),
                    hidden: user?.sessionScope?.isDocumentsGeneralsEnabled,
                },
                {
                    divider: true,
                },
                {
                    id: 'nti',
                    title: t('page.user.menu.nti'),
                    // icon: '',
                    onClick: () => toProgramaAntic('tipusDocumental'),
                    hidden: user?.sessionScope?.isTipusDocumentsEnabled,
                },
                {
                    id: 'dominis',
                    title: t('page.user.menu.dominis'),
                    // icon: '',
                    onClick: () => toProgramaAntic('domini'),
                    hidden: user?.sessionScope?.isDominisEnabled,
                },
                {
                    id: 'grups',
                    title: t('page.user.menu.grups'),
                    // icon: '',
                    onClick: () => toProgramaAntic('grup'),
                },
                {
                    id: 'organs',
                    title: t('page.user.menu.organs'),
                    // icon: '',
                    onClick: () => toProgramaAntic('organgestor'),
                },
                {
                    id: 'url',
                    title: t('page.user.menu.url'),
                    // icon: '',
                    onClick: () => toProgramaAntic('urlInstruccio'),
                    hidden: !user?.sessionScope?.isDominisEnabled,
                },
                {
                    divider: true,
                },
                {
                    id: 'permisos',
                    title: t('page.user.menu.permisos'),
                    // icon: '',
                    onClick: () => toProgramaAntic('permis'),
                },
            ],
        },
        {
            id: 'consultar',
            title: t('page.user.menu.consultar'),
            // icon: '',
            children: [
                {
                    id: 'continguts',
                    title: t('page.user.menu.continguts'),
                    // icon: '',
                    onClick: () => toProgramaAntic('contingutAdmin'),
                },
                {
                    id: 'dadesEstadistiques',
                    title: t('page.user.menu.dadesEstadistiques'),
                    // icon: '',
                    onClick: () => toProgramaAntic('historic'),
                },
                {
                    id: 'revisar',
                    title: t('page.user.menu.revisar'),
                    // icon: '',
                    onClick: () => toProgramaAntic('metaExpedientRevisio'),
                    hidden: !user?.sessionScope?.revisioActiva,
                },
                {
                    id: 'portafib',
                    title: t('page.user.menu.portafib'),
                    // icon: '',
                    onClick: () => toProgramaAntic('seguimentPortafirmes'),
                },
                {
                    id: 'notib',
                    title: t('page.user.menu.notib'),
                    // icon: '',
                    onClick: () => toProgramaAntic('seguimentNotificacions'),
                },
                {
                    id: 'pinbalEnviades',
                    title: t('page.user.menu.pinbalEnviades'),
                    // icon: '',
                    onClick: () => toProgramaAntic('seguimentPinbal'),
                },
                {
                    id: 'assignacio',
                    title: t('page.user.menu.assignacio'),
                    // icon: '',
                    onClick: () => toProgramaAntic('seguimentTasques'),
                },
                {
                    id: 'pendents',
                    title: t('page.user.menu.pendents'),
                    // icon: '',
                    onClick: () => toProgramaAntic('seguimentExpedientsPendents'),
                },
                {
                    id: 'comunicades',
                    title: t('page.user.menu.comunicades'),
                    // icon: '',
                    onClick: () => toProgramaAntic('expedientPeticioComunicades'),
                },
            ],
        },
    ]
    const content = <>
    </>

    return {
        entries,
        content,
    }
}
const useMenuAdminOrgan = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession()
    const { t } = useTranslation();

    const entries = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            // icon: '',
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: <MenuBadge badgeContent={numNotif}>{t('page.user.menu.anotacions')}</MenuBadge>,
            // icon: '',
            onClick: () => toProgramaAntic('expedientPeticio'),
        },
        {
            id: 'config',
            title: t('page.user.menu.config'),
            // icon: '',
            children: [
                {
                    id: 'procediments',
                    title: <MenuBadge badgeContent={user?.sessionScope?.organsNoSincronitzats} title={t('page.user.menu.procedimentsTitle')} sx={{pl: 0}}>
                        {t('page.user.menu.procediments')}
                    </MenuBadge>,
                    // icon: '',
                    onClick: () => toProgramaAntic('metaExpedient'),
                },
                {
                    id: 'grups',
                    title: t('page.user.menu.grups'),
                    // icon: '',
                    onClick: () => toProgramaAntic('grup'),
                },
            ],
        },
    ]
    const content = <>
    </>

    return {
        entries,
        content,
    }
}
const useMenuDissenyOrgan = () => {
    const { t } = useTranslation();

    const entries = [
        {
            id: 'procediments',
            title: t('page.user.menu.procediments'),
            // icon: '',
            onClick: () => toProgramaAntic('metaExpedient'),
        },
        {
            id: 'grups',
            title: t('page.user.menu.grups'),
            // icon: '',
            onClick: () => toProgramaAntic('grup'),
        },
    ]
    const content = <>
    </>

    return {
        entries,
        content,
    }
}
const useMenuUsuari = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession()
    const { value: numTasc } = useTasquesSession()
    const { t } = useTranslation();

    const entries = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            // icon: '',
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: <MenuBadge badgeContent={numNotif}>{t('page.user.menu.anotacions')}</MenuBadge>,
            // icon: '',
            onClick: () => toProgramaAntic('expedientPeticio'),
        },
        {
            id: 'tasca',
            title: <MenuBadge badgeContent={numTasc}>{t('page.user.menu.tasca')}</MenuBadge>,
            // icon: '',
            onClick: () => toProgramaAntic('usuariTasca'),
        },
        {
            id: 'flux',
            title: t('page.user.menu.flux'),
            // icon: '',
            onClick: () => toProgramaAntic('fluxusuari'),
            hidden: !user?.sessionScope?.isCreacioFluxUsuariActiu,
        },
        {
            id: 'consultar',
            title: t('page.user.menu.consultar'),
            // icon: '',
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
                    onClick: () => toProgramaAntic('seguimentPortafirmes'),
                    hidden: !user?.sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu,
                },
                {
                    id: 'notib',
                    title: t('page.user.menu.notib'),
                    // icon: '',
                    onClick: () => toProgramaAntic('seguimentNotificacions'),
                    hidden: !user?.sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu,
                },
            ],
        },
    ]

    const content = <>
    </>

    return {
        entries,
        content,
    }
}

const useAccionesMassivas = () => {
    const { value: user } = useUserSession();
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const { t } = useTranslation();

    const {handleOpen, dialog} = useExecucioMassiva();

    const entries = [
        {
            id: 'massive',
            title: t('page.user.massive.title'),
            // icon: '',
            children: [
                {
                    id: 'portafirmes',
                    title: t('page.user.massive.portafirmes'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/portafirmes'),
                },
                {
                    id: 'firmar',
                    title: t('page.user.massive.firmar'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/firmasimpleweb'),
                },
                {
                    id: 'marcar',
                    title: t('page.user.massive.marcar'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/definitiu'),
                    hidden: !user?.sessionScope?.isConvertirDefinitiuActiu,
                },
                {
                    id: 'estat',
                    title: t('page.user.massive.estat'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/canviEstat'),
                },
                {
                    id: 'tancar',
                    title: t('page.user.massive.tancar'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/tancament'),
                },
                {
                    id: 'custodiar',
                    title: t('page.user.massive.custodiar'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('seguimentArxiuPendents'),
                },
                {
                    id: 'csv',
                    title: t('page.user.massive.csv'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/csv'),
                    hiddden: !user?.sessionScope?.isUrlValidacioDefinida,
                },
                {
                    id: 'anexos',
                    title: t('page.user.massive.anexos'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/procesarAnnexosPendents'),
                },
                {
                    id: 'anotacio',
                    title: t('page.user.massive.anotacio'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/expedientPeticioCanviEstatDistribucio'),
                    hidden: !isRolActualAdmin,
                },
                {
                    id: 'prioritat',
                    title: t('page.user.massive.prioritat'),
                    // icon: '',
                    onClick: ()=> toProgramaAntic('massiu/canviPrioritats'),
                },
                {
                    divider: true,
                },
                {
                    id: 'masives',
                    title: t('page.user.massive.masives'),
                    // icon: '',
                    onClick: handleOpen,
                },
            ],
        }
    ]
    const content = <>
        {dialog}
    </>

    return {
        entries,
        content,
    }
}
const useMenuRevisor = () => {
    const { t } = useTranslation();

    const entries = [
        {
            id: 'revisar',
            title: t('page.user.menu.revisar'),
            // icon: '',
            onClick: () => toProgramaAntic('metaExpedientRevisio'),
        },
    ]
    const content = <>
    </>

    return {
        entries,
        content,
    }
}
export default UserHeadToolbar;