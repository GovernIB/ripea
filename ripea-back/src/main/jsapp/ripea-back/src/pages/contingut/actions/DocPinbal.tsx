import {useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
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
        <GridFormField name="tipusDocument"
                       namedQueries={[`PINBAL_DOC#${data?.expedient?.id}`]}
                       sortModel={sortModelTipusDocument}/>

        <GridFormField size={9.5} name="titular" filter={titularFilter}/>
        <GridButton
            icon={'add'}
            size={2.5}
            onClick={()=> {
                create({expedient: data?.expedient}, onCreateInteressat)
            }}
        >
            {t('page.interessat.action.new.label')}
        </GridButton>
        {content}

        <GridFormField name="consentiment" required/>
        <GridFormField name="finalitat" type={"textarea"}/>

        {/*<Grid size={12} sx={{ my: 1 }} hidden={!data?.codiServeiPinbal}>{data?.codiServeiPinbal}</Grid>*/}
        { values.includes(data?.codiServeiPinbal) &&
            <Grid size={12} sx={{ my: 1, borderBottom: '1px solid black' }} hidden={!data?.codiServeiPinbal}>
                {t('page.document.detall.dataEspecific')}
            </Grid>
        }

        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDCCAACPASWS01 && <>
            <GridFormField name="comunitatAutonoma" required/>
            <GridFormField name="provincia" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDSCDDWS01 && <>
            <GridFormField name="comunitatAutonoma" required/>
            <GridFormField name="provincia" required/>
            <GridFormField name="dataConsulta" type={"date"}/>
            <GridFormField name="dataNaixement" type={"date"}/>
            <GridFormField name="consentimentTipusDiscapacitat" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SCDCPAJU && <>
            <GridFormField name="provincia" required/>
            <GridFormField name="municipi" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDSCTFNWS01 && <>
            <GridFormField name="comunitatAutonoma" required/>
            <GridFormField name="dataConsulta" type={"date"}/>
            <GridFormField name="dataNaixement" type={"date"}/>
            <GridFormField name="numeroTitol"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDCCAACPCWS01 && <>
            <GridFormField name="comunitatAutonoma" required/>
            <GridFormField name="provincia" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDDELSEXWS01 && <>
            <GridFormField name="nacionalitat" required/>
            <GridFormField name="sexe"/>
            <GridFormField name="paisNaixament" required/>
            <GridFormField name="provinciaNaixament" required/>
            <GridFormField name="poblacioNaixament"/>
            <GridFormField name="municipiNaixament" required/>
            <GridFormField name="nomPare"/>
            <GridFormField name="nomMare"/>
            <GridFormField name="dataNaixement" type={"date"} required/>
            <GridFormField name="telefon"/>
            <GridFormField name="email"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SCDHPAJU && <>
            <GridFormField name="provincia" required/>
            <GridFormField name="municipi" required/>
            <GridFormField name="nombreAnysHistoric"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.NIVRENTI && <>
            <GridFormField name="exercici" required/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDDGPRESIDENCIALEGALDOCWS01 && <>
            <GridFormField name="numeroSoporte"/>
            <GridFormField name="tipusPassaport"/>
            <GridFormField name="dataCaducidad" type={"date"}/>
            <GridFormField name="nacionalitat" required/>
            <GridFormField name="dataExpedicion" type={"date"}/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDRRCCDEFUNCIONWS01 && <>
            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesRegistrals')}</Grid>
            <GridFormField name="registreCivil"/>
            <GridFormField name="tom"/>
            <GridFormField name="pagina"/>

            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.fetRegistral')}</Grid>
            <GridFormField name="dataRegistre" type={"date"}/>
            <GridFormField name="municipiRegistre"/>

            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.naixement')}</Grid>
            <GridFormField name="dataNaixement" type={"date"}/>
            <GridFormField name="municipiNaixament"/>

            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesAdicionals')}</Grid>
            <GridFormField name="ausenciaSegundoApellido"/>
            <GridFormField name="sexe"/>
            <GridFormField name="nomPare"/>
            <GridFormField name="nomMare"/>
        </>}
        {(data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDRRCCNACIMIENTOWS01 ||data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDRRCCMATRIMONIOWS01) && <>
            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesRegistrals')}</Grid>
            <GridFormField name="registreCivil" required/>
            <GridFormField name="tom" required/>
            <GridFormField name="pagina" required/>

            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.fetRegistral')}</Grid>
            <GridFormField name="dataRegistre" type={"date"} required/>
            <GridFormField name="municipiRegistre"/>

            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.naixement')}</Grid>
            <GridFormField name="dataNaixement" type={"date"}/>
            <GridFormField name="municipiNaixament"/>

            <Grid size={12} sx={{ mt: 1, ml:1, borderBottom: '1px solid grey' }} hidden={!data?.codiServeiPinbal}>{t('page.document.detall.dadesAdicionals')}</Grid>
            <GridFormField name="ausenciaSegundoApellido"/>
            <GridFormField name="sexe"/>
            <GridFormField name="nomPare"/>
            <GridFormField name="nomMare"/>
        </>}
        {data?.codiServeiPinbal == CodiServeiPinbalEnum.SVDBECAWS01 && <>
            <GridFormField name="curs" required/>
        </>}
    </Grid>
}

const DocPinbal = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"NEW_DOC_PINBAL"}
        title={t('page.document.action.pinbal.title')}
        formDialogButtons={[
            {icon: 'description', text: t('page.document.action.pinbal.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <DocPinbalForm/>
    </FormActionDialog>
}

const useDocPinbal = (entity:any,refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
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