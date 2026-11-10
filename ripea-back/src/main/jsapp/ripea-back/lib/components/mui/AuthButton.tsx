import React from 'react';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Avatar from '@mui/material/Avatar';
import Badge from '@mui/material/Badge';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Divider from '@mui/material/Divider';
import { useTheme } from '@mui/material/styles';
import { TextAvatar } from './Avatars';
import { useBaseAppContext } from '../BaseAppContext';
import { useAuthContext } from '../AuthContext';

type AuthButtonProps = {
    badgeIcon?: string;
    onEditClick?: () => void;
    currentUserCode?: string;
    currentUserName?: string;
    additionalComponents?: React.ReactElement | React.ReactElement[];
};

type IconBadgeProps = React.PropsWithChildren & {
    icon?: string;
};

export type AuthButtonApi = {
    close: () => any;
};

export type AuthButtonApiRef = React.RefObject<AuthButtonApi | undefined>;

export type AuthButtonContextType = {
    apiRef: AuthButtonApiRef;
};

export const AuthButtonContext = React.createContext<AuthButtonContextType | undefined>(undefined);

export const useAuthButtonContext = () => {
    const context = React.useContext(AuthButtonContext);
    if (context === undefined) {
        throw new Error('useAuthButtonContext must be used within a AuthButtonProvider');
    }
    return context;
};

const UserAvatar: React.FC<any> = (props: any) => {
    const { currentUserName } = props;
    const { getTokenParsed } = useAuthContext();
    const [tokenParsed, setTokenParsed] = React.useState<any>();
    const userName = currentUserName ?? tokenParsed?.name;
    React.useEffect(() => {
        setTokenParsed(getTokenParsed());
    }, []);
    if (tokenParsed?.imageUrl) {
        return <Avatar alt={userName} title={userName} src={tokenParsed.imageUrl} />;
    } else if (userName) {
        return <TextAvatar text={userName} />;
    } else {
        return <Icon {...props}>account_circle</Icon>;
    }
};

const IconBadge: React.FC<IconBadgeProps> = (props) => {
    const { icon, children } = props;
    const theme = useTheme();
    return icon ? (
        <Badge
            overlap="circular"
            anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            badgeContent={
                icon && (
                    <Avatar
                        sx={{
                            width: 16,
                            height: 16,
                            border: `2px solid ${theme.palette.background.paper}`,
                            bgcolor: theme.palette.primary.dark,
                        }}>
                        <Icon sx={{ fontSize: 10 }}>{icon}</Icon>
                    </Avatar>
                )
            }>
            {children}
        </Badge>
    ) : (
        children
    );
};

const LoginButton: React.FC = () => {
    const { signIn } = useAuthContext();
    return (
        <Button color="inherit" onClick={() => signIn?.()}>
            Login
        </Button>
    );
};

const LoggedInUserButton: React.FC<AuthButtonProps> = (props) => {
    const { badgeIcon, onEditClick, currentUserCode, currentUserName, additionalComponents } =
        props;
    const { t } = useBaseAppContext();
    const apiRef = React.useRef<AuthButtonApi>(undefined);
    const buttonRef = React.useRef<HTMLButtonElement>(null);
    const { getTokenParsed, signOut } = useAuthContext();
    const [tokenParsed, setTokenParsed] = React.useState<any>();
    const [menuOpened, setMenuOpened] = React.useState(false);
    React.useEffect(() => {
        setTokenParsed(getTokenParsed());
    }, []);
    const id = menuOpened ? 'auth-menu' : undefined;
    const handleIconButtonClick = () => {
        setMenuOpened(true);
    };
    const handleMenuClose = () => {
        setMenuOpened(false);
    };
    apiRef.current = {
        close: handleMenuClose,
    };
    const userCode = currentUserCode ?? tokenParsed?.preferred_username;
    const userName = currentUserName ?? tokenParsed?.name;
    return (
        <AuthButtonContext.Provider value={{ apiRef }}>
            <IconBadge icon={badgeIcon}>
                <IconButton
                    id="auth-button"
                    ref={buttonRef}
                    size="small"
                    onClick={handleIconButtonClick}
                    aria-label="auth menu"
                    aria-controls={menuOpened ? id : undefined}
                    aria-haspopup="true"
                    aria-expanded={menuOpened ? 'true' : undefined}>
                    <UserAvatar currentUserName={currentUserName} />
                </IconButton>
            </IconBadge>
            <Menu
                id={id}
                anchorEl={() => buttonRef.current}
                open={menuOpened}
                onClose={() => handleMenuClose()}
                MenuListProps={{
                    'aria-labelledby': 'auth-button',
                }}>
                <MenuItem
                    disableRipple
                    sx={{
                        '&.MuiButtonBase-root:hover': {
                            bgcolor: 'transparent',
                            cursor: 'default',
                        },
                    }}>
                    <ListItemAvatar>
                        <UserAvatar currentUserName={currentUserName} />
                    </ListItemAvatar>
                    <ListItemText primary={userName} secondary={userCode} />
                    {onEditClick && (
                        <IconButton onClick={onEditClick} size="small" sx={{ ml: 2 }}>
                            <Icon fontSize="small">edit</Icon>
                        </IconButton>
                    )}
                </MenuItem>
                <Divider />
                {additionalComponents}
                {additionalComponents && <Divider />}
                <MenuItem onClick={() => signOut?.()}>
                    <ListItemIcon>
                        <Icon fontSize="small">logout</Icon>
                    </ListItemIcon>
                    <ListItemText>{t('app.auth.logout')}</ListItemText>
                </MenuItem>
            </Menu>
        </AuthButtonContext.Provider>
    );
};

const AuthButton: React.FC<AuthButtonProps> = (props) => {
    const { badgeIcon, onEditClick, currentUserCode, currentUserName, additionalComponents } =
        props;
    const { isReady, isAuthenticated } = useAuthContext();
    return isReady ? (
        !isAuthenticated ? (
            <LoginButton />
        ) : (
            <LoggedInUserButton
                badgeIcon={badgeIcon}
                onEditClick={onEditClick}
                currentUserCode={currentUserCode}
                currentUserName={currentUserName}
                additionalComponents={additionalComponents}
            />
        )
    ) : null;
};

export default AuthButton;
