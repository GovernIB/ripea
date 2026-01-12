import { useEffect } from 'react';
import {useLocation} from 'react-router-dom';
import {useTranslation} from "react-i18next";

const TITLES: any = {
    '/expedient': "navigate.expedient",
    '/expedientPeticio': "navigate.expedientPeticio",
    '/usuariTasca': "navigate.usuariTasca",

    // Accions massives
    '/massiu/portafirmes': "navigate.massiu.portafirmes",
    '/massiu/firmasimpleweb': "navigate.massiu.firmasimpleweb",
    '/massiu/canviEstat': "navigate.massiu.canviEstat",
    '/massiu/tancament': "navigate.massiu.tancament",
    '/seguimentArxiuPendents': "navigate.massiu.seguimentArxiuPendents",
    '/massiu/csv': "navigate.massiu.csv",
    '/massiu/definitiu': "navigate.massiu.definitiu",
    '/massiu/canviPrioritats': "navigate.massiu.canviPrioritats",
    '/massiu/expedientPeticioCanviEstatDistribucio': "navigate.massiu.expedientPeticioCanviEstatDistribucio",
    '/massiu/procesarAnnexosPendents': "navigate.massiu.procesarAnnexosPendents",

    // Consultes
    '/contingutAdmin': "page.user.menu.continguts",
    '/historic': "page.user.menu.dadesEstadistiques",
    '/metaExpedientRevisio': "page.user.menu.revisar",
    '/seguimentPortafirmes': "page.user.menu.portafib",
    '/seguimentNotificacions': "page.user.menu.notib",
    '/seguimentPinbal': "page.user.menu.pinbalEnviades",
    '/seguimentTasques': "page.user.menu.assignacio",
    '/seguimentExpedientsPendents': "page.user.menu.pendents",
    '/expedientPeticioComunicades': "page.user.menu.comunicades",

    // Configurar
    '/metaExpedient': "page.user.menu.procediments",
    // '/metaExpedient/:id/permis': "page.user.menu.procedimentPermis",
    '/tipusDocumental': "page.user.menu.nti",
    '/grup': "page.user.menu.grups",
    // '/grupPermis/:id/permis': "page.user.menu.grupPermis",
    '/organgestor': "page.user.menu.organs",
    // '/organgestor/:id/permis': "page.user.menu.organPermis",
    '/permis': "page.user.menu.permisos",
    '/domini': "page.user.menu.dominis",
};

export const setTitlePage = (title:string) => {
    document.title = 'Ripea' + (title ?` - ${title}` :'');
}

const TitleHeaderConfigurator = () => {
    const { t } = useTranslation();
    const location = useLocation();

    useEffect(() => {
        // console.log(location, location.pathname, TITLES[location.pathname])
        setTitlePage(t(TITLES[location.pathname]));
    }, [location.pathname]);

    return null;
};

export default TitleHeaderConfigurator;
