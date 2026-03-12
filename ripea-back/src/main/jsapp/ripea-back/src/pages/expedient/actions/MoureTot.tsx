import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Alert, Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import * as builder from "../../../util/springFilterUtils.ts";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const MoureTotForm = () => {
	const { data } = useFormContext();
	const { t } = useTranslation();
	
	const filtreProcediment = builder.eq('metaExpedient.id', data?.metaExpedientId);
	
	//namedQueries={[`BY_PROCEDIMENT#${data?.metaExpedientId}`]}
	
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
		<Grid item xs={12}>
			<Alert severity="info">
			  <div>
			    <div>{t('page.expedient.alert.moureTot.title')}</div>
			    <ul style={{ marginTop: 8, paddingLeft: 20 }}>
					{(t('page.expedient.alert.moureTot.items', { returnObjects: true }) as string[])
					  .map(item => (
					    <li key={item}>{item}</li>
					))}
			    </ul>
			  </div>
			</Alert>
		</Grid>
		
        <GridFormField xs={12} name="expedientDesti" filter={filtreProcediment} required/>
    </Grid>
}

const MoureTot = (props:any) => {
    const { t } = useTranslation();
	
	return <FormActionDialog
	    resourceName={"expedientResource"}
		title={t('page.expedient.action.moureTot.title')}
		action={'MOURE_TOT'}
	    formDialogButtons={[
	        {icon: 'open_with', text: t('page.expedient.action.moureTot.button'), componentProps: { variant: 'contained' }, value: true },
	        {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
	    ]}
	    {...props}
	>
	    <MoureTotForm/>
	</FormActionDialog>
}

const useMoureTot = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();
	
	const handleShow = (id:any, row:any) :void => {
		apiRef.current?.show?.(id, { metaExpedientId: row?.metaExpedient?.id })
	}
	
	const onSuccess = (response:any) :void => {
	    temporalMessageShow(null, t('page.expedient.action.moureTot.ok', {expedient: response?.nom}), 'success');
		refresh?.()
	}
	
    return {
        handleShow,
        content: <MoureTot apiRef={apiRef} onSuccess={onSuccess}/>
    }
}

export default useMoureTot;