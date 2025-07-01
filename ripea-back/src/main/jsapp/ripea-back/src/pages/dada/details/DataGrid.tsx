import {useState} from "react";
import {Grid} from "@mui/material";
import {useFormContext, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";

const DadaForm = () => {
    const { t } = useTranslation()
    const { data }  = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} label={t('page.dada.grid.valor')} name={data.tipusValor?.toLowerCase()} hidden={data.tipusValor != 'DATA'} type={"date"} required/>
        <GridFormField xs={12} label={t('page.dada.grid.valor')} name={'importe'} hidden={data.tipusValor != 'IMPORT'} required/>
        <GridFormField xs={12} label={t('page.dada.grid.valor')} name={data.tipusValor?.toLowerCase()} hidden={data.tipusValor != 'TEXT'} required/>
        <GridFormField xs={12} label={t('page.dada.grid.valor')} name={data.tipusValor?.toLowerCase()} hidden={data.tipusValor != 'SENCER'} decimalScale={0} required/>
        <GridFormField xs={12} label={t('page.dada.grid.valor')} name={data.tipusValor?.toLowerCase()} hidden={data.tipusValor != 'FLOTANT'} required/>
        <GridFormField xs={12} label={t('page.dada.grid.valor')} name={data.tipusValor?.toLowerCase()} hidden={data.tipusValor != 'BOOLEA'} required/>
        <GridFormField xs={12} label={t('page.dada.grid.valor')} name={data.tipusValor?.toLowerCase()} hidden={data.tipusValor != 'DOMINI'} required
                       requestParams={{metaDada: data?.metaDada?.codi}}/>
    </Grid>
}

const columns = [
    // {
    //     field: 'metaDada',
    //     flex: 0.5,
    // },
    {
        field: 'valor',
        flex: 0.75,
        valueFormatter: (value:any, row:any)=> row?.dominiDescription || value,
    }
]

const sortModel:any = [{ field: 'ordre', sort: 'asc' }]

const DataGrid = (props:any) => {
    const { entity, contingut, refresh } = props
    const { t } = useTranslation();

    const [numDades, setNumDades] = useState<number>(0);

    return <StyledMuiGrid
        resourceName={"dadaResource"}
        popupEditFormDialogResourceTitle={t('page.dada.title', {metaDada: entity?.nom})}
        filter={
            builder.and(
                builder.eq('metaDada.id', entity?.id),
                builder.eq('node.id', contingut?.id),
            )
        }
        staticSortModel={sortModel}
        columns={columns}
        popupEditCreateActive
        popupEditFormContent={<DadaForm/>}
        formAdditionalData={{
            metaDada: entity,
            node:{id: contingut?.id},
            tipusValor: entity?.tipus,
        }}
        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'xs' }}
        onRowCountChange={(count:number)=>{
            setNumDades?.(count)
            refresh?.()
        }}
        autoHeight
        formInitOnChange
        toolbarHideCreate={ !contingut?.potModificar || numDades > 0 && !(entity?.multiplicitat == 'M_0_N' || entity?.multiplicitat == 'M_1_N') }
        rowHideUpdateButton={!contingut?.potModificar}
        rowHideDeleteButton={!contingut?.potModificar}
    />
}
const useDataGrid = (contingut:any, refresh?:() => void) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const content =
        <MuiDialog
            open={open}
            title={t('page.metaDada.detail', {metaDada: entity?.nom})}
            closeCallback={handleClose}
            componentProps={{ fullWidth: true, maxWidth: 'sm' }}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    icon: 'close'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <DataGrid entity={entity} contingut={contingut} refresh={refresh}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        content,
    }
}
export default useDataGrid;