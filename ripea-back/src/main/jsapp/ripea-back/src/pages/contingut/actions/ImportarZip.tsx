import { useEffect, useRef, useState } from "react";
import { Grid } from "@mui/material";
import { MuiFormDialogApi, useBaseAppContext } from "reactlib";
import { useTranslation } from "react-i18next";
import { FileFormField } from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import { usePollingArtifactAction } from "../../../components/ActionPollingOptions.tsx";
import ImportarZipResults from "./ImportarZipResults.tsx";
import BackdropLoading from "../../../components/BackdropLoading.tsx";

const usePolling = () => {
	const [progress, setProgress] = useState(0);
	const [progressMessage, setProgressMessage] = useState('');
	const [finished, setFinished] = useState(true);

	const { startPolling, cancelPolling } = usePollingArtifactAction("expedientResource",
		{
			intervalMs: 250,
			stopCondition: (data) => data?.finished,
			onProgress: (data) => {
				setProgress(data?.progres ?? 0);
				setFinished(data?.finished ?? false);

				const lastInfo = data?.info?.[data.info.length - 1];
				setProgressMessage(lastInfo?.text ?? 'Processant...');
			}
		}
	);

	return {
		startPolling,
		cancelPolling,
		progress,
		progressMessage,
		finished,
		setFinished
	};
};


const ImportarZipForm = ({ finished }: any) => {
	return (
		<>
			{finished && (
				<Grid container direction="row" columnSpacing={1} rowSpacing={1}>
					<FileFormField xs={12} name="documentZip" required />
				</Grid>
			)}

		</>
	);
};

const ImportarZip = ({ ...props }: any) => {
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
	}

	const handleCancel = async () => {
		try {
			setShowBackdrop(false);
			cancelPolling(
				entity?.id,
				'CANCEL_IMPORT_ZIP'
			);
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
				action="IMPORT_DOCS_ZIP"
				title={t('page.document.action.importZip.title')}
				formDialogButtons={[
					{ text: t('common.close'), componentProps: { variant: 'outlined' }, value: false },
					{ text: t('common.import'), icon: 'check', componentProps: { variant: 'contained', disabled: isProcessed }, value: true },
				]}
				//onSubmit={handleSubmit}
				{...props}
			>
				<ImportarZipForm
					key={progress}
					finished={finished}
					progress={progress}
					progressMessage={progressMessage} />
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

const useImportarZip = (entity: any, refresh?: () => void) => {
	const { t } = useTranslation();
	const apiRef = useRef<MuiFormDialogApi>();
	const { temporalMessageShow } = useBaseAppContext();

	const polling = usePolling();
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
				'GET_PROGRES_ZIP'
			);

			if (finalResult?.finished) {
				refresh?.();
				temporalMessageShow(
					null,
					t('page.document.action.importZip.ok'),
					'success'
				);
				setIsProcessing(false);
				setIsProcessed(true);
				return <ImportarZipResults results={finalResult} />;
			}

			setIsProcessing(false);
		}

		return null;
	};

	return {
		handleShow,
		content: (
			<ImportarZip
				entity={entity}
				refresh={refresh}
				apiRef={apiRef}
				polling={polling}
				isProcessed={isProcessed}
				formDialogResultProcessor={processResult}
			/>
		)
	};
};

export default useImportarZip;
