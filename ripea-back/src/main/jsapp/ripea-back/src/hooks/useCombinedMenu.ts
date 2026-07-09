import { useMemo } from 'react';
import {
    useAccionesMassivas,
    useMenuAdmin,
    useMenuAdminLectura,
    useMenuAdminOrgan,
    useMenuDissenyOrgan,
    useMenuRevisor,
    useMenuSupAdmin,
    useMenuUsuari,
} from './useMenu';
import { useTranslation } from 'react-i18next';
import { useToProgramaAntic } from '@src/pages/user/UserHeadToolbar';
import { useLocation } from 'react-router-dom';
import { useUserSession } from '@src/components/Session';

const filterHiddenEntries = (entries: any[]): any[] =>
    entries
        .filter(e => !e.hidden)
        .map(e => e.children ? { ...e, children: filterHiddenEntries(e.children) } : e);

export const useCombinedMenu = () => {
    const { t } = useTranslation();
    const { toProgramaAntic } = useToProgramaAntic()
    const location = useLocation();
    const { rol } = useUserSession();

    const supAdmin = useMenuSupAdmin();
    const admin = useMenuAdmin();
    const adminLectura = useMenuAdminLectura();
    const organAdmin = useMenuAdminOrgan();
    const dissenyOrgan = useMenuDissenyOrgan();
    const usuari = useMenuUsuari();
    const accionesMasivas = useAccionesMassivas();
    const revisor = useMenuRevisor();

    const side: any[] = [];
    const content: any[] = [];
    const header: any[] = [
        {
            id: 'recargar',
            title: t('page.user.menu.backVersio'),
            icon: 'fast_rewind',
            componentProps: { color: '#ff9523' },
            onClick: () => {
                if (location.pathname.includes('/tasca/')) {
                    const url = location.pathname
                        .replace('/tasca/', '?tascaId=') + '&origenTasques=true';
                    toProgramaAntic(url)
                    return;
                }

                toProgramaAntic(location.pathname)
            },
        },
    ];

    const { sideMenuEntries, headerActions, additionalContents } = useMemo(() => {
        const menus = [
            { condition: rol?.isSupAdmin, data: supAdmin },
            { condition: rol?.isAdmin, data: admin },
            { condition: rol?.isAdminLectura, data: adminLectura },
            { condition: rol?.isOrganAdmin, data: organAdmin },
            { condition: rol?.isDissenyOrgan, data: dissenyOrgan },
            { condition: rol?.isUser, data: usuari },
            { condition: rol?.isAdmin || rol?.isOrganAdmin || rol?.isUser, data: accionesMasivas },
            { condition: rol?.isRevisor, data: revisor },
        ];

        menus.forEach(({ condition, data }) => {
            if (condition) {
                const { appEntries, entries, content: c } = data;
                header.push(...appEntries);
                side.push(...filterHiddenEntries(entries));
                if (c) content.push(c);
            }
        });

        return { sideMenuEntries: side, headerActions: header, additionalContents: content };
    }, [supAdmin, admin, adminLectura, dissenyOrgan, organAdmin, revisor, rol, usuari, accionesMasivas]);

    return { sideMenuEntries, headerActions, additionalContents };
};
