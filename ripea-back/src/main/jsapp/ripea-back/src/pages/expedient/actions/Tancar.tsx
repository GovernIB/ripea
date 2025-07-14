import {useEffect, useMemo, useRef, useState} from "react";
import {Grid, Alert} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";

const columns = [
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'tipus',
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
    const {apiRef: formApiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [rowsCount, setRowsCount] = useState<any>();
    const [entity, setEntity] = useState<any>();

    const filter = builder.and(
        builder.eq('expedient.id', entity?.id),
        builder.eq('estat', "'REDACCIO'"),
        builder.eq('arxiuUuid', null),
    )

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('expedientResource');

    useEffect(() => {
        const id = formApiRef?.current?.getId?.()
        if (apiIsReady && id) {
            apiGetOne(id, {
                perspectives: [
                    'DOCUMENTS_OBLIGATORIS_TANCAR',// documentObligatorisAlTancar
                    'NOTIFICACIONS_CADUCADES',// conteNotificacionsCaducades
                    'DOCUMENTS_NO_MOGUTS',// conteDocumentsDeAnotacionesNoMogutsASerieFinal
                ]
            })
                .then((app) => setEntity(app))
                .catch(() => setEntity(undefined))
        }
    }, [apiIsReady]);

    useEffect(() => {
        formApiRef?.current?.setFieldValue('documentsPerFirmar', selectedRows);
    }, [selectedRows]);

    const selectedModel: any[] = useMemo(() => {
        return entity?.documentObligatorisAlTancar?.map?.((row: any) => row?.id) ?? []
    }, [entity])

    return <Load value={entity}>
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
                        isRowSelectable={(params) => !selectedModel.includes(params.row?.id)}
                        onRowCountChange={setRowsCount}
                        onRowSelectionModelChange={(newSelection) => {
                            setSelectedRows([...newSelection]);
                        }}
                        height={162 + 52 * 4}
                        readOnly
                    /></Load>
            </Grid>

            <Grid item xs={12} hidden={!entity?.conteNotificacionsCaducades}>
                <Alert severity={"warning"}>{t('page.expedient.alert.notificacio')}</Alert>
            </Grid>
            <Grid item xs={12} hidden={!entity?.conteDocumentsDeAnotacionesNoMogutsASerieFinal}>
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
        {...props}
    >
        <TancarForm/>
    </FormActionDialog>
}
const useTancar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id: any): void => {
        apiRef.current?.show?.(id)
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
export default useTancar;