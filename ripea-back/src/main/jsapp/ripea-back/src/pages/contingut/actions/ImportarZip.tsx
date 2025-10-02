import {useRef, useState} from "react";
import {Grid, Typography, Box, Backdrop, CircularProgress } from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import {FileFormField} from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ImportarZipForm = () => {
  return (
    <Grid container direction="row" columnSpacing={1} rowSpacing={1}>
      <FileFormField xs={12} name="documentZip" required />
    </Grid>
  );
};


const ImportarZip = (props:any) => {
    const { t } = useTranslation();
	const [loading, setLoading] = useState(false);
	
    return 	(
	    <>
		<FormActionDialog
        resourceName={"expedientResource"}
        action={"IMPORT_DOCS_ZIP"}
        title={t('page.document.action.importZip.title')}
        formDialogButtons={[
            {icon: 'save', text: t('common.import'), componentProps: { variant: 'contained' }, value: true},
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
		onSubmit={() => setLoading(true)}
		onSuccess={() => setLoading(false)}
		onError={() => setLoading(false)}
		onClose={() => setLoading(false)}
        {...props}
    >
	<ImportarZipForm/>

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
};

const useImportarZip = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () :void => {
        apiRef.current?.show?.(entity?.id);
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.importZip.ok'), 'success');
    }

    return {
        handleShow,
        content: <ImportarZip apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useImportarZip;