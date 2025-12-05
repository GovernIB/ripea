import {useEffect, useMemo, useRef, useState} from "react";
import {Grid, Alert, Box} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import ContingutIcon from "../../contingut/details/ContingutIcon.tsx";

const columns = [
    // {
    //     field: 'nom',
    //     flex: 0.5,
    // },
    {
        field: 'metaDocument',
        flex: 0.5,
    },
    {
        field: 'createdDate',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'createdBy',
        flex: 0.5,
    },
]
const TancarForm = () => {
    const { t } = useTranslation();
    const {data, apiRef: formApiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [rowsCount, setRowsCount] = useState<any>();
    const [entities, setEntities] = useState<any>();

    const filter = useMemo(() => builder.and(
        builder.inside('expedient.id', data?.ids),
        builder.or(
            builder.eq('estat', "'REDACCIO'"),
            builder.eq('arxiuUuid', null),
        ),
        builder.eq('esborrat', 0),
    ), [data?.ids])

    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService('expedientResource');

    useEffect(() => {
        if (apiIsReady && data?.ids) {
            apiFind({
                filter: builder.inside('id', data?.ids),
                unpaged: true,
                perspectives: [
                    'DOCUMENTS_OBLIGATORIS_TANCAR',// documentObligatorisAlTancar
                    'NOTIFICACIONS_CADUCADES',// conteNotificacionsCaducades
                    'DOCUMENTS_NO_MOGUTS',// conteDocumentsDeAnotacionesNoMogutsASerieFinal
                ]
            })
                .then((app) => setEntities(app?.rows))
                .catch(() => setEntities(undefined))
        }
    }, [apiIsReady]);

    useEffect(() => {
        formApiRef?.current?.setFieldValue('documentsPerFirmar', selectedRows);
    }, [selectedRows]);

    const temp = useMemo(()=>{
        return {
            documentObligatorisAlTancar: entities?.flatMap?.((e:any) => e?.documentObligatorisAlTancar),
            conteNotificacionsCaducades: entities?.some?.((e:any) => e?.conteNotificacionsCaducades),
            conteDocumentsDeAnotacionesNoMogutsASerieFinal: entities?.some?.((e:any) => e?.conteDocumentsDeAnotacionesNoMogutsASerieFinal),
        }
    },[entities])

    const selectedModel: any[] = useMemo(() => {
        const defaultSelection = temp?.documentObligatorisAlTancar?.map?.((row: any) => row?.id) ?? []
        formApiRef?.current?.setFieldValue('documentsPerFirmar', defaultSelection);
        return defaultSelection;
    }, [temp?.documentObligatorisAlTancar])

    return <Load value={entities && temp}>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <Grid item xs={12} hidden={!rowsCount}>
                <Alert severity={"info"}>{t('page.expedient.alert.borradors')}</Alert>
            </Grid>
            <Grid item xs={12}>
                <Load value={selectedModel} noEffect>
                    <StyledMuiGrid
                        resourceName={"documentResource"}
                        columns={columns}
                        filter={filter}
                        selectionActive
                        rowSelectionModel={selectedModel}
                        isRowSelectable={(params) => !selectedModel.includes(params.row?.id) && params.row?.tipus=="DOCUMENT"}
                        onRowCountChange={setRowsCount}
                        onRowSelectionModelChange={(newSelection) => {
                            setSelectedRows([...newSelection]);
                        }}
                        autoHeight
                        paginationActive={false}
                        readOnly

                        groupingColDef={{
                            headerName: t('page.contingut.grid.nom'),
                            flex: 1,
                            valueFormatter: (value: any, row: any) => {
                                if (row?.id) {
                                    return <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <ContingutIcon entity={row} />
                                    </Box>
                                }
                                return value;
                            },
                        }}
                        treeData
                        treeDataAdditionalRows={(_rows: any) => {
                            const additionalRows: any[] = [];
                            if (_rows!=null && entities!=null){
                                for (const entity of entities) {
                                    if (!additionalRows.map((b) => b.id).includes(entity?.id)) {
                                        additionalRows.push(entity)
                                    }
                                }
                            }
                            return additionalRows;
                        }}
                        getTreeDataPath={(row: any): string[] => {
                            return row?.expedient ?[`${row?.expedient?.id}`, `${row.id}`] :[`${row.id}`]
                        }}
                        isGroupExpandedByDefault={() => {
                            return true;
                        }}
                    />
                </Load>
            </Grid>

            <Grid item xs={12} hidden={!temp?.conteNotificacionsCaducades}>
                <Alert severity={"warning"}>{t('page.expedient.alert.notificacio')}</Alert>
            </Grid>
            <Grid item xs={12} hidden={!temp?.conteDocumentsDeAnotacionesNoMogutsASerieFinal}>
                <Alert severity={"warning"}>{t('page.expedient.alert.documents')}</Alert>
            </Grid>

            <GridFormField xs={12} name="motiu" type={"textarea"} required/>
        </Grid>
    </Load>
}

const Tancar = (props: any) => {
    const {t} = useTranslation();

    return <FormActionDialog
        resourceName={"expedientResource"}
        action={"TANCAR"}
        title={t('page.expedient.action.close.title')}
        formDialogButtons={[
            {icon: 'check', text: t('common.close'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <TancarForm/>
    </FormActionDialog>
}
export const useTancar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id: any): void => {
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
        })
    }
    const onSuccess = (result: any): void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.close.title', {expedient: result?.nom}), 'success');
    }

    return {
        handleShow,
        content: <Tancar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export const useTancarMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids: any[]): void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
        })
    }
    const onSuccess = (): void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'info');
    }

    return {
        handleShow,
        content: <Tancar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useTancar;