import { useEffect } from 'react';
import {useLocation} from 'react-router-dom';
import {useTranslation} from "react-i18next";

const TITLES: any = {
    '/expedient': "navigate.expedient",
    '/expedientPeticio': "navigate.expedientPeticio",
    '/usuariTasca': "navigate.usuariTasca",
    '/massiu/portafirmes': "navigate.massiu.portafirmes",
    '/massiu/firmasimpleweb': "navigate.massiu.firmasimpleweb",
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
