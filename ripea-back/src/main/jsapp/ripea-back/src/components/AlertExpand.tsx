import { useState } from 'react';
import { Alert, Collapse, Icon, IconButton } from '@mui/material';
import Box from '@mui/material/Box';
import AlertTitle from '@mui/material/AlertTitle';
import ContingutLink from './ContingutLink';
import { icons } from '../util/icons';

type LinkAlertExpandProps = {
    id: string;
    texte: string;
    codi: string;
};

/** Enllaç a l'expedient dins d'un avis: navega a la pestanya actual, no n'obre una de nova. */
export const LinkAlertExpand = (props: LinkAlertExpandProps) => {
    const { id, texte, codi, ...other } = props;

    return (
        <>
            {texte}
            <ContingutLink id={id} novaPestanya={false} {...other}>
                {codi}
            </ContingutLink>
        </>
    );
};

const AlertExpand = (props: any) => {
    const { title, label, sx, children, onClose, action, linkChildren, closeLabel, showDetailLabel, hideDetailLabel, ...other } = props;
    const [verDetalles, setVerDetalles] = useState(false);

    const toggleDetalles = () => setVerDetalles((prev) => !prev);

    return (
        <>
            <Alert
                {...other}
                sx={{
                    py: 0,
                    px: 1.5,
                    alignItems: 'center',
                    fontSize: '0.85rem',
                    minHeight: 'unset',
                    lineHeight: 1.3,
                    ...sx, // permite sobrescribir desde fuera
                }}
                action={
                    <>
                        {action}
                        <IconButton
                            aria-label={(verDetalles ? hideDetailLabel : showDetailLabel) ?? 'ver detalles'}
                            color="inherit"
                            size="small"
                            onClick={toggleDetalles}
                        >
                            <Icon sx={{ m: 0 }}>{verDetalles ? icons.plega : icons.desplega}</Icon>
                        </IconButton>
                        {onClose && (
                            <IconButton aria-label={closeLabel} color="inherit" size="small" onClick={() => onClose?.()}>
                                <Icon sx={{ m: 0 }}>{icons.cancela}</Icon>
                            </IconButton>
                        )}
                    </>
                }
            >
                {title && <AlertTitle sx={{ m: 0, lineHeight: 1.4 }}>{title}</AlertTitle>}
                {label}
                <Collapse in={verDetalles}>
                    <Box mt={1}>
                        {linkChildren
                            ? linkChildren.map((link: any, index: number) => (
                                  <span key={link.id}>
                                      <LinkAlertExpand {...link} />
                                      {index < linkChildren.length - 1 && ', '}
                                  </span>
                              ))
                            : children}{' '}
                    </Box>
                </Collapse>
            </Alert>
        </>
    );
};
export default AlertExpand;
