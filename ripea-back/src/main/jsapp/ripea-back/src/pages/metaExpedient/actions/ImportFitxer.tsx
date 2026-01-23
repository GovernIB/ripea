import {FormField, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Alert, Grid} from "@mui/material";
import GridFormField, {FileFormField} from "../../../components/GridFormField.tsx";
import {useMemo, useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {CardData} from "../../../components/CardData.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const Item = ({ identifiator = 'codi', fieldList, element, children, label }: any) => {
    const {data, fields, apiRef} = useFormContext()
    const fieldImportar = useMemo(() => (fields?.filter(i=>i.name=='importar')[0]), [fields]);

    const suffix = element?.[identifiator] ? `#${element?.[identifiator]}` : ''

    return (
        <CardData
            cardProps={element?.importar?{}:{backgroundColor: 'rgba(231,229,229,0.6)'}}
            headerProps={{ py: 0 }}
            header={
                <Grid
                    container
                    sx={{ display: "flex", flexDirection: "row", alignItems: 'center' }}
                    columnSpacing={1}
                    rowSpacing={1}
                >
                    <Grid xs={11.5} sx={{ pl: 1, pt: 1 }}>
                        {label ?? element?.nom}
                    </Grid>

                    <Grid xs={0.5}>
                        <FormField
                            componentProps={{ title: fieldImportar?.label }}
                            label=""
                            name={`importar${suffix}`}
                            value={element?.importar}
                            field={fieldImportar}
                            onChange={(value) => {
                                apiRef?.current?.setFieldValue(
                                    fieldList,
                                    editarElemento(
                                        data?.[fieldList],
                                        element?.[identifiator],
                                        { importar: value },
                                        identifiator
                                    )
                                )
                            }}
                        />
                    </Grid>
                </Grid>
            }
            disabled
            readOnly
        >
            {children}
        </CardData>
    )
}

const FieldResponsable = ({ fieldList, element, field, ...other }: any) => {
    const {t} = useTranslation()
    const {data, fields, apiRef} = useFormContext()
    const fieldResponsables = useMemo(() => (fields?.filter(i=>i.name=="portafirmesResponsables")[0]), [fields]);
    return <GridFormField
        xs={12}
        name={field + (element.codi ?`#${element.codi}`:'')}
        value={element?.[field]}
        field={fieldResponsables}
        onChange={(value) => {
            apiRef?.current?.setFieldValue(fieldList,
                editarElemento(data?.[fieldList], element.codi, {[field]: value})
            )
        }}
        disabled={!element?.importar}
        componentProps={{
            helperText: t('page.metaExpedient.detall.portafirmesResponsables'),
        }}
        {...other}
    />
}

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
    const {data} = useFormContext()

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        {data?.metaDocumentsImportats?.map((element:any) =>
            <Item fieldList={"metaDocumentsImportats"} element={element}>
                {element?.portafirmesFluxTipus == "SIMPLE" && !!element?.firmaPortafirmesActiva &&
                    <FieldResponsable
                        fieldList={"metaDocumentsImportats"}
                        field={"portafirmesResponsables"}
                        element={element}
                        filter={builder.neq('nif', null)}
                        namedQueries={[`ADD_PLUGIN_USERS`]}
                        multiple
                    />
                }
            </Item>
        )}
    </Grid>
}
const ImportFitxerFormMetaDades = () => {
    const {data} = useFormContext()

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        {data?.metaDadesImportats?.map((element:any) =>
            <Item fieldList={"metaDadesImportats"} element={element}/>
        )}
    </Grid>
}
const ImportFitxerFormEstat = () => {
    const {data} = useFormContext()

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        {data?.estatsImportats?.map((element:any) =>
            <Item fieldList={"estatsImportats"} element={element}>
                <FieldResponsable
                    fieldList={"estatsImportats"}
                    field={"responsable"}
                    element={element}
                />
            </Item>
        )}
    </Grid>
}
const ImportFitxerFormTasca = () => {
    const {data} = useFormContext()

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        {data?.tasquesImportats?.map((element:any) =>
            <Item fieldList={"tasquesImportats"} element={element}>
                <FieldResponsable
                    fieldList={"tasquesImportats"}
                    field={"responsable"}
                    element={element}
                />
            </Item>
        )}
    </Grid>
}
const ImportFitxerFormGrup = () => {
    const {data} = useFormContext()

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        {data?.grupsImportats?.map((element:any) =>
            <Item fieldList={"grupsImportats"} element={element} label={element?.descripcio}/>
        )}
    </Grid>
}
const ImportFitxerFormCarpeta = () => {
    const {data} = useFormContext()

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        {data?.carpetesImportats?.map((element:any) =>
            <Item fieldList={"carpetesImportats"} identifiator={'id'} element={element}/>
        )}
    </Grid>
}

function editarElemento(array, codi, cambios, identifiator = 'codi') {
    return array.map(item =>
        item?.[identifiator] === codi
            ? { ...item, ...cambios }
            : item
    );
}
const ImportFitxerForm = () => {
    const {t} = useTranslation()
    const {data, fieldErrors} = useFormContext()

    const dataError = useMemo(() => (
        fieldErrors?.some?.(e => ![
            "metaDocumentsImportats",
            "metaDadesImportats",
            "estatsImportats",
            "tasquesImportats",
            "grupsImportats",
            "carpetesImportats",
            "importar",
            "portafirmesResponsables",
        ].includes(e.field))
    ), [fieldErrors])

    const tabs = [
        {
            value: "data",
            label: t('page.metaExpedient.tabs.dades'),
            content: <ImportFitxerFormBase/>,
            error: dataError,
        },
        {
            value: "metaDocument",
            label: t('page.metaExpedient.tabs.metaDocument'),
            content: <ImportFitxerFormMetaDocument/>,
            badge: data?.metaDocumentsImportats?.length,
            hidden: data?.metaDocumentsImportats?.length == 0,
        },
        {
            value: "metaDada",
            label: t('page.metaExpedient.tabs.metaDada'),
            content: <ImportFitxerFormMetaDades/>,
            badge: data?.metaDadesImportats?.length,
            hidden: data?.metaDadesImportats?.length == 0,
        },
        {
            value: "estat",
            label: t('page.metaExpedient.tabs.expedientEstat'),
            content: <ImportFitxerFormEstat/>,
            badge: data?.estatsImportats?.length,
            hidden: data?.estatsImportats?.length == 0,
        },
        {
            value: "tasca",
            label: t('page.metaExpedient.tabs.tasca'),
            content: <ImportFitxerFormTasca/>,
            badge: data?.tasquesImportats?.length,
            hidden: data?.tasquesImportats?.length == 0,
        },
        {
            value: "grup",
            label: t('page.metaExpedient.tabs.grup'),
            content: <ImportFitxerFormGrup/>,
            badge: data?.grupsImportats?.length,
            hidden: data?.grupsImportats?.length == 0,
        },
        {
            value: "carpeta",
            label: t('page.metaExpedient.tabs.carpeta'),
            content: <ImportFitxerFormCarpeta/>,
            badge: data?.carpetesImportats?.length,
            hidden: data?.carpetesImportats?.length == 0,
        },
    ]

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <FileFormField xs={12} name={'importJson'} hidden={data?.importJson}/>
        {data?.importJson && <Grid xs={12}>
            <TabComponent tabs={tabs}/>
        </Grid>}
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