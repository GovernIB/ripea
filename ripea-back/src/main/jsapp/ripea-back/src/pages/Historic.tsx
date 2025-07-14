import {useState} from "react";
import {Grid} from "@mui/material";
import {BasePage, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../components/TabComponent.tsx";
import {formatDate} from "../util/dateUtils.ts";
import StyledMuiGrid from "../components/StyledMuiGrid.tsx";
import * as builder from "../util/springFilterUtils.ts";
import {CardData, ContenidoData} from "../components/CardData.tsx";
import Load from "../components/Load.tsx";

const columnsAccions = [
    {
        field: 'createdDate',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'createdBy',
        flex: 0.5,
    },
    {
        field: 'tipus',
        flex: 0.5,
        valueFormatter: (value: any, row:any)=> row?.secundari ?row?.objecte :value
    },
]

const sortModel:any = [{ field: 'createdDate', sort: 'asc' }];
const Accions = (props:any) => {
    const { id, onRowCountChange } = props;
    const { t } = useTranslation();

    const {handleOpen, dialog} = useAccioDialog()

    const actions = [
        {
            title: t('common.detail'),
            icon: 'info',
            showInMenu: false, // <-- Esto lo muestra como botón directo en la fila
            onClick: handleOpen,
        }
    ]

    return <BasePage>
        <StyledMuiGrid
            resourceName={'contingutLogResource'}
            filter={builder.eq('contingut.id', id)}
            staticSortModel={sortModel}
            columns={columnsAccions}
            rowAdditionalActions={actions}
            onRowCountChange={onRowCountChange}
            // paginationActive
            height={58 + 52 * 4}
            // autoHeight
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
                icon: 'close'
            },
        ]}
        buttonCallback={(value :any) :void=>{
            if (value=='close') {
                handleClose();
            }
        }}
    >
        <Grid container columnSpacing={1} rowSpacing={1}>
            <CardData title={t('page.contingut.log.param')}>
                <ContenidoData title={t('page.contingut.log.param1')} xs={6}>{entity?.param1}</ContenidoData>
                <ContenidoData title={t('page.contingut.log.param2')} xs={6}>{entity?.param2}</ContenidoData>
            </CardData>

            <CardData title={t('page.contingut.log.causa')} hidden={!entity?.pare}>
                <ContenidoData title={t('common.action')}>
                    {formatDate(entity?.pare?.createdDate)} | {entity?.pare?.createdBy} | {(entity?.pare?.objecteLogTipus ?? entity?.pare?.tipus)}
                </ContenidoData>
                <ContenidoData title={t('page.contingut.log.objecte')}>{entity?.objecte} {!!entity?.objecteNom ?' - '+entity?.objecteNom :''}</ContenidoData>
                <ContenidoData title={t('page.contingut.log.param1')} xs={6}>{entity?.pare?.param1}</ContenidoData>
                <ContenidoData title={t('page.contingut.log.param2')} xs={6}>{entity?.pare?.param2}</ContenidoData>
            </CardData>

            <CardData title={t('page.contingut.moviment.causa')} hidden={!entity?.moviment}>
                <ContenidoData title={t('page.contingut.moviment.origen')} xs={6}>#{entity?.moviment?.origen?.id}</ContenidoData>
                <ContenidoData title={t('page.contingut.moviment.desti')} xs={6}>#{entity?.moviment?.desti?.id}</ContenidoData>
            </CardData>
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
            // paginationActive
            height={58 + 52 * 4}
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
            <CardData title={t('page.contingut.history.create')} xs={6}>
                <ContenidoData title={t('page.contingut.history.user')}>{entity?.createdBy}</ContenidoData>
                <ContenidoData title={t('page.contingut.history.date')} >{formatDate(entity?.createdDate)}</ContenidoData>
            </CardData>
            <CardData title={t('page.contingut.history.update')} xs={6}>
                <ContenidoData title={t('page.contingut.history.user')} >{entity?.lastModifiedBy}</ContenidoData>
                <ContenidoData title={t('page.contingut.history.date')} >{formatDate(entity?.lastModifiedDate)}</ContenidoData>
            </CardData>
        </Grid>
    </BasePage>;
}

const useHistoric = () => {
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
            content: <Accions id={entity?.id} onRowCountChange={setNumAccions}/>,
            badge: numAccions,
        },
        {
            value: "move",
            label: t('page.contingut.tabs.move'),
            content: <Moviment id={entity?.id} onRowCountChange={setMoviment}/>,
            badge: numMoviment ?? entity?.numMoviments,
            disabled: entity?.numMoviments === 0,
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
                    icon: 'close'
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