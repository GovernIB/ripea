import React, { useMemo } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Typography, List, ListItem, ListItemText, Link, Divider, Icon } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { GridPage, MenuEntry } from 'reactlib';
import { useCombinedMenu } from '@src/hooks/useCombinedMenu.ts';
import { ScreenFrame } from '@src/components/ScreenFrame.tsx';
import { icons } from '@src/util/icons.ts';

const SitemapItem = ({ id, icon, title, to, children }: MenuEntry) => {
    return (
        <>
            {to && (
                <ListItem key={id} disablePadding>
                    <ListItemText
                        primary={
                            <Link component={RouterLink} to={to} display={'flex'} alignItems={'center'} underline="hover">
                                {icon && <Icon sx={{ mr: 1 }}>{icon}</Icon>}
                                {title}
                            </Link>
                        }
                    />
                </ListItem>
            )}
            {children?.map?.((item: MenuEntry) => (
                <SitemapItem key={item.id} {...item} />
            ))}
        </>
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

                <List>
                    {sitemap.map((site: MenuEntry) => (
                        <SitemapItem key={site.id} {...site} />
                    ))}
                </List>
            </ScreenFrame>
        </GridPage>
    );
};

export default Sitemap;
