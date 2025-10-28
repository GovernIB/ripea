import { useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import { Typography } from "@mui/material";
import * as builder from '../../../../util/springFilterUtils.ts';
import { MuiFormDialog, MuiFormDialogApi, useBaseAppContext, useFormContext } from "reactlib";

const sortModel: any = [{ field: 'nom', sort: 'asc' }];

const columns = [
  { field: 'nom', flex: 0.6 },
  { field: 'descripcio', flex: 0.6 },
];

const InteressatsManageGrupsForm = () => {
	const { data, fields, apiRef } = useFormContext();
	
	// Filtro de grupos del expediente
	const filter = useMemo(() => builder.and(
		builder.eq("expedient.id", data?.expedient?.id)
	), [data?.expedient?.id]);
	
	const selectionModel = useMemo(()=>{
	    return data?.grups?.map?.((a:any) => a.id)
	}, [])

	const [selectedRows, setSelectedRows] = useState<any[]>(selectionModel || []);
	
	useEffect(() => {
		apiRef?.current?.setFieldValue("grups", selectedRows?.map(id => ({ id })))
	}, [selectedRows]);

	return (
	    <>
	      <Typography variant="subtitle2">
              {fields?.find?.(item => item?.name === 'grups')?.label || ''}
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
            <InteressatsManageGrupsForm/>
        </MuiFormDialog>
    );
};

const useManageInteressatGrups = (refresh?: () => void) => {
    const { t } = useTranslation();
    const formApiRef = useRef<MuiFormDialogApi>();
    const { temporalMessageShow } = useBaseAppContext();

    const handleShow = (id:any, row:any) => {
        formApiRef.current?.show?.(id, { grups: row?.grups })
            .then(() => {
                refresh?.()
				temporalMessageShow(null, t('page.interessat.action.gestGrups.ok'));
            })
            .catch((error: any) => {
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    };

    return {
        handleShow,
        dialog: <ManageInteressatGrups apiRef={formApiRef} />
    };
};


export default useManageInteressatGrups;
