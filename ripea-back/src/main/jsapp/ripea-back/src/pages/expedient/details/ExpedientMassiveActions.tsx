import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useExportarDocumentsMassive} from "../actions/ExportarDocuments.tsx";
import { useTranslation } from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";
import {Divider} from "@mui/material";

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

	const exportExcel 	= (ids: any[]): void => { massiveReport(ids, 'EXPORT_EXCEL', t('page.expedient.results.actionBackgroundOk'), 'XLSX');}
	const exportCsv		= (ids: any[]): void => { massiveReport(ids, 'EXPORT_CSV', t('page.expedient.results.actionBackgroundOk'), 'CSV');}
	const exportIndexZip= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_ZIP', t('page.expedient.results.actionBackgroundOk'), 'ZIP');}
	const exportIndexPdf= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_PDF', t('page.expedient.results.actionBackgroundOk'), 'PDF');}
	const exportIndexXls= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_XLS', t('page.expedient.results.actionBackgroundOk'), 'XLSX');}
	const exportEni		= (ids: any[]): void => { massiveReport(ids, 'EXPORT_ENI', t('page.expedient.results.actionBackgroundOk'), 'ZIP');}
	const exportInside	= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INSIDE', t('page.expedient.results.actionBackgroundOk'), 'ZIP');}

    return {
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

    const actions = [
        {
            title: t('page.expedient.action.agafar.label'),
            icon: "lock",
            onClick: agafar,
        },
        {
            title: t('page.expedient.action.lliberar.label'),
            icon: "lock_open",
			onClick: alliberar,
        },
		{
		    title: t('page.expedient.action.retornar.label'),
		    icon: "undo",
			onClick: retornar,
		},		
        {
            title: t('page.expedient.action.follow.label'),
            icon: "person_add_alt1",
            onClick: follow,
        },
        {
            title: t('page.expedient.action.unfollow.label'),
            icon: "person_remove",
            onClick: unfollow,
        },
        {
            title: t('common.delete'),
            icon: "delete",
			onClick: esborrar,
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.expedient.action.exportFullCalcul.label'),
            icon: "download",
			onClick: exportExcel
        },
        {
            title: t('page.expedient.action.exportCSV.label'),
            icon: "download",
			onClick: exportCsv
        },
        {
            title: t('page.expedient.action.exportZIP.label'),
            icon: "download",
			onClick: exportIndexZip
        },
        {
            title: t('page.expedient.action.exportPDF.label'),
            icon: "download",
			onClick: exportIndexPdf,
        },
		{
            title: t('page.expedient.action.exportEXCEL.label'),
		    icon: "download",
			onClick: exportIndexXls,
            hidden: !(user?.sessionScope?.isExportacioExcelActiva),
		},
        {
            title: t('page.expedient.action.exportENI.label'),
            icon: "download",
			onClick: exportEni
        },
		{
            title: t('page.expedient.action.exportINSIDE.label'),
		    icon: "download",
			onClick: exportInside,
			hidden: !(user?.sessionScope?.isExportacioInsideActiva),
		},
        {
            title: t('page.expedient.action.exportZIP.label'),
            icon: "description",
            onClick: handleExportDoc,
        },
    ]

    const components = <>
        {contentExportDoc}
    </>

    return {
        actions,
        components
    }
}
export default useExpedientMassiveActions;