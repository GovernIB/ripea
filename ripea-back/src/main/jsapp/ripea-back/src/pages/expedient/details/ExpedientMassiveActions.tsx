import {useBaseAppContext, useResourceApiService} from "reactlib";
import useExportarDocuments from "../actions/ExportarDocuments.tsx";
import { useTranslation } from "react-i18next";
import {useUserSession} from "../../../components/Session.tsx";

const useMassiveActions = (refresh?: () => void)=> {
	
    const {temporalMessageShow} = useBaseAppContext();
	const { t } = useTranslation();
    const {artifactAction: apiAction, artifactReport: apiReport} = useResourceApiService('expedientResource');

    const massiveAction = (ids:any[], code:string, msg:string) => {
        return apiAction(undefined, {code :code, data:{ ids: ids, masivo: true }})
			.then(() => {
                refresh?.()
                temporalMessageShow(null, t(msg), 'info');
            })
            .catch((error) => {
                temporalMessageShow('Error', error?.message, 'error');
            })
    }
	
	const massiveReport = (ids:any[], code:string, msg:string, fileType:any) => {
	    return apiReport(undefined, {code :code, data:{ ids: ids, masivo: true }, fileType})
			.then(() => {
			    refresh?.()
			    temporalMessageShow(null, t(msg), 'info');
			})
			.catch((error) => {
			    temporalMessageShow('Error', error?.message, 'error');
			});		
	}

    const agafar 	= (ids: any[]): void => { massiveAction(ids, 'AGAFAR', 'page.expedient.results.actionBackgroundOk');}
	const alliberar = (ids: any[]): void => { massiveAction(ids, 'ALLIBERAR', 'page.expedient.results.actionBackgroundOk'); }
	const retornar 	= (ids: any[]): void => { massiveAction(ids, 'RETORNAR', 'page.expedient.results.actionBackgroundOk'); }
    const follow	= (ids: any[]): void => { massiveAction(ids, 'FOLLOW', 'page.expedient.results.actionBackgroundOk');}
    const unfollow 	= (ids: any[]): void => { massiveAction(ids, 'UNFOLLOW', 'page.expedient.results.actionBackgroundOk');}
	const esborrar 	= (ids: any[]): void => { massiveAction(ids, 'ESBORRAR', 'page.expedient.results.actionBackgroundOk');}
	
	const exportExcel 	= (ids: any[]): void => { massiveReport(ids, 'EXPORT_EXCEL', 'page.expedient.results.actionBackgroundOk', 'XLSX');}	
	const exportCsv		= (ids: any[]): void => { massiveReport(ids, 'EXPORT_CSV', 'page.expedient.results.actionBackgroundOk', 'CSV');}
	const exportIndexZip= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_ZIP', 'page.expedient.results.actionBackgroundOk', 'ZIP');}
	const exportIndexPdf= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_PDF', 'page.expedient.results.actionBackgroundOk', 'PDF');}
	const exportIndexXls= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INDEX_XLS', 'page.expedient.results.actionBackgroundOk', 'XLSX');}
	const exportEni		= (ids: any[]): void => { massiveReport(ids, 'EXPORT_ENI', 'page.expedient.results.actionBackgroundOk', 'ZIP');}
	const exportInside	= (ids: any[]): void => { massiveReport(ids, 'EXPORT_INSIDE', 'page.expedient.results.actionBackgroundOk', 'ZIP');}

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
			exportInside} = useMassiveActions(refresh);

    const {handleMassiveShow: handleExportDoc, content: contentExportDoc} = useExportarDocuments(refresh);

    const actions = [
        {
            title: "Agafar",
            icon: "lock",
            onClick: agafar,
        },
        {
            title: "Alliberar",
            icon: "lock_open",
			onClick: alliberar,
        },
		{
		    title: "Retornar",
		    icon: "undo",
			onClick: retornar,
		},		
        {
            title: "Seguir",
            icon: "person_add_alt1",
            onClick: follow,
        },
        {
            title: "Deixar de seguir",
            icon: "person_remove",
            onClick: unfollow,
        },
        {
            title: "Esborrar",
            icon: "delete",
			onClick: esborrar,
        },
        {
            title: "Exportar full de càlcul",
            icon: "download",
			onClick: exportExcel
        },
        {
            title: "Exportar CSV",
            icon: "download",
			onClick: exportCsv
        },
        {
            title: "Exportar índex ZIP",
            icon: "download",
			onClick: exportIndexZip
        },
        {
            title: "Exportar índex PDF",
            icon: "download",
			onClick: exportIndexPdf,
        },
		{
		    title: "Exportar índex Excel",
		    icon: "download",
			onClick: exportIndexXls,
            hidden: !(user?.sessionScope?.isExportacioExcelActiva),
		},
        {
            title: "Exportació ENI",
            icon: "download",
			onClick: exportEni
        },
		{
		    title: "Exportació INSIDE",
		    icon: "download",
			onClick: exportInside,
			hidden: !(user?.sessionScope?.isExportacioInsideActiva),
		},
        {
            title: "Exportar els documents dels expedients seleccionats...",
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