import {useState} from "react";
import {MuiGrid, MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import useNotificacioInteressatActions from "./NotificacioInteressatActions.tsx";

const columns = [
    {
        field: 'interessat',
        flex: 1,
    },
    {
        field: 'enviamentDatatEstat',
        flex: 0.5,
    },
    {
        field: 'registreNumeroFormatat',
        flex: 0.75,
    },
    {
        field: 'registreData',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
]

const useNotificacioInteressatGrid = (refresh?: () => void) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const {actions, components} = useNotificacioInteressatActions(refresh);

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const dialog =
        <MuiDialog
            open={open}
            title={t('page.notificacio.acciones.notificacioInteressat')}
            closeCallback={handleClose}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
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
            <MuiGrid
                resourceName={'documentEnviamentInteressatResource'}
                perspectives={['DETAIL']}
                columns={columns}
                filter={builder.and(
                    builder.eq('notificacio.id', entity?.id)
                )}
                titleDisabled
                staticSortModel={[{field: 'id', sort: 'asc'}]}
                disableColumnMenu
                disableColumnSorting
                readOnly
                autoHeight

                rowAdditionalActions={actions}
            />
            {components}
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default useNotificacioInteressatGrid;