import {Grid, Icon} from "@mui/material";
import {useUserSession} from "../../../components/Session.tsx";

const isInOptions = (value:string, ...options:string[]) => {
    return options.includes(value)
}

const ContingutIcon = (props:any) => {
    const {entity} = props;

    return <Grid display={"flex"} alignItems={"center"}>
        {entity?.tipus=="DOCUMENT" && <DocumentIcon entity={entity}/>}
        {entity?.tipus=="CARPETA" && <CarpetaIcon entity={entity}/>}

        {entity?.valid == false && <Icon title={"Este contenido tiene errores de validación."} color={"warning"}>warning</Icon>}
        {entity?.tipus!="CARPETA" && !entity?.metaNode &&
            <Icon title={"Este documento carece de un tipo de documento"}
                  color={"warning"}>warning</Icon>}

        {entity?.nom}
    </Grid>
}
const DocumentIcon = (props:any) => {
    const {entity} = props;
    const { value: user } = useUserSession();

    // console.log(entity)
    const extension = entity?.fitxerExtension;
    return <>
        {/*{entity?.expedient && entity?.estat == 'OBERT' && <Icon>O</Icon>}*/}
        {/*{entity?.expedient && entity?.estat != 'OBERT' && <Icon>T</Icon>}*/}

        {isInOptions(extension, 'pdf') && <Icon>picture_as_pdf</Icon>}
        {isInOptions(extension, 'doc', 'docx', 'odt') && <Icon>description</Icon>}
        {isInOptions(extension, 'xls', 'xlsx', 'ods') && <Icon>description</Icon>}
        {isInOptions(extension, 'zip') && <Icon>folder_zip</Icon>}
        {isInOptions(extension, 'xsig', 'xml', 'json', 'html') && <Icon>folder_code</Icon>}
        {isInOptions(extension, 'jpeg', 'png', 'bmp', 'jpg') && <Icon>image</Icon>}
        {isInOptions(extension, 'txt') && <Icon>description</Icon>}
        {isInOptions(extension, 'mp3', 'wav') && <Icon>audio_file</Icon>}
        {isInOptions(extension, 'mpeg', 'avi') && <Icon>video_file</Icon>}

        {isInOptions(entity?.documentTipus, 'IMPORTAT') && <Icon title={"Documento importado"}>info</Icon>}
        {isInOptions(entity?.estat, 'REDACCIO') && <Icon title={"Documento borrador"}>B</Icon>}
        {isInOptions(entity?.estat, 'CUSTODIAT', 'FIRMAT', 'ADJUNT_FIRMAT') && <Icon title={"Documento firmado"} color={"success"}>edit</Icon>}

        {entity?.gesDocOriginalId &&
            <Icon title={"Este documento contenía firmas inválidas y se ha clonado y firmado en servidor para poder guardarlo en el Archivo Digital. Se puede descargar el original desde el menú de acciones."}
                  color={"warning"}>file_copy</Icon>}

        {isInOptions(entity?.estat, 'FIRMAT') && entity?.gesDocFirmatId &&
            <Icon title={"Pendiente de custodiar documento firmado de portafrimes"}
                  color={"warning"}>warning</Icon>}

        {entity?.pendentMoverArxiu && !entity?.gesDocOriginalId &&
            <Icon title={"El documento de la anotación está pendiente de mover a la serie documental del procedimiento"}
                  color={"warning"}>warning</Icon>}

        {!entity?.validacioFirmaCorrecte &&//contingut.info.error.valid
            <Icon title={entity?.validacioFirmaErrorMsg}
                  color={"warning"}>warning</Icon>}

        {isInOptions(entity?.estat, 'DEFINITIU') &&
            <Icon title={"Document definitiu"}
                  color={"success"}>check_box</Icon>}

        {/*—------------------- INICI ICONES DE NOTIFICACIO —----------------------*/}
        {entity?.ambNotificacions && !entity?.errorDarreraNotificacio && isInOptions(entity?.estatDarreraNotificacio, 'PENDENT', 'REGISTRADA') &&
            <Icon color={"warning"}>mail</Icon>}

        {entity?.ambNotificacions && !entity?.errorDarreraNotificacio && isInOptions(entity?.estatDarreraNotificacio, 'ENVIADA_AMB_ERRORS', 'FINALITZADA_AMB_ERRORS') &&
            <Icon color={"error"}>mail</Icon>}

        {entity?.ambNotificacions && !entity?.errorDarreraNotificacio && isInOptions(entity?.estatDarreraNotificacio, 'PROCESSADA', 'FINALITZADA') &&
            <Icon color={"info"}>mail</Icon>}

        {entity?.ambNotificacions && entity?.errorDarreraNotificacio &&
            <Icon color={"success"}>mail</Icon>}
        {/*—------------------- FI ICONES DE NOTIFICACIO —----------------------*/}

        {isInOptions(entity?.estat, 'FIRMA_PENDENT_VIAFIRMA', 'FIRMA_PENDENT') &&
            <Icon title={"Pendiente de firmar"}
                  color={"warning"}>edit</Icon>}

        {isInOptions(entity?.estat, 'FIRMA_PARCIAL') &&
            <Icon title={"Firmado parcialmente"}
                  >edit</Icon>}

        {!isInOptions(entity?.estat, 'CUSTODIAT', 'REDACCIO' ) && entity?.errorEnviamentPortafirmes && !entity?.gesDocFirmatId &&
            <Icon title={"Error al enviar al portafirmas"}
                  color={"error"}>edit</Icon>}

        {!entity?.arxiuUuid && !user?.sessionScope?.isCreacioCarpetesLogica &&
            <Icon title={"Pendiente de guardar en archivo"}
                  color={"warning"}>warning</Icon>}
    </>
}
const CarpetaIcon = (props:any) => {
    const {entity} = props;

    return <>
        <Icon>folder</Icon>
    </>
}
export default ContingutIcon;