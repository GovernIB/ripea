import {useEffect, useState} from "react";
import {Grid, Alert, Icon} from "@mui/material";
import {useMuiFormDialogApiRef, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import useCreate from "../../interessats/actions/Create.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {InteressatDetail} from "../../interessats/details/InteressatDetail.tsx";
import Load from "../../../components/Load.tsx";

const perspectives = ['REPRESENTANT', 'ADRESSA']
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
                apiFindAll({unpaged: true, filter, perspectives})
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
            content: <InteressatDetail entity={interessat} isShowDireccio={!!data?.entregaPostal}/>
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

	
    const { create, content } = useCreate()
    const onCreateInteressat = (result?:any)=> {
        formApiRef?.current?.setFieldValue('interessats', [...(data?.interessats ?? []), {
            id: result?.id,
            description: result?.codiNom
        }])
    }

	const grupsFilter: string = builder.and(
	    builder.eq("expedient.id", data?.expedient?.id)
	);
	
    const interessatsFilter: string = builder.and(
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq('esRepresentant', false),
    );

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        {data?.interessatsAmbAvis?.length > 0 &&
            <Alert severity={"warning"}>
                {t('page.document.action.notificar.alert.interessatsAmbAvis.title')}
                <br/><br/>
                <ul>
                    <li><b>{t('page.document.action.notificar.alert.interessatsAmbAvis.marcat.title')}</b> {t('page.document.action.notificar.alert.interessatsAmbAvis.marcat.description')}</li>
                    <li><b>{t('page.document.action.notificar.alert.interessatsAmbAvis.noMarcat.title')}</b> {t('page.document.action.notificar.alert.interessatsAmbAvis.noMarcat.description')} <u>{t('page.document.action.notificar.alert.interessatsAmbAvis.noMarcat.warning')}</u></li>
                </ul>

                <br/>{t('page.document.action.notificar.alert.interessatsAmbAvis.description')}<br/>
                <ul>
                    {data?.interessatsAmbAvis?.map?.((i:any, index:any)=>
                        <li>Notificació {index + 1} - Titular : {i?.description} </li>
                    )}
                </ul>
            </Alert>
        }
        {data?.administracioSir &&
            <Alert severity={'warning'}>
                {t('page.document.action.notificar.alert.administracioSir.title')}<b>{t('page.document.action.notificar.alert.administracioSir.warning')}</b>
            </Alert>}

        <GridFormField name="tipus" required hiddenEnumValues={['MANUAL']}/>
        <GridFormField name="estat" required disabled/>

		<GridFormField name="grups" multiple filter={grupsFilter}/>
        <GridFormField size={9.5} name="interessats" multiple filter={interessatsFilter}/>

        <GridButton
            size={2.5}
            onClick={()=> {
                create({expedient: data?.expedient}, onCreateInteressat)
            }}
        >
            <Icon>add</Icon>{t('page.interessat.action.new.label')}
        </GridButton>
        {content}

        <GridFormField name="concepte" required/>
        <GridFormField name="serveiTipus" required/>
        <GridFormField name="descripcio" type={"textarea"}/>
        <GridFormField name="dataProgramada" type={"date"} componentProps={{title: t('page.contingut.detalle.dataProgramada')}}/>
        <GridFormField size={6} name="duracio" componentProps={{title: t('page.contingut.detalle.duracio')}}/>
        <GridFormField size={6} name="dataCaducitat" type={"date"} componentProps={{title: t('page.contingut.detalle.dataCaducitat')}}/>
        <GridFormField name="retard" componentProps={{title: t('page.contingut.detalle.retard')}}/>
        <GridFormField name="entregaPostal" hidden={!data?.permetreEnviamentPostal}/>

        <Grid size={12}>
            {/* Gatear per longitud: un array buit [] és truthy i faria renderitzar
                AdditionalInfo amb 0 pestanyes, deixant el Load intern de TabComponent
                (sense noEffect) girant indefinidament quan no hi ha destinataris. */}
            <Load value={data?.interessats?.length} noEffect>
                <AdditionalInfo data={data}/>
            </Load>
        </Grid>
    </Grid>
}

const Notificar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"documentResource"}
        action={"NOTIFICAR"}
        title={t('page.document.action.notificar.title')}
        formDialogButtons={[
            {icon: 'mail', text: t('page.document.action.notificar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
        initialOnChange
    >
        <NotificarForm/>
    </FormActionDialog>
}

const useNotificar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
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
