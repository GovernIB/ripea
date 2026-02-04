import React, {useEffect} from 'react';
import {BaseApp} from './components/BaseApp';
import ripea_logo from './assets/Drassana_RIP_DRA_COL.svg';
import favicon from './assets/favicon.png'
import governLogo from './assets/govern-logo.png'
import AppRoutes from './AppRoutes';
import {useEntitatSession} from "./components/Session.tsx";
import TitleHeaderConfigurator from "./TitleHeaderConfigurator.tsx";

const changeFavicon = (faviconUrl:any) => {
    const link:any =
        document.querySelector("link[rel~='icon']") ||
        document.createElement('link');

    link.rel = 'icon';
    link.href = faviconUrl;

    document.getElementsByTagName('head')[0].appendChild(link);
};

export const getImgFromBytes = (img:string) :string | undefined => {
    return img ?`data:image/png;base64,${img}` :undefined;
}

export const App: React.FC = () => {
    const version = '1.0.1';
    const { value: entitat } = useEntitatSession()
    useEffect(() => {
        changeFavicon(getImgFromBytes(entitat?.conf?.favicon) || favicon)
    }, [entitat?.conf?.favicon, favicon]);
    return <BaseApp
        code="cmd"
        logo={getImgFromBytes(entitat?.conf?.logo) || governLogo}
        style={{ height: '64px' }}
        logoStyle={{
            '& img': { height: '49px' },
            pl: 2,
            pr: 4,
            mr: 4,
            borderRight: `2px solid ${entitat?.conf?.colorLletra}`
        }}
        title={<img src={ripea_logo} style={{ height: '80px', paddingTop: '5px' }} />}
        version={version}
        appbarBackgroundColor={entitat?.conf?.colorFons}>
        <TitleHeaderConfigurator/>
        <AppRoutes/>
    </BaseApp>;
}

export default App;
