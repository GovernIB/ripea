import {useRef} from "react";
import {Grid} from "@mui/material";
import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import UsuarisRestriccioForm from "./restriccio/UsuarisRestriccioForm.tsx";

const ModificarForm = () => {
	const { data } = useFormContext();
	
	return (
		<>
			<Grid container direction="row" columnSpacing={1} rowSpacing={1}>
				<GridFormField xs={12} name="nom" />
				<GridFormField xs={12} name="restringida" />
			</Grid>

			{data?.restringida && (
				<>
					<Grid sx={{ mt: 2, mb: 1}}>
						<GridFormField xs={12} name="motiuRestriccio" />
					</Grid>
					<UsuarisRestriccioForm />
				</>
			)}
		</>
	);
};

const Modificar = (props:any) => {
    const { t } = useTranslation();
    return <MuiFormDialog
        resourceName={"carpetaResource"}
        action={"MODIFICAR_NOM"}
        title={t('page.carpeta.action.update.title')}
        formDialogButtons={[
            {icon: 'save', text: t('common.update'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <ModificarForm/>
    </MuiFormDialog>
}

const useModificar = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
		apiRef.current?.show?.(id,{
			expedient: {id: entity?.id},
			metaExpedientId: entity?.metaExpedient?.id,
			expedientRelacionat: {id: entity?.id},
			restriccions: row?.restriccions,
	    }).then((data:any) => {
			refresh?.()
			temporalMessageShow(null, t('page.carpeta.action.update.ok', {data}), 'success');
		}).catch((error:any) => {
			error?.message && temporalMessageShow(null, error?.message, 'error');
		});
    }

    return {
        handleShow,
        content: <Modificar apiRef={apiRef}/>
    }
}
export default useModificar;