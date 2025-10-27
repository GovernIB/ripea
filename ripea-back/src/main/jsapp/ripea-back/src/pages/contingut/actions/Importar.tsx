import {useRef, useState} from "react";
import {Alert, Typography, Backdrop, Box, CircularProgress, Grid} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from '../../../util/springFilterUtils.ts';

const ImportarForm = () => {
    const {data ,apiRef} = useFormContext();
    const { t } = useTranslation();

    const filterCarpeta = builder.and(
        builder.eq('esborrat', 0),
        builder.eq('expedient.id', apiRef?.current?.getId()),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipusImportacio" required/>
        <GridFormField xs={12} name="codiEni" hidden={data?.tipusImportacio!="CODI_ENI"} required/>
        <GridFormField xs={6} name="numeroRegistre" hidden={data?.tipusImportacio!="NUMERO_REGISTRE"} required/>
        <GridFormField xs={6} name="dataPresentacio" type={"datetime-local"} hidden={data?.tipusImportacio!="NUMERO_REGISTRE"} required/>
        <GridFormField xs={12} name="carpeta"
                       filter={filterCarpeta}
                       disabled={data?.novaCarpetaNom}/>
        <GridFormField xs={12} name="novaCarpetaNom" disabled={data?.carpeta}/>

        <Grid item xs={12} hidden={data?.carpeta || data?.novaCarpetaNom}>
            <Alert severity={"info"}>{t('page.document.alert.folder')}</Alert>
        </Grid>
    </Grid>
}

const Importar = (props:any) => {
    const { t } = useTranslation();
	const [loading, setLoading] = useState(false);
	
    return 	(
		    <>
	<FormActionDialog
        resourceName={"expedientResource"}
        action={"IMPORT_DOCS"}
        title={t('page.document.action.import.title')}
        formDialogButtons={[
            {icon: 'save', text: t('common.import'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
		onSubmit={() => setLoading(true)}
		onSuccess={() => setLoading(false)}
		onError={() => setLoading(false)}
		onClose={() => setLoading(false)}
        {...props}
    >
        <ImportarForm/>
    </FormActionDialog>
	
	<Backdrop open={loading} sx={{ zIndex: 1400, color: "#fff" }}>
		<Box textAlign="center">	
			<CircularProgress color="inherit" />
				<Typography mt={2}>
					{t("common.processing", "Procesando...")}
				</Typography>
		</Box>
	</Backdrop>	
	</>
 );		  
}

const useImportar = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () :void => {
        apiRef.current?.show?.(entity?.id)
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.import.ok'), 'success');
    }

    return {
        handleShow,
        content: <Importar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useImportar;