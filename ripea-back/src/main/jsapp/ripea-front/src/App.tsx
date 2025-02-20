import React from 'react';
// import { useTranslation } from 'react-i18next';
import { BaseApp } from './components/BaseApp';
import logo from './assets/logo.png';
import govern_logo from './assets/goib_logo_v.svg';
import uenegroma from './assets/uenegroma.png';
import feder7 from './assets/feder7.png';
import una_manera from './assets/una_manera.png';
import AppRoutes from './AppRoutes';


export const App: React.FC = () => {
    // const { t } = useTranslation();
    // const menuEntries = [{
    //     id: 'salut',
    //     title: t('menu.salut'),
    //     to: '/',
    //     icon: 'monitor_heart',
    //     resourceName: 'salut',
    // }, {
    //     id: 'app',
    //     title: t('menu.app'),
    //     to: '/app',
    //     icon: 'widgets',
    //     resourceName: 'app',
    // }];
    return <BaseApp
        code="cmd"
        logo={govern_logo}
        style={{height:'110px'}}
        logoStyle={{
            '& img': { height: '65px' },
            pl: 2,
            pr: 4,
            mr: 4,
            borderRight: '1px solid #fff'
        }}
        title="RIPEA"
        title_logo={logo}
        version="1.0.1"
        availableLanguages={['ca', 'es']}
        // menuEntries={menuEntries}
        appbarBackgroundColor="#ff9523"
        foot_logos={[uenegroma,feder7,una_manera]}
        // appbarBackgroundImg={headerBackground}
        >
        <AppRoutes />
    </BaseApp>;
}

export default App;
