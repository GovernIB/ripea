import React from 'react';
import {
    DataGridProProps as DataGridProps,
    GridRowsProp,
    GridRowParams,
    GridRowClassNameParams,
    GridColDef,
    GridSortModel,
    GridPaginationModel,
    GridRowSelectionModel,
    GridSlots,
    GridRowModes,
    GridRowModesModel,
    GridApiPro,
    GridEventListener,
    useGridApiRef as useMuiDatagridApiRef,
} from '@mui/x-data-grid-pro';
import Box from '@mui/material/Box';
import { capitalize } from '../../../util/text';
import useLogConsole from '../../../util/useLogConsole';
import { formattedFieldValue, isFieldNumericType } from '../../../util/fields';
import {
    ReactElementWithPosition,
    joinReactElementsWithPositionWithReactElementsWithPositions
} from '../../../util/reactNodePosition';
import { useResourceApiContext } from '../../ResourceApiContext';
import {
    useResourceApiService,
    ResourceApiFindCommonArgs,
    ResourceApiFindArgs,
    ResourceApiError
} from '../../ResourceApiProvider';
import { useBaseAppContext } from '../../BaseAppContext';
import { useConfirmDialogButtons } from '../../AppButtons';
import { toToolbarIcon } from '../ToolbarIcon';
import FormDialogPopup, { FormDialogPopupApi } from '../form/FormDialogPopup';
import { toGridActionItem } from './GridActionItem';
import { useGridQuickFilter } from './GridQuickFilter';
import { useToolbar, GridToolbarType } from './GridToolbar';
import GridRow from './GridRow';
import GridFooter from './GridFooter';
import GridNoRowsOverlay from './GridNoRowsOverlay';
import DataGrid from './GridCustomStyle';
import GridContext, { GridApi, GridApiRef, useGridContext } from './GridContext';

export const LOG_PREFIX = 'GRID';

export type NewGridColDef = GridColDef & {
    fieldType?: string;
    currencyType?: boolean;
    currencyCode?: string | ((row: any) => string);
    currencyDecimalPlaces?: number | ((row: any) => number);
    currencyLocale?: string | ((row: any) => string);
    decimalPlaces?: number | ((row: any) => number);
    noSeconds?: boolean;
    exportExcluded?: boolean;
};

export type MuiGridProps = {
    title?: string;
    titleDisabled?: true;
    resourceName: string;
    resourceFieldName?: string;
    columns: NewGridColDef[];
    readOnly?: boolean;
    paginationActive?: boolean;
    selectionActive?: boolean;
    sortModel?: GridSortModel;
    staticSortModel?: GridSortModel;
    quickFilterInitialValue?: string;
    quickFilterFullWidth?: true;
    filter?: string;
    staticFilter?: string;
    namedQueries?: string[];
    perspectives?: string[];
    toolbarType?: GridToolbarType;
    toolbarHideRefresh?: true;
    toolbarHideQuickFilter?: true;
    toolbarCreateLink?: string;
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    toolbarAdditionalRow?: React.ReactElement;
    rowLink?: string;
    rowDetailLink?: string;
    rowUpdateLink?: string;
    rowAdditionalActions?: ResourceApiGridAdditionalAction[];
    popupEditActive?: boolean;
    popupEditCreateActive?: boolean;
    popupEditUpdateActive?: boolean;
    popupEditFormContent?: React.ReactElement;
    popupEditFormAdditionalData?: any;
    popupEditFormDialogTitle?: string;
    popupEditFormDialogResourceTitle?: string;
    popupEditFormDialogComponentProps?: any;
    popupEditFormPerspectives?: string[];
    onRowsChange?: (rows: GridRowsProp) => void;
    onRowOrderChange?: GridEventListener<"rowOrderChange">;
    apiRef?: GridApiRef;
    datagridApiRef?: React.MutableRefObject<GridApiPro | null>;
    height?: number;
    autoHeight?: true;
    striped?: true;
    sx?: any;
    debug?: boolean;
} & Omit<DataGridProps, 'apiRef'>;

type ResourceApiGridAdditionalAction = {
    title?: string;
    apiLink?: string;
    apiAction?: string;
    apiReport?: string;
    icon?: string;
    linkTo?: string;
    onClick?: (id: any, event: React.MouseEventHandler<HTMLLIElement>) => void;
    showInMenu?: boolean;
};

/*type ResourceFieldApiFields = (findArgs: ResourceApiFindArgs) => Promise<any[]>;
type ResourceFieldApiFind = (findArgs: ResourceApiFindArgs) => Promise<ResourceApiFindResponse>;*/

export const rowActionLink = (
    action: ResourceApiGridAdditionalAction,
    rowLinks: any): any => {
    const linkName = action.apiLink ? action.apiLink : (
        action.apiAction ? 'EXEC_' + action.apiAction : (
            action.apiReport ? 'GENERATE_' + action.apiReport : null));
    const isNegative = linkName && linkName.startsWith('!');
    if (isNegative) {
        const linkPresent = rowLinks?.[linkName.substring(1)] != null;
        return linkPresent ? null : {};
    } else {
        return linkName ? rowLinks?.[linkName] : null;
    }
}

const rowActionsToGridActionsCellItems = (
    params: GridRowParams,
    rowActions: ResourceApiGridAdditionalAction[],
    forceDisabled?: boolean): React.ReactElement[] => {
    const rowLinks = params.row['_actions'];
    const actions: React.ReactElement[] = [];
    rowActions.forEach((action: ResourceApiGridAdditionalAction) => {
        const isLinkAction = action.apiLink || action.apiAction || action.apiReport;
        const link = isLinkAction ? rowActionLink(action, rowLinks) : null;
        const showAction = isLinkAction ? link != null : true;
        showAction && actions.push(
            toGridActionItem(
                params.id,
                action.title ?? (isLinkAction ? link?.title : action.apiLink || action.apiAction || action.apiReport),
                action.icon,
                action.linkTo?.replace('{{id}}', '' + params.id),
                action.onClick,
                action.showInMenu,
                forceDisabled));
    });
    return actions;
}

/*const useResourceFieldApiRequest = (
    resourceFieldName: string | undefined,
    apiIsReady: boolean,
    apiRequest: ResourceApiGenericRequest): [boolean, ResourceFieldApiFields, ResourceFieldApiFind] => {
    const [resourceFieldState, setResourceFieldState] = React.useState<any>();
    React.useEffect(() => {
        if (apiIsReady && resourceFieldName) {
            apiField({ name: resourceFieldName, callbacks: { state: setResourceFieldState } });
        }
    }, [apiIsReady, resourceFieldName]);
    const resourceFieldApiFields: ResourceFieldApiFields = () => new Promise((resolve, reject) => {
        if (resourceFieldState) {
            apiRequest('fieldOptionFields', null, {
                data: { name: resourceFieldName }
            }, resourceFieldState).then((state) => {
                const embeddedData = state.getEmbedded().map((e: any) => e.data);
                resolve(embeddedData);
            });
        } else {
            reject();
        }
    });
    const resourceFieldApiFind: ResourceFieldApiFind = (findArgs: ResourceApiFindArgs) => new Promise((resolve, reject) => {
        if (resourceFieldState) {
            apiRequest('fieldOptions', null, {
                data: { ...findArgs, name: resourceFieldName }
            }, resourceFieldState).then((state) => {
                const rows = state.getEmbedded().map((e: any) => ({
                    ...e.data,
                    '_links': processStateLinks(e.links),
                }));
                const page = state.data.page;
                resolve({ rows, page });
            });
        } else {
            reject();
        }
    });
    return [resourceFieldState != null, resourceFieldApiFields, resourceFieldApiFind];
}*/

/*const useEditableGridProps = (anyOtherProps: any, apiCurrentLinks: any): ResourceApiGridAdditionalAction[] => {
    const rowEditActions = anyOtherProps?.['rowEditActions'] ?? [];
    const onApiCurrentLinksChange: (apiCurrentLinks: any) => void = anyOtherProps?.['onApiCurrentLinksChange'];
    React.useEffect(() => {
        onApiCurrentLinksChange?.(apiCurrentLinks);
    }, [apiCurrentLinks]);
    return rowEditActions;
}*/

const useGridEditable = (
    resourceName: string,
    readOnly: boolean,
    toolbarCreateLink: string | undefined,
    rowUpdateLink: string | undefined,
    rowDetailLink: string | undefined,
    popupEditActive: boolean | undefined,
    popupEditCreateActive: boolean | undefined,
    popupEditUpdateActive: boolean | undefined,
    popupEditFormContent: React.ReactElement | undefined,
    popupEditFormAdditionalData: any,
    popupEditFormDialogTitle: string | undefined,
    popupEditFormDialogResourceTitle: string | undefined,
    popupEditFormDialogComponentProps: any,
    apiCurrentActions: any,
    apiDelete: (id: any) => Promise<any>,
    apiRef: GridApiRef) => {
    const {
        t,
        temporalMessageShow,
        messageDialogShow,
    } = useBaseAppContext();
    const formDialogPopupApiRef = React.useRef<FormDialogPopupApi>();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
    const isPopupEditCreate = popupEditActive || popupEditCreateActive;
    const isPopupEditUpdate = popupEditActive || popupEditUpdateActive;
    const doCreate = () => {
        formDialogPopupApiRef.current?.show().
            then(() => {
                apiRef.current?.refresh();
            }).
            catch(() => {
                // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
            });
    }
    const doUpdate = (id: any) => {
        formDialogPopupApiRef.current?.show(id).
            then(() => {
                apiRef.current?.refresh();
            }).
            catch(() => {
                // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
            });
    }
    const doDelete = (id: any) => {
        messageDialogShow(
            t('grid.delete.single.title'),
            t('grid.delete.single.confirm'),
            confirmDialogButtons,
            confirmDialogComponentProps).
            then((value: any) => {
                if (value) {
                    apiDelete(id)
                        .then(() => {
                            apiRef.current?.refresh();
                            temporalMessageShow(null, t('grid.delete.single.success'), 'success');
                        })
                        .catch((error: ResourceApiError) => {
                            temporalMessageShow(t('grid.delete.single.error'), error.message, 'error');
                        });
                }
            }).
            catch(() => {
                // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
            });
    }
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const toolbarAddElement = isCreateLinkPresent && !readOnly ? toToolbarIcon('add', {
        title: t('grid.create.title'),
        linkTo: toolbarCreateLink,
        onClick: !toolbarCreateLink ? doCreate : undefined,
    }) : undefined;
    const rowEditActions: ResourceApiGridAdditionalAction[] = [];
    !readOnly && rowEditActions.push({
        title: t('grid.update.title'),
        apiLink: 'update',
        icon: 'edit',
        linkTo: rowUpdateLink,
        onClick: !rowUpdateLink ? doUpdate : undefined,
    }, {
        title: t('grid.delete.title'),
        apiLink: 'delete',
        icon: 'delete',
        onClick: doDelete,
        showInMenu: true,
    });
    rowDetailLink && rowEditActions.push({
        title: t('grid.details.title'),
        apiLink: readOnly ? undefined : '!update',
        icon: 'info',
        linkTo: rowDetailLink,
    });
    const formDialogPopup = !readOnly && (isPopupEditCreate || isPopupEditUpdate) ? <FormDialogPopup
        resourceName={resourceName}
        title={popupEditFormDialogTitle}
        resourceTitle={popupEditFormDialogResourceTitle}
        formComponentProps={{ additionalData: popupEditFormAdditionalData }}
        dialogComponentProps={popupEditFormDialogComponentProps}
        apiRef={formDialogPopupApiRef}>
        {popupEditFormContent}
    </FormDialogPopup> : null;
    return {
        toolbarAddElement,
        rowEditActions,
        formDialogPopup
    };
}

const useGridColumns = (
    columns: NewGridColDef[],
    rowActions: ResourceApiGridAdditionalAction[],
    rowEditActions: ResourceApiGridAdditionalAction[],
    fields: any[] | undefined,
    rowModesModel?: GridRowModesModel) => {
    const { currentLanguage } = useResourceApiContext();
    const processedColumns = React.useMemo(() => {
        const processedColumns: NewGridColDef[] = columns.map(c => {
            const field = fields?.find(f => f.name === c.field);
            const isNumericType = isFieldNumericType(field, c.fieldType);
            const isCurrencyType = c.currencyType;
            return {
                valueGetter: (value: any, row: any, column: GridColDef) => {
                    if (column.field?.includes('.')) {
                        const value = column.field.split('.').reduce((o: any, x: string) => (typeof o == 'undefined' || o === null) ? o : o[x], row);
                        return value;
                    } else {
                        return value;
                    }
                },
                valueFormatter: (value: never, row: any) => {
                    const cany: any = c;
                    const formattedValue = formattedFieldValue(
                        value,
                        field, {
                        type: isCurrencyType ? 'currency' : c.fieldType,
                        currentLanguage,
                        currencyCode: cany['currencyCode'],
                        currencyDecimalPlaces: cany['currencyDecimalPlaces'],
                        currencyLocale: cany['currencyLocale'],
                        decimalPlaces: cany['decimalPlaces'],
                        noSeconds: cany['noSeconds'],
                        formatterParams: row,
                    });
                    return formattedValue;
                },
                headerName: field ? field?.label : '',
                headerAlign: isNumericType ? 'right' : undefined,
                align: isNumericType ? 'right' : undefined,
                display: 'flex',
                exportable: field != null,
                ...c,
            };
        });
        if (rowActions && rowActions.length) {
            processedColumns.push({
                field: ' ',
                type: 'actions',
                getActions: (params: GridRowParams) => {
                    const anyRowInEditMode = rowModesModel && Object.keys(rowModesModel).filter(m => rowModesModel[m].mode === GridRowModes.Edit).length > 0;
                    const isEditMode = rowModesModel && rowModesModel[params.id]?.mode === GridRowModes.Edit;
                    return rowActionsToGridActionsCellItems(
                        params,
                        isEditMode ? rowEditActions : rowActions,
                        anyRowInEditMode && !isEditMode);
                },
            });
        }
        return processedColumns;
    }, [columns, fields, rowModesModel]);
    return processedColumns;
}

export const useGridApiRef: () => React.MutableRefObject<GridApi> = () => {
    const gridApiRef = React.useRef<GridApi | any>({});
    return gridApiRef;
};

export const useGridApiContext: () => GridApiRef = () => {
    const gridContext = useGridContext();
    return gridContext.apiRef;
};

export const MuiGrid: React.FC<MuiGridProps> = (props) => {
    const {
        title,
        titleDisabled,
        resourceName,
        resourceFieldName,
        columns,
        readOnly,
        paginationActive,
        selectionActive,
        sortModel,
        staticSortModel,
        quickFilterInitialValue,
        quickFilterFullWidth,
        filter,
        staticFilter,
        namedQueries,
        perspectives,
        toolbarType = 'default',
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        toolbarCreateLink,
        toolbarElementsWithPositions,
        toolbarAdditionalRow,
        rowLink,
        rowDetailLink,
        rowUpdateLink,
        rowAdditionalActions = [],
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormAdditionalData,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
        popupEditFormPerspectives,
        onRowClick,
        onRowsChange,
        onRowOrderChange,
        apiRef: apiRefProp,
        datagridApiRef: datagridApiRefProp,
        height,
        autoHeight,
        striped,
        sx,
        debug = false,
        ...otherProps
    } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const apiRef = React.useRef<GridApi>();
    const datagridApiRef = useMuiDatagridApiRef();
    const [loading, setLoading] = React.useState<boolean>(true);
    const [rows, setRows] = React.useState<GridRowsProp>([]);
    const [pageInfo, setPageInfo] = React.useState<any>();
    const [findArgs, setFindArgs] = React.useState<any>();
    const [internalSortModel, setInternalSortModel] = React.useState<GridSortModel>(sortModel ?? []);
    const [internalFilter, setInternalFilter] = React.useState<string | undefined>(filter);
    const [paginationModel, setPaginationModel] = React.useState<GridPaginationModel>();
    const [rowSelectionModel, setRowSelectionModel] = React.useState<GridRowSelectionModel>();
    const {
        isReady: apiIsReady,
        currentFields: apiCurrentFields,
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        find: apiFind,
        delette: apiDelete,
    } = useResourceApiService(resourceName);
    /*const [
        resourceFieldApiRequestIsReady,
        resourceFieldApiFields,
        resourceFieldApiFind
    ] = useResourceFieldApiRequest(
        resourceFieldName,
        resourceApiIsReady,
        apiRequest);*/
    //const apiIsReady = resourceFieldName ? resourceFieldApiRequestIsReady : resourceApiIsReady;
    //const apiFields = resourceFieldName ? resourceFieldApiFields : resourceApiFields;
    if (datagridApiRefProp) {
        datagridApiRefProp.current = datagridApiRef.current;
    }
    const {
        value: quickFilterValue,
        component: quickFilterComponent
    } = useGridQuickFilter(quickFilterInitialValue, { fullWidth: quickFilterFullWidth, sx: { ml: quickFilterFullWidth ? 0 : 1 } });
    const getFindCommonArgs = React.useCallback(() => {
        const findSortModel = staticSortModel ?? internalSortModel;
        const filter = staticFilter ? (internalFilter ? '(' + staticFilter + ') and (' + internalFilter + ')' : staticFilter) : internalFilter;
        const sorts = findSortModel && findSortModel.length ? findSortModel.map(sm => sm.field + ',' + sm.sort) : undefined;
        const findCommonArgs: ResourceApiFindCommonArgs = {
            quickFilter: quickFilterValue?.length ? quickFilterValue : undefined,
            filter,
            sorts,
            namedQueries,
            perspectives,
            unpaged: !paginationActive,
            page: paginationModel?.page,
            size: paginationModel?.pageSize,
        };
        return findCommonArgs;
    }, [
        paginationActive,
        paginationModel,
        staticSortModel,
        internalSortModel,
        quickFilterValue,
        internalFilter,
        staticFilter
    ]);
    const refresh = React.useCallback(() => {
        const findCommonArgs = getFindCommonArgs();
        setFindArgs(findCommonArgs);
        const findArgs: ResourceApiFindArgs = {
            ...findCommonArgs,
            includeLinksInRows: true,
        };
        if (resourceFieldName) {
            /*if (resourceFieldApiRequestIsReady) {
                debug && logConsole.debug('Obtaining rows from API resourceFieldName', resourceName, resourceFieldName, findArgs);
                setLoading(true);
                resourceFieldApiFind(findArgs).then((response) => {
                    setPageInfo(response.page);
                    setRows(response.rows);
                    setLoading(false);
                });
            }*/
        } else {
            if (apiIsReady) {
                debug && logConsole.debug('Obtaining rows from API resource', resourceName, findArgs);
                setLoading(true);
                apiFind(findArgs).then((response) => {
                    setPageInfo(response.page);
                    setRows(response.rows);
                    setLoading(false);
                });
            }
        }
    }, [
        resourceName,
        resourceFieldName,
        getFindCommonArgs,
        //resourceFieldApiRequestIsReady,
        //resourceFieldApiFind,
        apiIsReady,
        apiFind
    ]);
    const isUpperToolbarType = toolbarType === 'upper';
    const gridMargins = isUpperToolbarType ? { m: 2 } : null;
    React.useEffect(() => {
        if (apiIsReady) {
            refresh();
        }
    }, [
        apiIsReady,
        paginationActive,
        paginationModel,
        staticSortModel,
        internalSortModel,
        staticFilter,
        internalFilter,
        quickFilterValue
    ]);
    React.useEffect(() => {
        onRowsChange?.(rows);
    }, [rows]);
    React.useEffect(() => {
        setInternalFilter(filter);
    }, [filter]);
    /*const rowEditActions = useEditableGridProps(
        otherProps,
        apiCurrentLinks);*/
    const {
        toolbarAddElement,
        rowEditActions,
        formDialogPopup
    } = useGridEditable(
        resourceName,
        readOnly ?? false,
        toolbarCreateLink,
        rowUpdateLink,
        rowDetailLink,
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormAdditionalData,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
        apiCurrentActions,
        apiDelete,
        apiRef);
    const toolbarGridElementsWithPositions: ReactElementWithPosition[] = [];
    toolbarAddElement != null && toolbarGridElementsWithPositions.push({
        position: 2,
        element: toolbarAddElement
    });
    const toolbarHideExport = true;
    const toolbarNumElements = 2 + (toolbarHideExport ? 0 : 1) + (toolbarHideRefresh ? 0 : 1) + (toolbarHideQuickFilter ? 0 : 1);
    const joinedToolbarElementsWithPositions = joinReactElementsWithPositionWithReactElementsWithPositions(
        toolbarNumElements,
        toolbarGridElementsWithPositions,
        toolbarElementsWithPositions);
    const toolbar = useToolbar(
        title ?? capitalize(resourceName) ?? '<unknown>',
        titleDisabled ?? false,
        toolbarType,
        apiCurrentError,
        quickFilterComponent,
        refresh,
        undefined,
        toolbarHideExport,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        joinedToolbarElementsWithPositions);
    //[...toolbarEditActions, ...(toolbarElementsWithPositions ?? [])]);
    const processedColumns = useGridColumns(
        columns,
        [...rowAdditionalActions, ...rowEditActions],
        rowEditActions,
        apiCurrentFields,
        otherProps.rowModesModel);
    apiRef.current = {
        refresh,
        setFilter: (filter) => setInternalFilter(filter ?? undefined),
    };
    if (apiRefProp) {
        if (apiRefProp.current) {
            apiRefProp.current.refresh = refresh;
            apiRefProp.current.setFilter = (filter) => setInternalFilter(filter ?? undefined);
        } else {
            logConsole.warn('apiRef prop must be initialized with an empty object');
        }
    }
    const sortingProps: any = {
        sortingMode: 'server',
        sortModel: internalSortModel,
        onSortModelChange: setInternalSortModel,
    };
    const paginationProps: any = paginationActive ? {
        paginationMode: 'server',
        pagination: true,
        autoPageSize: !autoHeight,
        paginationModel: paginationModel,
        onPaginationModelChange: setPaginationModel,
        rowCount: pageInfo?.totalElements ?? 0,
    } : null;
    const selectionProps: any = selectionActive ? {
        checkboxSelection: true,
        disableRowSelectionOnClick: true,
        onRowSelectionModelChange: setRowSelectionModel,
        rowSelectionModel,
        keepNonExistentRowsSelected: true,
    } : {
        disableRowSelectionOnClick: true
    };
    const stripedProps: any = striped ? {
        getRowClassName: (params: GridRowClassNameParams) => params.indexRelativeToCurrentPage % 2 === 0 ? 'even' : 'odd'
    } : null;
    const processedRows = [...rows];
    const content = <>
        {toolbar}
        {toolbarAdditionalRow ? <Box sx={{ ...gridMargins, mb: 0 }}>{toolbarAdditionalRow}</Box> : null}
        {formDialogPopup}
        <DataGrid
            {...otherProps}
            loading={loading}
            rows={processedRows}
            columns={processedColumns}
            onRowClick={onRowClick}
            onRowOrderChange={onRowOrderChange}
            apiRef={datagridApiRef}
            {...sortingProps}
            {...paginationProps}
            {...selectionProps}
            {...stripedProps}
            slots={{
                row: GridRow as GridSlots['row'],
                footer: GridFooter as GridSlots['footer'],
                noRowsOverlay: GridNoRowsOverlay,
            }}
            slotProps={{
                row: { linkTo: rowLink, cursorPointer: onRowClick != null },
                footer: { paginationActive, selectionActive, pageInfo, setRowSelectionModel },
            }}
            autoHeight={autoHeight}
            sx={{
                height: autoHeight ? 'auto' : height,
                ...gridMargins,
                ...sx,
            }} />
    </>;
    // Workaround for bug in MUI-X v6 related to the DataGrid height https://github.com/mui/mui-x/issues/10520
    const virtualScrollerStyles = {
        [`& .MuiDataGrid-main`]: {
            flex: '1 1 0px',
        }
    };
    const context = {
        resourceName,
        loading,
        findArgs,
        rows: processedRows,
        selection: rowSelectionModel,
        apiRef,
    };
    return <GridContext.Provider value={context}>
        {autoHeight || height ? content : <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', ...virtualScrollerStyles }}>
            {content}
        </Box>}
    </GridContext.Provider>;
}

export default MuiGrid;