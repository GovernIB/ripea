import React, {useCallback, useMemo} from "react";
import {Grid, Button, Icon} from "@mui/material";
import {StyledBadge} from "../../components/StyledBadge.tsx";
import {useEntitatSession, useUserSession} from "../../components/Session.tsx";
import {useTranslation} from "react-i18next";
import useExecucioMassiva from "./actions/ExecucioMassivaGrid.tsx";
import {useNotificacionsSession, useTasquesSession} from "../../components/SseClient.tsx";
import {useResourceApiContext} from "reactlib";
import AppMenu from "../../components/AppMenu.tsx";
import {
    Link as RouterLink,
    LinkProps as RouterLinkProps,
    useLocation,
} from 'react-router-dom';
import {getImgFromBytes} from "../../App.tsx";
import goib_escut_logo from "../../assets/goib_escut_logo.png"
import {useSistemaDetail} from "./monitor/SistemaDetail.tsx";

export const icons = {
    expedient: 'folder',
    anotacio: 'email',
    tasca: 'assignment_turned_in',
    consulta: 'search',
    firma: 'draw',
}

export const useToProgramaAntic = () => {
    const { apiUrl } = useResourceApiContext();
    const cleanApiUrl = apiUrl.replace(/\/api\/?$/, '/');

    const getUrl = useCallback((ref: string) => {
        // console.log("apiUrl", apiUrl, cleanApiUrl)
        if (cleanApiUrl.endsWith("/") && ref.startsWith("/")) {
            ref = ref.substring(1);
        }
        if (!cleanApiUrl.endsWith("/") && !ref.startsWith("/")) {
            ref = "/" + ref
        }
        return `${cleanApiUrl}${ref}`;
    },[]);

    return {
        getUrl,
        toProgramaAntic: (ref: string) => window.location.href = getUrl(ref)
    };
};

const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

const generateMenuItems = (appMenuEntries: any[], entitat:any) => {
	/*
	 iconVariant — valores posibles (Material Icons):
	  - 'material-icons'          : filled (por defecto)
	  - 'material-icons-outlined' : outlined
	  - 'material-icons-round'    : round
	  - 'material-icons-sharp'    : sharp
	  - 'material-icons-two-tone' : two-tone
	 Uso: si entry.iconVariant tiene valor, se pasa como Icon.baseClassName; si no, usar 'material-icons'.
	*/
    return appMenuEntries?.length
        ? appMenuEntries.map((entry) => (
            <Button
                className="appMenuItem"
                key={entry.id}
                style={{ color: entitat?.conf?.colorLletra, marginLeft: 0, ...entry?.componentProps }}
                component={Link}
                to={entry.to} // Navegació amb React Router
                onClick={entry?.onClick}>
                {entry?.icon && <Icon baseClassName={entry?.iconVariant ?? 'material-icons'}>{entry?.icon}</Icon>}
                {entry.title}
            </Button>
        )) : [];
}

const AppMenuBadge = (props:any) => {
    return <StyledBadge textcolor={'white'} badgecolor={'primary'} {...props}/>
}
const MenuBadge = (props:any) => {
    return <StyledBadge sx={{pl: 0}} textcolor={'primary'} badgecolor={'white'} {...props}/>
}

const UserHeadToolbar = () => {
    const { t } = useTranslation();
    const { rol } = useUserSession();
    const { value: entitat } = useEntitatSession()
    const { toProgramaAntic } = useToProgramaAntic()
    const location = useLocation();

    const menuLogo = useMemo(() => {
        return getImgFromBytes(entitat?.conf?.menuicon) || goib_escut_logo
    }, [entitat?.conf?.menuicon]);

    const appMenuEntries:any[] =[
        {
            id: 'recargar',
            title: t('page.user.menu.backVersio'),
            icon: 'fast_rewind',
            componentProps: {color: '#ff9523'},
            onClick: () => {
                if (location.pathname.includes('/tasca/')) {
                    const url = location.pathname
                            .replace('/tasca/', '?tascaId=') + '&origenTasques=true';
                    toProgramaAntic(url)
                    return;
                }

                toProgramaAntic(location.pathname)
            },
        },
        // {
        //     id: 'theme_color',
        //     icon: darkMode ?'toggle_off' :'toggle_on',
        //     onClick: () => {
        //         setDarkMode((prev) => !prev)
        //     }
        // }
    ]
    const menuEntries:any[] =[]
    const contents:any[] = []

    const menus = [
        { condition: rol?.isSupAdmin, hook: useMenuSupAdmin },
        { condition: rol?.isAdmin, hook: useMenuAdmin },
        { condition: rol?.isAdminLectura, hook: useMenuAdminLectura },
        { condition: rol?.isOrganAdmin, hook: useMenuAdminOrgan },
        { condition: rol?.isDissenyOrgan, hook: useMenuDissenyOrgan },
        { condition: rol?.isUser, hook: useMenuUsuari },
        { condition: (rol?.isAdmin || rol?.isOrganAdmin || rol?.isUser), hook: useAccionesMassivas },
        { condition: rol?.isRevisor, hook: useMenuRevisor },
    ];

    menus.forEach(({ condition, hook }) => {
        const { appEntries, entries, content } = hook();
        if (condition) {
            appMenuEntries.push(...appEntries);
            menuEntries.push(...entries);
            contents.push(content);
        }
    });

    appMenuEntries.forEach((entrie)=>{
        entrie.title = <AppMenuBadge badgeContent={entrie?.badge} title={entrie?.hover}>{entrie.title}</AppMenuBadge>
    })

    menuEntries.forEach((entrie)=>{
        entrie.title = <MenuBadge badgeContent={entrie?.badge} title={entrie?.hover}>{entrie.title}</MenuBadge>
    })

    return <Grid container rowSpacing={1} columnSpacing={1} item xs={8} flexDirection={"row"} alignContent={'center'} justifyContent={'end'}>
        <Grid item xs={10} display={"flex"} justifyContent={"end"}>{...generateMenuItems(appMenuEntries, entitat)} {/* Menu */}</Grid>
        {menuEntries?.length > 0 && <Grid item xs={1} display={"flex"} justifyContent={"center"}>
            <AppMenu key="app_menu" menuEntries={menuEntries} logo={menuLogo}/> {/* Side Menu */}
            {...contents} {/* Additional content */}
        </Grid>}
    </Grid>
}

const useMenuSupAdmin = () => {
    const { t } = useTranslation();
    const { toProgramaAntic } = useToProgramaAntic();
    const {handleOpen, dialog} = useSistemaDetail();

    const appEntries:any[] = [
        {
            id: 'entitat',
            title: t('page.user.menu.entitat'),
            icon: 'account_balance',
            to: '/entitat',
        },
        {
            id: 'integracions',
            title: t('page.user.menu.integracions'),
            // icon: '',
            to: 'integracio',
        },
        {
            id: 'excepcions',
            title: t('page.user.menu.excepcions'),
            // icon: '',
            onClick: () => toProgramaAntic('excepcio'),
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
            // icon: '',
            children: [
                {
                    id: 'integracions',
                    title: t('page.user.menu.integracions'),
                    // icon: '',
                    to: 'integracio',
                },
                {
                    id: 'excepcions',
                    title: t('page.user.menu.excepcions'),
                    // icon: '',
                    to:'excepcio',
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
            // icon: '',
            children: [
                {
                    id: 'props',
                    title: t('page.user.menu.props'),
                    // icon: '',
                    to: 'config',
                },
                {
                    id: 'pinbal',
                    title: t('page.user.menu.pinbal'),
                    // icon: '',
                    to: 'pinbalServei',
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
    ]
    const content = <>
        {dialog}
    </>

    return {
        appEntries,
        entries,
        content,
    }
}
const useMenuAdmin = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession()
    const { t } = useTranslation();

    const appEntries:any[] = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: t('page.user.menu.anotacions'),
            badge: numNotif,
            icon: icons.anotacio,
            to: '/expedientPeticio',
        },
        {
            id: 'procediments',
            title: t('page.user.menu.procediments'),
            badge: user?.sessionScope?.organsNoSincronitzats,
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
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: t('page.user.menu.anotacions'),
            badge: numNotif,
            icon: icons.anotacio,
            to: '/expedientPeticio',
        },
        {
            id: 'config',
            title: t('page.user.menu.config'),
            // icon: '',
            children: [
                {
                    id: 'procediments',
                    title: t('page.user.menu.procediments'),
                    barge: user?.sessionScope?.organsNoSincronitzats,
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
                    divider: true,
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
                    divider: true,
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
            icon: icons.consulta,
            children: [
                {
                    id: 'continguts',
                    title: t('page.user.menu.continguts'),
                    // icon: '',
                    to: 'contingutAdmin',
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
                    hidden: !user?.sessionScope?.revisioActiva,
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
    ]
    const content = <>
    </>

    return {
        appEntries,
        entries,
        content,
    }
}
const useMenuAdminLectura = () => {
    const { value: user } = useUserSession();
    const { t } = useTranslation();

    const appEntries:any[] = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'procediments',
            title: t('page.user.menu.procediments'),
            badge: user?.sessionScope?.organsNoSincronitzats,
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
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'procediments',
            title: t('page.user.menu.procediments'),
            barge: user?.sessionScope?.organsNoSincronitzats,
            hover: t('page.user.menu.procedimentsTitle'),
			icon: 'integration_instructions',
			iconVariant: 'material-icons-outlined',
            to: '/metaExpedient',
        },
    ]
    const content = <>
    </>

    return {
        appEntries,
        entries,
        content,
    }
}
const useMenuAdminOrgan = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession()
    const { t } = useTranslation();

    const appEntries:any[] = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: t('page.user.menu.anotacions'),
            badge: numNotif,
            icon: icons.anotacio,
            to: '/expedientPeticio',
        },
        {
            id: 'procediments',
            title: t('page.user.menu.procediments'),
            badge: user?.sessionScope?.organsNoSincronitzats,
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
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: t('page.user.menu.anotacions'),
            badge: numNotif,
            icon: icons.anotacio,
            to: '/expedientPeticio',
        },
        {
            id: 'config',
            title: t('page.user.menu.config'),
            // icon: '',
            children: [
                {
                    id: 'procediments',
                    title: t('page.user.menu.procediments'),
                    badge: user?.sessionScope?.organsNoSincronitzats,
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
    ]
    const content = <>
    </>

    return {
        appEntries,
        entries,
        content,
    }
}
const useMenuDissenyOrgan = () => {
    const { t } = useTranslation();

    const appEntries:any[] = [
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
    const entries:any[] = []
    const content = <>
    </>

    return {
        appEntries,
        entries,
        content,
    }
}
const useMenuUsuari = () => {
    const { value: user } = useUserSession();
    const { value: numNotif } = useNotificacionsSession()
    const { value: numTasc } = useTasquesSession()
    const { t } = useTranslation();
    const { toProgramaAntic } = useToProgramaAntic();

    const appEntries:any[] = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: t('page.user.menu.anotacions'),
            badge: numNotif,
            icon: icons.anotacio,
            to: '/expedientPeticio',
        },
        {
            id: 'tasca',
            title: t('page.user.menu.tasca'),
            badge: numTasc,
            icon: icons.tasca,
            to: '/usuariTasca',
        },
    ];
    const entries = [
        {
            id: 'expedient',
            title: t('page.user.menu.expedient'),
            icon: icons.expedient,
            to: '/expedient',
        },
        {
            id: 'anotacions',
            title: t('page.user.menu.anotacions'),
            badge: numNotif,
            icon: icons.anotacio,
            to: '/expedientPeticio',
        },
        {
            id: 'tasca',
            title: t('page.user.menu.tasca'),
            badge: numTasc,
            icon: icons.tasca,
            to: '/usuariTasca',
        },
        {
            id: 'flux',
            title: t('page.user.menu.flux'),
            icon: icons.firma,
            onClick: () => toProgramaAntic('fluxusuari'),
            hidden: !user?.sessionScope?.isCreacioFluxUsuariActiu,
        },
        {
            id: 'consultar',
            title: t('page.user.menu.consultar'),
            icon: icons.consulta,
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
                    to: 'seguimentNotificacions',
                    hidden: !user?.sessionScope?.isMostrarSeguimentEnviamentsUsuariActiu,
                },
            ],
        },
    ]
    const content = <>
    </>

    return {
        appEntries,
        entries,
        content,
    }
}

const useAccionesMassivas = () => {
    const { value: user, rol } = useUserSession();
    const { t } = useTranslation();

    const {handleOpen, dialog} = useExecucioMassiva();

    const appEntries:any[] = [];
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
                    hiddden: !user?.sessionScope?.isUrlValidacioDefinida,
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
                    divider: true,
                },
                {
                    id: 'masives',
                    title: t('page.user.action.massives.label'),
                    // icon: '',
                    onClick: handleOpen,
                },
            ],
        },
        {
            divider: true,
        },
    ]
    const content = <>
        {dialog}
    </>

    return {
        appEntries,
        entries,
        content,
    }
}
const useMenuRevisor = () => {
    const { t } = useTranslation();
    const { toProgramaAntic } = useToProgramaAntic();

    const appEntries:any[] = [];
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
        appEntries,
        entries,
        content,
    }
}
export default UserHeadToolbar;