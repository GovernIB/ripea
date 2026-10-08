import React, {useMemo} from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Typography, List, ListItem, ListItemText, Link, Divider, Icon } from '@mui/material';
import { useTranslation } from 'react-i18next';
import {GridPage, MenuEntry} from "reactlib";
import {useCombinedMenu} from "@src/hooks/useCombinedMenu.ts";
import {CardPage} from "@src/components/CardData.tsx";

const SitemapItem = ({id, icon, title, to, children}: MenuEntry) => {
    return <>
        {to && <ListItem key={id} disablePadding>
            <ListItemText
                primary={
                    <Link component={RouterLink} to={to} display={'flex'} alignItems={'center'} underline="hover">
                        {icon && <Icon sx={{mr: 1}}>{icon}</Icon>}
                        {title}
                    </Link>
                }
            />
        </ListItem>}
        {children?.map?.((item:MenuEntry) => (
            <SitemapItem key={item.id} {...item}/>
        ))}
    </>
}

const Sitemap: React.FC = () => {
    const { t } = useTranslation();

    const { sideMenuEntries: menuEntries } = useCombinedMenu();

    const sitemap: MenuEntry[] = useMemo(()=> {
        return [
            ...menuEntries,
            // additional
            { id: 'accessibilitat', title: t('navigate.accessibilitat'), icon: 'info', to: '/accessibilitat' }
        ]
    },[t, menuEntries])

    return (
        <GridPage autoHeight>
            <CardPage>
                <Typography variant="h4" component="h1" gutterBottom>
                    {t('page.sitemap.title')}
                </Typography>

                <Typography variant="body1" color="text.secondary" gutterBottom>
                    {t('page.sitemap.subtitle')}
                </Typography>

                <Divider sx={{ my: 1 }} />

                <List>
                    {sitemap.map((site:MenuEntry) => (
                        <SitemapItem key={site.id} {...site}/>
                    ))}
                </List>
            </CardPage>
        </GridPage>
    );
};

export default Sitemap;
