import React from 'react';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Link from '@mui/material/Link';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { toolbarBackgroundStyle } from 'reactlib';
import drassanaLogo from '../assets/drassana.png';
import { FOOTER_HEIGHT } from '../util/layout';

type DrassanaFooterProps = {
    title: string;
    version?: string;
    backgroundColor?: string;
    style?: React.CSSProperties;
};

type Manifest = Record<string, string>;
const getManifest = () => (window as unknown as { __MANIFEST__?: Manifest }).__MANIFEST__;

const footerLinkSx = {
    color: '#F6F6F6',
    mr: 2,
    mt: 1,
    alignSelf: 'flex-start',
    display: 'inline-flex',
    alignItems: 'center',
    minHeight: 24,
    minWidth: 24,
    // En pantalles estretes és el títol (noWrap) qui cedeix l'espai, no els enllaços
    flexShrink: 0,
    whiteSpace: 'nowrap',
};

export const DrassanaFooter: React.FC<DrassanaFooterProps> = (props) => {
    const { title, version, backgroundColor, style } = props;
    const { t } = useTranslation();
    const [buildTimestamp, setBuildTimestamp] = React.useState<string | null>(null);
    const [scmRevision, setScmRevision] = React.useState<string | null>(null);
    const [comandaVersion, setComandaVersion] = React.useState<string>(version ?? '');

    React.useEffect(() => {
        const applyManifest = (manifest: Manifest) => {
            setBuildTimestamp(manifest['Build-Timestamp'] ?? null);
            setScmRevision(manifest['Implementation-SCM-Revision'] ?? null);
            // En desenvolupament el manifest pot no dur versió: es manté la de l'aplicació
            setComandaVersion((current) => manifest['Implementation-Version'] ?? current);
        };
        // Comprova si window.__MANIFEST__ ja està disponible
        const manifest = getManifest();
        if (manifest) {
            applyManifest(manifest);
        } else {
            // Si no està disponible, espera a que l'script es carregui
            const checkManifestInterval = setInterval(() => {
                const loadedManifest = getManifest();
                if (loadedManifest) {
                    clearInterval(checkManifestInterval);
                    applyManifest(loadedManifest);
                }
            }, 100);

            // Estableix un temps límit per aturar la comprovació després d'un temps raonable (5 segons)
            const timeoutId = setTimeout(() => {
                clearInterval(checkManifestInterval);
                // Si el manifest encara no està disponible, podem establir valors per defecte o deixar-ho com a null
                if (!getManifest()) {
                    console.warn("Manifest no disponible després del temps d'espera");
                }
            }, 5000);

            // Neteja l'interval i el temps límit quan el component es desmunta
            return () => {
                clearInterval(checkManifestInterval);
                clearTimeout(timeoutId);
            };
        }
    }, []);

    const backgroundStyle = backgroundColor ? toolbarBackgroundStyle(backgroundColor) : {};

    return (
        <Toolbar
            style={{
                ...style,
                ...backgroundStyle,
            }}
            sx={{
                minHeight: FOOTER_HEIGHT + 'px !important',
                lineHeight: '0.5em',
                zIndex: (theme) => theme.zIndex.drawer + 100,
            }}
        >
            <Typography
                variant="caption"
                component="div"
                noWrap
                title={title + (comandaVersion ? ' v' + comandaVersion : '')}
                sx={{
                    flexGrow: 1,
                    minWidth: 0,
                    alignSelf: 'flex-start',
                    fontSize: '14px',
                    fontWeight: 'bold',
                    mt: 1,
                    color: '#F6F6F6',
                }}
            >
                {(title ? title : '') + (comandaVersion ? ' v' + comandaVersion : '')}
                <span id="versioData" style={{ color: backgroundColor, marginLeft: '16px' }}>
                    ({buildTimestamp} | Revisió: {scmRevision})
                </span>
            </Typography>
            <Link component={RouterLink} to="/sitemap" variant="body2" underline="hover" sx={footerLinkSx}>
                {t('navigate.sitemap')}
            </Link>
            <Link component={RouterLink} to="/accessibilitat" variant="body2" underline="hover" sx={footerLinkSx}>
                {t('navigate.accessibilitat')}
            </Link>
            <Box sx={{ mr: 0, pt: 0, pr: 0, height: FOOTER_HEIGHT + 'px' }}>
                <img src={drassanaLogo} alt="foot_logo" style={{ maxHeight: FOOTER_HEIGHT + 'px' }} />
            </Box>
        </Toolbar>
    );
};

export default DrassanaFooter;
