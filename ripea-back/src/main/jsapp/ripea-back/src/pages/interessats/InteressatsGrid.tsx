import React, {useMemo, useRef, useState} from "react";
import {Chip, Icon} from "@mui/material";
import { useMuiDataGridApiRef } from 'reactlib';
import {useTranslation} from "react-i18next";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import useInteressatActions, {useMassiveActions} from "./details/InteressatActions.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import useImport from "./actions/Import.tsx";
import {useActions as useExpedientActions} from "../expedient/details/CommonActions.tsx"
import {InteressatsGridForm} from "./InteressatsGridForm.tsx";
import {MenuActionButton} from "../../components/MenuButton.tsx";
import {useUserSession} from "../../components/Session.tsx";
import useInteressatDetail from "./details/InteressatDetail.tsx";
import {useGrupGridDialog} from "./GrupGrid.tsx";

const perspectives = ['REPRESENTANT', 'GRUPS']
const sortModel:any = [{field: 'id', sort: 'asc'}]

interface DetailGridProps {
    entity: any,
    num: number,
    onRowCountChange?: (number: number) => void,
}

const InteressatsGrid: React.FC<DetailGridProps> = (props: DetailGridProps) => {
    const {entity, num, onRowCountChange} = props
    const { t } = useTranslation();
    const { value: user } = useUserSession()
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
	const idToOriginalIdRef = useRef<Record<string, any>>({});

    const filter = useMemo(() => builder.and(
        builder.eq('expedient.id', entity?.id),
        builder.eq('esRepresentant', false)
    ),[entity])
	
    const columns = useMemo(() => [
        {
            field: 'tipus',
            flex: 0.5,
        },
        {
            field: 'documentNum',
            flex: 0.5,
        },
        {
            field: 'nomComplet',// organNom
            flex: 1.0,
            valueFormatter: (value: any, row:any) => value ?? row?.organNom,
            renderCell: (params:any) => <>
                {params?.formattedValue}
                {!params?.row?.isGroup && !params?.row?.arxiuPropagat &&
                    <Icon title={t('page.contingut.alert.guardarPendent')} color={'error'}>warning</Icon>}
            </>
        },
        {
            field: 'representant',
            flex: 1.0,
            renderCell: (params:any) => <>
                {params?.formattedValue}
                {params?.row?.representant && !params?.row?.representantInfo?.arxiuPropagat &&
                    <Icon title={t('page.contingut.alert.guardarPendent')} color={'error'}>warning</Icon>}
            </>,
        },
        {
            field: 'grups',
            headerName: t('page.interessat.grup.title'),
            flex: 1,
            renderCell: (params:any) => params?.row?.grups
                ?.map?.((g:any) => <Chip label={g?.description} size={"small"}/>),
        },
    ], [t]);

    const apiRef = useMuiDataGridApiRef()

    const refresh = ()=> {
		// refreshInteressats();
        apiRef?.current?.refresh()
    }

    const {actions, createActions, components} = useInteressatActions(entity, refresh)
    const {exportar} = useMassiveActions(refresh);
    const {excelInteressats} = useExpedientActions(refresh);
    const {handleShow: handleImport, content: contentImport} = useImport(entity, refresh);
    const {handleOpen, dialog} = useInteressatDetail();
    const {handleOpen: handleGrup, dialog: dialogGrup} = useGrupGridDialog(entity, refresh);

    return (
        <>
            <StyledMuiGrid
                resourceName="interessatResource"
                popupEditFormDialogResourceTitle={t('page.interessat.title')}
                columns={columns}
                paginationActive={false}
                autoHeight
                apiRef={apiRef}
                filter={filter}
                sortModel={sortModel}
                perspectives={perspectives}
                // disableColumnSorting
                popupEditCreateActive
                popupEditFormContent={<InteressatsGridForm />}
                formAdditionalData={{
                    expedient: { id: entity?.id },
                    esRepresentant: false,
                }}
                rowAdditionalActions={actions}
                onRowCountChange={onRowCountChange}
                toolbarCreateTitle={t('page.interessat.action.new.label')}
                toolbarShowCreate={!user?.sessionScope?.isMostrarImportacio && entity?.potModificar}
                toolbarShowQuickFilter
                toolbarHideRefresh
                selectionActive
                onRowSelectionModelChange={(newSelection) => {
                    // console.log('Selection changed:', newSelection);
                    setSelectedRows([...newSelection]);
                }}
                toolbarElementsWithPositions={[
                    {
                        position: 0,
                        element: (
                            <ToolbarButton
                                icon={'upload'}
                                onClick={() => {
                                    const originalIds = selectedRows.map((id) => idToOriginalIdRef.current[id] ?? id);
                                    exportar(originalIds, entity);
                                }}
                                disabled={selectedRows?.length == 0}
                                title={
                                    selectedRows?.length == 0 ? t('page.interessat.action.exportar.hint') : undefined
                                }
                            >
                                {t('page.interessat.action.exportar.label')}
                            </ToolbarButton>
                        ),
                    },
                    {
                        position: 0,
                        element: (
                            <ToolbarButton
                                icon={'download'}
                                onClick={() => handleImport()}
                                hidden={!entity?.potModificar}
                            >
                                {t('page.interessat.action.importar.label')}
                            </ToolbarButton>
                        ),
                    },
                    {
                        position: 0,
                        element: (
                            <ToolbarButton icon={'groups'} onClick={() => handleGrup()} hidden={!entity?.potModificar}>
                                {t('page.interessat.grup.title')}
                            </ToolbarButton>
                        ),
                    },
                    {
                        position: 0,
                        element: (
                            <ToolbarButton
                                icon={'description'}
                                color={'success'}
                                variant={'contained'}
                                title={t('page.expedient.action.excelInteressats.title')}
                                onClick={() => excelInteressats(entity?.id)}
                                // disabled={selectedRows?.length==0}
                                hidden={!entity?.potModificar || !num}
                            />
                        ),
                    },
                    {
                        position: 3,
                        element: (
                            <MenuActionButton
                                id={'createInteressats'}
                                hidden={!user?.sessionScope?.isMostrarImportacio || !entity?.potModificar}
                                buttonLabel={t('page.interessat.action.new.label')}
                                buttonProps={{
                                    startIcon: <Icon>add</Icon>,
                                    variant: 'outlined',
                                    sx: { borderRadius: '4px', minWidth: '20px', minHeight: '32px', py: 0 },
                                }}
                                actions={createActions}
                            />
                        ),
                    },
                ]}
                onRowClick={(params: any) => {
                    handleOpen(params?.row?.id);
                }}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.interessat.action.new.ok',
                    updateSuccess: 'page.interessat.action.update.ok',
                }}
            />
            {dialog}
            {contentImport}
            {dialogGrup}
            {components}
        </>
    );
}

export default InteressatsGrid;