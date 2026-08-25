import {useEffect, useMemo, useState} from "react";
import {Box, Grid, Alert} from "@mui/material";
import {FormField, useMuiFormDialogApiRef, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import useVisualitzar from "./Visualitzar.tsx";
import useRegistreInteressatDetail from "../details/RegistreInteressatDetail.tsx";
import {useUserSession} from "@src/components/Session.tsx";
import {useIframeDialog} from "@src/components/Iframe.tsx";
import {icons} from "@src/util/icons.ts";

// Id reservat a la fila del justificant de registre dins la graella d'annexos: el back distingeix el
// justificant dels annexos reals per aquest 0 (veure AcceptarAnotacioActionExecutor.exec).
const JUSTIFICANT_ROW_ID = 0;

// Les metadades del justificant surten d'una consulta a l'Arxiu, així que es demanen amb una perspectiva
// pròpia i només en obrir el diàleg. El llistat només rep teJustificant (perspectiva REGISTRE).
const justificantPerspectives = ['REGISTRE', 'JUSTIFICANT'];

const AcceptarTabExpedient = () => {

    const {data} =useFormContext();

    const filterMetaExpedientAnotacioCrear = builder.and(
        builder.eq('actiu', true),
        builder.eq('revisioEstat', "'REVISAT'"),
    );
    
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="accio" required/>
        <GridFormField name="metaExpedient" required
            filter={filterMetaExpedientAnotacioCrear}
            namedQueries={['EXPEDIENT_CREATE']}/>

        {data?.accio == "CREAR" &&
            <>
                <GridFormField name="newExpedientTitol" required/>
                <GridFormField name="prioritat" required/>
                <GridFormField name="prioritatMotiu" type={"textarea"} hidden={data?.prioritat == "B_NORMAL"} required/>
                <GridFormField name="organGestor"
                            namedQueries={[`EXPEDIENT_FORM#${data?.metaExpedient?.id ?? 0}`]}
                            disabled={!data?.metaExpedient || data?.disableOrganGestor}
                            readOnly={!data?.metaExpedient || data?.disableOrganGestor}
                            required/>
                <GridFormField size={6} name="sequencia" required disabled readOnly/>
                <GridFormField size={6} name="any" required/>
                <GridFormField name="grup"
                               namedQueries={[`BY_PROCEDIMENT#${data?.metaExpedient?.id ?? 0}`]}
                               hidden={!data?.grup && !data?.gestioAmbGrupsActiva} required/>
				<GridFormField name="seguidor"/>
            </>
        }
        {data?.accio == "INCORPORAR" &&
            <>
                <GridFormField name="expedient"
                        filter={builder.and(
                           builder.eq('metaExpedient.id', data?.metaExpedient?.id),
						   builder.eq('esborrat', 0),
                       )}                
                required/>
                <GridFormField name="agafarExpedient"/>
            </>
        }

        <GridFormField name="associarInteressats"/>
    </Grid>
}

/**
 * Cel·la amb el desplegable del tipus de document d'un annex. És un component propi i no una funció
 * dins de renderCell perquè requestParams s'ha de memoritzar: FormFieldEnum recarrega les opcions cada
 * vegada que en canvia la identitat de l'objecte, i un literal inline en crearia una de nova a cada
 * render de la cel·la. Amb el memo només es recarreguen quan realment canvien el procediment o les
 * seleccions dels altres annexos, que és el que filtra el servidor per multiplicitat.
 */
const TipusDocumentCell = (props:any) => {
    const {annexId, field} = props;
    const {data, apiRef} = useFormContext();

    const requestParams = useMemo(() => ({
        metaExpedientId: data?.metaExpedient?.id,
        annex: annexId,
        annexos: data?.annexos,
    }), [data?.metaExpedient?.id, annexId, data?.annexos]);

    return <FormField
        name={"annexos" + (data?.annexos?.[annexId] ? `#${annexId}` : '')}
        value={data?.annexos?.[annexId]}
        field={field}
        onChange={(value)=>{
            apiRef?.current?.setFieldValue('annexos', {
                ...data?.annexos,
                [annexId]: value,
            })
        }}
        componentProps={{ size: "small" }}
        requestParams={requestParams}
        required
    />
}

const AcceptarTabAnnexos = () => {
    const {data, fields, apiRef} = useFormContext();
    const  { t } = useTranslation()

    const fieldTipusDocument = fields?.filter(i=>i.name=='tipusDocument')[0];

    //Files reals de la graella (sense la del justificant). undefined = la graella encara no n'ha informat.
    const [filesAnnexos, setFilesAnnexos] = useState<any[]>();

    const mostrarJustificant = !!data?.isIncorporacioJustificantActiva && !!data?.justificant;
    const metaDocumentJustificantId = data?.metaDocumentJustificantId;

    // Manté el mapa annexos (clau = id de fila, valor = id del tipus de document) sincronitzat amb les
    // files que mostra la graella. La fila del justificant l'afegeix el rowsTransformer i no arriba per
    // onRowsChange, així que la seva clau s'afegeix aquí amb el tipus REGISTRE_JUSTIFICANT_ENTRADA del
    // procediment com a valor per defecte (en blanc si el procediment no en té cap d'actiu). Els valors
    // ja triats es conserven; només s'assigna el valor per defecte a les claus que encara no existeixen,
    // per no reescriure una tria que l'usuari hagi buidat expressament.
    useEffect(() => {
        if (!filesAnnexos) {
            return;
        }
        const annexos = data?.annexos ?? {};
        // En muntar la pestanya, onRowsChange arriba primer amb la llista buida perquè la graella encara
        // està carregant, i això no es pot distingir d'una anotació sense annexos. Si el mapa ja té claus
        // no s'hi toca: altrament, en tornar a la pestanya es perdrien els tipus de document ja triats.
        if (!filesAnnexos.length && Object.keys(annexos).length) {
            return;
        }
        const claus = filesAnnexos.map((fila:any) => String(fila.id));
        if (mostrarJustificant) {
            claus.push(String(JUSTIFICANT_ROW_ID));
        }
        const clausActuals = Object.keys(annexos);
        const mateixesClaus = claus.length == clausActuals.length
            && claus.every((clau) => clausActuals.includes(clau));
        if (mateixesClaus) {
            return;
        }
        const valor = (clau:string) => {
            if (clau in annexos) {
                return annexos[clau] ?? '';
            }
            return clau == String(JUSTIFICANT_ROW_ID) && metaDocumentJustificantId
                ? String(metaDocumentJustificantId)
                : '';
        };
        apiRef?.current?.setFieldValue('annexos', Object.fromEntries(claus.map((clau) => [clau, valor(clau)])));
    }, [filesAnnexos, mostrarJustificant, metaDocumentJustificantId, data?.annexos]);

    const filter = builder.eq("registre.id", data?.registre?.id)
    const columnsAnnexos = [
        {
            field: 'titol',
            flex: 0.5,
        },
        {
            field: 'nom',
            flex: 0.5,
        },
        {
            field: 'ntiTipoDocumental',
            headerName: '',
            sortable: false,
            flex: 0.5,
            renderCell: (params:any) => <TipusDocumentCell annexId={params.id} field={fieldTipusDocument}/>
        },
    ]

    const {handleOpen, dialog, isValid} = useVisualitzar()
    const {handleOpen: handleIframeOpen, dialog: dialogIframe} = useIframeDialog();

    const actions = [
        {
            label: t('page.document.action.view.label'),
            icon: icons.visualitza,
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => !isValid(row) || row?.justificant,
        },
        {
            label: t('page.document.action.view.label'),
            icon: icons.visualitza,
            showInMenu: false,
            onClick: (_id:any, row:any) => handleIframeOpen(`expedientPeticio/descarregarJustificant/${row?.registreId}`),
            hidden: (row:any) => !(['pdf', 'odt', 'docx'].includes(row?.fitxerExtension) && row?.justificant),
        }
    ]

    return <>
        <StyledMuiGrid
            resourceName={'registreAnnexResource'}
            persistentStateActive={false}
            filter={filter}
            columns={columnsAnnexos}
            rowAdditionalActions={actions}
            rowsTransformer={(_rows: any) => {
                //Es retorna una llista nova: la d'entrada és l'estat de files de la graella i mutar-la
                //faria que el justificant arribés també a onRowsChange com si fos un annex real.
                const files: any[] = _rows ?? [];
                if (!mostrarJustificant || files.some((fila:any) => fila.id === JUSTIFICANT_ROW_ID)) {
                    return files;
                }
                //S'afegeix encara que l'anotació no tengui cap annex: el justificant s'ha de poder
                //incorporar igualment. Mentre la graella carrega la fila queda tapada per l'overlay.
                return [...files, {
                    ...data?.justificant,
                    id: JUSTIFICANT_ROW_ID,
                    justificant: true,
                }];
            }}
            onRowsChange={(rows:any) => setFilesAnnexos(rows)}
            onRowClick={(params: any) => {
                if (isValid(params?.row)) {
                    handleOpen(params?.id)
                }
            }}
            toolbarHide
            paginationActive={false}
            readOnly
        />
        {dialog}
        {dialogIframe}
    </>
}

const columnsInteressats = [
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'documentNumero',
        flex: 0.5,
    },
    {
        field: 'nomComplet',
        flex: 0.75,
        valueFormatter: (value:any, row:any) => value || row?.raoSocial,
    },
    {
        field: 'representant',
        flex: 1,
    },
]

// Constant de mòdul: amb un literal en línia cada render crea un objecte nou, i l'useEffect
// de MuiDataGrid el torna a aplicar retornant la graella a la primera pàgina.
const paginationModelInteressats = {page: 0, pageSize: 5};

const AcceptarTabInteressats = () => {
    const { t } = useTranslation()
    const {data, apiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>(data?.interessats || []);

    useEffect(() => {
        apiRef?.current?.setFieldValue("interessats", selectedRows)
    }, [selectedRows]);

    const filter = builder.eq("registre.id", data?.registre?.id)

    const {handleOpen, dialog} = useRegistreInteressatDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => row?.tipus == "ADMINISTRACIO"
        },
    ]

    return <>
        <StyledMuiGrid
            resourceName={"registreInteressatResource"}
            persistentStateActive={false}
            filter={filter}
            columns={columnsInteressats}
            rowAdditionalActions={actions}
            selectionActive
            rowSelectionModel={selectedRows}
            onRowSelectionModelChange={(newSelection) => {
                setSelectedRows([...newSelection]);
            }}
            onRowClick={(params: any) => {
                if (params?.row?.tipus != "ADMINISTRACIO") {
                    handleOpen(params?.id, params?.row)
                }
            }}

            autoHeight
            paginationModel={paginationModelInteressats}
            readOnly
        />
        {dialog}
    </>
}

const AcceptarForm = () => {
    const {data, fieldErrors} =useFormContext();
    const { t } = useTranslation();

    const annexosError:boolean|undefined = useMemo(() =>
            fieldErrors?.some?.(e => e.field === "annexos")
    , [fieldErrors])

    const tabs = [
        {
            value: 'expedient',
            label: t('page.expedient.title'),
            content: <AcceptarTabExpedient/>,
        },
        {
            value: 'annexos',
            label: t('page.anotacio.tabs.annexos'),
            content: <AcceptarTabAnnexos/>,
            error: annexosError,
        },
        {
            value: 'interessats',
            label: t('page.anotacio.tabs.interessats'),
            content: <AcceptarTabInteressats/>,
            hidden: !data?.associarInteressats
        },
    ]

    return (
        <Box sx={{ height: '650px', minHeight: 0 }}>
            {data?.expedientNoTrobatMissatge &&
                <Alert severity="warning" sx={{ mb: 2 }}>{data.expedientNoTrobatMissatge}</Alert>}
            <TabComponent tabs={tabs}/>
        </Box>
    )
}

const Acceptar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"expedientPeticioResource"}
        action={"ACCEPTAR_ANOTACIO"}
        title={t('page.anotacio.action.acceptar.title')}
        initialOnChange
        formDialogButtons={[
            {icon: 'check_circle', text: t('page.anotacio.action.acceptar.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <AcceptarForm/>
    </FormActionDialog>
}

const useAcceptar = (refresh?: () => void) => {
    const {value: user} = useUserSession();
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();
    const {getOne} = useResourceApiService('expedientPeticioResource');

    const show = (id:any, row:any, justificant:any) :void => {
        apiRef.current?.show?.(id, {
            metaExpedient: row?.metaExpedient,
            registre: row?.registre,
            interessats: row?.registreInfo?.interessats?.map((i:any)=>i.id) || [],
            grup: row?.grup,
            isIncorporacioJustificantActiva: user?.sessionScope?.isIncorporacioJustificantActiva,
            justificant: justificant ? {
                registreId: row?.registreInfo?.id,
                ...justificant
            } : undefined
        })
    }

    const handleShow = (id:any, row:any) :void => {
        if (!user?.sessionScope?.isIncorporacioJustificantActiva || !row?.teJustificant) {
            show(id, row, undefined);
            return;
        }
        // El títol i el nom del fitxer del justificant surten d'una consulta a l'Arxiu: es fa en obrir el
        // diàleg i no per cada fila del llistat. Si la consulta falla la fila s'hi ha d'afegir igualment,
        // sense metadades, perquè l'usuari en pugui triar el tipus de document i el justificant s'incorpori.
        getOne(id, {perspectives: justificantPerspectives})
            .then((anotacio:any) => show(id, row, anotacio?.registreInfo?.justificant ?? {}))
            .catch(() => show(id, row, {}));
    }
    const onSuccess = () :void => {
        refresh?.();
        temporalMessageShow(null, t('page.anotacio.action.acceptar.ok'), 'success');
    }

    return {
        handleShow,
        content: <Acceptar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useAcceptar;