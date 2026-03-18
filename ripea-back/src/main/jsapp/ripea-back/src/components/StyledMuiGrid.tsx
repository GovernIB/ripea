import React, {useEffect, useMemo, useState} from "react";
import {Button, Icon, Tooltip} from "@mui/material";
import {GridSlots, useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";
import {MuiDataGridProps, MuiGrid, useMuiDataGridApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {useUserSession} from "./Session.tsx";
import MassiveActionSelector, {MassiveActionProps} from "./MassiveActionSelector.tsx";
import {DraggableGridRow, DraggableGridRowHandler} from "./DraggableContext.tsx";
import {DndContext} from "@dnd-kit/core";

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
                startIcon={<Icon sx={children ?{} :{m: 0}}>{icon}</Icon>}
                {...other}
                sx={{ borderRadius: '4px',  minWidth: '20px', minHeight: '32px' }}
            >
                {children}
            </Button>
        </span>
    </Tooltip>
}

type StyledMuiGridProps = MuiDataGridProps & {
    toolbarCreateTitle?: string,
    toolbarMassiveActions?: MassiveActionProps[],
    rowProps?: (row:any) => any,
    formInitOnChange?:boolean,
    rowExpansionChange?: ( params:any, event:any, details:any ) => void,
    onRefresh?: () => any,
    disabledMassiveDefSelector?: boolean,
    hiddenMassiveDefSelector?: boolean,
    onDragEnd?: ( event:any ) => void,
}

export const DndMuiGrid = (props:StyledMuiGridProps) => {
    const {onDragEnd, ...other} = props
    const dndEnabled = onDragEnd != null && !other?.readOnly

    const additionalColumns:any[] = useMemo(()=> [
        ...props.columns,
        {
            renderCell: () => <DraggableGridRowHandler />,
            flex: 0.1
        }
    ], [props.columns])

    if (!dndEnabled) return <StyledMuiGrid {...other}/>;

    return <DndContext onDragEnd={onDragEnd}><StyledMuiGrid
        {...other}
        rowActionsColumnIndex={-1}
        columns={additionalColumns}
        slots={{
            row: DraggableGridRow as GridSlots['row'],
        }}
    /></DndContext>;
}

const StyledMuiGrid = (props:StyledMuiGridProps) => {
    const { value: user } = useUserSession();
    const gridApiRef = useMuiDataGridApiRef();
    const dataApiRef = useMuiDatagridApiRef();
    const { t } = useTranslation();

    const {
        resourceName,
        filter,
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
        readOnly,
        onRowsChange,
        onRowCountChange,
        onRowSelectionModelChange,
        rowProps,
        formInitOnChange,
        popupEditFormDialogComponentProps,
        popupEditFormComponentProps,
        rowExpansionChange,
        onRefresh,
        disabledMassiveDefSelector = false,
        hiddenMassiveDefSelector = false,
        ...others
    } = props
    const [gridRows, setGridRows] = useState<any[]>([]);
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    const refresh = () => {
        onRefresh?.()
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
                namedQueries={namedQueries}
                actions={toolbarMassiveActions ?? []}
                disabledDefSelector={disabledMassiveDefSelector}
                hiddenDefSelector={hiddenMassiveDefSelector}
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
    React.useEffect(() => {
        if (datagridApiRef.current && Object.keys(datagridApiRef.current).length > 0 && rowExpansionChange) {
            datagridApiRef.current.subscribeEvent('rowExpansionChange', rowExpansionChange);
        }
    }, [datagridApiRef.current]);

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
            // cellClassName: col?.wordWrap ? 'multi-line-cell' : undefined,
        }));
    }, [columns])

    const paginationProps = useMemo(() => {
        return user?.conf?.numElementsPagina != null
            ? {
                getRowHeight: () => 'auto',
                autoHeight: true,
                paginationModel: {page: 0, pageSize: +user?.conf?.numElementsPagina},
                pageSizeOptions: [10, 20, 50, 100, 250],
            }
            : {}
    }, [user?.conf?.numElementsPagina])

    return <div style={{ display: 'flex', flexDirection: 'column', width: '100%', height: '100%' }}>
        <style>{rowStyles}</style>

        <MuiGrid
            resourceName={resourceName}
            filter={filter}
            namedQueries={namedQueries}
            // autoHeight
            key={user?.conf?.numElementsPagina}
            paginationActive
            titleDisabled
            disableColumnMenu
            disableColumnSorting={!!staticSortModel}
            staticSortModel={staticSortModel}

            apiRef={apiRef}
            datagridApiRef={datagridApiRef}
            columns={columnsWithWordWrap}
            getRowClassName={getRowClassName}
            onRowsChange={(rows, info) => {
                setGridRows([...rows]);
                setGridSelectedRows(others?.rowSelectionModel ?? [])
                onRowsChange?.(rows, info);
                onRowCountChange?.(info?.totalElements)
            }}
            onRowSelectionModelChange={(newSelection, details) => {
                setSelectedRows([...newSelection]);
                onRowSelectionModelChange?.(newSelection, details);
            }}

            selectionActive={selectionActive || (!!toolbarMassiveActions && !readOnly)}
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
            toolbarHideExport
            toolbarHideQuickFilter
            toolbarElementsWithPositions={toolbarElements}
            rowHideUpdateButton
            rowHideDeleteButton
            readOnly={readOnly}

            {...others}
            {...paginationProps}
        />
    </div>
}
export default StyledMuiGrid;