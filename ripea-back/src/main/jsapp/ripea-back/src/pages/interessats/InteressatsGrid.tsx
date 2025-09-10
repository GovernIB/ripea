import React, {useState} from "react";
import {Icon} from "@mui/material";
import { useMuiDataGridApiRef } from 'reactlib';
import {useTranslation} from "react-i18next";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import useInteressatActions, {useActions} from "./details/InteressatActions.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import useImport from "./actions/Import.tsx";
import {useActions as useExpedientActions} from "../expedient/details/CommonActions.tsx"
import {InteressatsGridForm} from "./InteressatsGridForm.tsx";

const perspectives = ['REPRESENTANT']
const sortModel:any = [{field: 'id', sort: 'asc'}]

interface DetailGridProps {
    entity: any,
    num: number,
    onRowCountChange?: (number: number) => void,
}

const InteressatsGrid: React.FC<DetailGridProps> = (props: DetailGridProps) => {
    const {entity, num, onRowCountChange} = props
    const { t } = useTranslation();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    const columns = [
        {
            field: 'tipus',
            flex: 0.5,
        },
        {
            field: 'documentNum',
            flex: 0.4,
        },
        {
            field: 'nomComplet',// organNom
            flex: 1.0,
            valueFormatter: (value: any, row:any) => value ?? row?.organNom,
            renderCell: (params:any) => <>
                {params?.formattedValue}
                {!params?.row?.arxiuPropagat &&
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
    ];

    const apiRef = useMuiDataGridApiRef()

    const refresh = ()=> {
        apiRef?.current?.refresh()
    }

    const {actions, components} = useInteressatActions(entity, refresh)
    const {exportar} = useActions(refresh);
    const {excelInteressats} = useExpedientActions(refresh);
    const {handleShow: handleImport, content: contentImport} = useImport(entity, refresh);

    return <>
        <StyledMuiGrid
            resourceName="interessatResource"
            popupEditFormDialogResourceTitle={t('page.interessat.title')}
            columns={columns}
            // paginationActive
            apiRef={apiRef}
            filter={builder.and(
                builder.eq('expedient.id', entity?.id),
                builder.eq('esRepresentant', false)
            )}
            sortModel={sortModel}
            perspectives={perspectives}
            disableColumnSorting
            popupEditCreateActive
            popupEditFormContent={<InteressatsGridForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
                esRepresentant: false,
            }}
            rowAdditionalActions={actions}
            onRowCountChange={onRowCountChange}
            toolbarCreateTitle={t('page.interessat.action.new.label')}
            toolbarHideCreate={!entity?.potModificar}
            toolbarHideQuickFilter={false}
            toolbarHideRefresh
            selectionActive
            onRowSelectionModelChange={(newSelection) => {
                // console.log('Selection changed:', newSelection);
                setSelectedRows([...newSelection]);
            }}
            toolbarElementsWithPositions={[
                {
                    position: 0,
                    element: <ToolbarButton icon={'upload'}
                                            onClick={()=>exportar(selectedRows, entity)}
                                            disabled={selectedRows?.length==0}
                    >{t('page.interessat.action.exportar.label')}</ToolbarButton>
                },
                {
                    position: 0,
                    element: <ToolbarButton icon={'download'}
                                            onClick={()=>handleImport()}
                                            hidden={!entity?.potModificar}
                    >{t('page.interessat.action.importar.label')}</ToolbarButton>
                },
                {
                    position: 0,
                    element: <ToolbarButton icon={'description'}
                                            color={'success'}
                                            variant={'contained'}
                                            title={t('page.expedient.action.excelInteressats.title')}
                                            onClick={()=>excelInteressats(entity?.id)}
                                            // disabled={selectedRows?.length==0}
                                            hidden={!entity?.potModificar || !num}
                    />,
                },
            ]}
            popupEditFormI18nKeys={{
                createSuccess: 'page.interessat.action.new.ok',
                updateSuccess: 'page.interessat.action.update.ok',
            }}
        />

        {contentImport}
        {components}
    </>
}

export default InteressatsGrid;