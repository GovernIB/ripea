import {useState} from "react";
import {Button, Grid, Icon, IconButton, Typography} from "@mui/material";
import {useGridApiRef} from "@mui/x-data-grid-pro";
import {
    MuiFilter,
    MuiGrid,
    useBaseAppContext,
    useFilterApiRef,
    useResourceApiService,
    MuiDialog
} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {StyledEstat} from "../ExpedientGrid.tsx";

const columns = [
    {
        field: 'metaExpedient',
        flex: 0.5,
    },
    {
        field: 'numero',
        flex: 0.5,
    },
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'estat',
        flex: 0.5,
        renderCell: (params: any) => <StyledEstat entity={params?.row} icon={"folder"}/>
    },
]

const springFilterBuilder = (data: any) :string => {
    const filterStr = builder.and(
        builder.eq('metaExpedient.id', data?.metaExpedient?.id),
        builder.like('numero', data?.numero),
        builder.like('nom', data?.nom),
        data.estat && builder.equals("estat",`'TANCAT'`, (data.estat==='TANCAT')),
    );
    // console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const ActionFilter = (props:any) => {
    const {onSpringFilterChange} = props;

    const filterRef = useFilterApiRef();

    const cercar = ()=> {
        filterRef.current.filter()
    }
    const netejar = ()=> {
        filterRef.current.clear()
    }

    return <MuiFilter
        resourceName="expedientResource"
        code="EXPEDIENT_FILTER"
        springFilterBuilder={springFilterBuilder}
        commonFieldComponentProps={{size: 'small'}}
        componentProps={{
            sx: {mb: 3, p: 2, backgroundColor: '#f5f5f5', border: '1px solid #e3e3e3', borderRadius: '10px'}
        }}
        apiRef={filterRef}
        onSpringFilterChange={onSpringFilterChange}
        buttonControlled
    >
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <GridFormField xs={3} name="metaExpedient"/>
            <GridFormField xs={3} name="numero"/>
            <GridFormField xs={3} name="nom"/>
            <GridFormField xs={3} name="estat"/>

            <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'end' }}>
                <Button onClick={netejar}>Netejar</Button>
                <Button onClick={cercar} variant="contained" sx={{borderRadius: 1}}><Icon>filter_alt</Icon>Cercar</Button>
            </Grid>
        </Grid>
    </MuiFilter>
}

const useRelacionar= (refresh?: () => void) => {
    const { t } = useTranslation();

    const [springFilter, setSpringFilter] = useState<string>();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleShow = (id:any, row:any) => {
        console.log(id, row);
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const gridApiRef=useGridApiRef()

    const {
        isReady: apiIsReady,
        patch: apiPatch
    } = useResourceApiService('expedientResource');
    const {temporalMessageShow} = useBaseAppContext();

    const relacionarAll = () => {
        relacionar([...gridApiRef.current.getSelectedRows().keys()])
    }

    const relacionar = (ids:any[]) => {
        if (apiIsReady) {
            apiPatch(entity?.id, {data: {relacionatsAmb: ids}})
                .then(() => {
                    refresh?.()
                    temporalMessageShow(null, '', 'success');
                })
                .catch((error) => {
                    error && temporalMessageShow('Error', error.message, 'error');
                });
        }
    }

    const content =
        <MuiDialog
            title={'Relacionar expediente'}
            open={open}
            closeCallback={handleClose}
            // title={entity?.nom}
            componentProps={{ fullWidth: true, maxWidth: 'xl', height: 'max-content'}}
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
            <ActionFilter onSpringFilterChange={setSpringFilter}/>
            <MuiGrid
                resourceName={'expedientResource'}
                titleDisabled
                datagridApiRef={gridApiRef}
                columns={columns}
                filter={builder.and(
                    builder.neq('id', entity?.id),
                    springFilter
                )}

                selectionActive
                // TODO: check
                rowSelectionModel={entity?.relacionatsAmb?.map((a:any)=>a.id)}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <IconButton onClick={relacionarAll}>
                            <Icon>link</Icon>
                            <Typography>{t('page.expedient.acciones.relacio')}</Typography>
                        </IconButton>,
                    },
                ]}

                // height={'calc(162px + calc(52px * 4))'}
                height={162 + 52 * 4}
                paginationActive
                readOnly
            />
    </MuiDialog>

    return {
        handleShow,
        content
    }
}
export default useRelacionar;