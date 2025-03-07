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
import { useResourceApiService } from '../../ResourceApiProvider';
import { toDataGridActionItem } from './DataGridActionItem';
import {
    useApiDataCommon,
    useDataCommonEditable,
    DataCommonAdditionalAction
} from '../datacommon/MuiDataCommon';
import { useDataToolbar, DataToolbarType } from '../datacommon/DataToolbar';
import DataGridRow from './DataGridRow';
import DataGridFooter from './DataGridFooter';
import DataGridNoRowsOverlay from './DataGridNoRowsOverlay';
import DataGridCustomStyle from './DataGridCustomStyle';
import DataGridContext, { MuiDataGridApi, MuiDataGridApiRef, useDataGridContext } from './DataGridContext';

export const LOG_PREFIX = 'GRID';

export type MuiDataGridColDef = GridColDef & {
    fieldType?: string;
    currencyType?: boolean;
    currencyCode?: string | ((row: any) => string);
    currencyDecimalPlaces?: number | ((row: any) => number);
    currencyLocale?: string | ((row: any) => string);
    decimalPlaces?: number | ((row: any) => number);
    noSeconds?: boolean;
    exportExcluded?: boolean;
};

export type MuiDataGridProps = {
    title?: string;
    titleDisabled?: true;
    subtitle?: string;
    resourceName: string;
    resourceFieldName?: string;
    columns: MuiDataGridColDef[];
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
    formAdditionalData?: ((row: any, action: string) => any) | any;
    treeDataAdditionalRows?: any[] | ((rows: any[]) => any[]);
    toolbarType?: DataToolbarType;
    toolbarHideRefresh?: true;
    toolbarHideQuickFilter?: true;
    toolbarCreateLink?: string;
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    toolbarAdditionalRow?: React.ReactElement;
    rowLink?: string;
    rowDetailLink?: string;
    rowUpdateLink?: string;
    rowActionsColumnProps?: any;
    rowAdditionalActions?: DataCommonAdditionalAction[];
    popupEditActive?: boolean;
    popupEditCreateActive?: boolean;
    popupEditUpdateActive?: boolean;
    popupEditFormContent?: React.ReactElement;
    popupEditFormDialogTitle?: string;
    popupEditFormDialogResourceTitle?: string;
    popupEditFormDialogComponentProps?: any;
    onRowsChange?: (rows: GridRowsProp) => void;
    onRowOrderChange?: GridEventListener<"rowOrderChange">;
    apiRef?: MuiDataGridApiRef;
    datagridApiRef?: React.MutableRefObject<GridApiPro>;
    height?: number;
    autoHeight?: true;
    striped?: true;
    sx?: any;
    debug?: boolean;
} & Omit<DataGridProps, 'apiRef'>;

const rowActionLink = (
    action: DataCommonAdditionalAction,
    rowLinks: any): any => {
    const rowLinkName = action.rowApiLink ? action.rowApiLink : (
        action.rowApiAction ? 'EXEC_' + action.rowApiAction : (
            action.rowApiReport ? 'GENERATE_' + action.rowApiReport : null));
    const isNegative = rowLinkName && rowLinkName.startsWith('!');
    if (isNegative) {
        const linkPresent = rowLinks?.[rowLinkName.substring(1)] != null;
        return linkPresent ? null : {};
    } else {
        return rowLinkName ? rowLinks?.[rowLinkName] : null;
    }
}

const rowActionsToGridActionsCellItems = (
    params: GridRowParams,
    rowActions: DataCommonAdditionalAction[],
    popupCreate: (row?: any) => void,
    popupUpdate: (id: any, row?: any) => void,
    forceDisabled?: boolean): React.ReactElement[] => {
    const rowLinks = params.row['_actions'];
    const actions: React.ReactElement[] = [];
    rowActions.forEach((action: DataCommonAdditionalAction) => {
        const isLinkAction = action.rowApiLink || action.rowApiAction || action.rowApiReport;
        const link = isLinkAction ? rowActionLink(action, rowLinks) : null;
        const showAction = isLinkAction ? link != null : true;
        const actionLinkTo = (typeof action.linkTo === 'function') ? action.linkTo?.(params.row) : action.linkTo?.replace('{{id}}', '' + params.id);
        const actionLinkState = (typeof action.linkState === 'function') ? action.linkState?.(params.row) : action.linkState;
        const actionOnClick = action.popupCreateOnClick ? () => popupCreate(params.row) : (action.popupUpdateOnClick ? () => popupUpdate(params.id, params.row) : action.onClick);
        const disabled = forceDisabled || ((typeof action.disabled === 'function') ? action.disabled(params.row) : action.disabled);
        showAction && actions.push(
            toDataGridActionItem(
                params.id,
                action.title ?? (isLinkAction ? link?.title : isLinkAction),
                params.row,
                action.icon,
                actionLinkTo,
                actionLinkState,
                actionOnClick,
                action.showInMenu,
                disabled));
    });
    return actions;
}

const useGridColumns = (
    columns: MuiDataGridColDef[],
    rowActionsColumnProps: any,
    rowActions: DataCommonAdditionalAction[],
    rowEditActions: DataCommonAdditionalAction[],
    fields: any[] | undefined,
    popupCreate: (row?: any) => void,
    popupUpdate: (id: any, row?: any) => void,
    rowModesModel?: GridRowModesModel) => {
    const { currentLanguage } = useResourceApiContext();
    const processedColumns = React.useMemo(() => {
        const processedColumns: MuiDataGridColDef[] = columns.map(c => {
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
                        popupCreate,
                        popupUpdate,
                        anyRowInEditMode && !isEditMode);
                },
                ...rowActionsColumnProps,
            });
        }
        return processedColumns;
    }, [columns, fields, rowModesModel]);
    return processedColumns;
}

export const useMuiDataGridApiRef: () => React.MutableRefObject<MuiDataGridApi> = () => {
    const gridApiRef = React.useRef<MuiDataGridApi | any>({});
    return gridApiRef;
};

export const useMuiDataGridApiContext: () => MuiDataGridApiRef = () => {
    const gridContext = useDataGridContext();
    return gridContext.apiRef;
};

export const MuiDataGrid: React.FC<MuiDataGridProps> = (props) => {
    const {
        title,
        titleDisabled,
        subtitle,
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
        filter: filterProp,
        staticFilter,
        namedQueries,
        perspectives,
        formAdditionalData,
        treeDataAdditionalRows,
        toolbarType = 'default',
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        toolbarCreateLink,
        toolbarElementsWithPositions,
        toolbarAdditionalRow,
        rowLink,
        rowDetailLink,
        rowUpdateLink,
        rowActionsColumnProps,
        rowAdditionalActions = [],
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
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
    const datagridApiRef = useMuiDatagridApiRef();
    const treeDataAdditionalRowsIsFunction = treeDataAdditionalRows ? typeof treeDataAdditionalRows === 'function' : false;
    const [internalSortModel, setInternalSortModel] = React.useState<GridSortModel>(sortModel ?? []);
    const [internalFilter, setInternalFilter] = React.useState<string | undefined>(filterProp);
    const [paginationModel, setPaginationModel] = React.useState<GridPaginationModel>();
    const [rowSelectionModel, setRowSelectionModel] = React.useState<GridRowSelectionModel>();
    const [additionalRows, setAdditionalRows] = React.useState<any[]>(!treeDataAdditionalRowsIsFunction ? [] : treeDataAdditionalRows as any[]);
    const {
        currentFields: apiCurrentFields,
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        delette: apiDelete,
    } = useResourceApiService(resourceName);
    if (datagridApiRefProp) {
        datagridApiRefProp.current = datagridApiRef.current as any;
    }
    const findArgs = React.useMemo(() => {
        const filter = staticFilter ? (internalFilter ? '(' + staticFilter + ') and (' + internalFilter + ')' : staticFilter) : internalFilter;
        const findSortModel = staticSortModel ?? internalSortModel;
        const sorts = findSortModel && findSortModel.length ? findSortModel.map(sm => sm.field + ',' + sm.sort) : undefined;
        const paginationArgs = paginationActive ? {
            page: paginationModel?.page,
            size: paginationModel?.pageSize,
        } : { unpaged: true }
        return {
            ...paginationArgs,
            sorts,
            filter,
            namedQueries,
            perspectives
        };
    }, [
        paginationActive,
        paginationModel,
        staticSortModel,
        internalSortModel,
        internalFilter,
        staticFilter
    ]);
    const {
        loading,
        rows,
        pageInfo,
        refresh,
        quickFilterComponent
    } = useApiDataCommon(
        resourceName,
        findArgs,
        quickFilterInitialValue,
        { fullWidth: quickFilterFullWidth, sx: { ml: quickFilterFullWidth ? 0 : 1 } });
    const isUpperToolbarType = toolbarType === 'upper';
    const gridMargins = isUpperToolbarType ? { m: 2 } : null;
    React.useEffect(() => {
        onRowsChange?.(rows);
        if (treeDataAdditionalRowsIsFunction) {
            setAdditionalRows((treeDataAdditionalRows as ((rows: any[]) => any[]))(rows));
        }
    }, [rows]);
    React.useEffect(() => {
        setInternalFilter(filterProp);
    }, [filterProp]);
    const {
        toolbarAddElement,
        rowEditActions,
        popupDialog,
        popupCreate,
        popupUpdate,
    } = useDataCommonEditable(
        resourceName,
        readOnly ?? false,
        formAdditionalData,
        toolbarCreateLink,
        rowDetailLink,
        rowUpdateLink,
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
        apiCurrentActions,
        apiDelete,
        refresh);
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
    const toolbar = useDataToolbar(
        title ?? capitalize(resourceName) ?? '<unknown>',
        titleDisabled ?? false,
        subtitle,
        toolbarType,
        apiCurrentError,
        quickFilterComponent,
        refresh,
        undefined,
        toolbarHideExport,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        joinedToolbarElementsWithPositions);
    const processedColumns = useGridColumns(
        columns,
        rowActionsColumnProps,
        [...rowAdditionalActions, ...rowEditActions],
        rowEditActions,
        apiCurrentFields,
        popupCreate,
        popupUpdate,
        otherProps.rowModesModel);
    const apiRef = React.useRef<MuiDataGridApi>({
        refresh,
        popupCreate,
        popupUpdate,
        setFilter: (filter) => setInternalFilter(filter ?? undefined),
    });
    if (apiRefProp) {
        if (apiRefProp.current) {
            apiRefProp.current.refresh = refresh;
            apiRefProp.current.popupCreate = popupCreate;
            apiRefProp.current.popupUpdate = popupUpdate;
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
    const processedRows = [...additionalRows, ...rows];
    const content = <>
        {toolbar}
        {toolbarAdditionalRow ? <Box sx={{ ...gridMargins, mb: 0 }}>{toolbarAdditionalRow}</Box> : null}
        {popupDialog}
        <DataGridCustomStyle
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
                row: DataGridRow as GridSlots['row'],
                footer: DataGridFooter as GridSlots['footer'],
                noRowsOverlay: DataGridNoRowsOverlay,
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
    return <DataGridContext.Provider value={context}>
        {autoHeight || height ? content : <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', ...virtualScrollerStyles }}>
            {content}
        </Box>}
    </DataGridContext.Provider>;
}

export default MuiDataGrid;