import {useEffect, useRef, useState} from "react";
import {Alert, Box, Grid} from "@mui/material";
import {FormField, MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import { useTranslation } from "react-i18next";
import GridFormField, {FileFormField, formatByteCount} from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import { usePollingArtifactAction } from "../../../components/ActionPollingOptions.tsx";
import ImportarZipResults from "./ImportarZipResults.tsx";
import BackdropLoading from "../../../components/BackdropLoading.tsx";
import {DataGridPro} from "@mui/x-data-grid-pro";
import Load from "../../../components/Load.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

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

const ImportarZipForm = () => {
    const {t} = useTranslation()
    const {data, fields, apiRef} = useFormContext()

    const fieldNom = fields?.filter(i=>i.name=='nom')[0];
    const fieldTipusDocument = fields?.filter(i=>i.name=='tipusDocument')[0];

    const metaDocumentFilter: string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("actiu", true),
    );

    const localVariableDocs = useRef<any[]>()
    const updateDocument = (rowId: any, field: string, value: any) => {
        if (!localVariableDocs.current)
            localVariableDocs.current = data?.documentsZip;

        localVariableDocs.current = localVariableDocs.current?.map((doc: any) =>
            doc?.id === rowId ? {...doc, [field]: value} : doc
        )
        apiRef?.current?.setFieldValue('documentsZip', localVariableDocs.current)
    }

    useEffect(() => {
        if (data?.documentZip) {
            apiRef?.current?.setFieldValue('documentZip', null)
        }
        localVariableDocs.current = undefined
    }, [data?.documentZip]);

    const columns:any[] = [
        {
            field: 'ruta',
            headerName: t('page.document.detall.ruta'),
            flex: 1,
        },		
        {
            headerName: fieldNom?.label,
            flex: 1,
            renderCell: (params:any) => {
                const value = data?.documentsZip.find((d: any) => d.id === params.id)?.nom
                return <Box mt={1}><FormField
                    name={"nom" + (value ? `#${params.id}` : '')}
                    value={value}
                    field={fieldNom}
                    onChange={(value) =>
                        updateDocument(params.id, "nom", value)
                    }
                    componentProps={{size: "small"}}
                    debounce
                    required
                /></Box>
            }
        },
        {
            field: 'extensio',
            headerName: t('page.document.detall.extensio'),
            flex: 0.4,
        },
        {
            field: 'mida',
            headerName: t('page.document.detall.mida'),
            flex: 0.4,
            valueFormatter: (value:number) => formatByteCount(value),
        },
        {
            field: 'tipusDocument',
            headerName: fieldTipusDocument?.label,
            flex: 1,
            renderCell: (params:any) => {
                const value = data?.documentsZip.find((d:any) => d.id === params.row.id)?.tipusDocument
                return <Box mt={1}><FormField
                    name={"tipusDocument" + (value ? `#${params.id}` : '')}
                    value={value}
                    field={fieldTipusDocument}
                    onChange={(value) =>
                        updateDocument(params.id, "tipusDocument", value)
                    }
                    componentProps={{size: "small"}}
                    namedQueries={[`CREATE_NEW_DOC#${apiRef?.current?.getId()}`]}
                    filter={metaDocumentFilter}
                    debounce
                    required
                /></Box>
            }
        },
    ]

	return <Grid container direction="row" columnSpacing={1} rowSpacing={1}>
        <FileFormField xs={12} name="documentZip" required />

        <Load value={data?.documentsZip} noEffect>
            <GridFormField xs={12}
                           name={"tipusDocument#default"}
                           field={fieldTipusDocument}
                           onChange={(value:any) => {
                               data.documentsZip.forEach((row:any) => {
                                   updateDocument(row.id, "tipusDocument", value)
                               })
                           }}
                           namedQueries={[`CREATE_NEW_DOC#${apiRef?.current?.getId()}`]}
                           filter={metaDocumentFilter}/>
            <Grid item xs={12}>
            <DataGridPro
                rows={data.documentsZip}
                columns={columns}
                onRowSelectionModelChange={(newSelection) => {
                    data.documentsZip.forEach((row:any) => {
                        updateDocument(row.id, "importar", newSelection.includes(row.id))
                    })
                }}

                rowSelectionModel={data?.documentsZip?.filter?.((row:any)=> row?.importar)?.map?.((row:any) => row.id)}
                checkboxSelection
                disableRowSelectionOnClick
                disableColumnMenu
                disableColumnSorting
            />
            </Grid>

            {!data?.documentsZip?.some?.((doc:any) => doc.importar) &&
                <Grid item xs={12}>
                    <Alert severity={'error'}>{t('page.document.alert.documentsZip')}</Alert>
                </Grid>
            }
        </Load>
    </Grid>
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
                formDialogComponentProps={{fullWidth: true, maxWidth: 'lg'}}
				formDialogButtons={[
                    { icon: 'upload_file', text: t('common.import'), componentProps: { variant: 'contained', disabled: isProcessed }, value: true },
					{ text: t('common.close'), componentProps: { variant: 'outlined' }, value: false },
				]}
				{...props}
			>
				<ImportarZipForm/>
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
