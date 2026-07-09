import React, { useState, useEffect } from 'react';
import Toolbar from '@mui/material/Toolbar';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { toolbarBackgroundStyle } from 'reactlib';
import {useTranslation} from "react-i18next";
import {Link} from "@mui/material";
import {useNavigate} from "react-router-dom";

type AppFootProps = {
    title?: string;
    version?: string;
    logos?: string[];
    logoStyle?: any;
    style?: any;
    backgroundColor?: string;
    backgroundImg?: string;
};

export const Footer: React.FC<AppFootProps> = (props) => {
    const {
        title,
        version,
        logos,
        logoStyle,
        style,
        backgroundColor,
        backgroundImg,
    } = props;

    const {t} = useTranslation();
    const navigate = useNavigate();
    const [buildTimestamp, setBuildTimestamp] = useState<string | null>(null);
    const [scmRevision, setScmRevision] = useState<string | null>(null);
    const [comandaVersion, setComandaVersion] = useState<string>(version ?? '');

	useEffect(() => {
	    // Comprova si window.__MANIFEST__ ja està disponible
        const manifest = (window as any).__MANIFEST__;
	    if (manifest) {
	        setBuildTimestamp(manifest["Build-Timestamp"]);
	        setScmRevision(manifest["Implementation-SCM-Revision"]);
	        setComandaVersion(manifest["Implementation-Version"]);
	    } else {
	        // Si no està disponible, espera a que l'script es carregui
	        const checkManifestInterval = setInterval(() => {
	            if (manifest) {
	                clearInterval(checkManifestInterval);
	                setBuildTimestamp(manifest["Build-Timestamp"]);
	                setScmRevision(manifest["Implementation-SCM-Revision"]);
	                setComandaVersion(manifest["Implementation-Version"]);
	            }
	        }, 100);

	        // Estableix un temps límit per aturar la comprovació després d'un temps raonable (5 segons)
	        const timeoutId = setTimeout(() => {
	            clearInterval(checkManifestInterval);
	            // Si el manifest encara no està disponible, podem establir valors per defecte o deixar-ho com a null
	            if (!manifest) {
	                console.warn('Manifest no disponible després del temps d\'espera');
	            }
	        }, 5000);

	        // Neteja l'interval i el temps límit quan el component es desmunta
	        return () => {
	            clearInterval(checkManifestInterval);
	            clearTimeout(timeoutId);
	        };
	    }
	}, []);

    const backgroundStyle = backgroundColor ? toolbarBackgroundStyle(backgroundColor, backgroundImg) : {};
    return <footer>
        <Toolbar style={{ ...style, ...backgroundStyle }} sx={{minHeight: '36px !important', lineHeight: '0.5em', zIndex: (theme) => theme.zIndex.drawer + 100}}>
            <Typography
                variant="caption"
                component="div"
                title={title + (comandaVersion ? ' v' + comandaVersion : '')}
                sx={{
                    flexGrow: 1,
                    alignSelf: 'flex-start',
                    fontSize: '14px',
                    fontWeight: 'bold',
                    mt: 1,
                    color: '#F6F6F6',
                }}>
                {(title ? title : '') + (comandaVersion ? ' v' + comandaVersion : '')}
                <span id="versioData" style={{ color: backgroundColor, marginLeft: '16px' }}>
                    ({buildTimestamp} | Revisió: {scmRevision})
                </span>
            </Typography>
            <Link onClick={()=>navigate('/accessibilitat')} color={'#F6F6F6'} variant={'body2'} sx={{cursor: 'pointer'}}>{t('navigate.accessibilitat')}</Link>
            {logos && logos.map((logo) =>
                <Box sx={{ ml: '10px', pt: 0, pr: 0, height: '36px', ...logoStyle }} key={logo}>
                    <img src={logo} alt="foot_logo" style={{maxHeight: '36px'}}/>
                </Box>)
            }
        </Toolbar>
    </footer>;
}

export default Footer;