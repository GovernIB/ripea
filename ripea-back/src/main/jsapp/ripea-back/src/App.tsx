import React, {useMemo} from 'react';
import { BaseApp } from './components/BaseApp';
import logo from './assets/logo.png';
import govern_logo from './assets/govern-logo.png';
import AppRoutes from './AppRoutes';
import {useEntitatSession} from "./components/Session.tsx";

export const App: React.FC = () => {
    const version = '1.0.1';
    const { value: entitat } = useEntitatSession()

    const entitatLogo = useMemo(()=>{
        return entitat?.logoImgBytes ?`data:image/png;base64,${entitat?.logoImgBytes}` :null;
    }, [entitat]);

    return <BaseApp
        code="cmd"
        logo={entitatLogo ?? govern_logo}
        style={{ height: '110px' }}
        logoStyle={{
            '& img': { height: '80px' },
            // component: "img",
            // src: {entitatLogo},
            // alt: "Imagen cargada",
            pl: 2,
            pr: 4,
            mr: 4,
            borderRight: `1px solid ${ entitat?.capsaleraColorLletra ?? '#fff' }`
        }}
        title={<img src={logo} title={'RIPEA v' + version} alt={'RIPEA v' + version} />}
        version={version}
        availableLanguages={['ca', 'es']}
        appbarBackgroundColor={entitat?.capsaleraColorFons ?? "#ff9523"}>
        <AppRoutes />
    </BaseApp>;
}

export default App;
