import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useExportarDocumentsMassive} from "../actions/ExportarDocuments.tsx";
import { useTranslation } from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";
import {Divider} from "@mui/material";
import useImportarDocumentMassive from "../actions/ImportarDocumentMassive.tsx";

export const useMassiveActions = (refresh?: () => void)=> {
	
    const {temporalMessageShow} = useBaseAppContext();
	const { t } = useTranslation();
    const {artifactAction: apiAction, artifactReport: apiReport} = useResourceApiService('expedientResource');

    const massiveAction = (ids:any[], code:string, msg:string) => {
        apiAction(undefined, {code :code, data:{ ids: ids, massivo: true }})
			.then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            })
    }
	
	const massiveReport = (ids:any[], code:string, msg:string, fileType:any) => {
	    apiReport(undefined, {code :code, data:{ ids: ids, massivo: true }, fileType})
			.then(() => {
			    refresh?.()
			    temporalMessageShow(null, msg, 'info');
			})
			.catch((error) => {
			    temporalMessageShow(null, error?.message, 'error');
			});		
	}

    const agafar 	= (ids: any[]): void => { massiveAction(ids, 'AGAFAR', t('page.expedient.results.actionBackgroundOk'));}
	const alliberar = (ids: any[]): void => { massiveAction(ids, 'ALLIBERAR', t('page.expedient.results.actionBackgroundOk'));}
	const retornar 	= (ids: any[]): void => { massiveAction(ids, 'RETORNAR', t('page.expedient.results.actionBackgroundOk'));}
    const follow	= (ids: any[]): void => { massiveAction(ids, 'FOLLOW', t('page.expedient.results.actionBackgroundOk'));}
    const unfollow 	= (ids: any[]): void => { massiveAction(ids, 'UNFOLLOW', t('page.expedient.results.actionBackgroundOk'));}
	const esborrar 	= (ids: any[]): void => { massiveAction(ids, 'ESBORRAR', t('page.expedient.results.actionBackgroundOk'));}
    const guardarArxiu = (ids: any[]): void => { massiveAction(ids, 'GUARDAR_ARXIU', t('page.expedient.results.actionBackgroundOk'));}
	const syncArxiu = (ids: any[]): void => { massiveAction(ids, 'SYNC_ARXIU', t('page.expedient.results.actionBackgroundOk'));}

	const exportExcel 	= (ids: any[]): void => { massiveReport(ids, 'EXPORT_EXCEL', t('page.expedient.results.actionBackgroundOk'), 'XLSX');}
	const exportCsv		= (ids: any[]): void => { massiveReport(ids, 'EXPORT_CSV', t('page.expedient.results.actionBackgroundOk'), 'CSV');}
	const exportIndexZip= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_ZIP', t('page.expedient.results.actionBackgroundOk'), 'ZIP');}
	const exportIndexPdf= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_PDF', t('page.expedient.results.actionBackgroundOk'), 'PDF');}
	const exportIndexXls= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_XLS', t('page.expedient.results.actionBackgroundOk'), 'XLSX');}
	const exportEni		= (ids: any[]): void => { massiveReport(ids, 'EXPORT_ENI', t('page.expedient.results.actionBackgroundOk'), 'ZIP');}
	const exportInside	= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INSIDE', t('page.expedient.results.actionBackgroundOk'), 'ZIP');}

    return {
        guardarArxiu,
        syncArxiu,
        agafar,
		alliberar,
		retornar,
        follow,
        unfollow,
		esborrar,
		exportExcel,
		exportCsv,
		exportIndexZip,
		exportIndexPdf,
		exportIndexXls,
		exportEni,
		exportInside		
    }
}

const useExpedientMassiveActions = (refresh?: () => void)=> {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const {	agafar,
			alliberar,
			retornar,
	        follow,
	        unfollow,
			esborrar,
			exportExcel,
			exportCsv,
			exportIndexZip,
			exportIndexPdf,
			exportIndexXls,
			exportEni,
			exportInside
    } = useMassiveActions(refresh);

    const {handleMassiveShow: handleExportDoc, content: contentExportDoc} = useExportarDocumentsMassive(refresh);
    const {handleShow: handleImpDocMass, content: contentImpDocMass} = useImportarDocumentMassive(refresh);

    const actions = [
        {
            label: t('page.expedient.action.agafar.label'),
            icon: "lock",
            showInMenu: true,
            onClick: agafar,
        },
        {
            label: t('page.expedient.action.lliberar.label'),
            icon: "lock_open",
            showInMenu: true,
			onClick: alliberar,
        },
		{
		    label: t('page.expedient.action.retornar.label'),
		    icon: "undo",
            showInMenu: true,
			onClick: retornar,
		},		
        {
            label: t('page.expedient.action.follow.label'),
            icon: "person_add_alt1",
            showInMenu: true,
            onClick: follow,
        },
        {
            label: t('page.expedient.action.unfollow.label'),
            icon: "person_remove",
            showInMenu: true,
            onClick: unfollow,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
			onClick: esborrar,
        },
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.expedient.action.exportFullCalcul.label'),
            icon: "download",
            showInMenu: true,
			onClick: exportExcel
        },
        {
            label: t('page.expedient.action.exportCSV.label'),
            icon: "download",
            showInMenu: true,
			onClick: exportCsv
        },
        {
            label: t('page.expedient.action.exportZIP.label'),
            icon: "download",
            showInMenu: true,
			onClick: exportIndexZip
        },
        {
            label: t('page.expedient.action.exportPDF.label'),
            icon: "download",
            showInMenu: true,
			onClick: exportIndexPdf,
        },
		{
            label: t('page.expedient.action.exportEXCEL.label'),
		    icon: "download",
            showInMenu: true,
			onClick: exportIndexXls,
            hidden: !(user?.sessionScope?.isExportacioExcelActiva),
		},
        {
            label: t('page.expedient.action.exportENI.label'),
            icon: "download",
            showInMenu: true,
			onClick: exportEni
        },
		{
            label: t('page.expedient.action.exportINSIDE.label'),
		    icon: "download",
            showInMenu: true,
			onClick: exportInside,
			hidden: !(user?.sessionScope?.isExportacioInsideActiva),
		},
        {
            label: t('page.expedient.action.exportDocs.label'),
            icon: "folder_zip",
            showInMenu: true,
            onClick: handleExportDoc,
        },
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.expedient.action.impDocMass.label'),
            icon: "add_notes",
            showInMenu: true,
            onClick: handleImpDocMass,
        },
    ]

    const components = <>
        {contentExportDoc}
        {contentImpDocMass}
    </>

    return {
        actions,
        components
    }
}
export default useExpedientMassiveActions;