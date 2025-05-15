import React, {useState} from "react";
import {Grid} from "@mui/material";
import {
    GridPage,
    useMuiDataGridApiRef,
} from 'reactlib';
import {useTranslation} from "react-i18next";
import GridFormField from "../../components/GridFormField.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {useUserSession} from "../../components/Session.tsx";
import useInteressatActions, {useActions} from "./details/InteressatActions.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import useImport from "./actions/Import.tsx";

export const InteressatsGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>
        <GridFormField xs={12} name="documentTipus" required/>
        <GridFormField xs={12} name="documentNum"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={6} name="llinatge1"/>
        <GridFormField xs={6} name="llinatge2"/>
        <GridFormField xs={6} name="pais"/>
        <GridFormField xs={6} name="provincia"/>
        <GridFormField xs={6} name="municipi"/>
        <GridFormField xs={6} name="codiPostal"/>
        <GridFormField xs={12} name="adresa" type={"textarea"}/>
        <GridFormField xs={6} name="email"/>
        <GridFormField xs={6} name="telefon"/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
        <GridFormField xs={12} name="preferenciaIdioma" required/>
    </Grid>
}

const perspectives = ['REPRESENTANT']
const sortModel:any = [{field: 'id', sort: 'asc'}]

const columns = [
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'documentNum',
        flex: 0.5,
    },
    {
        field: 'nomComplet',//organNom
        flex: 1,
        valueFormatter: (value: any, row:any) => row?.organNom ?? value
    },
    {
        field: 'representant',
        flex: 0.75,
    },
];

interface DetailGridProps {
    entity: any,
    onRowCountChange?: (number: number) => void,
}

const InteressatsGrid: React.FC<DetailGridProps> = (props: DetailGridProps) => {
    const {entity, onRowCountChange} = props
    const { t } = useTranslation();
    const {value: user} = useUserSession();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    const apiRef = useMuiDataGridApiRef()

    const refresh = ()=> {
        apiRef?.current?.refresh()
    }

    const readOnly = entity?.agafatPer?.id != user?.codi
    const {actions, components} = useInteressatActions(readOnly, refresh)
    const {exportar} = useActions(refresh);
    const {handleShow: handleImport, content: contentImport} = useImport(entity, refresh);

    return <GridPage>
        <StyledMuiGrid
            resourceName="interessatResource"
            popupEditFormDialogResourceTitle={t('page.interessat.title')}
            columns={columns}
            paginationActive
            apiRef={apiRef}
            filter={builder.and(
                builder.eq('expedient.id', entity?.id),
                builder.eq('esRepresentant', false)
            )}
            staticSortModel={sortModel}
            perspectives={perspectives}
            disableColumnSorting
            popupEditCreateActive
            popupEditFormContent={<InteressatsGridForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
            }}
            rowAdditionalActions={actions}
            onRowCountChange={onRowCountChange}
            toolbarCreateTitle={t('page.interessat.actions.new')}
            readOnly={readOnly}
            rowHideDeleteButton

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
                    >{t('common.export')}...</ToolbarButton>
                },
                {
                    position: 0,
                    element: <ToolbarButton icon={'download'}
                                            onClick={()=>handleImport()}
                                            hidden={readOnly}
                    >{t('common.import')}...</ToolbarButton>
                },
            ]}
        />

        {contentImport}
        {components}
    </GridPage>
}

export default InteressatsGrid;