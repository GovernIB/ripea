import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Alert, Grid} from "@mui/material";
import GridFormField, {FileFormField} from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {CardData} from "../../../components/CardData.tsx";
import TabComponent from "../../../components/TabComponent.tsx";

const ImportFitxerFormBase = () => {
    const {t} = useTranslation()
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name={'procediment'}/>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={2} name="tipusClassificacio" required/>
        <GridFormField xs={10} name="classificacio" debounce disabled={data?.tipusClassificacio == 'ID'}/>
        <Grid item xs={12} hidden={data?.msgSiaRolsac == null}>
            <Alert severity={'warning'} sx={{ mt: 0.5 }}>{data.msgSiaRolsac}</Alert>
        </Grid>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="descripcio"/>
        <GridFormField xs={12} name="serieDocumental"/>
        <GridFormField xs={4} name="procedimentComu"/>
        <GridFormField xs={8} name="organGestor" required hidden={data?.procedimentComu}/>
        <GridFormField xs={12} name="expressioNumero"
                       componentProps={{ helperText: t('page.metaExpedient.detall.expressioNumero') }}/>
    </Grid>
}
const ImportFitxerFormMetaDocument = () => {
    const {t} = useTranslation()
    const {data, fields, apiRef} = useFormContext()

    const fieldResponsables = fields?.filter(i=>i.name=='portafirmesResponsables')[0];
    const fieldImportar = fields?.filter(i=>i.name=='importar')[0];

    return <CardData title={"Tipus de document"}>
        {data?.metaDocumentsImportats?.map((metaDocument:any) => <>
            <CardData>
                <GridFormField
                    xs={12}
                    name={"codi" + (metaDocument.codi ?`#${metaDocument.codi}`:'')}
                    value={metaDocument?.codi}
                    required
                    disabled
                />
                <GridFormField
                    xs={12}
                    name={"portafirmesResponsables" + (metaDocument.codi ?`#${metaDocument.codi}`:'')}
                    value={metaDocument?.portafirmesResponsables}
                    field={fieldResponsables}
                    onChange={(value) => {
                        apiRef?.current?.setFieldValue('metaDocumentsImportats',
                            editarElemento(data?.metaDocumentsImportats, metaDocument.codi, {portafirmesResponsables: value})
                        )
                    }}
                    multiple
                />
                <GridFormField
                    xs={12}
                    name={"importar" + (metaDocument.codi ?`#${metaDocument.codi}`:'')}
                    value={metaDocument?.importar}
                    field={fieldImportar}
                    onChange={(value) => {
                        apiRef?.current?.setFieldValue('metaDocumentsImportats',
                            editarElemento(data?.metaDocumentsImportats, metaDocument.codi, {importar: value})
                        )
                    }}
                />
            </CardData>
        </>)}
    </CardData>
}
const ImportFitxerFormMetaDades = () => {
    const {t} = useTranslation()
    const {data, fields, apiRef} = useFormContext()

    // const fieldResponsables = fields?.filter(i=>i.name=='portafirmesResponsables')[0];
    const fieldImportar = fields?.filter(i=>i.name=='importar')[0];

    return <CardData title={"Meta-dada"}>
        {data?.metaDadesImportats?.map((metaDada:any) => <>
            <CardData>
                <GridFormField
                    xs={12}
                    name={"codi" + (metaDada.codi ?`#${metaDada.codi}`:'')}
                    value={metaDada?.codi}
                    required
                    disabled
                />
                {/*<GridFormField*/}
                {/*    xs={12}*/}
                {/*    name={"portafirmesResponsables" + (metaDada.codi ?`#${metaDada.codi}`:'')}*/}
                {/*    value={metaDada?.portafirmesResponsables}*/}
                {/*    field={fieldResponsables}*/}
                {/*    onChange={(value) => {*/}
                {/*        apiRef?.current?.setFieldValue('metaDadesImportats',*/}
                {/*            editarElemento(data?.metaDadesImportats, metaDada.codi, {portafirmesResponsables: value})*/}
                {/*        )*/}
                {/*    }}*/}
                {/*    multiple*/}
                {/*/>*/}
                <GridFormField
                    xs={12}
                    name={"importar" + (metaDada.codi ?`#${metaDada.codi}`:'')}
                    value={metaDada?.importar}
                    field={fieldImportar}
                    onChange={(value) => {
                        apiRef?.current?.setFieldValue('metaDadesImportats',
                            editarElemento(data?.metaDadesImportats, metaDada.codi, {importar: value})
                        )
                    }}
                />
            </CardData>
        </>)}
    </CardData>
}

function editarElemento(array, codi, cambios) {
    return array.map(item =>
        item.codi === codi
            ? { ...item, ...cambios }
            : item
    );
}
const ImportFitxerForm = () => {
    const {t} = useTranslation()
    const {data} = useFormContext()

    const tabs = [
        {
            value: "data",
            label: "data",
            content: <ImportFitxerFormBase/>,
        },
        {
            value: "metaDocument",
            label: "metaDocument",
            content: <ImportFitxerFormMetaDocument/>,
            badge: data?.metaDocumentsImportats?.length,
            hidden: data?.metaDocumentsImportats?.length == 0,
        },
        {
            value: "metaDades",
            label: "metaDades",
            content: <ImportFitxerFormMetaDades/>,
            badge: data?.metaDadesImportats?.length,
            hidden: data?.metaDadesImportats?.length == 0,
        },
    ]

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <FileFormField xs={12} name={'importJson'} hidden={data?.importJson}/>
        {data?.importJson && <>
            <TabComponent tabs={tabs}/>
        </>}
    </Grid>
}

const ImportFitxer = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"metaExpedientResource"}
        title={t('page.metaExpedient.action.importFitxer.title')}
        action={"IMPORT_FITXER"}
        // formDialogButtons={[
        //     {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
        //     {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        // ]}
        {...props}
    >
        <ImportFitxerForm/>
    </FormActionDialog>
}

export const useImportFitxer = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () :void => {
        apiRef.current?.show?.()
    }
    const onSuccess = (response:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.metaExpedient.action.importFitxer.ok', {data: response}), 'success');
    }

    return {
        handleShow,
        content: <ImportFitxer apiRef={apiRef} onSuccess={onSuccess}/>
    }
}