import {useState} from "react";
import {Grid2 as Grid} from "@mui/material";
import {BasePage, MuiDialog} from "reactlib";
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

    const {handleOpen, dialog} = useAccioDialog()

    const actions = [
        {
            label: t('common.detail'),
            icon: 'info',
            showInMenu: false,
            onClick: handleOpen,
        }
    ]

    return <BasePage>
        <StyledMuiGrid
            resourceName={'contingutLogResource'}
            filter={builder.and(
                builder.eq('contingutId', id),
                builder.eq('contingutTipus', `'${contingutTipus}'`),
            )}
            staticSortModel={sortModel}
            columns={columnsAccions}
            rowAdditionalActions={actions}
            onRowCountChange={onRowCountChange}
            autoHeight
            paginationModel={{page: 0, pageSize: 5}}
            toolbarHide
            readOnly
        />
        {dialog}
    </BasePage>;
}
const useAccioDialog = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row);
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
            paginationModel={{page: 0, pageSize: 5}}
            toolbarHide
            readOnly
        />
    </BasePage>;
}
const Auditoria = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();

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
const useHistoric = (contingutTipus:HistoricContingutTipus = HistoricContingutTipusEnum.CONTINGUT) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const [numAccions, setNumAccions] = useState<number>();
    const [numMoviment, setMoviment] = useState<number>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row);
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
            content: <Auditoria entity={entity}/>,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.action.history.label')}
            componentProps={{ fullWidth: true, maxWidth: 'xl'}}
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
                <TabComponent
                    indicatorColor={"primary"}
                    textColor={"primary"}
                    aria-label="scrollable force tabs"
                    tabs={tabs}
                    variant="scrollable"
                />
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useHistoric;