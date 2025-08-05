import React, {useEffect, useMemo} from 'react';
import {BaseApp} from './components/BaseApp';
import logo from './assets/Drassana_RIP_DRA_COL.svg';
import goib_logo from './assets/goib_logo.svg';
import logo_caib from '../public/logo_caib.svg';
import AppRoutes from './AppRoutes';
import {useEntitatSession} from "./components/Session.tsx";
import TitleHeaderConfigurator from "./TitleHeaderConfigurator.tsx";

const changeFavicon = (faviconUrl:any) => {
    const link =
        document.querySelector("link[rel~='icon']") ||
        document.createElement('link');

    link.rel = 'icon';
    link.href = faviconUrl;

    document.getElementsByTagName('head')[0].appendChild(link);
};

export const App: React.FC = () => {
    const version = '1.0.1';
    const { value: entitat } = useEntitatSession()
    const entitatLogo = useMemo(() => {
        return entitat?.logoImgBytes ? `data:image/png;base64,${entitat?.logoImgBytes}` : goib_logo;
    }, [entitat]);
    const backgroundColor = useMemo(() => {
        return entitat?.capsaleraColorFons
    }, [entitat]);
    useEffect(() => {
        changeFavicon(entitat?.logoImgBytes ?entitatLogo :logo_caib)
    }, [entitatLogo]);
    return <BaseApp
        code="cmd"
        logo={entitatLogo}
        // style={{ height: '110px' }}
        logoStyle={{
            '& img': { height: '60px' },
            pl: 2,
            pr: 4,
            mr: 4,
            borderRight: `2px solid ${ entitat?.capsaleraColorLletra ?? '#000' }`
        }}
        title={<img src={logo} title={'RIPEA v' + version} style={{ height: '80px' }} alt={'RIPEA v' + version} />}
        version={version}
        appbarBackgroundColor={backgroundColor ?? "#FFFFFF"}>
        <TitleHeaderConfigurator/>
        <AppRoutes/>
    </BaseApp>;
}

export default App;
