import {useEffect, useRef, useState} from "react";
import {Grid} from "@mui/material";
import {FormField, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import useVisualitzar from "./Visualitzar.tsx";

const AcceptarTabExpedient = () => {
    const {data} =useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="accio" required/>
        <GridFormField xs={12} name="metaExpedient" required/>

        {data?.accio == "CREAR" &&
            <>
                <GridFormField xs={12} name="newExpedientTitol" required/>
                <GridFormField xs={12} name="prioritat" required/>
                <GridFormField xs={12} name="prioritatMotiu" type={"textarea"} hidden={data?.prioritat == "B_NORMAL"}/>
                <GridFormField xs={12} name="organGestor" required disabled readOnly/>
                <GridFormField xs={6} name="sequencia" required disabled readOnly/>
                <GridFormField xs={6} name="any" required/>
            </>
        }
        {data?.accio == "INCORPORAR" &&
            <>
                <GridFormField xs={12} name="expedient" required/>
                <GridFormField xs={12} name="agafarExpedient"/>
            </>
        }

        <GridFormField xs={12} name="associarInteressats"/>
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

    const {handleOpen, dialog} = useVisualitzar()

    const actions = [
        {
            title: t('page.document.action.view.label'),
            icon: "search",
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => row?.fitxerExtension != 'pdf',
        },
    ]

    return <>
        <StyledMuiGrid
            resourceName={'registreAnnexResource'}
            filter={filter}
            columns={columnsAnnexos}
            rowAdditionalActions={actions}
            onRowsChange={(rows) => {
                if (rows.length > 0 && rows.length != Object.keys(data?.annexos).length) {
                    const annexos = Object.fromEntries(
                        rows.map((row) => [row.id, (data?.annexos[row.id] || '')])
                    );

                    apiRef?.current?.setFieldValue('annexos', annexos)
                }
            }}

            height={162 + 52 * 4}
            readOnly
        />
        {dialog}
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
    const {data, apiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>(data?.interessats || []);

    useEffect(() => {
        apiRef?.current?.setFieldValue("interessats", selectedRows)
    }, [selectedRows]);

    const filter = builder.eq("registre.id", data?.registre?.id)

    return <StyledMuiGrid
        resourceName={"registreInteressatResource"}
        filter={filter}
        columns={columnsInteressats}
        selectionActive
        rowSelectionModel={selectedRows}
        onRowSelectionModelChange={(newSelection) => {
            setSelectedRows([...newSelection]);
        }}

        height={162 + 52 * 4}
        readOnly
    />
}

const AcceptarForm = () => {
    const {data} =useFormContext();
    const { t } = useTranslation();

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
        {...props}
    >
        <AcceptarForm/>
    </FormActionDialog>
}

const useAcceptar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {
            metaExpedient: row?.metaExpedient,
            registre: row?.registre,
            interessats: row?.registreInfo?.interessats?.map((i:any)=>i.id) || [],
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