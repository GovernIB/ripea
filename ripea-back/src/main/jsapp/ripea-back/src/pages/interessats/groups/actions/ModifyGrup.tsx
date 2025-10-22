import {MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useEffect, useMemo, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import Grid from "@mui/material/Grid";
import GridFormField from "../../../../components/GridFormField";
import { Typography } from "@mui/material";
import * as builder from '../../../../util/springFilterUtils';
import StyledMuiGrid from "../../../../components/StyledMuiGrid";

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
	        {icon: 'save', text: t('common.update'), componentProps: { variant: 'contained' }, value: true },
	        {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
	    ]}
	    {...props}
	>
	    <CrearGrupForm expedientId={entity?.id}/>
	</MuiFormDialog>
	
}

const useModifyGrup = (entity:any, refresh?: () => void) => {
    const {t} = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
	const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};
	const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
	const {delete: apiDelete} = useResourceApiService('interessatGrupResource');
	
    const updateGrup = (id:number, interessats:any): void => {
        apiRef.current?.show?.(id, {
			interessats: interessats,
            expedient: {id: entity?.id},
        })
		.then(() => {
		    refresh?.()
		    temporalMessageShow(null, t('page.interessat.grup.action.update.ok') , 'success');
		})
		.catch((error:any) => {
		    temporalMessageShow(null, error?.message || t('common.error'), 'error');
		});
    }
	
	const deleteGrup = (id:any, row:any) :void => {
	    messageDialogShow(
	        t('page.interessat.grup.action.delete.check'),
	        t('page.interessat.grup.action.delete.description'),
	        [{
	            value: true,
	            text: t('common.accepta'),
	            componentProps: { variant: 'contained' }
	        },
	        {
	            value: false,
	            text: t('common.cancel'),
	            componentProps: { variant: 'outlined' }
	        }],
	        confirmDialogComponentProps)
	        .then((value: any) => {
	            if (value) {
	                apiDelete(id)
	                    .then(() => {
	                        refresh?.();
	                        temporalMessageShow(null, t('page.interessat.grup.action.delete.ok', {data: row}), 'success');
	                    })
	                    .catch((error) => {
	                        temporalMessageShow(null, error?.message, 'error');
	                    });
	            }
	        });
	}

    return {
        updateGrup,
		deleteGrup,
        content: <CreateGrup entity={entity} apiRef={apiRef}/>
    }
}
export default useModifyGrup;
