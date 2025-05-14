import {useEffect, useRef, useState} from "react";
import {Grid} from "@mui/material";
import {DataGridPro} from "@mui/x-data-grid-pro";
import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import Load from "../../../components/Load.tsx";

const columns = [
    {
        field: 'tipus',
        headerName: 'Interesados del fichero',
        flex: 0.75,
    },
    {
        field: 'documentNum',
        headerName: '',
        flex: 0.5,
    },
    {
        field: 'nomComplet',//organNom
        headerName: '',
        flex: 1,
        valueFormatter: (value: any, row:any) => row?.organNom ?? value
    },
    // {
    //     field: 'representant',
    //     flex: 0.75,
    // },
];

const ImportForm = () => {
    const {data, apiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    useEffect(() => {
        apiRef?.current?.setFieldValue("interessatsPerImportar", data?.interessatsFitxer?.filter((i:any)=>selectedRows.includes(i.id)))
    }, [selectedRows]);

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>

        <GridFormField xs={12} name="fitxerJsonInteressats" type={"file"} required/>

        <Load value={data?.interessatsFitxer} noEffect>
            <Grid item xs={12}>
            <DataGridPro
                rows={data?.interessatsFitxer}
                columns={columns}
                onRowSelectionModelChange={(newSelection) => {
                    setSelectedRows([...newSelection]);
                }}

                style={{
                    maxHeight: 162 + 52 * 4,
                }}
                checkboxSelection
                disableRowSelectionOnClick
                disableColumnMenu
                disableColumnSorting
            />
            </Grid>
        </Load>

    </Grid>
}

const Import = (props:any) => {
    const {t} = useTranslation();

    return <FormActionDialog
        resourceName={"interessatResource"}
        action={"IMPORTAR"}
        title={'Importar interesados'}
        {...props}
        formDialogComponentProps={{fullWidth: true, maxWidth: 'md'}}
    >
        <ImportForm/>
    </FormActionDialog>
}
const useImport = (entity:any, refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (): void => {
        apiRef.current?.show?.(undefined, {
            expedient: {
                id: entity?.id,
                description: entity?.nom,
            },
        })
    }
    const onSuccess = (): void => {
        refresh?.()
        temporalMessageShow(null, '', 'success');
    }
    const onError = (error: any): void => {
        temporalMessageShow('Error', error?.message, 'error');
    }

    return {
        handleShow,
        content: <Import apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useImport;