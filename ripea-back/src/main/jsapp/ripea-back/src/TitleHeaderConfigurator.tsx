import { useEffect } from 'react';
import {useLocation} from 'react-router-dom';
import {useTranslation} from "react-i18next";

const TITLES: any = {
    '/expedient': "navigate.expedient",
    '/expedientPeticio': "navigate.expedientPeticio",
    '/usuariTasca': "navigate.usuariTasca",
    '/massiu/portafirmes': "navigate.massiu.portafirmes",
    '/massiu/firmasimpleweb': "navigate.massiu.firmasimpleweb",
    '/massiu/canviEstat': "navigate.massiu.canviEstat",
    '/massiu/tancament': "navigate.massiu.tancament",
    '/seguimentArxiuPendents': "navigate.massiu.seguimentArxiuPendents",
    '/massiu/csv': "navigate.massiu.csv",
    '/massiu/definitiu': "navigate.massiu.definitiu",
    '/massiu/canviPrioritats': "navigate.massiu.canviPrioritats",
    '/massiu/expedientPeticioCanviEstatDistribucio': "navigate.massiu.expedientPeticioCanviEstatDistribucio",
    '/seguimentPortafirmes': "page.user.menu.portafib",
    '/seguimentNotificacions': "page.user.menu.notib",
    '/seguimentPinbal': "page.user.menu.pinbalEnviades",
    '/seguimentTasques': "page.user.menu.assignacio",
};

export const setTitlePage = (title:string) => {
    document.title = 'Ripea' + (title ?` - ${title}` :'');
}

const TitleHeaderConfigurator = () => {
    const { t } = useTranslation();
    const location = useLocation();

    useEffect(() => {
        setTitlePage(t(TITLES[location.pathname]));
    }, [location.pathname]);

    return null;
};

export default TitleHeaderConfigurator;
