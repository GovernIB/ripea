import { useEffect, useRef, useState } from "react";
import { Alert, Grid } from "@mui/material";
import { MuiFormDialogApi, useBaseAppContext, useFormContext } from "reactlib";
import { useTranslation } from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import * as builder from '../../../util/springFilterUtils.ts';
import InteressatsRegistre from "../details/InteressatsRegistre.tsx";
import { usePollingArtifactAction } from "../../../components/ActionPollingOptions.tsx";
import ImportarSgdResults from "./ImportarSgdResults.tsx";
import BackdropLoading from "../../../components/BackdropLoading.tsx";

export const usePollingImportSgd = () => {
	const [progress, setProgress] = useState(0);
	const [progressMessage, setProgressMessage] = useState('');
	const [finished, setFinished] = useState(true);

	const { startPolling, cancelPolling } = usePollingArtifactAction("expedientResource", {
		intervalMs: 250,
		stopCondition: (data) => data?.finished,
		onProgress: (data) => {
			setProgress(data?.progres ?? 0);
			setFinished(data?.finished ?? false);
			const lastInfo = data?.info?.[data.info.length - 1];
			setProgressMessage(lastInfo?.text ?? "Processant...");
		}
	});

	return { startPolling, cancelPolling, progress, progressMessage, finished, setFinished };
};


const ImportarForm = () => {
	const { data, apiRef } = useFormContext();
	const { t } = useTranslation();

	const filterCarpeta = builder.and(
		builder.eq('esborrat', 0),
		builder.eq('expedient.id', apiRef?.current?.getId()),
	);

	return (
		<>
			<Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
				<GridFormField xs={12} name="tipusImportacio" required />
				<GridFormField xs={12} name="codiEni" hidden={data?.tipusImportacio != "CODI_ENI"} required />
				<GridFormField xs={6} name="numeroRegistre" hidden={data?.tipusImportacio != "NUMERO_REGISTRE"} required />
				<GridFormField xs={6} name="dataPresentacio" type={"datetime-local"} hidden={data?.tipusImportacio != "NUMERO_REGISTRE"} required />
				<GridFormField xs={12} name="importarInteressats" disabled={!data?.numeroRegistre} /> 

				{data?.importarInteressats && (
					<Grid item xs={12}>
						<InteressatsRegistre
							expedientId={apiRef.current?.getId()}
							numeroRegistre={data?.numeroRegistre}
						/>
					</Grid>
				)}

				<GridFormField xs={12} name="carpeta"
					filter={filterCarpeta}
					disabled={data?.novaCarpetaNom} />
				<GridFormField xs={12} name="novaCarpetaNom" disabled={data?.carpeta} />

				<Grid item xs={12} hidden={data?.carpeta || data?.novaCarpetaNom}>
					<Alert severity={"info"}>{t('page.document.alert.folder')}</Alert>
				</Grid>
			</Grid>

		</>
	)
}

const Importar = ({ ...props }: any) => {
	const { entity, refresh, polling, apiRef, isProcessed } = props;
	const { t } = useTranslation();
	const { progress, finished, progressMessage, cancelPolling } = polling;
	const [showBackdrop, setShowBackdrop] = useState(false);

	useEffect(() => {
		setShowBackdrop(!finished);
	}, [finished]);

	const handleClose = () => {
		setShowBackdrop(false);
		apiRef.current?.close();
		refresh?.();
	};

	const handleCancel = () => {
		try {
			cancelPolling(entity?.id, "CANCEL_IMPORT_SGD");
			setShowBackdrop(false);
			refresh?.();
		} catch (error) {
			console.error("Error al cancelar:", error);
		}
	};

	return (
		<>
			<FormActionDialog
				apiRef={apiRef}
				resourceName="expedientResource"
				action="IMPORT_DOCS"
				title={t('page.document.action.importSgd.title')}
				formDialogButtons={[
					{ text: t('common.close'), componentProps: { variant: 'outlined' }, value: false },
					{ text: t('common.import'), componentProps: { variant: 'contained', disabled: isProcessed }, value: true },
				]}
				{...props}
			>
				<ImportarForm />
			</FormActionDialog>

			<BackdropLoading
				open={showBackdrop}
				progress={progress}
				progressMessage={progressMessage}
				onCancel={handleCancel}
				onClose={handleClose}
			/>
		</>
	);
};

const useImportar = (entity: any, refresh?: () => void) => {
	const apiRef = useRef<MuiFormDialogApi>();
	const { temporalMessageShow } = useBaseAppContext();
	const { t } = useTranslation();

	const polling = usePollingImportSgd();
	const [isProcessing, setIsProcessing] = useState(false);
	const [isProcessed, setIsProcessed] = useState(false);

	const handleShow = () => {
		polling.setFinished(true);
		setIsProcessed(false);
		apiRef.current?.show?.(entity?.id);
	};

	const processResult = async (resultat: any) => {
		if (!isProcessing) {
			setIsProcessing(true);
			polling.setFinished(false);

			const finalResult = await polling.startPolling(
				resultat?.id, 
				"GET_PROGRES_SGD"
			);

			if (finalResult?.finished) {
				refresh?.();
				temporalMessageShow(
					null, 
					t('page.document.action.importSgd.ok'), 
					'success');
				setIsProcessing(false);
				setIsProcessed(true);
				return <ImportarSgdResults results={finalResult} />;
			}

			setIsProcessing(false);
		}
		return null;
	};

	return {
		handleShow,
		content: <Importar 
			entity={entity} 
			refresh={refresh} 
			apiRef={apiRef} 
			polling={polling} 
			isProcessed={isProcessed} 
			formDialogResultProcessor={processResult} />
	};
};

export default useImportar;
