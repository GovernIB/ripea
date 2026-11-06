import React, { useMemo } from 'react';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { Typography, List, ListItem, Link, Divider, Icon } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { GridPage, MenuEntry } from 'reactlib';
import { useCombinedMenu } from '@src/hooks/useCombinedMenu.ts';
import { ScreenFrame } from '@src/components/ScreenFrame.tsx';
import { icons } from '@src/util/icons.ts';

// Una entrada és visible si té destí propi o algun descendent visible.
const hasContent = (entry: MenuEntry): boolean => !!entry.to || !!entry.children?.some?.((child: MenuEntry) => hasContent(child));

const SitemapItem = ({ icon, title, to, children }: MenuEntry) => {
    const { pathname } = useLocation();
    const visibleChildren = children?.filter?.(hasContent) ?? [];
    const iconEl = icon ? (
        <Icon aria-hidden="true" sx={{ mr: 1 }}>
            {icon}
        </Icon>
    ) : null;

    return (
        <ListItem disablePadding sx={{ display: 'block' }}>
            {to ? (
                <Link
                    component={RouterLink}
                    to={to}
                    display={'flex'}
                    alignItems={'center'}
                    underline="hover"
                    aria-current={pathname === to ? 'page' : undefined}
                    sx={{ py: 0.5 }}
                >
                    {iconEl}
                    {title}
                </Link>
            ) : (
                <Typography component="span" display={'flex'} alignItems={'center'} fontWeight={600} sx={{ py: 0.5 }}>
                    {iconEl}
                    {title}
                </Typography>
            )}
            {visibleChildren.length > 0 && (
                <List disablePadding sx={{ pl: 3 }}>
                    {visibleChildren.map((item: MenuEntry) => (
                        <SitemapItem key={item.id} {...item} />
                    ))}
                </List>
            )}
        </ListItem>
    );
};

const Sitemap: React.FC = () => {
    const { t } = useTranslation();

    const { sideMenuEntries: menuEntries } = useCombinedMenu();

    const sitemap: MenuEntry[] = useMemo(() => {
        return [
            ...menuEntries,
            // additional
            { id: 'accessibilitat', title: t('navigate.accessibilitat'), icon: icons.accessibilitat, to: '/accessibilitat' },
        ];
    }, [t, menuEntries]);

    return (
        <GridPage>
            <ScreenFrame icon={icons.mapaWeb} title={t('page.sitemap.title')}>
                <Typography variant="body1" color="text.secondary">
                    {t('page.sitemap.subtitle')}
                </Typography>

                <Divider />

                <nav aria-label={t('page.sitemap.title')}>
                    <List disablePadding>
                        {sitemap.filter(hasContent).map((site: MenuEntry) => (
                            <SitemapItem key={site.id} {...site} />
                        ))}
                    </List>
                </nav>
            </ScreenFrame>
        </GridPage>
    );
};

export default Sitemap;
