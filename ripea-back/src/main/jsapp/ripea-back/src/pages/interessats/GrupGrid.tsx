import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import {BasePage, MuiDialog, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {useEffect, useMemo, useState} from "react";
import GridFormField from "../../components/GridFormField.tsx";
import {Typography, Grid2 as Grid} from "@mui/material";

const sortModelInteressat: any = [{ field: 'nomComplet', sort: 'asc' }];
const columnsInteressat = [
    { field: 'nomComplet', flex: 0.5 },
    { field: 'documentNum', flex: 1 },
];

const CrearGrupForm = () => {
    const { data, fields, apiRef } = useFormContext();

    // Filtro de grupos del expediente
    const filter = useMemo(() => builder.and(
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq('esRepresentant', false),
    ), [data?.expedient?.id]);

    const selectionModel = useMemo(() => {
        return data?.interessats?.map((a: any) => a.id)
    }, [])

    const [selectedRows, setSelectedRows] = useState<any[]>(selectionModel || []);

    useEffect(() => {
        apiRef?.current?.setFieldValue("interessats", selectedRows?.map(id => ({ id })))
    }, [selectedRows]);

    return <>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <GridFormField name="nom" required/>
            <GridFormField name="descripcio" />
        </Grid>

        <Typography variant="subtitle2">
            {fields?.find?.(item => item?.name === 'interessats')?.label || ''}
        </Typography>

        <StyledMuiGrid
            resourceName="interessatResource"
            columns={columnsInteressat}
            filter={filter}
            sortModel={sortModelInteressat}
            toolbarHide
            autoHeight
            selectionActive
            rowSelectionModel={selectionModel}
            onRowSelectionModelChange={(newSelection) => {
                setSelectedRows([...newSelection]);
            }}
            readOnly
        />
    </>
}

const perspectives = ['INTERESSATS']
const sortModel:any = [{field: 'nom', sort: 'asc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'descripcio',
        flex: 1,
    },
]

export const GrupGrid = (props:any) => {
    const { entity } = props
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef()

    const actions = [
        {
            label: t('common.update'),
            icon: 'edit',
            showInMenu: true,
            onClick: (id:any, row:any) => apiRef?.current?.showUpdateDialog(id, row, { interessats: row?.interessats }),
            hidden: !entity?.potModificar,
        },
        {
            label: t('common.delete'),
            icon: 'delete',
            showInMenu: true,
            clickTriggerDelete: true,
            hidden: !entity?.potModificar,
        },
    ];

    return <BasePage>
        <StyledMuiGrid
            apiRef={apiRef}
            resourceName="interessatGrupResource"
            popupEditFormDialogResourceTitle={t('page.interessat.grup.title')}
            columns={columns}
            autoHeight
            paginationModel={{page: 0, pageSize: 5}}
            filter={builder.eq('expedient.id', entity?.id)}
            staticSortModel={sortModel}
            perspectives={perspectives}
            popupEditCreateActive
            popupEditFormContent={<CrearGrupForm/>}
            formAdditionalData={{
                expedient: {id: entity?.id},
            }}
            toolbarShowCreate={entity?.potModificar}
            rowAdditionalActions={actions}
            toolbarHideRefresh

            popupEditFormI18nKeys={{
                createSuccess: 'page.interessat.grup.action.new.ok',
                updateSuccess: 'page.interessat.grup.action.update.ok',
                deleteSuccess: 'page.interessat.grup.action.delete.ok',
            }}
        />
    </BasePage>
}
export const useGrupGridDialog = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);

    const handleOpen = () => {
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
            refresh?.()
        }
    };

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.interessat.grup.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={() :void => {
                handleClose();
            }}
        >
            <GrupGrid entity={entity}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}