import {useEffect, useMemo, useState} from "react";
import {Button, Icon, Tooltip} from "@mui/material";
import {MuiDataGridProps, MuiGrid, useMuiDataGridApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useUserSession} from "./Session.tsx";
import MassiveActionSelector, {MassiveActionProps} from "./MassiveActionSelector.tsx";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";

export const ToolbarButton = (props:any) => {
    const { title, icon, children, ...other } = props;

    return <Tooltip title={title}>
        <Button
            variant="outlined"
            size="small"
            startIcon={<Icon sx={{m: 0}}>{icon}</Icon>}
            {...other}
            sx={{ borderRadius: '4px',  minWidth: '20px', minHeight: '32px' }}
        >
            {children}
        </Button>
    </Tooltip>
}

type StyledMuiGridProps = MuiDataGridProps & {
    toolbarCreateTitle?: string,
    toolbarMassiveActions?: MassiveActionProps[],
    onRowCountChange?: (count:number) => void,
    rowProps?: any
}

const StyledMuiGrid = (props:StyledMuiGridProps) => {
    const { value: user } = useUserSession();
    const gridApiRef = useMuiDataGridApiRef();
    const dataApiRef = useMuiDatagridApiRef();
    const { t } = useTranslation();

    const {
        resourceName,
        filter,
        columns,
        apiRef = gridApiRef,
        datagridApiRef = dataApiRef,
        toolbarElementsWithPositions,
        toolbarCreateTitle,
        toolbarHideRefresh,
        toolbarHideCreate,
        toolbarMassiveActions,
        selectionActive,
        readOnly,
        onRowsChange,
        onRowCountChange,
        rowProps,
        ...others
    } = props
    const [gridRows, setGridRows] = useState<any[]>([]);
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }
    const create = () => {
        apiRef?.current?.showCreateDialog?.();
    }
    const setGridSelectedRows = (value:any) => {
        datagridApiRef?.current?.setRowSelectionModel?.(value)
    }

    const toolbarElements = [
        {
            position: 1,
            element: <MassiveActionSelector
                resourceName={resourceName}
                selectedRows={selectedRows}
                setSelectedRows={setGridSelectedRows}
                filter={filter}
                actions={toolbarMassiveActions ?? []}
            />,
            hidden: !toolbarMassiveActions || readOnly,
        },
        {
            position: 2,
            element: <ToolbarButton title={t('common.refresh')} icon={'refresh'} onClick={refresh} color={'primary'}/>,
            hidden: toolbarHideRefresh,
        },
        {
            position: 3,
            element: <ToolbarButton title={t('common.create')} icon={'add'} onClick={create} color={'primary'}>{toolbarCreateTitle}</ToolbarButton>,
            hidden: toolbarHideCreate || readOnly,
        },
        ...(toolbarElementsWithPositions ?? []),
    ]
        .filter((e:any) => !e?.hidden)

    useEffect(() => {
        if(!!user) {
            refresh()
        }
    }, [user]);

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
        return columns.map((col:any) => ({
            ...col,
            flex: col.flex ?? 1,
            cellClassName: 'cell-with-wrap',
        }));
    }, [columns])

    return <>
        <style>
            {`
                    .cell-with-wrap {
                        // white-space: normal !important;
                        // line-height: 1.2em;
                        // word-break: break-word;
                        // padding: 5px 10px !important;
                        // overflow: auto;
                        // display: flex;
                        // align-items: start !important;
                        text-overflow: ellipsis !important;
                    }
                    
                    .MuiDataGrid-checkboxInput {
                        transform: scale(0.8);
                    }
                    .MuiDataGrid-cell--withRenderer {
                        align-items: flex-start !important;
                    }
                    .MuiDataGrid-columnHeaderCheckbox, 
                    .MuiDataGrid-cellCheckbox {
                        align-items: flex-start !important;
                        padding-top: 4px !important;
                    }
                    [class^="row-with-color-"] .MuiDataGrid-cellCheckbox {
                        width: 48px !important;
                        max-width: 48px !important;
                        min-width: 48px !important;
                        margin-left: -4px !important;
                    }
            `}
            {rowStyles}
        </style>

        <MuiGrid
            resourceName={resourceName}
            filter={filter}

            {...others}
            apiRef={apiRef}
            datagridApiRef={datagridApiRef}
            columns={columnsWithWordWrap}
            getRowClassName={getRowClassName}
            onRowsChange={(rows, info) => {
                setGridRows([...rows]);
                onRowsChange?.(rows, info);
                onRowCountChange?.(info?.totalElements);
            }}
            rowSelectionModel={selectedRows}
            onRowSelectionModelChange={(newSelection) => {
                // console.log('Selection changed:', newSelection);
                setSelectedRows([...newSelection]);
            }}

            titleDisabled
            disableColumnMenu

            selectionActive={selectionActive || !!toolbarMassiveActions}
            checkboxSelection={selectionActive || !!toolbarMassiveActions}
            keepNonExistentRowsSelected={selectionActive || !!toolbarMassiveActions}

            toolbarHideRefresh
            toolbarHideCreate
            toolbarHideExport
            toolbarHideQuickFilter
            toolbarElementsWithPositions={toolbarElements}
            readOnly={readOnly}
        />
    </>
}
export default StyledMuiGrid;