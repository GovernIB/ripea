import React from 'react';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Avatar from '@mui/material/Avatar';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Divider from '@mui/material/Divider';
import { TextAvatar } from './Avatars';
import { useBaseAppContext } from '../BaseAppContext';
import { useAuthContext } from '../AuthContext';

type AuthButtonProps = {
    additionalComponents?: React.ReactElement | React.ReactElement[];
};

const UserAvatar: React.FC = (props: any) => {
    const { getTokenParsed } = useAuthContext();
    const [tokenParsed, setTokenParsed] = React.useState<any>();
    React.useEffect(() => {
        setTokenParsed(getTokenParsed());
    }, []);
    if (tokenParsed?.imageUrl) {
        return <Avatar alt={tokenParsed?.name} title={tokenParsed?.name} src={tokenParsed.imageUrl} />;
    } else if (tokenParsed?.name) {
        return <TextAvatar text={tokenParsed.name} />;
    } else {
        return <Icon {...props}>account_circle</Icon>;
    }
}

const LoginButton: React.FC = () => {
    const { signIn } = useAuthContext();
    return <Button color="inherit" onClick={() => signIn?.()}>
        Login
    </Button>;
}

const LoggedInUserButton: React.FC<AuthButtonProps> = (props) => {
    const { additionalComponents } = props;
    const { t } = useBaseAppContext();
    const { getTokenParsed, signOut } = useAuthContext();
    const [tokenParsed, setTokenParsed] = React.useState<any>();
    const [anchorEl, setAnchorEl] = React.useState();
    React.useEffect(() => {
        setTokenParsed(getTokenParsed());
    }, []);
    const menuOpened = !!anchorEl;
    const id = menuOpened ? 'auth-menu' : undefined;
    const handleIconButtonClick = (event: any) => {
        setAnchorEl(event.currentTarget);
    }
    const handleMenuClose = () => {
        setAnchorEl(undefined);
    }
    return <>
        <IconButton
            id="auth-button"
            size="small"
            aria-label="auth menu"
            aria-controls={menuOpened ? id : undefined}
            aria-haspopup="true"
            aria-expanded={menuOpened ? 'true' : undefined}
            onClick={handleIconButtonClick}>
            <UserAvatar />
        </IconButton>
        <Menu
            id={id}
            anchorEl={anchorEl}
            open={menuOpened}
            onClose={() => handleMenuClose()}
            MenuListProps={{
                'aria-labelledby': 'auth-button',
            }}>
            <MenuItem disableRipple
                sx={{
                    "&.MuiButtonBase-root:hover": {
                        bgcolor: "transparent",
                        cursor: "default"
                    }
                }}>
                <ListItemAvatar>
                    <UserAvatar />
                </ListItemAvatar>
                <ListItemText
                    primary={tokenParsed?.name}
                    secondary={tokenParsed?.preferred_username} />
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
    </>;
}

const AuthButton: React.FC<AuthButtonProps> = (props) => {
    const { additionalComponents } = props;
    const { isReady, isAuthenticated } = useAuthContext();
    return isReady ? (!isAuthenticated ? <LoginButton /> : <LoggedInUserButton additionalComponents={additionalComponents} />) : null;
}

export default AuthButton;