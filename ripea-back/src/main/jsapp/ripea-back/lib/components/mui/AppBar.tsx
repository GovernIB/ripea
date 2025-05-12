import React from 'react';
import {AppBar as MuiAppBar, Alert, Toolbar, Box, Typography, Icon} from "@mui/material";
import AuthButton from './AuthButton';
import { useOptionalAuthContext } from '../AuthContext';
import { toolbarBackgroundStyle } from '../../util/toolbar';

type AppBarProps = {
    title?: string | React.ReactElement;
    version?: string;
    logo?: string;
    logoStyle?: any;
    alertes?: any;
    menuButton: React.ReactNode,
    additionalToolbarComponents?: React.ReactElement | React.ReactElement[];
    additionalAuthComponents?: React.ReactElement | React.ReactElement[];
    style?: any;
    backgroundColor?: string;
    backgroundImg?: string;
    objectesSyncSessio?: any;
};

const getAlertSeverity = (avisNivell: string) => {
  switch (avisNivell) {
    case "INFO":
      return "info"; // Azul
    case "WARNING":
      return "warning"; // Amarillo
    case "ERROR":
      return "error"; // Rojo
    default:
      return "info"; // Por defecto INFO
  }
};

export const AppBar: React.FC<AppBarProps> = (props) => {
    const {
        title,
        version,
        logo,
        logoStyle,
        alertes,
        menuButton,
        additionalToolbarComponents,
        additionalAuthComponents,
        style,
        backgroundColor,
        backgroundImg,
        objectesSyncSessio,
    } = props;
    const authContext = useOptionalAuthContext();
    const authButton = authContext != null ? <AuthButton additionalComponents={additionalAuthComponents} /> : null;
    const backgroundStyle = backgroundColor ? toolbarBackgroundStyle(backgroundColor, backgroundImg) : {};
    return <MuiAppBar position="sticky" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar style={{ ...style, ...backgroundStyle }}>
            {menuButton}
            {logo ? <Box sx={{ mr: 2, pt: 1, pr: 2, cursor: 'pointer', ...logoStyle }}>
                <img src={logo}/>
            </Box> : null}
            <Typography
                variant="h6"
                component="div"
                title={title + (version ? ' v' + version : '')}
                sx={{ flexGrow: 1 }}>{title}</Typography>
            {additionalToolbarComponents}
            {authButton}
        </Toolbar>
        <div>
            {
                objectesSyncSessio?.avisos?.map((avis:any) => (
                    <Alert key={avis.id} severity={getAlertSeverity(avis.avisNivell)}>
                        <strong>{avis.assumpte}</strong>: {avis.missatge}
                    </Alert>
                ))
            }
        </div>
    </MuiAppBar>;
}

export default AppBar;