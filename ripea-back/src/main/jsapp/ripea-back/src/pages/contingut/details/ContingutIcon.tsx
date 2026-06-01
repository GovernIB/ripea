import {Grid, Icon} from "@mui/material";
import {useUserSession} from "../../../components/Session.tsx";
import {useTranslation} from "react-i18next";

const isInOptions = (value:string, ...options:string[]) => {
    return options.includes(value)
}

const ContingutIcon = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    return <Grid display={"flex"} alignItems={"center"}>
        {entity?.tipus=="DOCUMENT" && <DocumentIcon entity={entity}/>}
        {entity?.tipus=="CARPETA" && <CarpetaIcon entity={entity}/>}

        {entity?.valid == false && <Icon title={t('page.contingut.alert.valid')} color={"warning"}>warning</Icon>}
        {entity?.tipus!="CARPETA" && !entity?.metaNode &&
            <Icon title={t('page.contingut.alert.metaNode')}
                  color={"warning"}>question_mark</Icon>}

        {!entity?.arxiuUuid && !user?.sessionScope?.isCreacioCarpetesLogica &&
            <Icon title={t('page.contingut.alert.guardarPendent')} color={"error"}>warning</Icon>}

        {entity?.nom}

        {entity?.restringida && <Icon title={entity?.motiuRestriccio}>lock</Icon>}
    </Grid>
}
const DocumentIcon = (props:any) => {
    const { t } = useTranslation();
    const {entity} = props;

    const extension = entity?.fitxerExtension;
    return <>
        {/*{entity?.expedient && entity?.estat == 'OBERT' && <Icon>O</Icon>}*/}
        {/*{entity?.expedient && entity?.estat != 'OBERT' && <Icon>T</Icon>}*/}

        {isInOptions(extension, 'pdf') ?<Icon title={extension}>picture_as_pdf</Icon>
        :isInOptions(extension, 'doc', 'docx', 'odt') ?<Icon title={extension}>description</Icon>
        :isInOptions(extension, 'xls', 'xlsx', 'ods') ?<Icon title={extension}>description</Icon>
        :isInOptions(extension, 'zip') ?<Icon title={extension}>folder_zip</Icon>
        :isInOptions(extension, 'xsig', 'xml', 'json', 'html') ?<Icon title={extension}>code</Icon>
        :isInOptions(extension, 'jpeg', 'png', 'bmp', 'jpg') ?<Icon title={extension}>image</Icon>
        :isInOptions(extension, 'txt') ?<Icon title={extension}>description</Icon>
        :isInOptions(extension, 'mp3', 'wav') ?<Icon title={extension}>audio_file</Icon>
        :isInOptions(extension, 'mpeg', 'avi') ?<Icon title={extension}>video_file</Icon>
        :<Icon title={t('page.document.title')}>description</Icon>}

        {isInOptions(entity?.documentTipus, 'IMPORTAT') && <Icon title={t('page.document.alert.import')} color={"info"}>info</Icon>}
        {isInOptions(entity?.estat, 'REDACCIO') && <Icon sx={{marginBottom: '5px'}} title={t('page.document.alert.delete')} color={"warning"}>B</Icon>}
        {isInOptions(entity?.estat, 'CUSTODIAT', 'FIRMAT', 'ADJUNT_FIRMAT') && <Icon title={t('page.document.alert.firma')} color={"success"}>edit</Icon>}

        {entity?.gesDocOriginalId && <Icon title={t('page.document.alert.original')} color={"warning"}>file_copy</Icon>}

        {isInOptions(entity?.estat, 'FIRMAT') && entity?.gesDocFirmatId &&
            <Icon title={t('page.document.alert.custodiar')} color={"warning"}>warning</Icon>}

        {entity?.pendentMoverArxiu && !entity?.gesDocOriginalId &&
            <Icon title={t('page.document.alert.moure')} color={"warning"}>warning</Icon>}

        {!entity?.validacioFirmaCorrecte && <Icon title={entity?.validacioFirmaErrorMsg} color={"warning"}>warning</Icon>}
        {isInOptions(entity?.estat, 'DEFINITIU') && <Icon title={t('page.document.alert.definitiu')} color={"success"}>check_box</Icon>}

        {/*—------------------- INICI ICONES DE NOTIFICACIO —----------------------*/}
        {entity?.ambNotificacions && (!entity?.errorDarreraNotificacio
                ?<>
                    {isInOptions(entity?.estatDarreraNotificacio, 'PENDENT', 'REGISTRADA') &&
                        <Icon color={"info"} title={t(`enum.estatNotificacio.${entity?.estatDarreraNotificacio}`)}>mail</Icon>}

                    {isInOptions(entity?.estatDarreraNotificacio, 'ENVIADA_AMB_ERRORS', 'FINALITZADA_AMB_ERRORS') &&
                        <Icon color={"error"} title={t(`enum.estatNotificacio.${entity?.estatDarreraNotificacio}`)}>mail</Icon>}

                    {isInOptions(entity?.estatDarreraNotificacio, 'PROCESSADA', 'FINALITZADA') &&
                        <Icon color={"success"} title={t(`enum.estatNotificacio.${entity?.estatDarreraNotificacio}`)}>mail</Icon>}
                </>
                :<Icon color={"error"} title={t(`enum.estatNotificacio.${entity?.estatDarreraNotificacio}`)}>mail</Icon>
        )}
        {/*—------------------- FI ICONES DE NOTIFICACIO —----------------------*/}

        {isInOptions(entity?.estat, 'FIRMA_PENDENT_VIAFIRMA', 'FIRMA_PENDENT') &&
            <Icon title={t('page.document.alert.firmaPendent')} color={"warning"}>edit</Icon>}

        {isInOptions(entity?.estat, 'FIRMA_PARCIAL') &&
            <Icon title={t('page.document.alert.firmaParcial')}>edit</Icon>}

        {!isInOptions(entity?.estat, 'CUSTODIAT', 'REDACCIO' ) && entity?.errorEnviamentPortafirmes && !entity?.gesDocFirmatId &&
            <Icon title={t('page.document.alert.errorPortafirmes')} color={"error"}>edit</Icon>}
    </>
}
const CarpetaIcon = (_props:any) => {
    const { t } = useTranslation();

    return <>
        <Icon title={t('page.carpeta.title')}>folder</Icon>
    </>
}
export default ContingutIcon;