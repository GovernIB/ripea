import {FormField, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid, Box, Alert, Typography} from "@mui/material";
import {useEffect, useMemo, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {DataGridPro, GridToolbarContainer} from "@mui/x-data-grid-pro";
import {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";

const ImportarDocumentMassiveForm = (props:any) => {
    const { setDisabled } = props;
    const { data, fields, apiRef } = useFormContext();
    const { t } = useTranslation();

    useEffect(() => {
        if (data?.totsExpedientsMateixProcediment != null) {
            setDisabled(!data?.totsExpedientsMateixProcediment)
        }
    }, [data?.totsExpedientsMateixProcediment]);

    if (!data?.totsExpedientsMateixProcediment) {
        return <Alert severity={"warning"}>{t('page.expedient.action.impDocMass.warning')}</Alert>
    }

    const fieldFitxer = fields?.filter(i=>i.name=='file')[0];
    const fieldTipusDocument = fields?.filter(i=>i.name=='tipusDocument')[0];

    const updateDocument = (rowId: any, field: string, value: any) => {
        const newDocs = data.documents.map((doc:any) =>
            doc?.id === rowId ? { ...doc, [field]: value } : doc
        )
        apiRef?.current?.setFieldValue('documents', newDocs)
    }

    const columns = useMemo(() => [
        {
            field: 'file',
            headerName: fieldFitxer.label,
            flex: 1,
            renderCell: (params:any) => {
                const value = data?.documents.find((d:any) => d.id === params.row.id)?.fitxer
                return <Box mt={1.5} width={'100%'}><FormField
                    name={'file' + (value ? `#${params?.row?.id}` : '')}
                    field={fieldFitxer}
                    componentProps={{size: "small"}}
                    value={value}
                    onChange={(value) => updateDocument(params.row.id, 'fitxer', value)}
                    required
                /></Box>
            }
        },
        {
            field: 'tipusDocument',
            headerName: fieldTipusDocument.label,
            flex: 1,
            renderCell: (params:any) => {
                const value = data?.documents.find((d:any) => d.id === params.row.id)?.tipusDocument
                return <Box mt={1.5} width={'100%'}><FormField
                    name={'tipusDocument' + (value ? `#${params?.row?.id}` : '')}
                    field={fieldTipusDocument}
                    componentProps={{size: "small"}}
                    value={value}
                    onChange={(value) => updateDocument(params.row.id, 'tipusDocument', value)}
                    requestParams={{
                        metaExpedientId: data?.metaExpedientId,
                        value: value,
                        tipusDocument: data.documents.map((doc:any) => doc?.tipusDocument),
                    }}
                    required
                /></Box>
            }
        },
        {
            field: 'delete',
            headerName: "",
            flex: 0.25,
            renderCell: (params:any) => <ToolbarButton
                icon={'remove'}
                onClick={() => {
                    const newDocs = data?.documents?.filter((doc:any) => doc.id !== params.row.id) || []
                    apiRef?.current?.setFieldValue('documents', newDocs)
                }}
            />,
        }
    ], [apiRef, data.documents, data?.metaExpedientId, fieldFitxer, fieldTipusDocument, updateDocument]);

    const CustomToolbar = () => {
        return <GridToolbarContainer sx={{ height: '52px', display: 'flex', justifyContent: 'space-between', mr: 1 }}>
            <Typography ml={1}>{t('page.expedient.action.impDocMass.mssg', {num: data?.ids?.length})}</Typography>
            <ToolbarButton
                title={t('common.create')}
                icon={'add'}
                onClick={() => {
                    const newDocs = [...(data?.documents || []), { id: crypto.randomUUID() }]
                    apiRef?.current?.setFieldValue('documents', newDocs)
                }}
            />
        </GridToolbarContainer>
    }

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid xs={12}>
            <DataGridPro
                rows={data?.documents}
                columns={columns}

                getRowHeight={() => 75}
                style={{
                    height: 162 + 75 * (data?.documents.length>0 ?data?.documents.length :1),
                }}
                disableColumnMenu
                disableColumnSorting
                disableRowSelectionOnClick
                slots={{ // añadimos nuestra Toolbar
                    toolbar: CustomToolbar,
                }}
            />
        </Grid>
    </Grid>
}

export const ImportarDocumentMassive = (props:any) => {
    const { t } = useTranslation();
    const [disabled, setDisabled] = useState<boolean>(true)

    return <FormActionDialog
        resourceName={"expedientResource"}
        action={"IMPORT_DOCS_MASS"}
        title={t('page.expedient.action.impDocMass.title')}
        formDialogButtons={[
            !disabled && {icon: 'check', text: t('common.import'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        initialOnChange
        {...props}
    >
        <ImportarDocumentMassiveForm setDisabled={setDisabled}/>
    </FormActionDialog>
}

const useImportarDocumentMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[]) :void => {
        apiRef.current?.show?.(undefined, { ids, documents: [{ id: crypto.randomUUID() }] })
    }
    const onSuccess = () :void => {
        refresh?.()
        // temporalMessageShow(null, t('page.expedient.action.impDocMass.ok'), 'success');
        temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'info');
    }

    return {
        handleShow,
        content: <ImportarDocumentMassive apiRef={apiRef} onSuccess={onSuccess}/>
    }
}

export default useImportarDocumentMassive;