import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useExportarDocumentsMassive} from "../actions/ExportarDocuments.tsx";
import { useTranslation } from "react-i18next";
import {Divider} from "@mui/material";
import useImportarDocumentMassive from "../actions/ImportarDocumentMassive.tsx";
import {useExportarExpedientsMassive} from "../actions/ExportarExpedientsMassive.tsx";

export const useMassiveActions = (refresh?: () => void)=> {
	
    const {temporalMessageShow} = useBaseAppContext();
	const { t } = useTranslation();
    const {artifactAction: apiAction} = useResourceApiService('expedientResource');

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

    const agafar 	= (ids: any[]): void => { massiveAction(ids, 'AGAFAR', t('page.expedient.results.actionBackgroundOk'));}
	const alliberar = (ids: any[]): void => { massiveAction(ids, 'ALLIBERAR', t('page.expedient.results.actionBackgroundOk'));}
	const retornar 	= (ids: any[]): void => { massiveAction(ids, 'RETORNAR', t('page.expedient.results.actionBackgroundOk'));}
    const follow	= (ids: any[]): void => { massiveAction(ids, 'FOLLOW', t('page.expedient.results.actionBackgroundOk'));}
    const unfollow 	= (ids: any[]): void => { massiveAction(ids, 'UNFOLLOW', t('page.expedient.results.actionBackgroundOk'));}
	const esborrar 	= (ids: any[]): void => { massiveAction(ids, 'ESBORRAR', t('page.expedient.results.actionBackgroundOk'));}
    const guardarArxiu = (ids: any[]): void => { massiveAction(ids, 'GUARDAR_ARXIU', t('page.expedient.results.actionBackgroundOk'));}
	const syncArxiu = (ids: any[]): void => { massiveAction(ids, 'SYNC_ARXIU', t('page.expedient.results.actionBackgroundOk'));}

    return {
        guardarArxiu,
        syncArxiu,
        agafar,
		alliberar,
		retornar,
        follow,
        unfollow,
		esborrar,	
    }
}

const useExpedientMassiveActions = (refresh?: () => void)=> {
    const { t } = useTranslation();

    const {	agafar,
			alliberar,
			retornar,
	        follow,
	        unfollow,
			esborrar,
    } = useMassiveActions(refresh);

    const {handleMassiveShow: handleExportDoc, content: contentExportDoc} = useExportarDocumentsMassive(refresh);
    const {handleShow: handleImpDocMass, content: contentImpDocMass} = useImportarDocumentMassive(refresh);
    const {handleShow: handleExportMass, content: contentExportMass} = useExportarExpedientsMassive(refresh);

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
            label: t('page.expedient.action.exportMass.label'),
            icon: "file_download",
            showInMenu: true,
			onClick: handleExportMass,
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
        {contentExportMass}
    </>

    return {
        actions,
        components
    }
}
export default useExpedientMassiveActions;