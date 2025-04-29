import {useState} from "react";
import {useGridApiRef} from "@mui/x-data-grid-pro";
import {
    useBaseAppContext,
    useResourceApiService,
    MuiDialog
} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import {StyledEstat} from "../ExpedientGrid.tsx";

const sortModel = [{ field: 'createdDate', sort: 'desc' }];
const perspectives = ["ESTAT"];

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
    {
        field: 'createdDate',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
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

    return <StyledMuiFilter
        resourceName="expedientResource"
        code="EXPEDIENT_FILTER"
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <GridFormField xs={3} name="metaExpedient"/>
        <GridFormField xs={3} name="numero"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="estat"/>
    </StyledMuiFilter>
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
            apiPatch(entity?.id, {data: {relacionatsAmb: ids.map(id=>{ return {id:id}} )}})
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
                {
                    value: 'relacio',
                    text: t('page.expedient.acciones.relacio'),
                    icon: 'link',
                    componentProps: {
                        variant: "contained",
                        style: {
                            borderRadius: '4px',
                        },
                    }
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
                if (value=='relacio') {
                    relacionarAll();
                    handleClose();
                }
            }}
        >
            <ActionFilter onSpringFilterChange={setSpringFilter}/>
            <StyledMuiGrid
                resourceName={'expedientResource'}
                titleDisabled
                datagridApiRef={gridApiRef}
                columns={columns}
                filter={builder.and(
                    builder.neq('id', entity?.id),
                    springFilter
                )}
                sortModel={sortModel}
                perspectives={perspectives}

                selectionActive
                // TODO: check seleccionados al inicio
                rowSelectionModel={entity?.relacionatsAmb?.map((a:any)=>a.id)}

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