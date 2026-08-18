import React, {useEffect, useMemo, useState} from "react";
import {Box, Button, Chip, Icon, Tooltip, Typography} from "@mui/material";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";
import {MuiDataGridProps, MuiGrid, useMuiDataGridApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useUserSession} from "./Session.tsx";
import MassiveActionSelector, {MassiveActionProps} from "./MassiveActionSelector.tsx";
import {toSelectionModel, fromSelectionModel} from "../util/selectionModelUtils";

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
                {children && <Typography variant={'body2'} sx={{display: {xs: 'none', sm: 'none', md: 'block'}}}
                             ml={1}>{children}</Typography>}
            </Button>
        </span>
    </Tooltip>
}

export const countTopLevel = (filter?: string): number => {
    if (!filter || !filter?.trim()) return 0;
    let str = filter.trim();

    if (str.startsWith('(') && str.endsWith(')')) {
        let bal = 0;
        for (let i = 0; i < str.length; i++) {
            bal += str[i] === '(' ? 1 : str[i] === ')' ? -1 : 0;
            if (bal === 0 && i < str.length - 1) {
                bal = 1; break;
            }
        }
        if (bal === 0) str = str.slice(1, -1);
    }

    let depth = 0;
    let flat = '';
    for (let i = 0; i < str.length; i++) {
        const c = str[i];
        if (c === '(') depth++;
        else if (c === ')') depth--;
        else if (depth === 0) flat += c;
    }

    const matches = flat.match(/(?<=\s)(AND|OR)(?=\s)/gi);
    return matches ? matches.length + 1 : 1;
};

type FilterCount = number | ((num: number) => number);

/** Nombre de filtres aplicats: el compte del filtre spring, ajustat si es rep una funció. */
export const resolveFilterCount = (filter?: string, filterCount?: FilterCount): number =>
    (typeof filterCount === 'function') ? filterCount(countTopLevel(filter)) : (filterCount ?? countTopLevel(filter));

// Xip amb el nombre de filtres aplicats. A part de la barra d'eines de la graella, s'usa a
// les pantalles amb vistes alternatives (calendari, kanban), on no hi ha barra d'eines.
export const FilterCountChip = (props: { filter?: string, filterCount?: FilterCount, sx?: any }) => {
    const { filter, filterCount, sx } = props;
    const { t } = useTranslation();
    const num = resolveFilterCount(filter, filterCount);

    if (!num) {
        return null;
    }

    return <Chip label={t('common.filterCount', {num})} size={"small"} icon={<Icon>filter_alt</Icon>} color={"primary"} sx={{px: 0.5, ...sx}} />
}

type StyledMuiGridProps = Omit<MuiDataGridProps,
    'rowSelectionModel'
    | 'onRowSelectionModelChange'
    | 'readOnly'
    | 'paginationActive'
    | 'persistentStateActive'
> & {
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
        toolbarShowFilterCount = false,
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
        ...others
    } = props
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
    const toolbarElements = [
        {
            position: 0,
            element: <FilterCountChip filter={filter} filterCount={filterCount} />,
            hidden: !toolbarShowFilterCount || !filterNum,
        },
        {
            position: 1,
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
        {
            position: 3,
            element: <ToolbarButton title={t('common.refresh')} icon={'refresh'} onClick={refresh} color={'primary'}/>,
            hidden: toolbarHideRefresh,
        },
        {
            position: 3,
            element: <ToolbarButton title={t('common.create')} icon={'add'} onClick={create} color={'primary'}>{toolbarCreateTitle}</ToolbarButton>,
            hidden: toolbarHideCreate || !toolbarShowCreate || readOnly,
        },
        ...(toolbarElementsWithPositions ?? []),
    ]
        .filter((e:any) => !e?.hidden)

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
            //cellClassName: (user?.conf?.numElementsPagina!= null) ? 'multi-line-cell' : undefined,
        }));
    }, [columns])

    const paginationProps = useMemo(() => {
        // En mode auto-height (scroll global de la pàgina) la graella creix amb el
        // contingut, per això no es pot usar autoPageSize (necessita alçada fixa i
        // deixaria el cos buit). S'usa sempre autoHeight amb una mida de pàgina per
        // defecte quan l'usuari no en té cap de configurada.
        return {
            getRowHeight: () => 'auto',
            autoHeight: true,
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
            height: '100%',
            // El filtre ràpid de la barra d'eines és un TextField "small" de 37px i quedava
            // més alt que la resta de controls de la barra (32px). S'iguala des d'aquí perquè
            // el crea reactlib i no admet ni props ni estils des de fora.
            '& .MuiToolbar-root .MuiOutlinedInput-root': { height: '32px' },
            '& .MuiToolbar-root .MuiOutlinedInput-input': { pt: 0, pb: 0 },
        }}
    >
        <style>{rowStyles}</style>

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
                {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
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
