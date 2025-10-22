import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {useEffect, useMemo, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import Grid from "@mui/material/Grid";
import { Typography } from "@mui/material";
import * as builder from '../../../../util/springFilterUtils';
import StyledMuiGrid from "../../../../components/StyledMuiGrid";
import GridFormField from "../../../../components/GridFormField";

const sortModel: any = [{ field: 'nomComplet', sort: 'asc' }];

const columns = [
  { field: 'nomComplet', flex: 0.6 },
  { field: 'identificador', flex: 0.6 },
];

const CrearGrupForm = (props: {expedientId: number}) => {
	const { expedientId } = props;
	const { data, apiRef } = useFormContext();
	const {t} = useTranslation();
	
	// Filtro de grupos del expediente
	const filter = useMemo(() => builder.and(
		builder.eq("expedient.id", expedientId)
	), [expedientId]);
	
	const selectionModel = useMemo(() => {
		return data?.interessats?.map((a: any) => a.id)
	}, [])

	const [selectedRows, setSelectedRows] = useState<any[]>(selectionModel || []);

	useEffect(() => {
		apiRef?.current?.setFieldValue("interessats", selectedRows?.map(id => ({ id })))
	}, [selectedRows]);
	
	return <>
		<Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
			<GridFormField xs={12} name="nom" required/>
			<GridFormField xs={12} name="descripcio" />
		</Grid>

		<Typography variant="subtitle2">
			{t('page.interessat.grup.action.interessats.label')}
		</Typography>

		<StyledMuiGrid
			resourceName="interessatResource"
			columns={columns}
			filter={filter}
			sortModel={sortModel}
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

const CreateGrup = (props:any) => {
    const {t} = useTranslation();
	const { entity } = props
	
	return <MuiFormDialog
	    resourceName={"interessatGrupResource"}
	    resourceTitle={t('page.interessat.grup.title')}
	    onClose={(reason?: string) => reason !== 'backdropClick'}
	    dialogButtons={[
	        {icon: 'save', text: t('common.create'), componentProps: { variant: 'contained' }, value: true },
	        {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
	    ]}
	    {...props}
	>
	    <CrearGrupForm expedientId={entity?.id}/>
	</MuiFormDialog>
	
}

const useCreateGrup = (entity:any, refresh?: () => void, setGrups?: (fn: any) => void) => {
    const {t} = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (): void => {
        apiRef.current?.show?.(undefined, {
            expedient: {id: entity?.id},
        })
		.then((newGrup:any) => {
		    refresh?.()
			setGrups?.((prev: any[]) => [...prev, newGrup]);
		    temporalMessageShow(null, t('page.interessat.grup.action.new.ok'), 'success');
		})
		.catch((error:any) => {
		    error?.message && temporalMessageShow(null, error?.message, 'error');
		});
    }

    return {
        handleShow,
        content: <CreateGrup entity={entity} apiRef={apiRef}/>
    }
}
export default useCreateGrup;