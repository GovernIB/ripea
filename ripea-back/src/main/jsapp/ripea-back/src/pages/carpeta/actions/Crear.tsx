import {MuiFormDialog, useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {Box, Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
import UsuarisRestriccioForm from "./restriccio/UsuarisRestriccioForm.tsx";
import {useUserSession} from "../../../components/Session.tsx";

const CrearForm = () => {
	const { data } = useFormContext();
    const { value: user } = useUserSession();

	return (
		<>
			<Grid container direction="row" columnSpacing={1} rowSpacing={1}>
				<GridFormField xs={12} name="nom" />
				{user?.sessionScope?.isRestringirCarpetesActiu &&
				    <GridFormField xs={12} name="restringida" />
				}
			</Grid>

			{data?.restringida && (
				<Box p={1}>
					<UsuarisRestriccioForm />
				</Box>
			)}
		</>
	);
};

const Crear = (props:any) => {
    const { t } = useTranslation();

    return <MuiFormDialog
        resourceName={"carpetaResource"}
        resourceTitle={t('page.carpeta.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        dialogButtons={[
            {icon: 'save', text: t('common.create'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <CrearForm/>
    </MuiFormDialog>
}

const useCrear = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = () => {
        apiRef.current?.show(undefined, {
            expedient: {id: entity?.id},
			metaExpedientId: entity?.metaExpedient?.id,
            expedientRelacionat: {id: entity?.id},
        })
            .then((data:any) => {
                refresh?.()
                temporalMessageShow(null, t('page.carpeta.action.new.ok', {data}), 'success');
            })
            .catch((error:any) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow,
        content: <Crear apiRef={apiRef}/>,
    }
}
export default useCrear;