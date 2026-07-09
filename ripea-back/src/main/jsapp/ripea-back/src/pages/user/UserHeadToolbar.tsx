import React, { useCallback } from 'react';
import { Grid, Button, Icon } from '@mui/material';
import { useEntitatSession } from '../../components/Session.tsx';
import { useTranslation } from 'react-i18next';
import { useResourceApiContext } from 'reactlib';
import { Link as RouterLink, LinkProps as RouterLinkProps, useLocation } from 'react-router-dom';
import { useTheme, useMediaQuery } from '@mui/material';
import { StyledBadge } from '../../components/StyledBadge.tsx';

export const useToProgramaAntic = () => {
    const { apiUrl } = useResourceApiContext();
    const cleanApiUrl = apiUrl.replace(/\/api\/?$/, '/');

    const getUrl = useCallback(
        (ref: string) => {
            // console.log("apiUrl", apiUrl, cleanApiUrl)
            if (cleanApiUrl.endsWith('/') && ref.startsWith('/')) {
                ref = ref.substring(1);
            }
            if (!cleanApiUrl.endsWith('/') && !ref.startsWith('/')) {
                ref = '/' + ref;
            }
            return `${cleanApiUrl}${ref}`;
        },
        [cleanApiUrl]
    );

    return {
        getUrl,
        toProgramaAntic: (ref: string) => (window.location.href = getUrl(ref)),
    };
};

const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

const generateMenuItems = (appMenuEntries: any[], entitat: any, iconOnly: boolean) => {
    /*
	 iconVariant — valores posibles (Material Icons):
	  - 'material-icons'          : filled (por defecto)
	  - 'material-icons-outlined' : outlined
	  - 'material-icons-round'    : round
	  - 'material-icons-sharp'    : sharp
	  - 'material-icons-two-tone' : two-tone
	 Uso: si entry.iconVariant tiene valor, se pasa como Icon.baseClassName; si no, usar 'material-icons'.
	*/
    return appMenuEntries?.length
        ? appMenuEntries.map((entry) => (
              <Button
                  className="appMenuItem"
                  title={entry.hover || entry.title}
                  key={entry.id}
                  style={{ color: entitat?.conf?.colorLletra, minWidth: '3rem', marginLeft: 0, ...entry?.componentProps }}
                  {...(entry.to ? { component: Link, to: entry.to } : {})}
                  onClick={entry?.onClick}
              >
                  {entry?.icon && <Icon baseClassName={entry?.iconVariant ?? 'material-icons'}>{entry?.icon}</Icon>}
                  {!iconOnly && entry.children}
              </Button>
          ))
        : [];
};

const AppMenuBadge = (props: any) => {
    return <StyledBadge sx={{ px: 0.5 }} textcolor={'white'} badgecolor={'primary'} {...props} />;
};

const UserHeadToolbar = () => {
    const { t } = useTranslation();
    const { value: entitat } = useEntitatSession();
    const { toProgramaAntic } = useToProgramaAntic();
    const location = useLocation();

    // Menu superior
    const appMenuEntries: any[] = [
        {
            id: 'recargar',
            title: t('page.user.menu.backVersio'),
            icon: 'fast_rewind',
            componentProps: { color: '#ff9523' },
            onClick: () => {
                if (location.pathname.includes('/tasca/')) {
                    const url = location.pathname.replace('/tasca/', '?tascaId=') + '&origenTasques=true';
                    toProgramaAntic(url);
                    return;
                }

                toProgramaAntic(location.pathname);
            },
        },
    ];

    appMenuEntries.forEach((entrie) => {
        entrie.children = <AppMenuBadge badgeContent={entrie?.badge}>{entrie.title}</AppMenuBadge>;
    });

    const theme = useTheme();
    const iconOnly = useMediaQuery(theme.breakpoints.down('md'));

    return (
        <Grid container display={'flex'} rowSpacing={1} flexDirection={'row'} alignContent={'center'} justifyContent={'end'} sx={{ mr: 2 }}>
            <Grid size={11} gap={1.5} display={'flex'} justifyContent={'end'}>
                {...generateMenuItems(appMenuEntries, entitat, iconOnly)}
            </Grid>
        </Grid>
    );
};

export default UserHeadToolbar;
