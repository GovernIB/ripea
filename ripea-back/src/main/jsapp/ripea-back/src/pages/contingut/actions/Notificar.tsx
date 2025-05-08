import {useEffect, useRef, useState} from "react";
import {Grid, Alert} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const Notificacio = (props:any) => {
    const {entity, entregaPostal} = props;
    const representant = entity?.representantInfo

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>

        { entity?.incapacitat == true && (!entity?.representant || entity?.representant?.incapacitat) &&
            <Alert severity="warning">En caso de titular con incapacidad es obligatorio indicar un destinatario.</Alert>
        }

        <CardData title={"Interesado"}>
            <ContenidoData title={"NIF/CIF/NIE"}>{entity?.documentNum}</ContenidoData>
            <ContenidoData title={"Nombre / Razón social"}>{entity?.nomComplet} {entity?.raoSocial}</ContenidoData>
            <ContenidoData title={"Apellidos"}>{entity?.llinatge1} {entity?.llinatge2}</ContenidoData>
            <ContenidoData title={"Correo electrónico"}>{entity?.email}</ContenidoData>
            <ContenidoData title={"Teléfono"}>{entity?.telefon}</ContenidoData>
            <ContenidoData title={"Incapacidad"}>{entity?.incapacitat}</ContenidoData>

            <CardData title={"Dirección postal"} hidden={representant || !entregaPostal}>
                <ContenidoData title={"Pais"}>{entity?.pais}</ContenidoData>
                <ContenidoData title={"Provincia"}>{entity?.provincia}</ContenidoData>
                <ContenidoData title={"Municipio"}>{entity?.municipi}</ContenidoData>
                <ContenidoData title={"Código postal"}>{entity?.codiPostal}</ContenidoData>
                <ContenidoData title={"Dirección"}>{entity?.adresa}</ContenidoData>
            </CardData>

            <CardData title={"Representante"} hidden={!representant}>
                <ContenidoData title={"NIF/CIF/NIE"}>{representant?.documentNum}</ContenidoData>
                <ContenidoData title={"Nombre / Razón social"}>{representant?.nom} {representant?.raoSocial}</ContenidoData>
                <ContenidoData title={"Apellidos"}>{representant?.llinatge1} {representant?.llinatge2}</ContenidoData>
                <ContenidoData title={"Correo electrónico"}>{representant?.email}</ContenidoData>
                <ContenidoData title={"Teléfono"}>{representant?.telefon}</ContenidoData>
                <ContenidoData title={"Incapacidad"}>{representant?.incapacitat}</ContenidoData>
            </CardData>

            <CardData title={"Dirección postal"} hidden={!representant || !entregaPostal}>
                <ContenidoData title={"Dirección"}>{representant?.pais} {representant?.provincia} {representant?.municipi} {representant?.codiPostal} {representant?.adresa}</ContenidoData>
            </CardData>
        </CardData>
    </Grid>
}

const AdditionalInfo = (props:any) => {
    const {data} = props;
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        find: apiFindAll,
    } = useResourceApiService('interessatResource');
    const [interessats, setInteressats] = useState<any[]>([]);

    useEffect(() => {
        if (apiIsReady) {
            if (data?.interessats?.length>0) {
                const filter = builder.inside('id', data?.interessats?.map?.((interessat:any)=>interessat?.id))
                apiFindAll({unpaged: true, filter: filter, perspectives: ['REPRESENTANT']})
                    .then((app) => {
                        setInteressats(app?.rows);
                    })
            } else {
                setInteressats([])
            }
        }
    }, [apiIsReady, data?.interessats]);

    const tabs :any[] = interessats.map((interessat:any ,index:number)=> {
        return {
            value: interessat?.id,
            label: `${t('page.notificacio.title')} ${index+1}`,
            content: <Notificacio entity={interessat} entregaPostal={data?.entregaPostal}/>
        }
    });

    return <TabComponent
        tabs={tabs}
        variant="scrollable"
    />
}

const NotificarForm = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();

    const interessatsFilter: string = builder.and(
        builder.eq("expedient.id", data?.expedient?.id),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus"/>
        <GridFormField xs={12} name="estat" required disabled/>
        <GridFormField xs={12} name="interessats" multiple filter={interessatsFilter}/>
        <GridFormField xs={12} name="concepte" required/>
        <GridFormField xs={12} name="serveiTipus" required/>
        <GridFormField xs={12} name="descripcio" type={"textarea"}/>
        <GridFormField xs={12} name="dataProgramada" type={"date"} componentProps={{title: t('page.contingut.detalle.dataProgramada')}}/>
        <GridFormField xs={6} name="duracio" componentProps={{title: t('page.contingut.detalle.duracio')}}/>
        <GridFormField xs={6} name="dataCaducitat" type={"date"} componentProps={{title: t('page.contingut.detalle.dataCaducitat')}}/>
        <GridFormField xs={12} name="retard" componentProps={{title: t('page.contingut.detalle.retard')}}/>
        <GridFormField xs={12} name="entregaPostal" /*hidden={!data?.permetreEnviamentPostal}*//>

        <Grid item xs={12}>
            <AdditionalInfo data={data}/>
        </Grid>
    </Grid>
}

const Notificar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"NOTIFICAR"}
        title={(data:any)=> `${t('page.document.action.notificar')}: ${data.nom}`}
        {...props}
        initialOnChange
    >
        <NotificarForm/>
    </FormActionDialog>
}

const useNotificar = (refresh?: () => void) => {
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id,{
            nom: row?.nom,
            expedient: {id: row?.expedient.id}
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, '', 'success');
        window.location.reload();
    }
    const onError = (error:any) :void => {
        temporalMessageShow('Error', error.message, 'error');
    }

    return {
        handleShow,
        content: <Notificar apiRef={apiRef} onSuccess={onSuccess} onError={onError}/>
    }
}
export default useNotificar;