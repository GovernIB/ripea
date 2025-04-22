import {useState} from "react";
import {Grid} from "@mui/material";
import {MuiGrid, useFormContext, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";

const DadaForm = () => {
    const { data }  = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="valor" type={data.tipusValor} required/>
    </Grid>
}

const tipusValor = {
    // TODO: revisar tipado
    TEXT: 'text',
    DATA: 'date',
    IMPORT: 'number',
    SENCER: 'number',
    FLOTANT: 'number',
    BOOLEA: 'checkbox',
    DOMINI: null,
}

export const getDataFieldType = (tipus:string) => {
    return tipusValor[tipus] ?? 'text'
}

const columns = [
    // {
    //     field: 'metaDada',
    //     flex: 0.5,
    // },
    {
        field: 'valor',
        flex: 0.75,
    }
]

const useDataGrid = (contingut:any, refresh?:() => void) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [numDades, setNumDades] = useState<number>(0);

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const content =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
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
            <MuiGrid
                titleDisabled
                resourceName={"dadaResource"}
                filter={
                    builder.and(
                        builder.eq('metaDada.id', entity?.id),
                        builder.eq('node.id', contingut?.id),
                    )
                }
                staticSortModel={[{ field: 'ordre', sort: 'asc' }]}
                columns={columns}
                popupEditCreateActive
                popupEditFormContent={<DadaForm/>}
                formAdditionalData={{
                    metaDada:{id: entity?.id},
                    node:{id: contingut?.id},
                    tipusValor: getDataFieldType(entity?.tipus),
                }}
                onRowsChange={(rows, info) => {
                    setNumDades?.(info?.totalElements)
                    refresh?.()
                }}
                autoHeight
                toolbarHideCreate={ numDades > 0 && !(entity?.multiplicitat == 'M_0_N' || entity?.multiplicitat == 'M_1_N') }
            />
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        content,
    }
}
export default useDataGrid;