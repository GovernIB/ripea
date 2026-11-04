import {useEffect, useMemo, useRef, useState} from "react";
import {useMuiFormDialogApiRef, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {Alert, Box, CircularProgress, Grid, Typography} from "@mui/material";
import GridFormField, {GridButton} from "../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import useCreate from "../../interessats/actions/Create.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useUserSession} from "../../../components/Session.tsx";
import {useSession} from "@src/components/SessionStorageContext.tsx";

const sortModelTipusDocument:any = [{field: 'nom',sort: 'asc'}]

/**
 * Prefix dels camps de les dades específiques del servei PINBAL: el servidor recull les propietats amb aquest prefix
 * (DocumentResource.NewDocPinbalForm.DADA_ESPECIFICA_PREFIX) i en treu el path de la dada a PINBAL.
 */
const DADA_ESPECIFICA_PREFIX = 'dadaEspecifica:';
/** Camp del formulari (no visible) del qual es copia la definició per als camps de llista de les dades específiques. */
const CAMP_OPCIO_DADA_ESPECIFICA = 'opcioDadaEspecifica';
const nomCamp = (codi: string) => DADA_ESPECIFICA_PREFIX + codi;

// Formulari de dades específiques del servei (PinbalFormulariDto)
type CampTipus = 'TEXT' | 'NUMERIC' | 'DATA' | 'BOOLEA' | 'DOC_IDENT' | 'ENUM' | 'PAIS' | 'PROVINCIA' | 'MUNICIPI_3' | 'MUNICIPI_5' | 'ETIQUETA' | 'ADJUNT';
type CampPinbal = {
    codi: string,
    text: string,
    tipus: CampTipus,
    comentari?: string,
    obligatori: boolean,
    modificable: boolean,
    valorInicial?: string,
    /** Municipis: camp del formulari amb la província */
    campProvincia?: string,
    /** Municipis sense camp de província: la província */
    provincia?: string,
};
type SeccioPinbal = {
    codi?: string,
    nom?: string,
    ajuda?: string,
    camps: CampPinbal[],
    subseccions: SeccioPinbal[],
};
type FormulariPinbal = {
    serveiCodi: string,
    /** Nom del servei a RIPEA (o el codi, si no en té) */
    serveiNom: string,
    arrel: SeccioPinbal,
};

const TIPUS_LLISTA: CampTipus[] = ['ENUM', 'PAIS', 'PROVINCIA', 'MUNICIPI_3', 'MUNICIPI_5'];
const TIPUS_COMPONENT: Partial<Record<CampTipus, string>> = {
    TEXT: 'text',
    DOC_IDENT: 'text',
    NUMERIC: 'number',
    DATA: 'date',
    BOOLEA: 'checkbox',
    ADJUNT: 'file',
};

const recorrerCamps = (seccio: SeccioPinbal, callback: (camp: CampPinbal) => void) => {
    seccio.camps?.forEach(callback);
    seccio.subseccions?.forEach((subseccio) => recorrerCamps(subseccio, callback));
}

const teCamps = (seccio?: SeccioPinbal): boolean =>
    !!seccio && ((seccio.camps?.length ?? 0) > 0 || (seccio.subseccions ?? []).some(teCamps));

/** Valor inicial del camp al formulari (els booleans, com a booleà). */
const valorInicialCamp = (camp: CampPinbal) =>
    camp.tipus === 'BOOLEA' ? camp.valorInicial === 'true' : (camp.valorInicial ?? null);

const CampEtiquetaPinbal = ({camp}: {camp: CampPinbal}) =>
    <Grid size={12}>
        <Typography variant="body2" sx={{whiteSpace: 'pre-line'}}>{camp.comentari}</Typography>
    </Grid>

const CampSimplePinbal = ({camp}: {camp: CampPinbal}) => {
    const name = nomCamp(camp.codi);
    const type = TIPUS_COMPONENT[camp.tipus] ?? 'text';
    // Definició del camp, que no existeix al formulari del servidor
    const field = useMemo(() => ({
        name,
        type,
        label: camp.text,
        required: camp.obligatori,
        helperText: camp.comentari,
    }), [name, type, camp]);

    return <GridFormField
        name={name}
        label={camp.text}
        type={type}
        field={field}
        required={camp.obligatori}
        disabled={!camp.modificable}
    />
}

/**
 * Camp de llista (ENUM, PAIS, PROVINCIA, MUNICIPI_3 i MUNICIPI_5): les opcions són els valors del camp a PINBAL. Els
 * municipis es carreguen de la província del camp de província del formulari o, si no n'hi ha, de la indicada pel
 * servidor.
 */
const CampLlistaPinbal = ({camp, serveiCodi}: {camp: CampPinbal, serveiCodi: string}) => {
    const {fields, data, apiRef} = useFormContext();
    const name = nomCamp(camp.codi);
    const esMunicipi = camp.tipus === 'MUNICIPI_3' || camp.tipus === 'MUNICIPI_5';
    const provincia = esMunicipi
        ? (camp.campProvincia ? data?.[nomCamp(camp.campProvincia)] : camp.provincia)
        : undefined;

    const campOpcions = useMemo(() => fields?.find((f: any) => f.name === CAMP_OPCIO_DADA_ESPECIFICA), [fields]);
    const field = useMemo(() => campOpcions && ({
        ...campOpcions,
        name,
        label: camp.text,
        required: camp.obligatori,
        helperText: camp.comentari,
    }), [campOpcions, name, camp]);
    const requestParams = useMemo(() => ({
        serveiCodi,
        campCodi: camp.codi,
        enumTipus: camp.tipus,
        ...(esMunicipi ? {filtre: provincia ?? ''} : {}),
    }), [serveiCodi, camp.codi, camp.tipus, esMunicipi, provincia]);

    // Si canvia la província, el municipi triat ja no és vàlid (no quan la província s'inicialitza)
    const provinciaAnterior = useRef(provincia);
    useEffect(() => {
        if (esMunicipi && provinciaAnterior.current !== provincia) {
            if (provinciaAnterior.current != null) {
                apiRef?.current?.setFieldValue(name, null);
            }
            provinciaAnterior.current = provincia;
        }
    }, [esMunicipi, provincia]);

    if (!field) {
        return null;
    }
    return <GridFormField
        name={name}
        label={camp.text}
        field={field}
        required={camp.obligatori}
        disabled={!camp.modificable}
        requestParams={requestParams}
    />
}

const CampPinbalForm = ({camp, serveiCodi}: {camp: CampPinbal, serveiCodi: string}) => {
    if (camp.tipus === 'ETIQUETA') {
        return <CampEtiquetaPinbal camp={camp}/>
    }
    if (TIPUS_LLISTA.includes(camp.tipus)) {
        return <CampLlistaPinbal camp={camp} serveiCodi={serveiCodi}/>
    }
    return <CampSimplePinbal camp={camp}/>
}

/** Secció del formulari: els seus camps i després les seves subseccions. Cada grup és un fieldset amb el seu nom. */
const SeccioPinbalForm = ({seccio, serveiCodi}: {seccio: SeccioPinbal, serveiCodi: string}) => {
    const contingut = <>
        {seccio.camps?.map((camp) => <CampPinbalForm key={camp.codi} camp={camp} serveiCodi={serveiCodi}/>)}
        {seccio.subseccions?.map((subseccio) =>
            <Grid key={subseccio.codi} size={12}>
                <SeccioPinbalForm seccio={subseccio} serveiCodi={serveiCodi}/>
            </Grid>
        )}
    </>;
    if (seccio.codi == null) {
        // Secció arrel: els camps sense grup i els grups de primer nivell, directament al formulari
        return contingut;
    }
    return <Box
        component="fieldset"
        sx={{m: 0, minWidth: 0, px: 1.5, pt: 0.5, pb: 1.5, border: 1, borderColor: 'divider', borderRadius: 1}}
    >
        <Typography component="legend" variant="subtitle2" sx={{px: 0.5}}>{seccio.nom}</Typography>
        {seccio.ajuda && <Typography variant="body2" color="text.secondary" sx={{mb: 1, whiteSpace: 'pre-line'}}>{seccio.ajuda}</Typography>}
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            {contingut}
        </Grid>
    </Box>
}

/**
 * Dades específiques del servei PINBAL del tipus de document: el formulari es construeix amb els camps que el servei
 * té definits a PINBAL.
 */
const DadesEspecifiquesPinbal = () => {
    const {data, apiRef: formApiRef} = useFormContext();
    const {t} = useTranslation();
    const {isReady: apiIsReady, artifactReport} = useResourceApiService('documentResource');
    const tipusDocumentId = data?.tipusDocument?.id;
    const [formulari, setFormulari] = useState<FormulariPinbal>();
    const [carregant, setCarregant] = useState<boolean>(false);
    const [error, setError] = useState<string>();

    useEffect(() => {
        setFormulari(undefined);
        setError(undefined);
        if (!apiIsReady || tipusDocumentId == null) {
            return;
        }
        let actual = true;
        setCarregant(true);
        artifactReport(undefined, {code: 'PINBAL_FORMULARI', data: {tipusDocument: {id: tipusDocumentId}}})
            .then((result: any) => {
                if (!actual) return;
                const formulariServei: FormulariPinbal | undefined = result?.[0];
                if (formulariServei?.arrel) {
                    // Valors inicials: també reemplacen els d'un servei triat abans amb camps iguals
                    recorrerCamps(formulariServei.arrel, (camp) => {
                        if (camp.tipus !== 'ETIQUETA') {
                            formApiRef?.current?.setFieldValue(nomCamp(camp.codi), valorInicialCamp(camp));
                        }
                    });
                }
                setFormulari(formulariServei);
            })
            .catch((e: any) => actual && setError(e?.message || t('page.document.action.pinbal.formulari.error')))
            .finally(() => actual && setCarregant(false));
        return () => {
            actual = false;
        };
    }, [apiIsReady, tipusDocumentId]);

    if (carregant) {
        return <Grid size={12} role="status" sx={{display: 'flex', alignItems: 'center', gap: 1, my: 1}}>
            <CircularProgress size={20} aria-hidden="true"/>
            <Typography variant="body2">{t('page.document.action.pinbal.formulari.carregant')}</Typography>
        </Grid>
    }
    if (error) {
        return <Grid size={12}><Alert severity="error">{error}</Alert></Grid>
    }
    if (!formulari || !teCamps(formulari.arrel)) {
        return null;
    }
    return <>
        <Grid size={12} sx={{ my: 1, borderBottom: '1px solid black' }}>
            <Typography component="h3" variant="body1">{t('page.document.detall.dataEspecific')} "{formulari.serveiNom}"</Typography>
        </Grid>
        <SeccioPinbalForm key={formulari.serveiCodi} seccio={formulari.arrel} serveiCodi={formulari.serveiCodi}/>
    </>
}

/**
 * 'carpetaFixada': el diàleg s'ha obert des de dins una carpeta. Igual que al formulari de nou document,
 * la carpeta es mostra sempre i només es pot canviar si la creació de carpetes està activa.
 */
const DocPinbalForm = ({carpetaFixada}: {carpetaFixada?: boolean}) => {
    const {data, apiRef: formApiRef} = useFormContext();
    const { t } = useTranslation()
    const { value: user } = useUserSession();

    const { create, content } = useCreate()
    const {value: numInteressats, save: setNumInteressats} = useSession(`expedient-${data?.expedient?.id}-numInteressats`)
    const onCreateInteressat = (result?:any)=> {
        formApiRef?.current?.setFieldValue('titular', {
            id: result?.id,
            description: result?.codiNom
        })
        setNumInteressats(numInteressats + 1)
    }

    const titularFilter: string = builder.and(
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq('esRepresentant', false),
    );

    const carpetaFilter: string = useMemo(() => (
        builder.and(
            builder.eq("expedient.id", data?.expedient?.id),
            builder.eq("esborrat", 0),
        )
    ), [data?.expedient?.id]);

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="tipusDocument"
                       namedQueries={[`PINBAL_DOC#${data?.expedient?.id}`]}
                       sortModel={sortModelTipusDocument}/>

        <GridFormField size={"grow"} name="titular" filter={titularFilter}/>
        <GridButton
            size={"auto"}
            icon={'person_add'}
            title={t('page.interessat.action.new.label')}
            sx={{minWidth: 0, px: 1.75}}
            onClick={()=> {
                create({expedient: data?.expedient}, onCreateInteressat)
            }}
        />
        {content}

        <GridFormField size={{xs: 12, sm: 4}} name="consentiment" required/>
        <GridFormField name="finalitat" type={"textarea"}/>

        {(user?.sessionScope?.isCreacioCarpetesActiva || carpetaFixada) &&
            <GridFormField
                name="carpeta"
                filter={carpetaFilter}
                readOnly={!user?.sessionScope?.isCreacioCarpetesActiva}
            />
        }

        <DadesEspecifiquesPinbal/>
    </Grid>
}

const DocPinbal = ({carpetaFixada, ...props}:any) => {
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
        <DocPinbalForm carpetaFixada={carpetaFixada}/>
    </FormActionDialog>
}

const useDocPinbal = (entity:any, refresh?: () => void, contingutPareId?: string | number | null, contingutPareNom?: string | null) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    // Si s'obre des de dins una carpeta, el document es desa per defecte a aquesta carpeta
    const carpetaFixada = contingutPareId != null && contingutPareId !== '';

    const handleShow = () :void => {
        apiRef.current?.show?.(undefined,{
            expedient: {id: entity?.id},
            ...(carpetaFixada
                ? {
                    carpeta: {
                        id: contingutPareId,
                        description: contingutPareNom != null && contingutPareNom !== ''
                            ? contingutPareNom
                            : String(contingutPareId),
                    },
                }
                : {}),
        })
    }
    const onSuccess = (result:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.document.action.pinbal.ok', {codiServeiPinbal: result?.codiServeiPinbal}), 'success');
    }

    return {
        handleShow,
        content: <DocPinbal apiRef={apiRef} onSuccess={onSuccess} carpetaFixada={carpetaFixada}/>
    }
}
export default useDocPinbal;
