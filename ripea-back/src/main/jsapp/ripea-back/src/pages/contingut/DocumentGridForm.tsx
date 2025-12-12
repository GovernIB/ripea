import {useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useScanFinalitzatSession} from "../../components/SseExpedient.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {Alert, Grid, Icon} from "@mui/material";
import GridFormField, {FileFormField, GridButton} from "../../components/GridFormField.tsx";
import Iframe from "../../components/Iframe.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import TabComponent from "../../components/TabComponent.tsx";

const ScanerTabForm = () => {
    const { data, apiRef } = useFormContext();
    const { t } = useTranslation();
    const { onChange } = useScanFinalitzatSession();
    const { value: user } = useUserSession()

    onChange((value) => {
        if (user?.codi == value?.usuari) {
            apiRef?.current?.setFieldValue("scaned", true)
            apiRef?.current?.setFieldValue("adjunt", {
                name: value?.nomDocument,
                content: value?.contingut,
                contentType: value?.mimeType
            });
        }
    });

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid item xs={12} hidden={!data?.scaned}>
            <Alert severity={"success"}>{t('page.document.alert.scaned')}</Alert>
        </Grid>
        <GridFormField xs={12} name="digitalitzacioPerfil"/>
        <Grid item xs={12}>
            <Iframe src={data?.digitalitzacioProcesUrl} />
        </Grid>
    </Grid>
}
const FileTabForm = () => {
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <FileFormField xs={12} name="adjunt" required/>
        <GridFormField xs={6} name="hasFirma" hidden={!data.adjunt} disabled={data?.deteccioFirmaAutomaticaActiva && data?.documentFirmaTipus == "FIRMA_ADJUNTA"} />
        <GridFormField xs={6} name="documentFirmaTipus" hidden={!data?.adjunt || !data?.hasFirma} disabled={data?.deteccioFirmaAutomaticaActiva} required/>
        <FileFormField xs={12} name="firmaAdjunt" hidden={!data?.hasFirma || data?.documentFirmaTipus != "FIRMA_SEPARADA"} required/>
		<GridFormField xs={6} name="firmaParcial" hidden={!data?.hasFirma} />
    </Grid>
}

const DocumentsGridForm = () => {
    const { t } = useTranslation();
    const { data, apiRef, id } = useFormContext();
	const { value: user } = useUserSession();
    const { artifactAction: apiAction } = useResourceApiService('documentResource');
	
	const isPermesModificarCustodiatsVar = () => {
	    return user?.sessionScope?.isPermesModificarCustodiats && isInOptions(data?.estat, 'CUSTODIAT', 'FIRMAT', 'FIRMA_PARCIAL', 'DEFINITIU')
	}
	
	const isInOptions = (value:string, ...options:string[]) => {
	    return options.includes(value)
	}
	
    const actualizarDatos = () => {
        if (data?.adjunt && data.pluginSummarizeActiu) {
            apiAction(undefined, { code: "RESUM_IA", data: { adjunt: data?.adjunt } })
                .then((result) => {
                    if (result) {
                        apiRef?.current?.setFieldValue("nom", result.titol)
                        apiRef?.current?.setFieldValue("descripcio", result.resum)
                    }
                });
        }
    };

    const metaDocumentFilter: string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("actiu", true),
    );
	
	const carpetaFilter: string = builder.and(
	    builder.eq("expedient.id", data?.expedient?.id),
	    builder.eq("esborrat", 0),
	);

    const tabs = [
        {
            value: "file",
            label: t('page.document.tabs.file'),
            content: <FileTabForm />,
        },
        {
            value: "scaner",
            label: t('page.document.tabs.scaner'),
            content: !data?.funcionariHabilitatDigitalib
                ? <ScanerTabForm />
                : <Alert severity={"warning"} sx={{ width: '100%' }}>{t('page.document.alert.funcionariHabilitatDigitalib')}</Alert>,
        }
    ];

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaDocument"
                       namedQueries={
                           apiRef?.current?.getId()
                               ? [`UPDATE_DOC#${apiRef?.current?.getId()}`]
                               : [`CREATE_NEW_DOC#${data?.expedient?.id}`]
                       }
                       filter={metaDocumentFilter} />
        <GridFormField xs={data.pluginSummarizeActiu ? 11 : 12} name="nom" />
        <GridButton xs={1} title={t('page.document.detall.summarize')}
                    onClick={actualizarDatos}
                    disabled={!data?.adjunt}
                    hidden={!data.pluginSummarizeActiu}>
            <Icon>assistant</Icon>IA
        </GridButton>
        <GridFormField xs={12} name="descripcio" type={"textarea"} />
        <GridFormField xs={12} name="dataCaptura" type={"date"} disabled required />
        <GridFormField xs={12} name="ntiOrigen" required />
        <GridFormField xs={12} name="ntiEstadoElaboracion" required />
		<GridFormField xs={12} name="ntiIdDocumentoOrigen"
		               componentProps={{ title: t('page.document.detall.documentOrigenFormat') }}
		               required={!!data?.ntiEstadoElaboracion}
		               hidden={!data?.ntiEstadoElaboracion
		                   || data?.ntiEstadoElaboracion == 'EE01'
		                   || data?.ntiEstadoElaboracion == 'EE99'}/>		
        {id==null &&
            <GridFormField xs={12} name="carpeta"
                        filter={carpetaFilter}
                        hidden={!user?.sessionScope?.isCreacioCarpetesActiva}/>
        }
		{!isPermesModificarCustodiatsVar() &&	
	        <Grid item xs={12}>
	            <TabComponent
	                tabs={tabs}
	                variant="scrollable"
	            />
	        </Grid>
		}
    </Grid>
}
export default DocumentsGridForm;