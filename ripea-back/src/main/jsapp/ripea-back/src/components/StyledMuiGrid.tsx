import {useEffect, useState} from "react";
import {MuiGrid, useMuiDataGridApiRef} from "reactlib";
import {useUserSession} from "./Session.tsx";
import {Button, Icon, Tooltip} from "@mui/material";
import {useTranslation} from "react-i18next";
import MassiveActionSelector from "./MassiveActionSelector.tsx";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro/hooks/utils/useGridApiRef";

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

const StyledMuiGrid = (props:any) => {
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
        ...(toolbarElementsWithPositions ?? []),
        {
            position: 2,
            element: <MassiveActionSelector
                resourceName={resourceName}
                selectedRows={selectedRows}
                setSelectedRows={setGridSelectedRows}
                filter={filter}
                actions={toolbarMassiveActions}
            />,
            hidden: !toolbarMassiveActions || readOnly,
        },
        {
            position: 3,
            element: <ToolbarButton title={t('common.refresh')} icon={'refresh'} onClick={refresh} color={'info'}/>,
            hidden: toolbarHideRefresh,
        },
        {
            position: 4,
            element: <ToolbarButton title={t('common.create')} icon={'add'} onClick={create} color={'info'}>{toolbarCreateTitle}</ToolbarButton>,
            hidden: toolbarHideCreate || readOnly,
        }
    ]
        .filter(e => !e.hidden)

    useEffect(() => {
        if(!!user) {
            refresh()
        }
    }, [user]);

    // Custom row styling with colored bar
    const getRowClassName = (params: any) :string => {
        const color = params.row?.estatAdditionalInfo?.color;
        const className = (color ? `row-with-color-${params.row.id} ` : '') + (params.indexRelativeToCurrentPage % 2 === 0 ? 'even' : 'odd');
        // console.log('Row className:', className);
        return className;
    };

    // Apply custom CSS for rows with color
    const getRowStyle = () => {
        const styles: any = {};
        if (gridRows.length > 0) {
            gridRows.forEach((row: any) => {
                const color = row?.estatAdditionalInfo?.color;
                if (color) {
                    styles[`.row-with-color-${row.id}`] = {
                        'box-shadow': `${color} -6px 0px 0px`,
                        'border-left': `6px solid ${color}`
                    };
                }
            });
        }
        return styles;
    };

    // Applica word wrap a totes les columnes
    const columnsWithWordWrap = columns.map((col:any) => ({
        ...col,
        flex: col.flex || 1,
        cellClassName: 'cell-with-wrap',
    }));

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
                    ${Object.entries(getRowStyle()).map(([className, style]) =>
                `${className} { ${Object.entries(style as any).map(([prop, value]) =>
                    `${prop}: ${value};`).join(' ')} }`
            ).join('\n')}
                    `}
        </style>

        <MuiGrid
            resourceName={resourceName}
            filter={filter}

            {...others}
            apiRef={apiRef}
            datagridApiRef={datagridApiRef}
            columns={columnsWithWordWrap}
            getRowClassName={getRowClassName}
            onRowsChange={(rows) => { setGridRows([...rows]); }}
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