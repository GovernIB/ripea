import React from 'react';
import MuiAppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import AuthButton from './AuthButton';
import { useOptionalAuthContext } from '../AuthContext';
import { toolbarBackgroundStyle } from '../../util/toolbar';

type AppBarProps = {
    title: string | React.ReactElement;
    version?: string;
    logo?: string;
    logoStyle?: any;
    menuButton: React.ReactNode;
    additionalToolbarComponents?: React.ReactElement | React.ReactElement[];
    additionalAuthComponents?: React.ReactElement | React.ReactElement[];
    style?: any;
    backgroundColor?: string;
    backgroundImg?: string;
    authBadgeIcon?: string;
    authOnEditClick?: () => void;
    authCurrentUserCode?: string;
    authCurrentUserName?: string;
};

export const AppBar: React.FC<AppBarProps> = (props) => {
    const {
        title,
        version,
        logo,
        logoStyle,
        menuButton,
        additionalToolbarComponents,
        additionalAuthComponents,
        style,
        backgroundColor,
        backgroundImg,
        authBadgeIcon,
        authOnEditClick,
        authCurrentUserCode,
        authCurrentUserName,
    } = props;
    const authContext = useOptionalAuthContext();
    const authButton =
        authContext != null ? (
            <AuthButton
                badgeIcon={authBadgeIcon}
                onEditClick={authOnEditClick}
                currentUserCode={authCurrentUserCode}
                currentUserName={authCurrentUserName}
                additionalComponents={additionalAuthComponents}
            />
        ) : null;
    const backgroundStyle = backgroundColor
        ? toolbarBackgroundStyle(backgroundColor, backgroundImg)
        : {};
    return (
        <MuiAppBar position="sticky" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
            <Toolbar style={{ ...style, ...backgroundStyle }}>
                {menuButton}
                {logo ? (
                    <Box sx={{ mr: 2, pt: 1, pr: 2, cursor: 'pointer', ...logoStyle }}>
                        <img src={logo} alt="logo" />
                    </Box>
                ) : null}
                {typeof title === 'string' ? (
                    <Typography
                        variant="h6"
                        component="div"
                        title={title + (version ? ' v' + version : '')}
                        sx={{ flexGrow: 1 }}>
                        {title}
                    </Typography>
                ) : (
                    <div
                        style={{
                            flexGrow: 1,
                        }}>
                        {title}
                    </div>
                )}
                {additionalToolbarComponents}
                {authButton}
            </Toolbar>
        </MuiAppBar>
    );
};

export default AppBar;
