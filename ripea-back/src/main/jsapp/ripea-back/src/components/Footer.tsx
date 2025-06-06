import React, { useState, useEffect } from 'react';
import Toolbar from '@mui/material/Toolbar';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { toolbarBackgroundStyle } from 'reactlib';

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

    const [buildTimestamp, setBuildTimestamp] = useState<string | null>(null);
    const [scmRevision, setScmRevision] = useState<string | null>(null);
    const [comandaVersion, setComandaVersion] = useState<string>(version ?? '');

    useEffect(() => {
        fetch('/build-info.json')
            .then(response => response.json())
            .then(data => {
                setBuildTimestamp(data.buildTimestamp);
                setScmRevision(data.scmRevision);
                data.comandaVersion && setComandaVersion(data.comandaVersion);
            });
    }, []);

    const backgroundStyle = backgroundColor ? toolbarBackgroundStyle(backgroundColor, backgroundImg) : {};
    return <footer>
        <Toolbar style={{ ...style, ...backgroundStyle }} sx={{minHeight: '36px !important', lineHeight: '0.5em'}}>
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
            {logos && logos.map((logo) =>
                <Box sx={{ mr: 0, pt: 0, pr: 0, height: '36px', cursor: 'pointer', ...logoStyle }} key={logo}>
                    <img src={logo} alt="foot_logo" style={{maxHeight: '36px'}}/>
                </Box>)
            }
        </Toolbar>
    </footer>;
}

export default Footer;