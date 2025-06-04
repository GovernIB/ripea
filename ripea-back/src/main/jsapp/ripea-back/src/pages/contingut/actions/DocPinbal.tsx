import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const sortModelTipusDocument:any = [{field: 'nom',sort: 'asc'}]
const DocPinbalForm = () => {
    const {data} = useFormContext();
    // console.log("data", data)

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipusDocument"
                       namedQueries={[`PINBAL_DOC#${data?.expedient?.id}`]}
                       sortModel={sortModelTipusDocument}/>
        <GridFormField xs={12} name="titular"/>
        <GridFormField xs={12} name="consentiment" required/>
        <GridFormField xs={12} name="finalitat" type={"textarea"}/>

        <Grid item xs={12} sx={{ my: 1 }} hidden={!data?.codiServeiPinbal}>{data?.codiServeiPinbal}</Grid>

        {data?.codiServeiPinbal == "SVDCCAACPASWS01" && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="provincia" required/>
        </>}
        {data?.codiServeiPinbal == "SVDSCDDWS01" && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="provincia" required/>
            <GridFormField xs={12} name="dataConsulta" type={"date"}/>
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="consentimentTipusDiscapacitat" required/>
        </>}
        {data?.codiServeiPinbal == "SCDCPAJU" && <>
            <GridFormField xs={12} name="provincia" required/>
            <GridFormField xs={12} name="municipi"/>
        </>}
        {data?.codiServeiPinbal == "SVDSCTFNWS01" && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="dataConsulta" type={"date"}/>
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="numeroTitol"/>
        </>}
        {data?.codiServeiPinbal == "SVDCCAACPCWS01" && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="provincia" required/>
        </>}
        {data?.codiServeiPinbal == "SVDDELSEXWS01" && <>
            <GridFormField xs={12} name="nacionalitat" required/>
            <GridFormField xs={12} name="sexe"/>
            <GridFormField xs={12} name="paisNaixament" required/>
            <GridFormField xs={12} name="provinciaNaixament" required/>
            <GridFormField xs={12} name="poblacioNaixament"/>
            <GridFormField xs={12} name="municipiNaixament" required/>
            <GridFormField xs={12} name="nomPare"/>
            <GridFormField xs={12} name="nomMare"/>
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="telefon"/>
            <GridFormField xs={12} name="email"/>
        </>}
        {data?.codiServeiPinbal == "SCDHPAJU" && <>
            <GridFormField xs={12} name="provincia" required/>
            <GridFormField xs={12} name="municipi" required/>
            <GridFormField xs={12} name="nombreAnysHistoric"/>
        </>}
        {data?.codiServeiPinbal == "NIVRENTI" && <>
            <GridFormField xs={12} name="exercici"/>
        </>}
        {data?.codiServeiPinbal == "SVDDGPRESIDENCIALEGALDOCWS01" && <>
            <GridFormField xs={12} name="numeroSoporte"/>
            <GridFormField xs={12} name="tipusPassaport"/>
            <GridFormField xs={12} name="dataCaducidad" type={"date"}/>
            <GridFormField xs={12} name="nacionalitat" required/>
            <GridFormField xs={12} name="dataExpedicion" type={"date"}/>
        </>}
        {data?.codiServeiPinbal == "SVDRRCCNACIMIENTOWS01" && <>
            {/* contingut.pinbal.form.legend.dadesRegistrals */}
            <GridFormField xs={12} name="registreCivil"/>
            <GridFormField xs={12} name="tom"/>
            <GridFormField xs={12} name="pagina"/>

            {/* contingut.pinbal.form.legend.fetRegistral */}
            <GridFormField xs={12} name="dataRegistre"/>
            <GridFormField xs={12} name="municipiNaixament" required/>

            {/* contingut.pinbal.form.legend.dadesAdicionals */}
            <GridFormField xs={12} name="ausenciaSegundoApellido"/>
            <GridFormField xs={12} name="sexe"/>
            <GridFormField xs={12} name="nomPare"/>
            <GridFormField xs={12} name="nomMare"/>
        </>}
        {(data?.codiServeiPinbal == "SVDRRCCMATRIMONIOWS01" || data?.codiServeiPinbal == "SVDRRCCDEFUNCIONWS01") && <>
            {/* contingut.pinbal.form.legend.dadesRegistrals */}
            <GridFormField xs={12} name="registreCivil"/>
            <GridFormField xs={12} name="tom"/>
            <GridFormField xs={12} name="pagina"/>

            {/* contingut.pinbal.form.legend.fetRegistral */}
            <GridFormField xs={12} name="dataRegistre" type={"date"}/>
            <GridFormField xs={12} name="municipiRegistre"/>

            {/* contingut.pinbal.form.legend.naixement */}
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="municipiNaixament"/>

            {/* contingut.pinbal.form.legend.dadesAdicionals */}
            <GridFormField xs={12} name="ausenciaSegundoApellido"/>
            <GridFormField xs={12} name="sexe"/>
            <GridFormField xs={12} name="nomPare"/>
            <GridFormField xs={12} name="nomMare"/>
        </>}
        {data?.codiServeiPinbal == "SVDBECAWS01" && <>
            <GridFormField xs={12} name="curs"/>
        </>}
    </Grid>
}

const DocPinbal = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"NEW_DOC_PINBAL"}
        title={t('page.document.action.pinbal.title')}
        {...props}
    >
        <DocPinbalForm/>
    </FormActionDialog>
}

const useDocPinbal = (entity:any,refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id,{
            expedient: {id: entity?.id}
        })
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.pinbal.ok', {document: result?.nom}), 'success');
    }
    const onError = (error:any) :void => {
        temporalMessageShow(null, error.message, 'error');
    }

    return {
        handleShow,
        content: <DocPinbal apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useDocPinbal;