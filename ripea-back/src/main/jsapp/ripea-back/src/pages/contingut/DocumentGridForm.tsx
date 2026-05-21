import {useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useScanFinalitzatSession} from "../../components/SseExpedient.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {Alert, Grid} from "@mui/material";
import GridFormField, {FileFormField, GridButton} from "../../components/GridFormField.tsx";
import Iframe from "../../components/Iframe.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import TabComponent from "../../components/TabComponent.tsx";
import {useCallback, useEffect, useMemo} from "react";

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
        <Grid size={12} hidden={!data?.scaned}>
            <Alert severity={"success"}>{t('page.document.alert.scaned')}</Alert>
        </Grid>
        <GridFormField name="digitalitzacioPerfil"/>
        <Grid size={12}>
            <Iframe src={data?.digitalitzacioProcesUrl} />
        </Grid>
    </Grid>
}
const FileTabForm = () => {
    const { data, apiRef } = useFormContext();

    const onChange = useCallback(() => {
        if (data?.deteccioFirmaAutomaticaActiva) {
            apiRef?.current?.setFieldValue("disabled", true)
        }
    }, [apiRef, data?.deteccioFirmaAutomaticaActiva])
    const componentProps = useMemo(() => (
        (data?.disabled) ? {helperText: "Detectant firmes..."} : {}
    ), [data?.disabled])

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <FileFormField size={12} name="adjunt"
                       onChange={onChange}
                       componentProps={componentProps}
                       readOnly={data?.disabled}
                       required/>
        <Grid size={12} hidden={data?.validacioFirmaCorrecte !== false || !data?.validacioFirmaErrorMsg}>
            <Alert severity={"warning"}>{data?.validacioFirmaErrorMsg}</Alert>
        </Grid>
        <GridFormField size={6} name="hasFirma" hidden={!data.adjunt} disabled={data?.deteccioFirmaAutomaticaActiva && data?.documentFirmaTipus == "FIRMA_ADJUNTA"} />
        <GridFormField size={6} name="documentFirmaTipus" hidden={!data?.adjunt || !data?.hasFirma} disabled={data?.deteccioFirmaAutomaticaActiva} required/>
        <FileFormField name="firmaAdjunt" hidden={!data?.hasFirma || data?.documentFirmaTipus != "FIRMA_SEPARADA"} required/>
		<GridFormField size={6} name="firmaParcial" hidden={!data?.hasFirma} />
    </Grid>
}

const DocumentsGridForm = ({ setDisabled }:any) => {
    const { t } = useTranslation();
    const { data, apiRef, id } = useFormContext();
	const { value: user } = useUserSession();
    const { artifactAction: apiAction } = useResourceApiService('documentResource');

    useEffect(() => {
        if (data?.deteccioFirmaAutomaticaActiva) {
            setDisabled?.(data?.disabled);
        }
    }, [setDisabled, data?.disabled, data?.deteccioFirmaAutomaticaActiva]);
    useEffect(() => {
        if (data?.deteccioFirmaAutomaticaActiva) {
            apiRef?.current?.setFieldValue("disabled", data?.adjunt && !data?.fitxerNom)
        }
    }, [apiRef, data?.adjunt, data?.fitxerNom, data?.deteccioFirmaAutomaticaActiva]);

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
	
	const carpetaFilter: string = useMemo(() => (
	    builder.and(
	        builder.eq("expedient.id", data?.expedient?.id),
	        builder.eq("esborrat", 0),
	    )
	), [data?.expedient?.id]);

    const tabs = [
        {
            value: "file",
            label: t('page.document.tabs.file'),
            content: <FileTabForm/>,
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
        <GridFormField name="metaDocument"
                       namedQueries={
                           apiRef?.current?.getId()
                               ? [`UPDATE_DOC#${apiRef?.current?.getId()}`]
                               : [`CREATE_NEW_DOC#${data?.expedient?.id}`]
                       }
                       filter={metaDocumentFilter} />
        <GridFormField size={data.pluginSummarizeActiu ? 11 : 12} name="nom" />
        <GridButton size={1} title={t('page.document.detall.summarize')}
                    icon={'assistant'}
                    onClick={actualizarDatos}
                    disabled={!data?.adjunt}
                    hidden={!data.pluginSummarizeActiu}>
            IA
        </GridButton>
        <GridFormField name="descripcio" type={"textarea"} />
        <GridFormField name="dataCaptura" type={"date"} disabled required />
        <GridFormField name="ntiOrigen" required />
        <GridFormField name="ntiEstadoElaboracion" required />
		<GridFormField name="ntiIdDocumentoOrigen"
		               componentProps={{ title: t('page.document.detall.documentOrigenFormat') }}
		               required={!!data?.ntiEstadoElaboracion}
		               hidden={!data?.ntiEstadoElaboracion
		                   || data?.ntiEstadoElaboracion == 'EE01'
		                   || data?.ntiEstadoElaboracion == 'EE99'}/>		
        {id == null && (user?.sessionScope?.isCreacioCarpetesActiva || data?.contingutScopeDestiCarpeta) && (
            <GridFormField
                name="carpeta"
                filter={carpetaFilter}
                readOnly={!user?.sessionScope?.isCreacioCarpetesActiva}
            />
        )}
		{!isPermesModificarCustodiatsVar() &&	
	        <Grid size={12}>
	            <TabComponent
	                tabs={tabs}
	                variant="scrollable"
	            />
	        </Grid>
		}
    </Grid>
}
export default DocumentsGridForm;