import { useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import { Typography } from "@mui/material";
import * as builder from '../../../../util/springFilterUtils';
import { MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext } from "reactlib";

const sortModel: any = [{ field: 'nom', sort: 'asc' }];

const columns = [
  { field: 'nom', flex: 0.6 },
  { field: 'descripcio', flex: 0.6 },
];

const InteressatsManageGrupsForm = (props: { expedientId:number }) => {
	const { expedientId } = props;
	const { data, apiRef } = useFormContext();
	const { t } = useTranslation();
	
	// Filtro de grupos del expediente
	const filter = useMemo(() => builder.and(
		builder.eq("expedient.id", expedientId)
	), [expedientId]);
	
	const selectionModel = useMemo(()=>{
	    return data?.grups?.map((a:any) => a.id)
	}, [])

	const [selectedRows, setSelectedRows] = useState<any[]>(selectionModel || []);
	
	useEffect(() => {
		console.log(selectedRows?.map(id => ({ id })))
		apiRef?.current?.setFieldValue("grups", selectedRows?.map(id => ({ id })))
	}, [selectedRows]);

	return (
	    <>
	      <Typography variant="subtitle2">
	        {t('page.interessat.grup.action.grups.label')}
	      </Typography>

	      <StyledMuiGrid
	        resourceName="interessatGrupResource"
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
	  );
}

const ManageInteressatGrups = (props:any) => {
    const { t } = useTranslation();
	const { entity } = props

    return (
        <MuiFormDialog
            resourceName={"interessatResource"}
            title={t('page.interessat.action.gestGrups.title')}
            onClose={(reason?: string) => reason !== 'backdropClick'}
			dialogButtons={[
			    {icon: 'save', text: t('common.update'), componentProps: { variant: 'contained' }, value: true },
			    {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
			]}
            {...props}
        >
            <InteressatsManageGrupsForm expedientId={entity?.id}/>
        </MuiFormDialog>
    );
};

const useManageInteressatGrups = (entity: any, refresh?: () => void) => {
    const { t } = useTranslation();
    const formApiRef = useRef<MuiFormDialogApi>();
    const { temporalMessageShow } = useBaseAppContext();

    const handleShow = (id:any, grups:any) => {
        formApiRef.current?.show?.(id, { grups: grups })
            .then(() => {
                refresh?.()
				temporalMessageShow(null, t('page.interessat.action.gestGrups.ok'));
            })
            .catch((error: any) => {
                temporalMessageShow(null, error?.message || t('common.error'), 'error');
            });
    };

    return {
        handleShow,
        dialog: <ManageInteressatGrups entity={entity} apiRef={formApiRef} />
    };
};


export default useManageInteressatGrups;
