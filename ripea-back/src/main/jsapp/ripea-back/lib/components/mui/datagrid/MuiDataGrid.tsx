import React from 'react';
import {
    DataGridProProps as DataGridProps,
    GridRowsProp,
    GridRowParams,
    GridRowClassNameParams,
    GridColDef,
    GridSortModel,
    GridSortDirection,
    GridPaginationModel,
    GridRowSelectionModel,
    GridSlots,
    GridRowModes,
    GridRowModesModel,
    GridApiPro,
    GridEventListener,
    GridCallbackDetails,
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
import { useResourceApiContext, ResourceType, ExportFileType } from '../../ResourceApiContext';
import { useResourceApiService } from '../../ResourceApiProvider';
import { toDataGridActionItem, DataGridActionItemOnClickFn } from './DataGridActionItem';
import {
    useApiDataCommon,
    useDataCommonEditable,
    DataCommonAdditionalAction,
    DataCommonShowCreateDialogFn,
    DataCommonShowUpdateDialogFn,
    DataCommonTriggerDeleteFn,
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
    noTime?: boolean;
    noSeconds?: boolean;
    exportExcluded?: boolean;
    sortProcessor?: (field: string, sort: GridSortDirection) => GridSortModel | undefined;
};

export type MuiDataGridProps = {
    title?: string;
    titleDisabled?: true;
    subtitle?: string;
    resourceName: string;
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    resourceFieldName?: string;
    columns: MuiDataGridColDef[];
    readOnly?: boolean;
    findDisabled?: boolean;
    paginationActive?: boolean;
    selectionActive?: boolean;
    sortModel?: GridSortModel;
    staticSortModel?: GridSortModel;
    quickFilterInitialValue?: string;
    quickFilterSetFocus?: true;
    quickFilterFullWidth?: true;
    filter?: string;
    staticFilter?: string;
    namedQueries?: string[];
    perspectives?: string[];
    exportFileType?: ExportFileType;
    formAdditionalData?: ((row: any, action: string) => any) | any;
    treeDataAdditionalRows?: any[] | ((rows: any[]) => any[]);
    toolbarType?: DataToolbarType;
    toolbarHide?: true;
    toolbarHideExport?: false;
    toolbarHideCreate?: true;
    toolbarHideRefresh?: true;
    toolbarHideQuickFilter?: true;
    toolbarCreateLink?: string;
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    toolbarAdditionalRow?: React.ReactElement;
    rowLink?: string;
    rowDetailLink?: string;
    rowUpdateLink?: string;
    rowDisableUpdateButton?: boolean | ((row: any) => boolean);
    rowDisableDeleteButton?: boolean | ((row: any) => boolean);
    rowDisableDetailsButton?: boolean | ((row: any) => boolean);
    rowHideUpdateButton?: boolean | ((row: any) => boolean);
    rowHideDeleteButton?: boolean | ((row: any) => boolean);
    rowHideDetailsButton?: boolean | ((row: any) => boolean);
    rowActionsColumnProps?: any;
    rowAdditionalActions?: DataCommonAdditionalAction[];
    rowSelectionModel?: GridRowSelectionModel,
    popupEditActive?: boolean;
    popupEditCreateActive?: boolean;
    popupEditUpdateActive?: boolean;
    popupEditFormContent?: React.ReactElement;
    popupEditFormDialogTitle?: string;
    popupEditFormDialogResourceTitle?: string;
    popupEditFormDialogComponentProps?: any;
    popupEditFormDialogOnClose?: (reason?: string) => boolean;
    popupEditFormComponentProps?: any;
    onRowsChange?: (rows: GridRowsProp, pageInfo: any) => void;
    onRowOrderChange?: GridEventListener<'rowOrderChange'>;
    onRowSelectionModelChange?: (rowSelectionModel: GridRowSelectionModel, details: GridCallbackDetails) => void;
    apiRef?: MuiDataGridApiRef;
    datagridApiRef?: React.MutableRefObject<GridApiPro>;
    height?: number;
    autoHeight?: true;
    striped?: true;
    semiBordered?: true;
    sx?: any;
    debug?: boolean;
} & Omit<DataGridProps, 'apiRef'>;

const processFindSortModel = (sortModel: GridSortModel, columns: MuiDataGridColDef[]) => {
    const result: any[] = [];
    sortModel.forEach(({ field, sort }) => {
        const columnForCurrentField = columns.find((c) => c.field === field);
        const mappedFields = columnForCurrentField?.sortProcessor
            ? columnForCurrentField.sortProcessor(field, sort)
            : undefined;
        if (mappedFields) {
            mappedFields.forEach((mappedField) => result.push(mappedField));
        } else {
            result.push({ field, sort });
        }
    });
    return result as GridSortModel;
};

const rowLinkFind = (rowLink: string | undefined, rowLinks: any[] | undefined) => {
    if (rowLink != null) {
        const isNegative = rowLink != null && rowLink.startsWith('!');
        return isNegative ? rowLinks?.[(rowLink.substring(1) as any)] : rowLinks?.[(rowLink as any)];
    }
}
const rowLinkShowCheck = (rowLink: string | undefined, rowLinks: any[] | undefined) => {
    const found = rowLinkFind(rowLink, rowLinks);
    if (found) {
        const isNegative = rowLink != null && rowLink.startsWith('!');
        return isNegative ? found == null : found != null;
    } else {
        return true;
    }
}
const rowArtifactShowCheck = (action: string | undefined, report: string | undefined, artifacts: any[] | undefined) => {
    if (action != null) {
        return artifacts?.find(a => a.type === 'ACTION' && a.code === action) != null;
    } else if (report != null) {
        return artifacts?.find(a => a.type === 'REPORT' && a.code === report) != null;
    } else {
        return true;
    }
}
const getRowActionOnClick = (
    rowAction: DataCommonAdditionalAction,
    showCreateDialog: DataCommonShowCreateDialogFn,
    showUpdateDialog: DataCommonShowUpdateDialogFn,
    triggerDelete: DataCommonTriggerDeleteFn): DataGridActionItemOnClickFn | undefined => {
    if (rowAction.clickShowCreateDialog) {
        return (_id, row) => showCreateDialog(row);
    } else if (rowAction.clickShowUpdateDialog) {
        return (id, row) => showUpdateDialog(id, row);
    } else if (rowAction.clickTriggerDelete) {
        return (id) => triggerDelete(id);
    } else {
        return rowAction.onClick;
    }
}

const rowActionsToGridActionsCellItems = (
    rowActions: DataCommonAdditionalAction[],
    params: GridRowParams,
    showCreateDialog: DataCommonShowCreateDialogFn,
    showUpdateDialog: DataCommonShowUpdateDialogFn,
    triggerDelete: DataCommonTriggerDeleteFn,
    artifacts: any[] | undefined,
    forceDisabled?: boolean): React.ReactElement[] => {
    const actions: React.ReactElement[] = [];
    rowActions.forEach((rowAction: DataCommonAdditionalAction) => {
        const rowLink = rowLinkFind(rowAction.rowLink, params.row['_actions']);
        const rowLinkShow = rowLinkShowCheck(rowAction.rowLink, params.row['_actions']);
        const rowArtifactShow = rowArtifactShowCheck(rowAction.action, rowAction.report, artifacts);
        const rowActionLinkTo = (typeof rowAction.linkTo === 'function') ? rowAction.linkTo?.(params.row) : rowAction.linkTo?.replace('{{id}}', '' + params.id);
        const rowActionLinkState = (typeof rowAction.linkState === 'function') ? rowAction.linkState?.(params.row) : rowAction.linkState;
        const rowActionOnClick = getRowActionOnClick(rowAction, showCreateDialog, showUpdateDialog, triggerDelete);
        const showInMenu = (typeof rowAction.showInMenu === 'function') ? rowAction.showInMenu(params.row) : rowAction.showInMenu;
        const disabled = forceDisabled || ((typeof rowAction.disabled === 'function') ? rowAction.disabled(params.row) : rowAction.disabled);
        const hidden = (typeof rowAction.hidden === 'function') ? rowAction.hidden(params.row) : rowAction.hidden;
        rowLinkShow && rowArtifactShow && !hidden && actions.push(
            toDataGridActionItem(
                params.id,
                rowAction.title ?? (rowLink != null ? rowLink?.title : rowAction),
                params.row,
                rowAction.icon,
                rowActionLinkTo,
                rowActionLinkState,
                rowActionOnClick,
                showInMenu,
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
    showCreateDialog: DataCommonShowCreateDialogFn,
    showUpdateDialog: DataCommonShowUpdateDialogFn,
    triggerDelete: DataCommonTriggerDeleteFn,
    artifacts: any[] | undefined,
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
                        noTime: cany['noTime'],
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
                        isEditMode ? rowEditActions : rowActions,
                        params,
                        showCreateDialog,
                        showUpdateDialog,
                        triggerDelete,
                        artifacts,
                        anyRowInEditMode && !isEditMode);
                },
                ...rowActionsColumnProps,
            });
        }
        return processedColumns;
    }, [columns, fields, rowModesModel, artifacts]);
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
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        columns,
        readOnly,
        findDisabled,
        paginationActive,
        selectionActive,
        sortModel,
        staticSortModel,
        quickFilterInitialValue,
        quickFilterSetFocus,
        quickFilterFullWidth,
        filter: filterProp,
        staticFilter,
        namedQueries,
        perspectives,
        exportFileType = 'PDF',
        formAdditionalData,
        treeDataAdditionalRows,
        toolbarType = 'default',
        toolbarHide,
        toolbarHideExport = true,
        toolbarHideCreate,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        toolbarCreateLink,
        toolbarElementsWithPositions,
        toolbarAdditionalRow,
        rowLink,
        rowDetailLink,
        rowUpdateLink,
        rowDisableUpdateButton,
        rowDisableDeleteButton,
        rowDisableDetailsButton,
        rowHideUpdateButton,
        rowHideDeleteButton,
        rowHideDetailsButton,
        rowActionsColumnProps,
        rowAdditionalActions = [],
        rowSelectionModel: rowSelectionModelProp = [],
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
        popupEditFormDialogOnClose,
        popupEditFormComponentProps,
        onRowClick,
        onRowsChange,
        onRowOrderChange,
        onRowSelectionModelChange,
        apiRef: apiRefProp,
        datagridApiRef: datagridApiRefProp,
        height,
        autoHeight,
        striped,
        semiBordered,
        sx,
        debug = false,
        ...otherProps
    } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const datagridApiRef = useMuiDatagridApiRef();
    const anyArtifactRowAction = rowAdditionalActions?.find(a => a.action != null || a.report != null) != null;
    const treeDataAdditionalRowsIsFunction = treeDataAdditionalRows ? typeof treeDataAdditionalRows === 'function' : false;
    const [internalSortModel, setInternalSortModel] = React.useState<GridSortModel>(sortModel ?? []);
    const [internalFilter, setInternalFilter] = React.useState<string | undefined>(filterProp);
    const [paginationModel, setPaginationModel] = React.useState<GridPaginationModel>();
    const [rowSelectionModel, setRowSelectionModel] = React.useState<GridRowSelectionModel>(rowSelectionModelProp);
    const [additionalRows, setAdditionalRows] = React.useState<any[]>(!treeDataAdditionalRowsIsFunction ? [] : treeDataAdditionalRows as any[]);
    const {
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        delete: apiDelete,
    } = useResourceApiService(resourceName);
    if (datagridApiRefProp) {
        datagridApiRefProp.current = datagridApiRef.current as any;
    }
    const findArgs = React.useMemo(() => {
        const filter = staticFilter ? (internalFilter ? '(' + staticFilter + ') and (' + internalFilter + ')' : staticFilter) : internalFilter;
        const findSortModel = staticSortModel ?? internalSortModel;
        const processedFindSortModel = processFindSortModel(findSortModel, columns);
        const sorts = processedFindSortModel?.length ? processedFindSortModel.map(({ field, sort }) => `${field},${sort}`) : undefined;
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
        staticFilter,
        namedQueries,
        perspectives,
        columns,
    ]);
    const {
        loading,
        fields,
        rows,
        pageInfo,
        artifacts,
        refresh,
        export: exportt,
        quickFilterComponent
    } = useApiDataCommon(
        resourceName,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        findDisabled,
        findArgs,
        quickFilterInitialValue,
        quickFilterSetFocus,
        { fullWidth: quickFilterFullWidth, sx: { ml: quickFilterFullWidth ? 0 : 1 } },
        anyArtifactRowAction);
    const isUpperToolbarType = toolbarType === 'upper';
    const gridMargins = isUpperToolbarType ? { m: 2 } : null;
    React.useEffect(() => {
        onRowsChange?.(rows, pageInfo);
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
        formDialogComponent,
        showCreateDialog,
        showUpdateDialog,
        triggerDelete,
    } = useDataCommonEditable(
        resourceName,
        readOnly ?? false,
        formAdditionalData,
        toolbarCreateLink,
        rowDetailLink,
        rowUpdateLink,
        rowDisableUpdateButton,
        rowDisableDeleteButton,
        rowDisableDetailsButton,
        rowHideUpdateButton,
        rowHideDeleteButton,
        rowHideDetailsButton,
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogComponentProps,
        popupEditFormDialogOnClose,
        popupEditFormComponentProps,
        apiCurrentActions,
        apiDelete,
        refresh);
    const toolbarNodesPosition = 2;
    const toolbarGridElementsWithPositions: ReactElementWithPosition[] = [];
    toolbarAddElement != null && toolbarGridElementsWithPositions.push({
        position: toolbarNodesPosition,
        element: !toolbarHideCreate ? toolbarAddElement : <span/>,
    });
    const toolbarNumElements = toolbarNodesPosition + (toolbarHideExport ? 0 : 1) + (toolbarHideRefresh ? 0 : 1) + (toolbarHideQuickFilter ? 0 : 1);
    const joinedToolbarElementsWithPositions = joinReactElementsWithPositionWithReactElementsWithPositions(
        toolbarNumElements,
        toolbarGridElementsWithPositions,
        toolbarElementsWithPositions);
    const gridExport = () => {
        const exportFields: string[] = columns.filter(c => {
            const field = fields?.find(f => f.name === c.field);
            return field != null;
        }).map(c => c.field);
        exportt(exportFields, exportFileType, true);
    }
    const toolbar = useDataToolbar(
        title ?? capitalize(resourceName) ?? '<unknown>',
        titleDisabled ?? false,
        subtitle,
        toolbarType,
        apiCurrentError,
        quickFilterComponent,
        refresh,
        gridExport,
        toolbarHideExport,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        joinedToolbarElementsWithPositions);
    const processedColumns = useGridColumns(
        columns,
        rowActionsColumnProps,
        [...rowAdditionalActions, ...rowEditActions],
        rowEditActions,
        fields,
        showCreateDialog,
        showUpdateDialog,
        triggerDelete,
        artifacts,
        otherProps.rowModesModel);
    const apiRef = React.useRef<MuiDataGridApi>({
        refresh,
        export: gridExport,
        showCreateDialog,
        showUpdateDialog,
        setFilter: (filter) => setInternalFilter(filter ?? undefined),
    });
    if (apiRefProp) {
        if (apiRefProp.current) {
            apiRefProp.current.refresh = refresh;
            apiRefProp.current.export = gridExport;
            apiRefProp.current.showCreateDialog = showCreateDialog;
            apiRefProp.current.showUpdateDialog = showUpdateDialog;
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
        onRowSelectionModelChange: (rowSelectionModel: GridRowSelectionModel, details: GridCallbackDetails) => {
            setRowSelectionModel(rowSelectionModel);
            onRowSelectionModelChange?.(rowSelectionModel, details);
        },
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
        {!toolbarHide && toolbar}
        {toolbarAdditionalRow ? <Box sx={{ ...gridMargins, mb: 0 }}>{toolbarAdditionalRow}</Box> : null}
        {formDialogComponent}
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
                noRowsOverlay: { findDisabled },
            }}
            semiBordered={semiBordered}
            autoHeight={autoHeight}
            sx={{
                height: autoHeight ? 'auto' : undefined,
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
        {autoHeight ? content : <Box sx={{ display: 'flex', flexDirection: 'column', height: height ? height : '100%', ...virtualScrollerStyles }}>
            {content}
        </Box>}
    </DataGridContext.Provider>;
}

export default MuiDataGrid;