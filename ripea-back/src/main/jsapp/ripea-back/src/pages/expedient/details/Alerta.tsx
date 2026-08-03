import {useEffect, useState} from "react";
import {MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import Load from "../../../components/Load.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import {useUserSession} from "../../../components/Session.tsx";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const {
        artifactAction: apiAction,
    } = useResourceApiService('alertaResource');

    const action = (id:any, code:string, msg:string = '') => {
        apiAction(undefined, {code :code, data:{ ids: [id], massivo: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const massiveAction = (ids:any[], code:string, msg:string = '') => {
        apiAction(undefined, {code :code, data:{ ids: ids, masivo: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'info');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            })
    }

    const llegit = (id: any): void => action(id, 'LLEGIT', t('page.alert.action.read.ok'))
    const massiveLlegit = (ids: any): void => massiveAction(ids, 'LLEGIT', t('page.alert.action.read.massiveOk'))

    return {
        llegit,
        massiveLlegit
    }
}

const columns = [
    {
        field: "text",
        flex: 1.1,
    },
    {
        field: "createdDate",
        flex: 0.4,
        valueFormatter: (value: any) => formatDate(value)
    },
]
const sortModel:any = [{ field: 'createdDate', sort: 'desc' }];
const Alerta = (props:any) => {
    const {entity, onRowCountChange} = props
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const { value: user } = useUserSession();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {llegit, massiveLlegit} = useActions(refresh);
    const actions = user?.rolActual != "IPA_ADMIN_LECTURA" ?[
        {
            label: t('page.alert.action.read.label'),
            icon: "mark_email_read",
            showInMenu: false,
            onClick: llegit,
        },
    ] :undefined
    const massiveActions = user?.rolActual != "IPA_ADMIN_LECTURA" ?[
        {
            label: t('page.alert.action.read.label'),
            icon: "mark_email_read",
            showInMenu: false,
            onClick: massiveLlegit,
        },
    ] :undefined

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
        autoHeight

        onRowCountChange={onRowCountChange}

        toolbarHideCreate
    />
}
const useAlerta = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [count, setCount] = useState<number>();

    const handleOpen = (id:any, row:any) => {
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
        if (count === 0){
            handleClose()
        }
    }, [count]);

    const dialog = <MuiDialog
        open={open}
        closeCallback={handleClose}
        title={t('page.alert.action.read.title')}
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