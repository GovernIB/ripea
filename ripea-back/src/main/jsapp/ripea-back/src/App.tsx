import React from 'react';
import { BaseApp } from './components/BaseApp';
import logo from './assets/logo.png';
import govern_logo from './assets/goib_logo_v.svg';
import AppRoutes from './AppRoutes';

export const App: React.FC = () => {
    const version = '1.0.1';
    return <BaseApp
        code="cmd"
        logo={govern_logo}
        style={{ height: '110px' }}
        logoStyle={{
            '& img': { height: '80px' },
            pl: 2,
            pr: 4,
            mr: 4,
            borderRight: '1px solid #fff'
        }}
        title={<img src={logo} alt={'RIPEA v' + version} />}
        version={version}
        availableLanguages={['ca', 'es']}
        appbarBackgroundColor="#ff9523">
        <AppRoutes />
    </BaseApp>;
}

export default App;
