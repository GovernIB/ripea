import React from 'react';
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
    const backgroundStyle = backgroundColor ? toolbarBackgroundStyle(backgroundColor, backgroundImg) : {};
    return <footer>
        <Toolbar style={{ ...style, ...backgroundStyle }}>
            <Typography
                variant="caption"
                component="div"
                title={title + (version ? ' v' + version : '')}
                sx={{ flexGrow: 1 }}>
                {(title ? title : '') + (version ? ' v' + version : '')}
                {/* {version && <Typography variant="caption">{title}&nbsp;v{version}</Typography>} */}
            </Typography>
            {logos && logos.map((logo) =>
                <Box sx={{ mr: 2, pt: 1, pr: 2, cursor: 'pointer', ...logoStyle }} key={logo}>
                    <img src={logo} alt="foot_logo" style={{maxHeight: '40px'}}/>
                </Box>)
            }
        </Toolbar>
    </footer>;
}

export default Footer;