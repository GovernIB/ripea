import {useState} from "react";
import {Grid} from "@mui/material";
import {useFormContext, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";

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

const sortModel:any = [{ field: 'ordre', sort: 'asc' }]

const DataGrid = (props:any) => {
    const { entity, contingut, refresh } = props
    const [numDades, setNumDades] = useState<number>(0);

    return <StyledMuiGrid
        resourceName={"dadaResource"}
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
            metaDada:{id: entity?.id},
            node:{id: contingut?.id},
            tipusValor: getDataFieldType(entity?.tipus),
        }}
        onRowCountChange={(count:number)=>{
            setNumDades?.(count)
            refresh?.()
        }}
        autoHeight
        // height={162 + 52 * 4}
        toolbarHideCreate={ numDades > 0 && !(entity?.multiplicitat == 'M_0_N' || entity?.multiplicitat == 'M_1_N') }
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
            <DataGrid entity={entity} contingut={contingut} refresh={refresh}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        content,
    }
}
export default useDataGrid;