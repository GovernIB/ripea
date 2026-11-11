import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import { useAvisos, getAvisNivell, type AvisNivell } from './AvisosContext';
import AlertExpand from './AlertExpand';

/**
 * Avisos actius de l'aplicació, damunt del contingut de la pàgina.
 *
 * Cada avís és un {@link AlertExpand} (l'assumpte sempre visible i el detall, amb els enllaços als
 * expedients si n'hi ha, es desplega en fer-hi clic). La X de cada avís el minimitza: deixa de
 * sortir aquí però no desapareix, es recupera des dels chips de la capçalera
 * ({@link ./AvisosChips.tsx}). En recarregar la pàgina tornen a sortir tots (veure {@link ./AvisosContext.ts}).
 *
 * La llista arriba per SSE (veure {@link ./SseClient.tsx}).
 */

const NIVELL_SEVERITY: Record<AvisNivell, 'info' | 'warning' | 'error'> = {
    INFO: 'info',
    WARNING: 'warning',
    ERROR: 'error',
};

export const AvisosBanner: React.FC = () => {
    const { t } = useTranslation();
    const { visibles, minimitza } = useAvisos();

    if (visibles.length === 0) {
        return null;
    }

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: '2px', flex: '0 0 auto' }}>
            {visibles.map((avis) => (
                <AlertExpand
                    key={avis.id}
                    title={avis.assumpte}
                    severity={NIVELL_SEVERITY[getAvisNivell(avis)]}
                    sx={{ m: 0, flex: '0 0 auto' }}
                    onClose={() => minimitza(avis.id)}
                    closeLabel={t('common.avisos.tanca')}
                    showDetailLabel={t('common.avisos.mostra')}
                    hideDetailLabel={t('common.avisos.amaga')}
                    linkChildren={avis.expedients}
                >
                    {avis.missatge}
                </AlertExpand>
            ))}
        </Box>
    );
};

export default AvisosBanner;
