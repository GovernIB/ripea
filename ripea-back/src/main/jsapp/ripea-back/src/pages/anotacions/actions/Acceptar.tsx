import {useEffect, useMemo, useState} from "react";
import {Grid} from "@mui/material";
import {FormField, useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import useVisualitzar from "./Visualitzar.tsx";
import useRegistreInteressatDetail from "../details/RegistreInteressatDetail.tsx";
import {useUserSession} from "@src/components/Session.tsx";
import {useIframeDialog} from "@src/components/Iframe.tsx";

const AcceptarTabExpedient = () => {

    const {data} =useFormContext();

    const filterMetaExpedientAnotacioCrear = builder.and(
        builder.eq('actiu', true),
        builder.eq('revisioEstat', "'REVISAT'"),
    );
    
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="accio" required/>
        <GridFormField name="metaExpedient" required
            filter={filterMetaExpedientAnotacioCrear}
            namedQueries={['EXPEDIENT_CREATE']}/>

        {data?.accio == "CREAR" &&
            <>
                <GridFormField name="newExpedientTitol" required/>
                <GridFormField name="prioritat" required/>
                <GridFormField name="prioritatMotiu" type={"textarea"} hidden={data?.prioritat == "B_NORMAL"} required/>
                <GridFormField name="organGestor"
                            namedQueries={[`EXPEDIENT_FORM#${data?.metaExpedient?.id ?? 0}`]}
                            disabled={!data?.metaExpedient || data?.disableOrganGestor}
                            readOnly={!data?.metaExpedient || data?.disableOrganGestor}
                            required/>
                <GridFormField size={6} name="sequencia" required disabled readOnly/>
                <GridFormField size={6} name="any" required/>
                <GridFormField name="grup"
                               namedQueries={[`BY_PROCEDIMENT#${data?.metaExpedient?.id ?? 0}`]}
                               hidden={!data?.grup && !data?.gestioAmbGrupsActiva} required/>
				<GridFormField name="seguidor"/>
            </>
        }
        {data?.accio == "INCORPORAR" &&
            <>
                <GridFormField name="expedient"
                        filter={builder.and(
                           builder.eq('metaExpedient.id', data?.metaExpedient?.id),
						   builder.eq('esborrat', 0),
                       )}                
                required/>
                <GridFormField name="agafarExpedient"/>
            </>
        }

        <GridFormField name="associarInteressats"/>
    </Grid>
}

const AcceptarTabAnnexos = () => {
    const {data, fields, apiRef} = useFormContext();
    const  { t } = useTranslation()

    const fieldTipusDocument = fields?.filter(i=>i.name=='tipusDocument')[0];

    const filter = builder.eq("registre.id", data?.registre?.id)
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
            renderCell: (params:any) => <FormField
                name={"annexos" + (data?.annexos[params.id] ?`#${params.id}`:'')}
                value={data?.annexos[params.id]}
                field={fieldTipusDocument}
                onChange={(value)=>{
                    apiRef?.current?.setFieldValue('annexos', {
                        ...data?.annexos,
                        [params.id]: value,
                    })
                }}
                componentProps={{ size: "small" }}
                requestParams={{
                    metaExpedientId: data?.metaExpedient?.id,
                    annex: params.id,
                    annexos: data?.annexos,
                }}
                required
            />
        },
    ]

    const {handleOpen, dialog, isValid} = useVisualitzar()
    const {handleOpen: handleIframeOpen, dialog: dialogIframe} = useIframeDialog();

    const actions = [
        {
            label: t('page.document.action.view.label'),
            icon: "search",
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => !isValid(row) || row?.justificant,
        },
        {
            label: t('page.document.action.view.label'),
            icon: "search",
            showInMenu: false,
            onClick: (_id:any, row:any) => handleIframeOpen(`expedientPeticio/descarregarJustificant/${row?.registreId}`),
            hidden: (row:any) => !(['pdf', 'odt', 'docx'].includes(row?.fitxerExtension) && row?.justificant),
        }
    ]

    return <>
        <StyledMuiGrid
            resourceName={'registreAnnexResource'}
            persistentStateActive={false}
            filter={filter}
            columns={columnsAnnexos}
            rowAdditionalActions={actions}
            rowsTransformer={(_rows: any) => {
                if (!_rows || _rows.length == 0) return [];
                const additionalRows: any[] = _rows;
                if (data?.isIncorporacioJustificantActiva && data?.justificant && !additionalRows.map((b) => b.id).includes(0)) {
                    additionalRows.push({
                        ...data?.justificant,
                        id: 0,
                        justificant: true,
                    })
                }
                return additionalRows;
            }}
            onRowsChange={(rows:any) => {
                if (rows.length > 0 && rows.length != Object.keys(data?.annexos).length) {
                    const annexos = Object.fromEntries(
                        rows.map((row:any) => [row.id, (data?.annexos[row.id] || '')])
                    );

                    apiRef?.current?.setFieldValue('annexos', annexos)
                }
            }}
            onRowClick={(params: any) => {
                if (isValid(params?.row)) {
                    handleOpen(params?.id)
                }
            }}
            toolbarHide
            paginationActive={false}
            readOnly
        />
        {dialog}
        {dialogIframe}
    </>
}

const columnsInteressats = [
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'documentNumero',
        flex: 0.5,
    },
    {
        field: 'nomComplet',
        flex: 0.75,
        valueFormatter: (value:any, row:any) => value || row?.raoSocial,
    },
    {
        field: 'representant',
        flex: 1,
    },
]

const AcceptarTabInteressats = () => {
    const { t } = useTranslation()
    const {data, apiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>(data?.interessats || []);

    useEffect(() => {
        apiRef?.current?.setFieldValue("interessats", selectedRows)
    }, [selectedRows]);

    const filter = builder.eq("registre.id", data?.registre?.id)

    const {handleOpen, dialog} = useRegistreInteressatDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => row?.tipus == "ADMINISTRACIO"
        },
    ]

    return <>
        <StyledMuiGrid
            resourceName={"registreInteressatResource"}
            persistentStateActive={false}
            filter={filter}
            columns={columnsInteressats}
            rowAdditionalActions={actions}
            selectionActive
            rowSelectionModel={selectedRows}
            onRowSelectionModelChange={(newSelection) => {
                setSelectedRows([...newSelection]);
            }}
            onRowClick={(params: any) => {
                if (params?.row?.tipus != "ADMINISTRACIO") {
                    handleOpen(params?.id, params?.row)
                }
            }}

            autoHeight
            paginationModel={{page: 0, pageSize: 5}}
            readOnly
        />
        {dialog}
    </>
}

const AcceptarForm = () => {
    const {data, fieldErrors} =useFormContext();
    const { t } = useTranslation();

    const annexosError:boolean|undefined = useMemo(() =>
            fieldErrors?.some?.(e => e.field === "annexos")
    , [fieldErrors])

    const tabs = [
        {
            value: 'expedient',
            label: t('page.expedient.title'),
            content: <AcceptarTabExpedient/>,
        },
        {
            value: 'annexos',
            label: t('page.anotacio.tabs.annexos'),
            content: <AcceptarTabAnnexos/>,
            error: annexosError,
        },
        {
            value: 'interessats',
            label: t('page.anotacio.tabs.interessats'),
            content: <AcceptarTabInteressats/>,
            hidden: !data?.associarInteressats
        },
    ]

    return <TabComponent tabs={tabs}/>
}

const Acceptar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientPeticioResource"}
        action={"ACCEPTAR_ANOTACIO"}
        title={t('page.anotacio.action.acceptar.title')}
        initialOnChange
        formDialogButtons={[
            {icon: 'check_circle', text: t('page.anotacio.action.acceptar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <AcceptarForm/>
    </FormActionDialog>
}

const useAcceptar = (refresh?: () => void) => {
    const {value: user} = useUserSession();
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        const isIncorporacioJustificantActiva = user?.sessionScope?.isIncorporacioJustificantActiva
        apiRef.current?.show?.(id, {
            metaExpedient: row?.metaExpedient,
            registre: row?.registre,
            interessats: row?.registreInfo?.interessats?.map((i:any)=>i.id) || [],
            grup: row?.grup,
            isIncorporacioJustificantActiva: isIncorporacioJustificantActiva,
            justificant: isIncorporacioJustificantActiva ? {
                registreId: row?.registreInfo?.id,
                ...row?.registreInfo?.justificant
            } :{}
        })
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, t('page.anotacio.action.acceptar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Acceptar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useAcceptar;