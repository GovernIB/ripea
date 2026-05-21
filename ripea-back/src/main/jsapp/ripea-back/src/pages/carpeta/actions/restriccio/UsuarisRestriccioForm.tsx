import { FormControl, FormHelperText, Typography } from "@mui/material";
import { useEffect, useMemo, useState } from "react";
import { useFormContext } from "reactlib";
import StyledMuiGrid from "../../../../components/StyledMuiGrid";
import { useTranslation } from "react-i18next";

const columns = [
  { field: 'codi', flex: 0.6 },
  { field: 'nom', flex: 0.6 },
];

const sortModel: any = [{ field: 'nom', sort: 'asc' }];
const UsuarisRestriccioForm = () => {
	const { data, apiRef } = useFormContext();
	const { t } = useTranslation();

	const selectionModel = useMemo(()=>{
	    return data?.restriccions?.map?.((a:any) => a.id)
	}, [])
	
	const namedQueries = useMemo(() => {
	  return data?.metaExpedientId
	    ? [`AMB_PERMIS_SOBRE_PROCEDIMENT#${data.metaExpedientId}`]
	    : undefined;
	}, [data?.metaExpedientId]);
	
	const [selectedRows, setSelectedRows] = useState<any[]>(selectionModel || []);
	
	
	useEffect(() => {
		apiRef?.current?.setFieldValue(
			"restriccions", 
			selectedRows.map((id: string) => ({ id })))
	}, [selectedRows]);
	
	const hasError = data?.restringida && selectedRows.length === 0;
	
    return (
		<>
			<FormControl error={hasError} fullWidth>
				<Typography variant="subtitle2">
					{t('page.carpeta.restriccions.title')}
				</Typography>
					  
				<StyledMuiGrid
				  resourceName="usuariResource"
				  namedQueries={namedQueries}
				  columns={columns}
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
				
				{hasError && <FormHelperText>{t('page.carpeta.restriccions.notEmpty.message')}</FormHelperText>}
			
			</FormControl>
	</>
	)
}

export default UsuarisRestriccioForm;
