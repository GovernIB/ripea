import React from 'react';
import {
    Drawer,
    List,
    ListItemIcon,
    ListItemText,
    ListItemButton,
    Icon,
    IconButton,
    Divider,
    Box,
    Typography,
} from '@mui/material';
import {
    styled,
    useTheme,
    Theme,
    CSSObject
} from '@mui/material/styles';
import {useBaseAppContext} from "reactlib";
import { useSmallScreen, useSmallHeader } from '../../lib/util/useSmallScreen';
import logo from '../assets/goib_escut_logo.png';
import SideWrapper from "./SideWrapper";

export type MenuEntry = {
    id: string;
    title?: any;
    to?: string;
    icon?: string;
    onClick?: () => void;
    children?: MenuEntry[];
    divider?: boolean;
    hidden?: boolean;
};

export type MenuProps = {
    title?: string;
    entries?: MenuEntry[];
    level?: number;
    onTitleClose?: () => void;
    shrink?: boolean;
    iconClicked?: boolean;
    drawerWidth?: number;
};
type ListMenuContentProps = MenuProps & {
    onMenuItemClick?: () => void;
};

type MenuItemProps = React.PropsWithChildren & {
    primary: string;
    to?: string;
    icon?: string;
    level?: number;
    selected?: boolean;
    shrink?: boolean;
    onClick?: () => void;
    onMenuItemClick?: () => void;
}

type MenuTitleProps = {
    title: string;
    onClose?: () => void;
};

const openedMixin = (theme: Theme, width: number): CSSObject => ({
    width,
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
    overflowX: 'hidden',
});

const closedMixin = (theme: Theme): CSSObject => ({
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    overflowX: 'hidden',
    width: `calc(${theme.spacing(6)} + 1px)`,
    [theme.breakpoints.up('sm')]: {
        width: `calc(${theme.spacing(7)} + 1px)`,
    },
});

const ShrinkableDrawer = styled(Drawer, { shouldForwardProp: (prop) => prop !== 'open' && prop !== 'width' })(
    ({ theme, open, ...otherProps }) => {
        const width = (otherProps as any)?.['width'];
        const shrinkableStyles = {
            ...(open && {
                ...openedMixin(theme, width),
                '& .MuiDrawer-paper': openedMixin(theme, width),
            }),
            ...(!open && {
                ...closedMixin(theme),
                whiteSpace: 'nowrap',
                '& .MuiDrawer-paper': closedMixin(theme),
            }),
        };
        return {
            flexShrink: 0,
            boxSizing: 'border-box',
            ...shrinkableStyles
        };
    });

const StyledList = styled(List)<{ component?: React.ElementType }>({
    '& .MuiListItemIcon-root': {
        minWidth: 0,
        marginRight: 16,
        color: '#F6F6F6'
    },
    '& .MuiSvgIcon-root': {
        fontSize: 20,
    },
    paddingTop: 0,
    paddingBottom: 0,
});

const isCurrentMenuEntryOrAnyChildrenSelected = (menuEntry: MenuEntry, locationPath: string): boolean => {
    const selected = menuEntry.to != null && (menuEntry.to === locationPath || locationPath.startsWith(menuEntry.to + '/'));
    return selected || menuEntry.children?.find(e => isCurrentMenuEntryOrAnyChildrenSelected(e, locationPath)) != null;
}

const MenuItem: React.FC<MenuItemProps> = (props) => {
    const {
        primary,
        to,
        icon,
        level = 0,
        selected,
        shrink,
        onClick,
        onMenuItemClick,
        children
    } = props;
    const { getLinkComponent } = useBaseAppContext();
    const [expanded, setExpanded] = React.useState<boolean>(false);
    const itemButtonSx = {
        minHeight: shrink ? 48 : 48,
        justifyContent: !shrink ? 'initial' : 'center',
        py: 0,
        pl: 3,
        pr: !shrink ? 1 : 3,
        '& :before': (level ?? 0) > 0 && !shrink ? {
            content: '""',
            display: 'block',
            position: 'absolute',
            zIndex: '100',
            left: '34px',
            height: '70%',
            width: '2px',
            opacity: '1',
            background: selected ? 'hsl(210, 100%, 60%)' : 'hsl(215, 15%, 92%)',
        } : undefined
    };
    const itemIconSx = {
        minWidth: 0,
        ml: !shrink ? 1 : -1,
        mr: !shrink ? 1 : 'auto',
        justifyContent: 'center',
    };
    const itemTextSx = {
        opacity: !shrink ? 1 : 0,
        '& span': { fontSize: '14px', fontWeight: level === 0 ? 'bold' : undefined }
    };
    const handleMenuItemClick = () => {
        if (children != null) {
            setExpanded(expanded => !expanded);
        }

        // Close menu when clicking on a route option
        if (to) {
            onMenuItemClick?.();
        }
        onClick?.();
    }
    const processedIcon = shrink ? icon : (children != null ? (expanded ? 'expand_more' : 'chevron_right') : icon);
    const iconComponent = processedIcon ? <ListItemIcon sx={itemIconSx}>
        <Icon fontSize={'small'}>{processedIcon}</Icon>
    </ListItemIcon> : null;
    return <>
        {(!shrink || !children) && <ListItemButton
            title={shrink ? primary : undefined}
            selected={selected}
            to={children == null ? to : undefined}
            component={children == null ? (to != null ? getLinkComponent() : undefined) : undefined}
            onClick={handleMenuItemClick}
            sx={itemButtonSx}
            style={{ paddingLeft: level > 0 ? ((3 + 2 * level) * 8) + 'px' : '40px' }}>
            {iconComponent}
            <ListItemText primary={primary} sx={itemTextSx} />
        </ListItemButton>}
        {(shrink || expanded) && children}
    </>;
}

const ListMenuContent: React.FC<ListMenuContentProps> = (props) => {
    const {
        entries,
        level,
        shrink,
        onMenuItemClick,
    } = props;
    const { useLocationPath } = useBaseAppContext();
    const locationPath = useLocationPath();
    return <StyledList>
        {entries?.filter(entry=>!entry?.hidden)?.map((item, index) => {
            const selected = isCurrentMenuEntryOrAnyChildrenSelected(item, locationPath);
            const entryComponent = item.divider ?
                <Divider key={index} /> :
                <MenuItem
                    key={index}
                    primary={item.title ?? ''}
                    to={item.to}
                    icon={item.icon}
                    level={level}
                    selected={selected}
                    shrink={shrink}
                    onClick={item?.onClick}
                    onMenuItemClick={onMenuItemClick}>
                    {item.children?.length ? <Box sx={{ pl: 6 }}>
                        <ListMenuContent
                            entries={item.children}
                            level={(level ?? 0) + 1}
                            shrink={shrink}
                            onMenuItemClick={onMenuItemClick} />
                    </Box> : null}
                </MenuItem>;
            return entryComponent;
        })}
    </StyledList>;
}

const MenuTitle: React.FC<MenuTitleProps> = (props) => {
    const { title, onClose } = props;
    const theme = useTheme();
    const handleButtonClick = () => onClose?.();
    return <Box>
        <ListItemButton sx={{ backgroundColor: theme.palette.grey[200] }}>
            <ListItemIcon sx={{ minWidth: '40px' }}>
                <IconButton size="small" onClick={handleButtonClick}>
                    <Icon fontSize={'small'}>clear</Icon>
                </IconButton>
            </ListItemIcon>
            <ListItemText primary={title} sx={{ '& span': { fontWeight: 'bold' } }} />
        </ListItemButton>
        <Divider />
    </Box>;
}
const sideMenuClass = 'side-menu';
const titol = "Menú";
const closeIcon = 'close';

export const Menu: React.FC<MenuProps & { onClose?: () => void }> = (props) => {
    const {
        title,
        entries,
        onTitleClose,
        shrink,
        iconClicked,
        drawerWidth = 240,
        onClose,
    } = props;
    const smallScreen = useSmallScreen();
    const smallHeader = useSmallHeader();
    const [open, setOpen] = React.useState<boolean>(false);
    React.useEffect(() => {
        console.log('Open', open);
        setOpen(o => !o);
    }, [iconClicked]);
    React.useEffect(() => {
        setOpen(false);
    }, [smallScreen]);

    // Add ESC key handling to close the menu
    React.useEffect(() => {
        const handleEscKey = (event: KeyboardEvent) => {
            if (event.key === 'Escape') {
                setOpen(false);
                onClose?.();
            }
        };

        window.addEventListener('keydown', handleEscKey);

        return () => {
            window.removeEventListener('keydown', handleEscKey);
        };
    }, []);
    const handleMenuItemClick = () => {
        setOpen(false);
        onClose?.();
    }
    const drawerContent = <SideWrapper onOutsideClick={handleMenuItemClick}>
        <Box sx={{ mt: smallHeader ? 1 : 1 }} >
            {logo ? <Box sx={{ display: 'flex', mr: 2, pt: 1, pr: 2, cursor: 'pointer', 'img': { height: '38px' }, pl: 4 }}>
                <img src={logo} alt="logo" />
                <Typography
                    variant="h6"
                    component="span"
                    title={titol}
                    sx={{ flexGrow: 1, pl: 2 }}>
                    {titol}
                </Typography>
                <IconButton
                    color="inherit"
                    aria-label="close menu"
                    onClick={handleMenuItemClick}
                    edge="start"
                    sx={{flexGrow: 0, mr: 0 }}
                >
                    <Icon fontSize={'medium'}>{closeIcon}</Icon>
                </IconButton>
            </Box> : null}
            <Divider sx={{ color: '#F6F6F6', mb: 2, mt: 1 }}/>
        </Box>
        {title && <MenuTitle title={title} onClose={onTitleClose} />}
        <ListMenuContent
            entries={entries}
            shrink={!smallScreen ? shrink : false}
            onMenuItemClick={handleMenuItemClick} />
    </SideWrapper>;
    return <ShrinkableDrawer
        className={sideMenuClass}
        variant={'permanent'}
        open={!shrink}
        {...{ width: drawerWidth }}
        sx={{
            position: 'absolute', top: 0, right: 0,
            '& .MuiDrawer-paper': { right: 0, left: 'auto', backgroundColor: '#004B99', color: '#fff' },
        }}>
        {drawerContent}
    </ShrinkableDrawer>;
}

export default Menu;
