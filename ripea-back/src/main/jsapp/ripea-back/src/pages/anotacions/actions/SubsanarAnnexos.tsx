import {Alert} from "@mui/material";
import {FormField, useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const SubsanarAnnexosForm = () => {
    const {data, fields, apiRef} = useFormContext();
    const {t} = useTranslation();

    const fieldTipusDocument = fields?.filter(i => i.name == 'tipusDocument')[0];

    //Només els annexos de l'anotació que han quedat amb error (sense document creat o amb error registrat).
    const filter = builder.and(
        builder.eq('registre.id', data?.registre?.id),
        builder.or(
            builder.eq('document', null),
            builder.neq('error', null),
        ),
    );

    const columnsAnnexos = [
        {
            field: 'titol',
            flex: 0.5,
        },
        {
            field: 'nom',
            flex: 0.5,
        },
        {
            field: 'ntiTipoDocumental',
            headerName: '',
            sortable: false,
            flex: 0.5,
            renderCell: (params: any) => <FormField
                name={"annexos" + (data?.annexos[params.id] ? `#${params.id}` : '')}
                value={data?.annexos[params.id]}
                field={fieldTipusDocument}
                onChange={(value) => {
                    apiRef?.current?.setFieldValue('annexos', {
                        ...data?.annexos,
                        [params.id]: value,
                    })
                }}
                requestParams={{
                    expedientId: data?.expedient?.id,
                    annex: params.id,
                    annexos: data?.annexos,
                }}
                required
            />
        },
    ]

    return <>
        <Alert severity="info" sx={{mb: 1}}>{t('page.anotacio.action.subsanarAnnexos.info')}</Alert>
        <StyledMuiGrid
            resourceName={'registreAnnexResource'}
            persistentStateActive={false}
            filter={filter}
            perspectives={['DOCUMENT_METADOC']}
            columns={columnsAnnexos}
            onRowsChange={(rows: any) => {
                if (rows.length > 0 && rows.length != Object.keys(data?.annexos).length) {
                    //Per als annexos que ja tenen document, preseleccionam el seu tipus de document actual.
                    const annexos = Object.fromEntries(
                        rows.map((row: any) => [
                            row.id,
                            (data?.annexos[row.id] || (row.documentMetaDocumentId != null ? String(row.documentMetaDocumentId) : '')),
                        ])
                    );
                    apiRef?.current?.setFieldValue('annexos', annexos)
                }
            }}
            toolbarHide
            paginationActive={false}
            readOnly
        />
    </>
}

const SubsanarAnnexos = (props: any) => {
    const {t} = useTranslation();

    return <FormActionDialog
        resourceName={"expedientPeticioResource"}
        action={"SUBSANAR_ANNEXOS"}
        title={t('page.anotacio.action.subsanarAnnexos.title')}
        formDialogButtons={[
            {icon: 'save', text: t('common.save'), componentProps: {variant: 'contained'}, value: true},
            {text: t('common.cancel'), componentProps: {variant: 'outlined'}, value: false},
        ]}
        {...props}
    >
        <SubsanarAnnexosForm/>
    </FormActionDialog>
}

const useSubsanarAnnexos = (refresh?: () => void) => {
    const {t} = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id: any, row: any): void => {
        apiRef.current?.show?.(id, {
            registre: row?.registre,
            expedient: row?.expedient,
        })
    }
    const onSuccess = (): void => {
        refresh?.();
        temporalMessageShow(null, t('page.anotacio.action.subsanarAnnexos.ok'), 'success');
    }

    return {
        handleShow,
        content: <SubsanarAnnexos apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useSubsanarAnnexos;
