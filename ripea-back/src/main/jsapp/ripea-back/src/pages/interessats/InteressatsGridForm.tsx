import {useTranslation} from "react-i18next";
import {useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import {useState} from "react";

const InteressatsGridFormFilter = () => {
    const {data} = useFormContext()

    return <>
        <GridFormField xs={6} name="nivell"/>
        <GridFormField xs={6} name="comunitatAutonoma"/>
        <GridFormField xs={6} name="provincia" requestParams={{comunitatAutonoma: data?.comunitatAutonoma}}/>
        <GridFormField xs={6} name="municipiUo" requestParams={{provincia: data?.provincia}}/>
        <GridFormField xs={6} name="nif"/>
        <GridFormField xs={6} name="nom"/>
        <GridFormField xs={9.6} name="unitatArrel" type={"checkbox"}/>
    </>
}

export const InteressatsGridForm = () => {
    const { t } = useTranslation();
    const {data} = useFormContext()
    const [value, setValue] = useState<{}>({})
    const filterButtons = [
        {
            value: 'search',
            text: t('common.actualize'),
            icon: 'system_update_alt',
            componentProps: {
                variant: "contained",
                sx: { borderRadius: '4px'},
            },
        },
    ]

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>

        {!!data?.filter &&
            <Grid item xs={12}>
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

        {data?.tipus === 'InteressatAdministracioEntity' && <GridFormField xs={11} name="organCodi"
                                                                           requestParams={{...(value ?? {}), isInteressatAdministracio: data?.tipus == 'InteressatAdministracioEntity'}}
                                                                           required autocomplete/>}
        <GridButtonField xs={1} name={"filter"} icon={"search"} hidden={data?.tipus != 'InteressatAdministracioEntity'}/>

        <GridFormField xs={12} name="documentTipus"
                       disabled={data?.tipus != 'InteressatPersonaFisicaEntity'}
                       readOnly={data?.tipus != 'InteressatPersonaFisicaEntity'}
                       required/>
        <GridFormField xs={12} name="documentNum"
                       debounce
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}
                       required={data?.tipus != 'InteressatAdministracioEntity'}/>

        <GridFormField xs={12} name={"raoSocial"}
                       hidden={data?.tipus != 'InteressatPersonaJuridicaEntity'}
                       required/>

        {data?.tipus == 'InteressatPersonaFisicaEntity' && <>
            <GridFormField xs={12} name="nom" required/>
            <GridFormField xs={6} name="llinatge1" required/>
            <GridFormField xs={6} name="llinatge2"/>
        </>}

        <GridFormField xs={6} name="pais"
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'} autocomplete/>
        <GridFormField xs={6} name="provincia" requestParams={{pais: data?.pais}}
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'} autocomplete/>
        <GridFormField xs={6} name="municipi" requestParams={{provincia: data?.provincia}}
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'} autocomplete/>
        <GridFormField xs={6} name="codiPostal"
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>

        <GridFormField xs={12} name="adressaTipus" required
                       disabled={data?.tipus == 'InteressatAdministracioEntity'}
                       readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>

        {data?.adressaTipus != "SENSE_NORMALITZAR" && data?.tipus != 'InteressatAdministracioEntity' ? <>
            <GridFormField xs={4} name="adressaTipusVia" required/>
            <GridFormField xs={8} name="adresa" required/>
            <GridFormField xs={4} name="adresaApartatCorreus"/>
            <GridFormField xs={4} name="adressaNumCasa" required/>
            <GridFormField xs={4} name="adresaPuntKm"/>
            <GridFormField xs={4} name="adresaPortal"/>
            <GridFormField xs={4} name="adresaEscala"/>
            <GridFormField xs={4} name="adresaPlanta"/>
            <GridFormField xs={4} name="adresaPorta"/>
            <GridFormField xs={4} name="adresaBloc"/>
            <GridFormField xs={4} name="adresaQualificador"/>
            <GridFormField xs={12} name="adresaPoblacio"/>
            <GridFormField xs={12} name="adresaComplement"/>
        </> : <>
            <GridFormField xs={12} name="adresa" type={"textarea"}
                           disabled={data?.tipus == 'InteressatAdministracioEntity'}
                           readOnly={data?.tipus == 'InteressatAdministracioEntity'}/>
        </>}

        <GridFormField xs={6} name="email" required={data?.entregaDeh}/>
        <GridFormField xs={6} name="telefon"/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
        <GridFormField xs={12} name="preferenciaIdioma" required/>

        <GridFormField xs={6} name="entregaDeh"/>
        <GridFormField xs={6} name="entregaDehObligat" hidden={!data?.entregaDeh}/>
    </Grid>
}