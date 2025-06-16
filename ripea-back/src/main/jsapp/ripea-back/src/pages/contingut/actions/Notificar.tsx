import {useEffect, useRef, useState} from "react";
import {Grid, Alert, Icon} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {CardData, ContenidoData} from "../../../components/CardData.tsx";
import useCreate from "../../interessats/actions/Create.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const Notificacio = (props:any) => {
    const {entity, entregaPostal} = props;
    const { t } = useTranslation();
    const representant = entity?.representantInfo

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>

        { entity?.incapacitat == true && (!entity?.representant || entity?.representant?.incapacitat) &&
            <Alert severity="warning">{t('page.interessat.alert.incapacitat')}</Alert>
        }

        <CardData title={t('page.interessat.title')}>
            <ContenidoData title={t('page.interessat.detall.nif')}>{entity?.documentNum}</ContenidoData>
            <ContenidoData title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{entity?.nomComplet} {entity?.raoSocial}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.llinatges')}>{entity?.llinatge1} {entity?.llinatge2}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.email')}>{entity?.email}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.telefon')}>{entity?.telefon}</ContenidoData>
            <ContenidoData title={t('page.interessat.detall.incapacitat')}>{entity?.incapacitat}</ContenidoData>

            <CardData title={t('page.interessat.detall.direccioPostal')} hidden={representant || !entregaPostal}>
                <ContenidoData title={t('page.interessat.detall.direccio')}>{entity?.pais} {entity?.provincia} {entity?.municipi} {entity?.codiPostal} {entity?.adresa}</ContenidoData>
            </CardData>

            <CardData title={t('page.interessat.rep')} hidden={!representant}>
                <ContenidoData title={t('page.interessat.detall.nif')}>{representant?.documentNum}</ContenidoData>
                <ContenidoData title={`${t('page.interessat.detall.nom')} / ${t('page.interessat.detall.raoSocial')}`}>{representant?.nom} {representant?.raoSocial}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.llinatges')}>{representant?.llinatge1} {representant?.llinatge2}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.email')}>{representant?.email}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.telefon')}>{representant?.telefon}</ContenidoData>
                <ContenidoData title={t('page.interessat.detall.incapacitat')}>{representant?.incapacitat}</ContenidoData>
            </CardData>

            <CardData title={t('page.interessat.detall.direccioPostal')} hidden={!representant || !entregaPostal}>
                <ContenidoData title={t('page.interessat.detall.direccio')}>{representant?.pais} {representant?.provincia} {representant?.municipi} {representant?.codiPostal} {representant?.adresa}</ContenidoData>
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
    const { data, apiRef: formApiRef } = useFormContext();

    const { create, content } = useCreate(t('page.interessat.title'))
    const onCreateInteressat = (result?:any)=> {
        formApiRef?.current?.setFieldValue('interessats', [...data?.interessats, {
            id: result?.id,
            description: result?.codiNom
        }])
    }

    const interessatsFilter: string = builder.and(
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq('esRepresentant', false),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus"/>
        <GridFormField xs={12} name="estat" required disabled/>

        <GridFormField xs={10} name="interessats" multiple filter={interessatsFilter}/>

        <GridButton
            xs={2}
            onClick={()=> {
                create(undefined, {expedient: data?.expedient,}, onCreateInteressat)
            }}
        >
            <Icon>add</Icon>{t('page.interessat.actions.new')}
        </GridButton>
        {content}

        <GridFormField xs={12} name="concepte" required/>
        <GridFormField xs={12} name="serveiTipus" required/>
        <GridFormField xs={12} name="descripcio" type={"textarea"}/>
        <GridFormField xs={12} name="dataProgramada" type={"date"} componentProps={{title: t('page.contingut.detalle.dataProgramada')}}/>
        <GridFormField xs={6} name="duracio" componentProps={{title: t('page.contingut.detalle.duracio')}}/>
        <GridFormField xs={6} name="dataCaducitat" type={"date"} componentProps={{title: t('page.contingut.detalle.dataCaducitat')}}/>
        <GridFormField xs={12} name="retard" componentProps={{title: t('page.contingut.detalle.retard')}}/>
        <GridFormField xs={12} name="entregaPostal" hidden={!data?.permetreEnviamentPostal}/>

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
        title={t('page.document.action.notificar.title')}
        formDialogComponentProps={{fullWidth: true, maxWidth: 'lg'}}
        {...props}
        initialOnChange
    >
        <NotificarForm/>
    </FormActionDialog>
}

const useNotificar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id,{
            nom: row?.nom,
            expedient: row?.expedient
        })
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.notificar.ok'), 'success');
        window.location.reload();
    }

    return {
        handleShow,
        content: <Notificar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useNotificar;