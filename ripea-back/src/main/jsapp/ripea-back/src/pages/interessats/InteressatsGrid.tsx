import React, {useState} from "react";
import {Grid, Icon} from "@mui/material";
import {
    GridPage, useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import {useTranslation} from "react-i18next";
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import useInteressatActions, {useActions} from "./details/InteressatActions.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import useImport from "./actions/Import.tsx";
import {useActions as useExpedientActions} from "../expedient/details/CommonActions.tsx"
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import {useSession} from "../../components/SessionStorageContext.tsx";

const InteressatsGridFormFilter = () => {
    const {data} = useFormContext()

    return <>
        <GridFormField xs={6} name="nivell"/>
        <GridFormField xs={6} name="comunitatAutonoma"/>
        <GridFormField xs={6} name="provincia" requestParams={{comunitatAutonoma: data?.comunitatAutonoma}}/>
        <GridFormField xs={6} name="municipi" requestParams={{provincia: data?.provincia}}/>
        <GridFormField xs={6} name="nif"/>
        <GridFormField xs={6} name="nom"/>
        <GridFormField xs={6} name="unitatArrel" type={"checkbox"}/>
    </>
}

export const InteressatsGridForm = () => {
    const {data} = useFormContext()
    const { value } = useSession("UNITAT_ORGANITZATIVA_FILTER");

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>

        {!!data?.filter &&
            <Grid item xs={12}>
                <StyledMuiFilter
                    resourceName={"interessatResource"}
                    code={"UNITAT_ORGANITZATIVA_FILTER"}
                    springFilterBuilder={()=>{}}
                    onSpringFilterChange={()=>{}}
                >
                    <InteressatsGridFormFilter/>
                </StyledMuiFilter>
            </Grid>
        }

        {data?.tipus === 'InteressatAdministracioEntity' && <GridFormField xs={11} name="organCodi"
                       requestParams={{...(value ?? []), isInteressatAdministracio: data?.tipus == 'InteressatAdministracioEntity'}}
                       required/>}
        <GridButtonField xs={1} name={"filter"} icon={"search"} hidden={data?.tipus != 'InteressatAdministracioEntity'}/>

        <GridFormField xs={12} name="documentTipus"
                       disabled={data?.tipus != 'InteressatPersonaFisicaEntity'}
                       readOnly={data?.tipus != 'InteressatPersonaFisicaEntity'}
                       required/>
        <GridFormField xs={12} name="documentNum"
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}
                       required={data?.tipus != 'InteressatAdministracioEntity'}/>

        {data?.tipus == 'InteressatPersonaFisicaEntity' && <>
            <GridFormField xs={12} name="nom"/>
            <GridFormField xs={6} name="llinatge1"/>
            <GridFormField xs={6} name="llinatge2"/>
        </>}

        <GridFormField xs={6} name="pais"
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>
        <GridFormField xs={6} name="provincia" requestParams={{pais: data?.pais}}
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>
        <GridFormField xs={6} name="municipi" requestParams={{provincia: data?.provincia}}
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>
        <GridFormField xs={6} name="codiPostal"
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>
        <GridFormField xs={12} name="adresa" type={"textarea"}
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>

        <GridFormField xs={6} name="email" required={data?.entregaDeh}/>
        <GridFormField xs={6} name="telefon"/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
        <GridFormField xs={12} name="preferenciaIdioma" required/>

        <GridFormField xs={6} name="entregaDeh"/>
        <GridFormField xs={6} name="entregaDehObligat" hidden={!data?.entregaDeh}/>
    </Grid>
}

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
            flex: 0.5,
        },
        {
            field: 'nomComplet',// organNom
            flex: 1,
            valueFormatter: (value: any, row:any) => value ?? row?.organNom,
            renderCell: (params:any) => <>
                {params?.formattedValue}
                {!params?.row?.arxiuPropagat &&
                    <Icon title={t('page.contingut.alert.guardarPendent')} color={'error'}>warning</Icon>}
            </>
        },
        {
            field: 'representant',
            flex: 0.75,
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

    return <GridPage>
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
            toolbarCreateTitle={t('page.interessat.action.new.label')}
            toolbarHideCreate={!entity?.potModificar}

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
        />

        {contentImport}
        {components}
    </GridPage>
}

export default InteressatsGrid;