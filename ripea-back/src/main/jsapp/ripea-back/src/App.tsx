import React, {useMemo} from 'react';
import {BaseApp} from './components/BaseApp';
import logo from './assets/Drassana_RIP_DRA_COL.svg';
import goib_logo from './assets/goib_logo.svg';
import AppRoutes from './AppRoutes';
import {useEntitatSession} from "./components/Session.tsx";
import TitleHeaderConfigurator from "./TitleHeaderConfigurator.tsx";

export const App: React.FC = () => {
    const version = '1.0.1';
    const { value: entitat } = useEntitatSession()
    const entitatLogo = useMemo(() => {
        return entitat?.logoImgBytes ? `data:image/png;base64,${entitat?.logoImgBytes}` : null;
    }, [entitat]);
    const backgroundColor = useMemo(() => {
        return entitat?.capsaleraColorFons
    }, [entitat]);
    return <BaseApp
        code="cmd"
        logo={entitatLogo ?? goib_logo}
        style={{ height: '64px' }}
        logoStyle={{
            '& img': { height: '49px' },
            pl: 2,
            pr: 4,
            mr: 4,
            borderRight: `2px solid ${ entitat?.capsaleraColorLletra ?? '#000' }`
        }}
        title={<img src={logo} style={{ height: '80px', paddingTop: '5px' }} />}
        version={version}
        appbarBackgroundColor={backgroundColor ?? "#FFFFFF"}>
        <TitleHeaderConfigurator/>
        <AppRoutes/>
    </BaseApp>;
}

export default App;
