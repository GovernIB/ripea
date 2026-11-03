import React from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { accentColor } from '../theme';
import {
    ICON_SIZE,
    MAIN_GAP,
    MAIN_PADDING,
    SCREEN_TITLE_GAP,
    SCREEN_TITLE_MIN_HEIGHT,
    SCREEN_TITLE_PADDING_X,
} from '../util/layout';
import type { IconName } from '../util/icons';
import { ScreenFrameContext } from './ScreenFrameContext';
import { titleBarControlsSx } from './titleBarControlsSx';

export type ScreenFrameProps = {
    /** Icona del concepte (catàleg `icons` de util/icons.ts): la mateixa que l'entrada del menú. */
    icon: IconName;
    /** Títol de la pantalla. */
    title: string;
    /**
     * Accions pròpies de la pantalla (cercador, botons...) a la dreta de la barra de títol, per a les
     * pantalles sense graella. Les graelles (StyledMuiGrid) ja hi superposen la seva pròpia barra d'eines.
     */
    actions?: React.ReactNode;
    /**
     * El marc només ocupa l'alçada del seu contingut (i no tota la disponible), de manera que
     * la resta de la pantalla deixa veure el fons. Per a pantalles curtes sense graella.
     */
    fitContent?: boolean;
    children: React.ReactNode;
};

/**
 * Marc d'una pantalla del backoffice: barra de títol (icona + títol + informació + accions) i, a
 * sota, el contingut.
 *
 * Ocupa tota l'alçada disponible dins `<main>` (veure BaseApp.tsx), de manera que el títol hi queda
 * sempre visible i és el contingut qui fa scroll si no hi cap. Les graelles (StyledMuiGrid) s'hi
 * adapten: ocupen l'alçada que sobra, fan scroll de les files per dins, hi pugen el recompte de
 * filtres (a la barra de títol, al costat del títol) i hi superposen la seva barra d'eines (a la
 * dreta de la barra de títol).
 *
 * Substitueix `CardPage` (components/CardData.tsx) a les pantalles d'aquesta aplicació. És el contracte
 * de pantalla que s'ha de poder copiar tal qual a les altres apps de Drassana: cap dependència de
 * pàgines ni de recursos, només el tema, util/layout.ts i ScreenFrameContext.ts.
 */
export const ScreenFrame: React.FC<ScreenFrameProps> = (props) => {
    const { icon, title, actions, fitContent, children } = props;
    const [infoSlot, setInfoSlot] = React.useState<HTMLElement | null>(null);
    const [toolbarWidth, setToolbarWidth] = React.useState(0);
    const context = React.useMemo(() => ({ infoSlot, setToolbarWidth }), [infoSlot]);

    // Si el títol, el recompte i la barra d'eines no caben en una sola fila, la barra d'eines
    // passa a una segona fila de la mateixa barra de títol (en lloc de comprimir el títol fins
    // a fer-lo il·legible). L'amplada natural del títol surt del seu scrollWidth (és `noWrap`),
    // i la de la barra d'eines no depèn de la fila on cau: no hi ha oscil·lació.
    const barRef = React.useRef<HTMLDivElement>(null);
    const titleRef = React.useRef<HTMLHeadingElement>(null);
    const actionsRef = React.useRef<HTMLDivElement>(null);
    const [wrapToolbar, setWrapToolbar] = React.useState(false);

    React.useLayoutEffect(() => {
        const bar = barRef.current;
        const heading = titleRef.current;
        if (!bar || !heading) {
            return;
        }
        const measure = () => {
            const info = infoSlot?.offsetWidth ?? 0;
            // Les `actions` (pantalles sense graella) també passen a la segona fila si no hi caben.
            const actionsNatural = actionsRef.current?.scrollWidth ?? 0;
            const extra = toolbarWidth + actionsNatural;
            const needed =
                2 * SCREEN_TITLE_PADDING_X +
                ICON_SIZE +
                SCREEN_TITLE_GAP +
                heading.scrollWidth +
                (info > 0 ? SCREEN_TITLE_GAP + info : 0) +
                extra;
            setWrapToolbar(extra > 0 && needed > bar.clientWidth);
        };
        measure();
        const observer = new ResizeObserver(measure);
        observer.observe(bar);
        if (infoSlot) {
            observer.observe(infoSlot);
        }
        if (actionsRef.current) {
            observer.observe(actionsRef.current);
        }
        return () => observer.disconnect();
    }, [toolbarWidth, infoSlot, title, actions]);

    return (
        <ScreenFrameContext.Provider value={context}>
            <Card
                // `position: relative`: la barra d'eines de la graella s'hi ancora (position:
                // absolute) sobre la barra de títol, per sobre del contenidor amb scroll.
                className="screen-frame"
                sx={{
                    position: 'relative',
                    // Fila on s'ancora la barra d'eines de la graella (veure StyledMuiGrid.tsx).
                    '--screen-toolbar-top': wrapToolbar ? SCREEN_TITLE_MIN_HEIGHT + 'px' : '0px',
                    height: fitContent ? 'auto' : '100%',
                    flex: fitContent ? '0 1 auto' : undefined,
                    minHeight: 0,
                    display: 'flex',
                    flexDirection: 'column',
                    overflow: 'hidden',
                }}
            >
                <Box
                    ref={barRef}
                    className="screen-frame-title"
                    sx={(theme) => ({
                        display: 'flex',
                        alignItems: wrapToolbar ? 'flex-start' : 'center',
                        gap: SCREEN_TITLE_GAP + 'px',
                        flex: '0 0 auto',
                        // Una fila, o dues quan la barra d'eines de la graella passa a sota.
                        height: (wrapToolbar ? 2 : 1) * SCREEN_TITLE_MIN_HEIGHT + 'px',
                        boxSizing: 'border-box',
                        // Cada fila mesura SCREEN_TITLE_MIN_HEIGHT: el contingut de la primera es
                        // centra en aquesta alçada i no pas en la de la barra sencera.
                        pt: wrapToolbar ? (SCREEN_TITLE_MIN_HEIGHT - ICON_SIZE) / 2 + 'px' : 0,
                        pl: SCREEN_TITLE_PADDING_X + 'px',
                        // Espai que ocupa la barra d'eines superposada de la graella, si n'hi ha i hi cap al costat del títol.
                        pr: SCREEN_TITLE_PADDING_X + (wrapToolbar ? 0 : toolbarWidth) + 'px',
                        // Vora del color principal a l'esquerra i degradat del fons del paper al color principal
                        borderLeft: `4px solid ${theme.palette.primary.main}`,
                        borderBottom: `1px solid ${theme.palette.divider}`,
                        background: `linear-gradient(90deg, ${theme.palette.background.paper} 25%, ${theme.palette.primary.main} 75%)`,
                    })}
                >
                    <Icon sx={(theme) => ({ fontSize: ICON_SIZE + 'px', color: accentColor(theme), flex: '0 0 auto' })}>
                        {icon}
                    </Icon>
                    <Typography ref={titleRef} variant="h6" component="h1" noWrap sx={{ fontWeight: 600, minWidth: 0 }}>
                        {title}
                    </Typography>
                    <Box ref={setInfoSlot} sx={{ display: 'flex', alignItems: 'center', flex: '0 0 auto' }} />
                    {actions != null && (
                        <Box
                            ref={actionsRef}
                            sx={{
                                ml: 'auto',
                                ...(wrapToolbar && {
                                    position: 'absolute',
                                    top: SCREEN_TITLE_MIN_HEIGHT + 'px',
                                    right: SCREEN_TITLE_PADDING_X + 'px',
                                    height: SCREEN_TITLE_MIN_HEIGHT + 'px',
                                }),
                                display: 'flex',
                                alignItems: 'center',
                                gap: 1,
                                flex: '0 0 auto',
                                // Els controls cauen sobre el degradat de la barra de títol
                                ...titleBarControlsSx,
                            }}
                        >
                            {actions}
                        </Box>
                    )}
                </Box>
                <Box
                    sx={{
                        flex: '1 1 auto',
                        minHeight: 0,
                        display: 'flex',
                        flexDirection: 'column',
                        gap: MAIN_GAP + 'px',
                        p: MAIN_PADDING + 'px',
                        overflowY: 'auto',
                    }}
                >
                    {children}
                </Box>
            </Card>
        </ScreenFrameContext.Provider>
    );
};

export default ScreenFrame;
