import {useEffect, useState} from "react";
import {Box, Grid, useTheme} from "@mui/material";
import {BasePage, MuiDialog, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../components/TabComponent.tsx";
import {formatDate} from "../util/dateUtils.ts";
import StyledMuiGrid from "../components/StyledMuiGrid.tsx";
import * as builder from "../util/springFilterUtils.ts";
import {DetailCard, DetailCardContent} from "../components/CardData.tsx";
import Load from "../components/Load.tsx";

const columnsAccions = [
    {
        field: 'createdDate',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
    {
        field: 'tipus',
        flex: 0.5,
        renderCell: (params: any) => params?.row?.secundari ?params?.row?.mssg :params?.formattedValue,
    },
]

const sortModel:any = [{ field: 'createdDate', sort: 'asc' }];
const Accions = (props:any) => {
    const { id, onRowCountChange, contingutTipus } = props;
    const { t } = useTranslation();
    const theme = useTheme();

    const {handleOpen, dialog} = useAccioDialog()

    const actions = [
        {
            label: t('common.detail'),
            icon: 'info',
            showInMenu: false,
            onClick: handleOpen,
        }
    ]

    return (
        <BasePage>
            <Box
                sx={{
                    '& .MuiDataGrid-actionsCell .MuiIcon-root': {
                        color: theme.palette.mode === 'dark' ? theme.palette.primary.contrastText : theme.palette.text.secondary,
                    },
                }}
            >
                <StyledMuiGrid
                    resourceName={'contingutLogResource'}
                    filter={builder.and(builder.eq('contingutId', id), builder.eq('contingutTipus', `'${contingutTipus}'`))}
                    staticSortModel={sortModel}
                    columns={columnsAccions}
                    rowAdditionalActions={actions}
                    onRowCountChange={onRowCountChange}
                    autoHeight
                    toolbarHide
                    readOnly
                />
                {dialog}
            </Box>
        </BasePage>
    );
}
const useAccioDialog = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any, row:any) => {
        // console.log(id, row);
        setEntity(row);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const dialog =
    <MuiDialog
        open={open}
        title={t('page.contingut.action.history.detail')}
        closeCallback={handleClose}
        componentProps={{ fullWidth: true, maxWidth: 'md'}}
        buttons={[
            {
                value: 'close',
                text: t('common.close'),
                componentProps: { variant: 'outlined' }
            },
        ]}
        buttonCallback={(value :any) :void=>{
            if (value=='close') {
                handleClose();
            }
        }}
    >
        <Grid container columnSpacing={1} rowSpacing={1}>
            <DetailCard title={t('page.contingut.log.param')}>
                <DetailCardContent title={t('page.contingut.log.param1')} size={6}>{entity?.param1}</DetailCardContent>
                <DetailCardContent title={t('page.contingut.log.param2')} size={6}>{entity?.param2}</DetailCardContent>
            </DetailCard>

            <DetailCard title={t('page.contingut.log.causa')} hidden={!entity?.pare}>
                <DetailCardContent title={t('common.action')}>
                    {formatDate(entity?.pare?.createdDate)} | {entity?.pare?.createdByFullName} | {entity?.pare?.tipusString}
                </DetailCardContent>
                <DetailCardContent title={t('page.contingut.log.objecte')}>
                    {entity?.pare?.mssg} {entity?.pare?.objecteNom ?' - '+entity?.pare?.objecteNom :''}
                </DetailCardContent>
                <DetailCardContent title={t('page.contingut.log.param1')} size={6}>{entity?.pare?.param1}</DetailCardContent>
                <DetailCardContent title={t('page.contingut.log.param2')} size={6}>{entity?.pare?.param2}</DetailCardContent>
            </DetailCard>

            <DetailCard title={t('page.contingut.moviment.causa')} hidden={!entity?.moviment}>
                <DetailCardContent title={t('page.contingut.moviment.origen')} size={6}>#{entity?.moviment?.origen?.id}</DetailCardContent>
                <DetailCardContent title={t('page.contingut.moviment.desti')} size={6}>#{entity?.moviment?.desti?.id}</DetailCardContent>
            </DetailCard>
        </Grid>
    </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

const columnsMoviment = [
    {
        field: 'createdDate',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'remitent',
        flex: 0.5,
    },
    {
        field: 'origen',
        flex: 0.5,
        valueFormatter: (value: any) => '#'+value?.id,
    },
    {
        field: 'desti',
        flex: 0.5,
        valueFormatter: (value: any) => '#'+value?.id,
    },
    {
        field: 'comentari',
        flex: 0.5,
    },
]
const Moviment = (props:any) => {
    const { id, onRowCountChange } = props;
    return <BasePage>
        <StyledMuiGrid
            resourceName={'contingutMovimentResource'}
            filter={builder.eq('contingut.id', id)}
            staticSortModel={sortModel}
            columns={columnsMoviment}
            onRowCountChange={onRowCountChange}
            autoHeight
            toolbarHide
            readOnly
        />
    </BasePage>;
}
const AUDITORIA_PERSPECTIVE = 'AUDITORIA';

/**
 * El nom complet de l'usuari (Nom (codi)) només el resol la perspectiva AUDITORIA. Els llistats no
 * la demanen -perquè no pinten aquestes columnes i costa una lectura d'usuari per fila-, així que la
 * fila arriba amb el codi de l'usuari com a valor per defecte i la pipella d'auditoria mostrava el
 * codi en lloc del nom.
 *
 * Quan la fila ja porta el nom resolt (detall de l'expedient, llistats que sí demanen AUDITORIA) es
 * fa servir tal qual; si no, es rellegeix el recurs amb la perspectiva, i només quan s'obre la
 * pipella, que és quan es munta aquest component.
 */
const useAuditoriaEntity = (entity: any, resourceName?: string) => {
    const { isReady: apiIsReady, getOne: apiGetOne } = useResourceApiService(resourceName);
    const [auditoriaEntity, setAuditoriaEntity] = useState<any>();

    const id = entity?.id;
    // El back retorna el codi de l'usuari com a valor per defecte del nom complet: si són iguals, el
    // nom no s'ha resolt. Un usuari sense codi (createdBy buit) no té res a resoldre.
    const isResolt = (codi?: string, nomComplet?: string) => !codi || codi !== nomComplet;
    const necessitaCarrega = id != null && resourceName != null &&
        (!isResolt(entity?.createdBy, entity?.createdByFullName) ||
            !isResolt(entity?.lastModifiedBy, entity?.lastModifiedByFullName));

    useEffect(() => {
        if (!necessitaCarrega || !apiIsReady) {
            return;
        }
        let cancelled = false;
        apiGetOne(id, {perspectives: [AUDITORIA_PERSPECTIVE]})
            .then((resource: any) => {
                if (!cancelled) {
                    setAuditoriaEntity(resource);
                }
            })
            // Si falla es continua mostrant el que porta la fila (el codi de l'usuari): la pipella
            // és informativa i val més ensenyar el codi que no un error.
            .catch(() => {});
        return () => {
            cancelled = true;
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [necessitaCarrega, apiIsReady, id]);

    // Es compara l'id perquè, si es reobre l'històric d'un altre element, no es mostrin les dades de
    // l'anterior mentre no arribi la nova lectura.
    return auditoriaEntity?.id === id ? auditoriaEntity : entity;
}
const Auditoria = (props:any) => {
    const { entity: rowEntity, resourceName } = props;
    const { t } = useTranslation();
    const entity = useAuditoriaEntity(rowEntity, resourceName);

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <DetailCard title={t('page.contingut.history.create')} size={6}>
                <DetailCardContent title={t('page.contingut.history.user')}>{entity?.createdByFullName}</DetailCardContent>
                <DetailCardContent title={t('page.contingut.history.date')} >{formatDate(entity?.createdDate)}</DetailCardContent>
            </DetailCard>
            <DetailCard title={t('page.contingut.history.update')} size={6}>
                <DetailCardContent title={t('page.contingut.history.user')} >{entity?.lastModifiedByFullName}</DetailCardContent>
                <DetailCardContent title={t('page.contingut.history.date')} >{formatDate(entity?.lastModifiedDate)}</DetailCardContent>
            </DetailCard>
        </Grid>
    </BasePage>;
}

export const HistoricContingutTipusEnum = {
    CONTINGUT: "CONTINGUT",
    METANODE: "METANODE",
    TASCA: "TASCA",
} as const;
type HistoricContingutTipus = keyof typeof HistoricContingutTipusEnum;
/**
 * @param contingutTipus tipus de contingut del que es llisten les accions.
 * @param auditResourceName recurs del que s'ha de rellegir l'auditoria quan la fila no porta el nom
 *        complet de l'usuari resolt. Només s'hi pot posar un recurs que declari la perspectiva
 *        AUDITORIA; si s'omet, la pipella mostra el que porti la fila.
 */
const useHistoric = (contingutTipus:HistoricContingutTipus = HistoricContingutTipusEnum.CONTINGUT, auditResourceName?: string) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const [numAccions, setNumAccions] = useState<number>();
    const [numMoviment, setMoviment] = useState<number>();

    const handleOpen = (id:any, row:any) => {
        // console.log(id, row);
        setEntity(row);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const tabs = [
        {
            value: 'actions',
            label: t('page.contingut.tabs.actions'),
            content: <Accions id={entity?.id} contingutTipus={contingutTipus} onRowCountChange={setNumAccions}/>,
            badge: numAccions,
        },
        {
            value: "move",
            label: t('page.contingut.tabs.move'),
            content: <Moviment id={entity?.id} contingutTipus={contingutTipus} onRowCountChange={setMoviment}/>,
            badge: numMoviment ?? entity?.numMoviments,
            disabled: entity?.numMoviments === 0,
            hidden: entity?.numMoviments == null,
            showZero: true,
        },
        {
            value: "auditoria",
            label: t('page.contingut.tabs.auditoria'),
            content: <Auditoria entity={entity} resourceName={auditResourceName}/>,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.action.history.label')}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
            dialogContentProps={{ sx: { px: 0, py: 0 } }}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Load value={entity}>
                <Box sx={{ height: '650px', minHeight: 0 }}>
                    <TabComponent
                        indicatorColor={"primary"}
                        textColor={"primary"}
                        aria-label="scrollable force tabs"
                        tabs={tabs}
                        variant="scrollable"
                    />
                </Box>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useHistoric;