import {useTranslation} from "react-i18next";
import {useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import {useState} from "react";

const InteressatsGridFormFilter = () => {
    const {data} = useFormContext()

    return <>
        <GridFormField size={{xs: 12, sm: 6}} name="nivell"/>
        <GridFormField size={{xs: 12, sm: 6}} name="comunitatAutonoma"/>
        <GridFormField size={{xs: 12, sm: 6}} name="provincia" requestParams={{comunitatAutonoma: data?.comunitatAutonoma}}/>
        <GridFormField size={{xs: 12, sm: 6}} name="municipiUo" requestParams={{provincia: data?.provincia}}/>
        <GridFormField size={{xs: 12, sm: 6}} name="nif"/>
        <GridFormField size={{xs: 12, sm: 6}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 6}} name="unitatArrel" type={"checkbox"}/>
    </>
}

export const InteressatsGridForm = () => {
    const { t } = useTranslation();
    const {data} = useFormContext()
    const [value, setValue] = useState<object>({})
    const filterButtons = [
        {
            value: 'search',
            text: t('common.actualize'),
            icon: 'system_update_alt',
            componentProps: {
                variant: "contained",
            },
        },
    ]

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="tipus" required/>

        {!!data?.filter &&
            <Grid size={12}>
                <StyledMuiFilter
                    resourceName={"interessatResource"}
                    code={"UNITAT_ORGANITZATIVA_FILTER"}
                    sessionKey={null}
                    springFilterBuilder={(data:any)=> setValue(data)}
                    onSpringFilterChange={() => {}}
                    buttons={filterButtons}
                    disableGridBinding={true} // <-- evitar que la búsqueda actualice el grid
                >
                    <InteressatsGridFormFilter/>
                </StyledMuiFilter>
            </Grid>
        }

        {data?.tipus === 'InteressatAdministracioEntity' && <GridFormField size={11} name="organCodi"
                                                                           requestParams={{...(value ?? {}), isInteressatAdministracio: data?.tipus == 'InteressatAdministracioEntity'}}
                                                                           required autocomplete/>}
        <GridButtonField size={1} name={"filter"} icon={"search"} hidden={data?.tipus != 'InteressatAdministracioEntity'}/>

        <GridFormField name="documentTipus"
                       disabled={data?.tipus != 'InteressatPersonaFisicaEntity'}
                       readOnly={data?.tipus != 'InteressatPersonaFisicaEntity'}
                       required/>
        <GridFormField name="documentNum"
                       debounce
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}
                       required={data?.tipus != 'InteressatAdministracioEntity'}/>

        <GridFormField name={"raoSocial"}
                       hidden={data?.tipus != 'InteressatPersonaJuridicaEntity'}
                       required/>

        {data?.tipus == 'InteressatPersonaFisicaEntity' && <>
            <GridFormField name="nom" required/>
            <GridFormField size={6} name="llinatge1" required/>
            <GridFormField size={6} name="llinatge2"/>
        </>}

        <GridFormField size={6} name="pais"
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'} autocomplete/>
        <GridFormField size={6} name="provincia" requestParams={{pais: data?.pais}}
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'} autocomplete/>
        <GridFormField size={6} name="municipi" requestParams={{provincia: data?.provincia}}
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'} autocomplete/>
        <GridFormField size={6} name="codiPostal"
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>

        <GridFormField name="adressaTipus" required
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>

        {data?.adressaTipus != "SENSE_NORMALITZAR" && data?.tipus != 'InteressatAdministracioEntity' ? <>
            <GridFormField size={4} name="adressaTipusVia" required/>
            <GridFormField size={8} name="adresa" required/>
            <GridFormField size={4} name="adresaApartatCorreus"/>
            <GridFormField size={4} name="adressaNumCasa" required/>
            <GridFormField size={4} name="adresaPuntKm"/>
            <GridFormField size={4} name="adresaPortal"/>
            <GridFormField size={4} name="adresaEscala"/>
            <GridFormField size={4} name="adresaPlanta"/>
            <GridFormField size={4} name="adresaPorta"/>
            <GridFormField size={4} name="adresaBloc"/>
            <GridFormField size={4} name="adresaQualificador"/>
            <GridFormField name="adresaPoblacio"/>
            <GridFormField name="adresaComplement"/>
        </> : <>
            <GridFormField name="adresa" type={"textarea"}
                           disabled={data?.tipus == 'InteressatAdministracioEntity'}
                           readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>
        </>}

        <GridFormField size={6} name="email" required={data?.entregaDeh}/>
        <GridFormField size={6} name="telefon"/>
        <GridFormField name="observacions" type={"textarea"}/>
        <GridFormField name="preferenciaIdioma" required/>

        <GridFormField size={6} name="entregaDeh"/>
        <GridFormField size={6} name="entregaDehObligat" hidden={!data?.entregaDeh}/>
    </Grid>
}