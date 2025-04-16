import React from 'react';
import { BaseApp } from './components/BaseApp';
import logo from './assets/logo.png';
import govern_logo from './assets/govern-logo.png';
import AppRoutes from './AppRoutes';
import {useEntitatSession} from "./components/Session.tsx";

export const App: React.FC = () => {
    const version = '1.0.1';
    const { value: entitat } = useEntitatSession()

    return <BaseApp
        code="cmd"
        logo={govern_logo}
        style={{ height: '110px' }}
        logoStyle={{
            '& img': { height: '80px' },
            pl: 2,
            pr: 4,
            mr: 4,
            borderRight: `1px solid ${ entitat?.capsaleraColorLletra ?? '#fff' }`
        }}
        title={<img src={logo} alt={'RIPEA v' + version} />}
        version={version}
        availableLanguages={['ca', 'es']}
        appbarBackgroundColor={entitat?.capsaleraColorFons ?? "#ff9523"}>
        <AppRoutes />
    </BaseApp>;
}

export default App;
