import React, {useEffect, useMemo, useState} from "react";
import {createPortal} from "react-dom";
import {Box, Button, Icon, IconButton, Theme, Tooltip, Typography} from "@mui/material";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";
import {MuiDataGridProps, MuiGrid, useMuiDataGridApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useUserSession} from "./Session.tsx";
import MassiveActionSelector, {MassiveActionProps} from "./MassiveActionSelector.tsx";
import {toSelectionModel, fromSelectionModel} from "../util/selectionModelUtils";
import {FilterCountChip, resolveFilterCount, type FilterCount} from "./FilterCountChip.tsx";
import {useScreenFrame} from "./ScreenFrameContext.ts";
import {titleBarControlsSx} from "./titleBarControlsSx.ts";
import {SCREEN_GRID_MIN_HEIGHT, SCREEN_TITLE_MIN_HEIGHT, SCREEN_TITLE_PADDING_X} from "../util/layout.ts";
import {icons} from "../util/icons.ts";

export const ToolbarButton = (props:any) => {
    const { title, icon, hidden, children, ...other } = props;

    if (hidden){
        return <></>
    }

    return <Tooltip title={title}>
        <span>
            <Button
                variant="outlined"
                size="small"
                startIcon={<Icon sx={{m: 0}}>{icon}</Icon>}
                {...other}
                sx={{ borderRadius: '4px',  minWidth: '20px', minHeight: '32px' }}
            >
                {children && <Typography variant={'body2'} sx={{display: {xs: 'none', sm: 'none', md: 'block'}, whiteSpace: 'nowrap'}}
                             ml={1}>{children}</Typography>}
            </Button>
        </span>
    </Tooltip>
}

/** Posició que col·loca un element al final de la barra d'eines de la llibreria (veure reactNodePosition). */
const TOOLBAR_END = 100;

/**
 * Selector del contenidor de la barra d'eines que la llibreria pinta sobre la graella (DataToolbar),
 * fill de l'embolcall de la llibreria. Es descriu pel que conté (el `.MuiToolbar-root` de MUI) i no
 * per la posició, perquè no depengui de l'ordre dels germans. Únic punt a revisar si la llibreria
 * canvia aquesta estructura: l'usen l'sx i l'efecte de mesura d'aquest component.
 *
 * Només s'aplica a les graelles que no són `autoHeight` (el DOM de la llibreria no hi té l'embolcall).
 */
const LIB_TOOLBAR = 'div:has(> .MuiToolbar-root):not(.MuiDataGrid-root)';

type StyledMuiGridProps = Omit<MuiDataGridProps,
    'rowSelectionModel'
    | 'onRowSelectionModelChange'
    | 'readOnly'
    | 'paginationActive'
    | 'persistentStateActive'
> & {
    /**
     * Alçada automàtica (la graella creix amb el contingut i la pàgina fa scroll). Per defecte només ho és
     * fora d'un ScreenFrame: dins d'un marc la graella ocupa l'alçada que sobra i fa scroll per dins. Les
     * graelles que no són el contingut principal d'una pantalla amb marc (diàlegs, pestanyes, detalls) han
     * de passar `autoHeight`.
     */
    autoHeight?: boolean,
    filterCount?: FilterCount,
    toolbarShowFilterCount?: boolean,
    toolbarCreateTitle?: string,
    toolbarMassiveActions?: MassiveActionProps[],
    rowProps?: (row:any) => any,
    formInitOnChange?:boolean,
    rowExpansionChange?: ( params:any, event:any, details:any ) => void,
    onRefresh?: () => any,
    disabledMassiveDefSelector?: boolean,
    hiddenMassiveDefSelector?: boolean,
    toolbarShowCreate?: boolean,
    toolbarShowQuickFilter?: boolean,
    staticSortModel?: any[],
    rowSelectionModel?: any[],
    onRowSelectionModelChange?: (ids:any[], detail:any) => void,
    paginationActive?: boolean,
    readOnly?: boolean,
    persistentStateActive?: boolean,
}

/** Marge mínim entre el menú contextual i les vores de la finestra. */
const CONTEXT_MENU_VIEWPORT_MARGIN = 8;

const StyledMuiGrid = (props:StyledMuiGridProps) => {
    const { value: user } = useUserSession();
    const gridApiRef = useMuiDataGridApiRef();
    const dataApiRef = useMuiDatagridApiRef();
    const { t } = useTranslation();
    const containerRef = React.useRef<HTMLDivElement>(null);
    const contextMenuButtonRef = React.useRef<any>(null);

    const {
        resourceName,
        filter,
        filterCount,
        toolbarShowFilterCount: toolbarShowFilterCountProp,
        namedQueries,
        columns,
        apiRef = gridApiRef,
        datagridApiRef = dataApiRef,
        toolbarElementsWithPositions,
        toolbarCreateTitle,
        toolbarHideRefresh,
        toolbarHideCreate,
        toolbarMassiveActions,
        selectionActive,
        staticSortModel,
        paginationActive = true,
        readOnly,
        onRowsChange,
        onRowCountChange,
        rowSelectionModel: rowSelectionModelProp,
        onRowSelectionModelChange,
        rowProps,
        formInitOnChange,
        popupEditFormDialogComponentProps,
        popupEditFormComponentProps,
        rowExpansionChange,
        onRefresh,
        disabledMassiveDefSelector = false,
        hiddenMassiveDefSelector = false,
        toolbarShowCreate = true,
        toolbarShowQuickFilter = false,
        persistentStateActive = true,
        autoHeight: autoHeightProp,
        ...others
    } = props
    // Dins un ScreenFrame, la barra d'eines de la llibreria es superposa a la barra de títol (CSS de més
    // avall) i el recompte de filtres es pinta al costat del títol. Fora d'un marc tot es manté a la barra
    // d'eines de la llibreria. Les graelles `autoHeight` (diàlegs, pestanyes, detalls) no s'hi enganxen: el
    // context de React travessa els portals dels diàlegs i es mourien les eines de la graella principal.
    const frame = useScreenFrame();
    const autoHeight = autoHeightProp ?? (frame == null);
    const infoSlot = !autoHeight ? (frame?.infoSlot ?? null) : null;
    const setToolbarWidth = !autoHeight ? frame?.setToolbarWidth : undefined;
    // El recompte és visible per defecte quan la graella és al marc d'una pantalla.
    const toolbarShowFilterCount = toolbarShowFilterCountProp ?? infoSlot != null;
    const [gridRows, setGridRows] = useState<any[]>([]);
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    const refresh = () => {
        onRefresh?.()
        apiRef?.current?.refresh?.();
    }
    const create = () => {
        apiRef?.current?.triggerCreate?.();
    }
    const setGridSelectedRows = (value:any) => {
        datagridApiRef?.current?.setRowSelectionModel?.(toSelectionModel(value))
    }

    const filterNum:number = resolveFilterCount(filter, filterCount)
    // Ordre de la barra d'eines (d'esquerra a dreta): xip de filtres, filtre ràpid (el posa la llibreria),
    // accions massives, accions pròpies de la pantalla (toolbarElementsWithPositions), «Nou/Nova X» i,
    // sempre al final de tot, refresca. Tot va a «TOOLBAR_END» (el final de la barra) en aquest ordre: la
    // llibreria insereix per posició relativa i les pantalles en passaven de diferents: aquestes posicions
    // s'ignoren i es manté l'ordre de l'array de cada pantalla.
    const toolbarElements = [
        {
            position: 0,
            element: <FilterCountChip filter={filter} filterCount={filterCount} sx={{ml: 1.5}} />,
            hidden: !toolbarShowFilterCount || !filterNum || infoSlot != null,
        },
        {
            position: TOOLBAR_END,
            element: <MassiveActionSelector
                resourceName={resourceName}
                selectedRows={selectedRows}
                setSelectedRows={setGridSelectedRows}
                filter={filter}
                namedQueries={namedQueries}
                actions={toolbarMassiveActions ?? []}
                disabledDefSelector={disabledMassiveDefSelector}
                hiddenDefSelector={hiddenMassiveDefSelector}
                isRowSelectable={props?.isRowSelectable}
            />,
            hidden: !toolbarMassiveActions || readOnly,
        },
        ...(toolbarElementsWithPositions ?? []).map((e: any) => ({...e, position: TOOLBAR_END})),
        {
            position: TOOLBAR_END,
            element: <ToolbarButton title={t('common.create')} icon={icons.crea} onClick={create} color={'primary'}>{toolbarCreateTitle}</ToolbarButton>,
            hidden: toolbarHideCreate || !toolbarShowCreate || readOnly,
        },
        {
            position: TOOLBAR_END,
            // Sobre la barra de títol (graella amb marc) cau damunt el color principal; a la resta, damunt el paper
            element: <IconButton
                title={t('common.refresh')}
                onClick={refresh}
                sx={{color: (theme: Theme) => infoSlot != null ? theme.palette.primary.contrastText : theme.palette.text.primary}}
            >
                <Icon>{icons.refresca}</Icon>
            </IconButton>,
            hidden: toolbarHideRefresh,
        },
    ]
        .filter((e:any) => !e?.hidden)

    useEffect(() => {
        if (!setToolbarWidth || infoSlot == null) {
            return;
        }
        const wrapper = containerRef.current;
        if (!wrapper) {
            return;
        }
        // Separació entre el títol (o el recompte) i la primera eina de la barra.
        const GAP = 12;
        let toolbar: HTMLElement | null = null;
        let sizeObserver: ResizeObserver | undefined;
        // La barra de la llibreria (LIB_TOOLBAR, veure l'sx) no es pinta fins que la graella és a
        // punt: se la cerca a cada canvi del DOM de dins.
        const attach = () => {
            const found = wrapper.querySelector<HTMLElement>(`:scope > div > ${LIB_TOOLBAR}`);
            if (found === toolbar) {
                return;
            }
            sizeObserver?.disconnect();
            toolbar = found;
            if (toolbar) {
                const observed = toolbar;
                sizeObserver = new ResizeObserver(() => setToolbarWidth(Math.ceil(observed.offsetWidth) + GAP));
                sizeObserver.observe(observed);
            } else {
                setToolbarWidth(0);
            }
        };
        const domObserver = new MutationObserver(attach);
        domObserver.observe(wrapper, { childList: true, subtree: true });
        attach();
        return () => {
            domObserver.disconnect();
            sizeObserver?.disconnect();
            setToolbarWidth(0);
        };
    }, [setToolbarWidth, infoSlot]);
    useEffect(() => {
        if(!!user) {
            refresh()
        }
    }, [user]);
    React.useEffect(() => {
        if (datagridApiRef.current && Object.keys(datagridApiRef.current).length > 0 && rowExpansionChange) {
            datagridApiRef.current.subscribeEvent('rowExpansionChange', rowExpansionChange);
        }
    }, [datagridApiRef]);

    // Torna a deixar el botó d'accions de la fila amb el seu rectangle original (veure
    // onRowContextMenu). Si es rep un botó, només es restaura si és el que s'havia
    // substituït: quan s'obre el menú d'una altra fila el menú anterior es tanca després,
    // i sense la comprovació es desfaria la substitució del menú que s'acaba d'obrir.
    const restoreContextMenuButton = (button?: any) => {
        const patchedButton = contextMenuButtonRef.current;
        if (patchedButton && (button == null || button === patchedButton)) {
            delete patchedButton.getBoundingClientRect;
            contextMenuButtonRef.current = null;
        }
    };
    React.useEffect(() => {
        if (datagridApiRef.current && Object.keys(datagridApiRef.current).length > 0) {
            return datagridApiRef.current.subscribeEvent('menuClose', (params: any) =>
                restoreContextMenuButton(params?.target));
        }
    }, [datagridApiRef]);
    React.useEffect(() => () => restoreContextMenuButton(), []);

    // Custom row styling with colored bar
    const getRowClassName = (params: any) :string =>
        `row-with-color-${params.row.id} ${params.indexRelativeToCurrentPage % 2 === 0 ? 'even' : 'odd'}`;

    // Apply custom CSS for rows with color
    const rowStyles = useMemo(() => (
        gridRows.map((row: any) => {
            const style = rowProps?.(row);
            return style
                ? `.row-with-color-${row.id} { ${Object.entries(style).map(([k, v]) => `${k}: ${v};`).join(' ')} }`
                : '';
        }).join('\n')
    ), [gridRows]);

    // Applica word wrap a totes les columnes
    const columnsWithWordWrap = useMemo(()=>{
        return columns.filter((col:any) => !col?.hidden).map((col:any) => ({
            ...col,
            flex: col.flex ?? 1,
        }));
    }, [columns])

    const paginationProps = useMemo(() => {
        // Es fixa sempre una mida de pàgina (la del perfil de l'usuari o 10): amb autoHeight (la graella
        // creix amb el contingut) no es pot usar autoPageSize, que necessita alçada fixa i deixaria el
        // cos buit, i sense autoHeight fixar-la desactiva l'autoPageSize de la llibreria, que ajustaria
        // el nombre de files a l'alçada disponible.
        return {
            getRowHeight: () => 'auto',
            defaultPaginationModel: {
                page: 0,
                pageSize: user?.conf?.numElementsPagina != null ? +user?.conf?.numElementsPagina : 10,
            },
            pageSizeOptions: [10, 20, 50, 100, 250],
        }
    }, [user?.conf?.numElementsPagina])

    return <Box
        ref={containerRef}
        sx={{
            display: 'flex',
            flexDirection: 'column',
            width: '100%',
            // Sense autoHeight, dins ScreenFrame (columna flex amb alçada acotada) la graella ocupa l'alçada que
            // sobra, amb un mínim: si no hi cap, és el contingut de la pantalla qui fa scroll i el títol i el
            // peu es mantenen visibles (cal que la pàgina usi <GridPage> sense autoHeight, que fixa l'alçada
            // disponible). Amb autoHeight dins un marc (diàlegs, pestanyes) l'embolcall no té alçada pròpia:
            // amb `height: 100%` es comprimia fins a l'alçada del pare i retallava les files. Fora d'un marc
            // (pantalles sense ScreenFrame) es manté el `height: 100%` de sempre.
            ...(!autoHeight && { flex: '1 1 0', minHeight: SCREEN_GRID_MIN_HEIGHT + 'px' }),
            ...(autoHeight && frame == null && { height: '100%' }),
            // El filtre ràpid de la barra d'eines és un TextField "small" de 37px i quedava
            // més alt que la resta de controls de la barra (32px). S'iguala des d'aquí perquè
            // el crea reactlib i no admet ni props ni estils des de fora.
            '& .MuiToolbar-root .MuiOutlinedInput-root': { height: '32px' },
            '& .MuiToolbar-root .MuiOutlinedInput-input': { pt: 0, pb: 0 },
            // Les icones de les cel·les (check de booleans, avisos, estats...) són de mida "small" (20px) en lloc
            // dels 24px per defecte de MUI, i amb 4px de marge dret (com abans del redisseny). Les que ja demanen
            // `fontSize="inherit"` segueixen la mida del text. Les que porten la classe `grid-icon-medium`
            // (botons d'icona amb badge: comentaris, seguidors, permisos...) mantenen els 24px de MUI. El marge no
            // s'aplica a les icones dins d'un botó amb text (startIcon/endIcon), que ja porten el seu.
            '& .MuiDataGrid-cell .MuiIcon-root:not(.MuiIcon-fontSizeInherit)': { marginRight: '4px' },
            '& .MuiDataGrid-cell .MuiButton-icon .MuiIcon-root': { marginRight: 0 },
            '& .MuiDataGrid-cell .MuiIcon-root:not(.MuiIcon-fontSizeInherit):not(.grid-icon-medium)': { fontSize: '1.25rem' },
            // Barra d'eines de la llibreria sobre la barra de títol del ScreenFrame, a la dreta i centrada
            // verticalment (DataToolbar, que no es pot moure per props): amb `position: absolute` s'ancora al
            // `.screen-frame` i conserva l'ordre dels seus elements, el filtre ràpid i l'indicador d'error.
            // Es localitza amb LIB_TOOLBAR, el mateix selector que fa servir l'efecte de mesura.
            ...(infoSlot != null && {
                [`& > div > ${LIB_TOOLBAR}`]: {
                    position: 'absolute',
                    // `--screen-toolbar-top` el posa el ScreenFrame: 0 si la barra hi cap al costat del títol,
                    // o l'alçada d'una fila si ha de passar a sota.
                    top: 'var(--screen-toolbar-top, 0px)',
                    right: SCREEN_TITLE_PADDING_X + 'px',
                    height: SCREEN_TITLE_MIN_HEIGHT + 'px',
                    display: 'flex',
                    alignItems: 'center',
                    zIndex: 1,
                    '& .MuiToolbar-root': { width: 'auto', minHeight: 0, mt: 0 },
                    // Botons, xips i filtre ràpid sobre la barra de títol
                    ...titleBarControlsSx,
                },
            }),
        }}
    >
        <style>{rowStyles}</style>
        {toolbarShowFilterCount && infoSlot != null
            ? createPortal(<FilterCountChip filter={filter} filterCount={filterCount} />, infoSlot)
            : null}

        <MuiGrid
            resourceName={resourceName}
            filter={filter}
            namedQueries={namedQueries}
            paginationActive={paginationActive ?true :undefined}
            titleDisabled
            disableColumnMenu
            disableColumnSorting={!!staticSortModel}
            fixedSortModel={staticSortModel}

			persistentStateActive={persistentStateActive ? true : undefined}
			persistentStateClearPageSortPropsOnTopLevelRouteChange

            apiRef={apiRef}
            datagridApiRef={datagridApiRef}
            columns={columnsWithWordWrap}
            getRowClassName={getRowClassName}
            onRowsChange={(rows, info) => {
                setGridRows([...rows]);
                setGridSelectedRows(toSelectionModel(rowSelectionModelProp))
                onRowsChange?.(rows, info);
                onRowCountChange?.(info?.totalElements)
            }}
            rowSelectionModel={toSelectionModel(rowSelectionModelProp)}
            onRowSelectionModelChange={(newSelection, details) => {
                const ids = fromSelectionModel(newSelection);
                setSelectedRows(ids);
                onRowSelectionModelChange?.(ids, details);
            }}

            selectionActive={(selectionActive || (!!toolbarMassiveActions && !readOnly)) ?true :undefined}
            checkboxSelection={selectionActive || (!!toolbarMassiveActions && !readOnly)}
            keepNonExistentRowsSelected={selectionActive || (!!toolbarMassiveActions && !readOnly)}

            popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'md', initOnChangeRequest: formInitOnChange, ...popupEditFormDialogComponentProps }}
            popupEditFormComponentProps={{ ...(popupEditFormComponentProps ?? []), avoidSubmitIfAnyValidatorErrors: true }}
            popupEditFormDialogOnClose={(reason?: string) => reason !== 'backdropClick' }
            popupEditFormDialogButtons={[
                {icon: icons.desa, text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
                {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
            ]}

            toolbarHideRefresh
            toolbarHideCreate
            // toolbarHideExport
            toolbarHideQuickFilter={!toolbarShowQuickFilter ?true :undefined}
            toolbarElementsWithPositions={toolbarElements}
            rowHideUpdateButton
            rowHideDeleteButton
            readOnly={readOnly ?true :undefined}
            sx={{
                '& .MuiDataGrid-cell': {
                    paddingTop: '5px',
                    paddingBottom: '5px',
                },
            }}
            {...paginationProps}
            {...others}
            autoHeight={autoHeight}

            onRowContextMenu={(event:any, row:any) => {
                event.preventDefault();
                const rowElement = containerRef.current?.querySelector(`.MuiDataGrid-row[data-id="${row.id}"]`);
                const menuButton:any = rowElement?.querySelector('button[role="menuitem"]');
                if (!menuButton) {
                    return;
                }
                restoreContextMenuButton();
                const { clientX, clientY } = event;
                // El menú d'accions de la fila és un Popper de MUI X ancorat al botó "⋮" amb
                // placement 'bottom-end', que no es pot configurar des d'aquí. Per obrir-lo al
                // punt del clic se substitueix el rectangle del botó per un de situat al cursor:
                // amb l'amplada del menú, la vora dreta (l'"end" de l'ancoratge) cau a
                // clientX + amplada i el menú queda amb la cantonada superior esquerra al cursor.
                // L'alçada evita que surti per sota de la finestra. La mida es llegeix amb
                // offsetWidth/offsetHeight perquè getBoundingClientRect la retornaria escalada
                // per l'animació d'obertura (Grow). Es restaura en tancar el menú.
                menuButton.getBoundingClientRect = () => {
                    const menuPaper:any = document.querySelector('.MuiDataGrid-menu .MuiPaper-root');
                    const menuWidth = menuPaper?.offsetWidth ?? 0;
                    const menuHeight = menuPaper?.offsetHeight ?? 0;
                    const top = Math.max(
                        CONTEXT_MENU_VIEWPORT_MARGIN,
                        Math.min(clientY, window.innerHeight - menuHeight - CONTEXT_MENU_VIEWPORT_MARGIN));
                    return new DOMRect(clientX, top, menuWidth, 0);
                };
                contextMenuButtonRef.current = menuButton;
                menuButton.click();
            }}
        />
    </Box>
}
export default StyledMuiGrid;
