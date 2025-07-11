import {MuiFormDialogApi, useBaseAppContext, useFormContext} from "reactlib";
import {Grid, Icon} from "@mui/material";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import {useRef} from "react";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import useCreate from "../../interessats/actions/Create.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const values = [
    "SVDCCAACPASWS01",
    "SVDSCDDWS01",
    "SCDCPAJU",
    "SVDSCTFNWS01",
    "SVDCCAACPCWS01",
    "SVDDELSEXWS01",
    "SCDHPAJU",
    "NIVRENTI",
    "SVDDGPRESIDENCIALEGALDOCWS01",
    "SVDRRCCNACIMIENTOWS01",
    "SVDRRCCMATRIMONIOWS01",
    "SVDRRCCDEFUNCIONWS01",
    "SVDBECAWS01",
];
const CodiServeiPinbalEnum = Object.fromEntries(values.map(v => [v, v]));

const sortModelTipusDocument:any = [{field: 'nom',sort: 'asc'}]
const DocPinbalForm = () => {
    const {data, apiRef: formApiRef} = useFormContext();
    const { t } = useTranslation()

    const { create, content } = useCreate()
    const onCreateInteressat = (result?:any)=> {
        formApiRef?.current?.setFieldValue('titular', {
            id: result?.id,
            description: result?.codiNom
        })
    }

    const titularFilter: string = builder.and(
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq('esRepresentant', false),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipusDocument"
                       namedQueries={[`PINBAL_DOC#${data?.expedient?.id}`]}
                       sortModel={sortModelTipusDocument}/>

        <GridFormField xs={9.5} name="titular" filter={titularFilter}/>
        <GridButton
            xs={2.5}
            onClick={()=> {
                create({expedient: data?.expedient}, onCreateInteressat)
            }}
        >
            <Icon>add</Icon>{t('page.interessat.action.new.label')}
        </GridButton>
        {content}

        <GridFormField xs={12} name="consentiment" required/>
        <GridFormField xs={12} name="finalitat" type={"textarea"}/>

        {/*<Grid item xs={12} sx={{ my: 1 }} hidden={!data?.codiServeiPinbal}>{data?.codiServeiPinbal}</Grid>*/}
        { values.includes(data?.codiServeiPinbal) &&
            <Grid item xs={12} sx={{ my: 1, borderBottom: '1px solid black' }} hidden={!data?.codiServeiPinbal}>
                {t('page.document.detall.dataEspecific')}
            </Grid>
        }

        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDCCAACPASWS01 && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="provincia" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDSCDDWS01 && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="provincia" required/>
            <GridFormField xs={12} name="dataConsulta" type={"date"}/>
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="consentimentTipusDiscapacitat" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SCDCPAJU && <>
            <GridFormField xs={12} name="provincia" required/>
            <GridFormField xs={12} name="municipi" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDSCTFNWS01 && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="dataConsulta" type={"date"}/>
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="numeroTitol"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDCCAACPCWS01 && <>
            <GridFormField xs={12} name="comunitatAutonoma" required/>
            <GridFormField xs={12} name="provincia" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDDELSEXWS01 && <>
            <GridFormField xs={12} name="nacionalitat" required/>
            <GridFormField xs={12} name="sexe"/>
            <GridFormField xs={12} name="paisNaixament" required/>
            <GridFormField xs={12} name="provinciaNaixament" required/>
            <GridFormField xs={12} name="poblacioNaixament"/>
            <GridFormField xs={12} name="municipiNaixament" required/>
            <GridFormField xs={12} name="nomPare"/>
            <GridFormField xs={12} name="nomMare"/>
            <GridFormField xs={12} name="dataNaixement" type={"date"} required/>
            <GridFormField xs={12} name="telefon"/>
            <GridFormField xs={12} name="email"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SCDHPAJU && <>
            <GridFormField xs={12} name="provincia" required/>
            <GridFormField xs={12} name="municipi" required/>
            <GridFormField xs={12} name="nombreAnysHistoric"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.NIVRENTI && <>
            <GridFormField xs={12} name="exercici" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDDGPRESIDENCIALEGALDOCWS01 && <>
            <GridFormField xs={12} name="numeroSoporte"/>
            <GridFormField xs={12} name="tipusPassaport"/>
            <GridFormField xs={12} name="dataCaducidad" type={"date"}/>
            <GridFormField xs={12} name="nacionalitat" required/>
            <GridFormField xs={12} name="dataExpedicion" type={"date"}/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDRRCCDEFUNCIONWS01 && <>
            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesRegistrals')}</Grid>
            <GridFormField xs={12} name="registreCivil"/>
            <GridFormField xs={12} name="tom"/>
            <GridFormField xs={12} name="pagina"/>

            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.fetRegistral')}</Grid>
            <GridFormField xs={12} name="dataRegistre" type={"date"}/>
            <GridFormField xs={12} name="municipiRegistre"/>

            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.naixement')}</Grid>
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="municipiNaixament"/>

            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesAdicionals')}</Grid>
            <GridFormField xs={12} name="ausenciaSegundoApellido"/>
            <GridFormField xs={12} name="sexe"/>
            <GridFormField xs={12} name="nomPare"/>
            <GridFormField xs={12} name="nomMare"/>
        </>}
        {(data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDRRCCNACIMIENTOWS01 ||data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDRRCCMATRIMONIOWS01) && <>
            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesRegistrals')}</Grid>
            <GridFormField xs={12} name="registreCivil" required/>
            <GridFormField xs={12} name="tom" required/>
            <GridFormField xs={12} name="pagina" required/>

            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.fetRegistral')}</Grid>
            <GridFormField xs={12} name="dataRegistre" type={"date"} required/>
            <GridFormField xs={12} name="municipiRegistre"/>

            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.naixement')}</Grid>
            <GridFormField xs={12} name="dataNaixement" type={"date"}/>
            <GridFormField xs={12} name="municipiNaixament"/>

            <Grid item xs={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesAdicionals')}</Grid>
            <GridFormField xs={12} name="ausenciaSegundoApellido"/>
            <GridFormField xs={12} name="sexe"/>
            <GridFormField xs={12} name="nomPare"/>
            <GridFormField xs={12} name="nomMare"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDBECAWS01 && <>
            <GridFormField xs={12} name="curs" required/>
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

    const handleShow = () :void => {
        apiRef.current?.show?.(undefined,{
            expedient: {id: entity?.id}
        })
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.pinbal.ok', {codiServeiPinbal: result?.codiServeiPinbal}), 'success');
    }

    return {
        handleShow,
        content: <DocPinbal apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useDocPinbal;