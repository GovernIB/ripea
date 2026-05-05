import { useBaseAppContext, useFormContext, useResourceApiService } from "reactlib";
import { useTranslation } from "react-i18next";
import { useEffect, useMemo, useState } from "react";
import { FormControl, Typography } from "@mui/material";
import { GridRowSelectionModel } from '@mui/x-data-grid-pro';
import { toSelectionModel, fromSelectionModel } from '../../../util/selectionModelUtils.ts';
import { DataGridPro } from "@mui/x-data-grid-pro";


const mapInteressat = (src?: any): any | null => {
  if (!src) return null;

  return {
    tipus: src.tipusInteressat,
	documentTipus: src.tipusDocumentIdentificacio,
    documentNum: src.documentNum,
    nom: src.nom,
    llinatge1: src.llinatge1,
    llinatge2: src.llinatge2,
	raoSocial: src.raoSocial,
    pais: src.pais,
    provincia: src.provincia,
    municipi: src.municipi,
	adresa: src.adresa,
	codiPostal: src.codiPostal,
	email: src.email,
	telefon: src.telefon,
	emailHabilitat: src.emailHabilitat,
	canalPreferent: src.canalPreferent,
	observacions: src.observacions,
  };
};

const mapRegistreDadesInteressat = (src: any): any => {
  const interessat = mapInteressat(src.interessat);
  const representant = mapInteressat(src.representant);

  if (interessat && representant) {
    interessat.representant = representant;
  }

  return interessat;
};

const useInteressatsActions = (expedientId: any, numeroRegistre: any) => {
	const { temporalMessageShow } = useBaseAppContext();
	const { artifactAction: apiAction, isLoading: apiLoading } = useResourceApiService('expedientResource');
	const [interessatsRegistre, setInteressatsRegistre] = useState<any>();
	const [loading, setLoading] = useState(false);

	const getInteressatsRegistre = () => {
		if (!numeroRegistre || apiLoading) return;
		setLoading(true);
		setInteressatsRegistre(undefined);

		apiAction(expedientId, { code: "IMPORT_INTE", data: { numeroRegistre: numeroRegistre } })
			.then(res => setInteressatsRegistre(res))
			.catch(err => {
				if (err?.message) temporalMessageShow(null, err.message, 'error');
			})
			.finally(() => setLoading(false));
	};

	useEffect(() => {
		getInteressatsRegistre();
	}, [numeroRegistre, apiLoading]);

	return { interessatsRegistre, loading };
};

const InteressatsRegistre = (props: any) => {
	const { expedientId, numeroRegistre } = props;
	const { t } = useTranslation();
	const { apiRef } = useFormContext();
	const { interessatsRegistre, loading } = useInteressatsActions(expedientId, numeroRegistre);
	const interessats = interessatsRegistre?.interessats;

	const tipusMap: Record<string, string> = {
		"1": t('page.document.action.importSgd.interessat.tipus.1'),
		"2": t('page.document.action.importSgd.interessat.tipus.2'),
		"3": t('page.document.action.importSgd.interessat.tipus.3'),
	};
	
	const columns = useMemo(() => [
		{ field: 'tipus', headerName: t('page.interessat.detall.tipus'), flex: 0.9 },
		{ field: 'nom', headerName: t('page.interessat.detall.nom'), flex: 1 },
		{ field: 'documentNum', headerName: t('page.interessat.detall.nif'), flex: 0.6 },
		{ field: 'representant', headerName: t('page.interessat.rep'), flex: 1 },
	], [t]);
	
	const rows = useMemo(() => {
	console.log(interessats);
	  return interessats?.map((item: any, index: number) => ({
	    id: index,
	    __raw: item,
	    nom: item.interessat?.nom,
	    documentNum: item.interessat?.documentNum,
	    tipus: tipusMap[item.interessat?.tipusInteressat] ?? item.interessat?.tipusInteressat,
	    representant: item.representant
	      ? item.representant.nom
	      : '-',
	  })) ?? [];
	}, [interessats]);

	const [selectionModel, setSelectionModel] = useState<GridRowSelectionModel>(toSelectionModel([]));

	useEffect(() => {
	  const selectedIds = fromSelectionModel(selectionModel);
	  const interessatsSeleccionats = selectedIds.map((id) =>
	    mapRegistreDadesInteressat(rows[id].__raw)
	  );

	  apiRef.current?.setFieldValue(
	    "interessats",
	    interessatsSeleccionats
	  );
	}, [selectionModel]);

	return (
		<>
			<FormControl fullWidth>
				<Typography variant="subtitle2">{t('page.document.action.importSgd.interessats')}</Typography>

				<DataGridPro
					rows={rows}
					columns={columns}
					loading={loading}
					checkboxSelection
					rowSelectionModel={selectionModel}
					onRowSelectionModelChange={(newSelection) => {
						setSelectionModel(newSelection as GridRowSelectionModel);
					}}
					style={{
						height: 162 + 75 * (rows.length > 0 ? rows.length : 1),
					}}
					disableRowSelectionOnClick
					hideFooter
				/>
			</FormControl>
		</>
	)
}

export default InteressatsRegistre;