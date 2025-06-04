import {useEffect, useState} from "react";
import {MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";

const useActions = (refresh?: () => void) => {
    // const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const {
        artifactAction: apiAction,
    } = useResourceApiService('alertaResource');

    const action = (id:any, code:string, msg:string = '') => {
        return apiAction(undefined, {code :code, data:{ ids: [id], massivo: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const massiveAction = (ids:any[], code:string, msg:string = '') => {
        return apiAction(undefined, {code :code, data:{ ids: ids, masivo: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            })
    }

    const llegit = (id: any): void => { action(id, 'LLEGIT'); }
    const massiveLlegit = (ids: any): void => { massiveAction(ids, 'LLEGIT'); }

    return {
        llegit,
        massiveLlegit
    }
}

const columns = [
    {
        field: "text",
        flex: 0.5,
    },
    {
        field: "createdDate",
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
]
const sortModel:any = [{ field: 'createdDate', sort: 'desc' }];
const Alerta = (props:any) => {
    const {entity, onRowCountChange} = props
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {llegit, massiveLlegit} = useActions(refresh);
    const actions =[
        {
            title: t('page.alert.acciones.read'),
            icon: "mail",
            showInMenu: false,
            onClick: llegit,
        },
    ]
    const massiveActions =[
        {
            title: t('page.alert.acciones.read'),
            icon: "mail",
            onClick: massiveLlegit,
        },
    ]

    return <StyledMuiGrid
        resourceName={"alertaResource"}
        columns={columns}
        filter={builder.and(
            builder.eq('contingut.id', entity?.id),
            builder.eq('llegida', false),
        )}
        sortModel={sortModel}
        apiRef={apiRef}
        rowAdditionalActions={actions}
        toolbarMassiveActions={massiveActions}
        // height={162 + 52 * 4}
        // paginationActive
        autoHeight

        onRowCountChange={onRowCountChange}

        toolbarHideCreate
        rowHideUpdateButton
        rowHideDeleteButton
    />
}
const useAlerta = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [count, setCount] = useState<number>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    useEffect(() => {
        if (count == 0){
            handleClose()
        }
    }, [count]);

    const dialog = <MuiDialog
        open={open}
        closeCallback={handleClose}
        title={t('page.alert.title')}
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
        <Load value={entity} noEffect>
            <Alerta entity={entity} onRowCountChange={setCount}/>
        </Load>
    </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
        count
    }
}
export default useAlerta;