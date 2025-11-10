import {useEffect, useRef, useState} from "react";
import {Grid, Alert, Icon} from "@mui/material";
import {MuiFormDialogApi, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import useCreate from "../../interessats/actions/Create.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {InteressatDetail} from "../../interessats/details/InteressatDetail.tsx";

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
        formApiRef?.current?.setFieldValue('interessats', [...data?.interessats, {
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
                Hi ha notificacions amb destinatari sense NIF/NIE. Aquestes notficacions no es poden enviar a la carpeta ciutadana, degut a que és necessari un NIF o NIE per a accedir-hi.
                <br/><br/>
                <ul>
                    <li><b>Si ha marcat entrega postal:</b> La notificació s'enviarà per correu postal, sempre que l'òrgan gestor tengui un CIE (Centre de Impressió i Ensobrat) definit.</li>
                    <li><b>Si NO ha seleccionat entrega postal:</b> La notificació telemàtica no es realitzarà. En el seu lloc s'enviarà un correu electrònic d'avís informant al titular que en breu rebrà una notificació per correu postal. <u>És necessari que feu la notificació en Paper.</u></li>
                </ul>

                <br/>Els notificacions sense NIF/NIE són els següents:<br/>
                <ul>
                    {data?.interessatsAmbAvis?.map?.((i:any, index:any)=>
                        <li>Notificació {index + 1} - Titular : {i?.description} </li>
                    )}
                </ul>
            </Alert>
        }

        <GridFormField xs={12} name="tipus" required hiddenEnumValues={['MANUAL']}/>
        <GridFormField xs={12} name="estat" required disabled/>

		<GridFormField xs={12} name="grups" multiple filter={grupsFilter}/>
        <GridFormField xs={9.5} name="interessats" multiple filter={interessatsFilter}/>

        <GridButton
            xs={2.5}
            onClick={()=> {
                create({expedient: data?.expedient}, onCreateInteressat)
            }}
        >
            <Icon>add</Icon>{t('page.interessat.action.new.label')}
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
        formDialogButtons={[
            {icon: 'save', text: t('page.document.action.notificar.button'), componentProps: { variant: 'contained' }, value: true },
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
