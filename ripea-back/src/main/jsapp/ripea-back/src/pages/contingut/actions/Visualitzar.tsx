import {useState} from "react";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import Iframe from "../../../components/Iframe.tsx";

const getUrl = (id:any) => {
    return `${import.meta.env.VITE_BASE_URL}contingut/document/${id}/getPDF`
}

const Visualitzar = (props:any) => {
    const {entity} = props;

    return <Load value={entity}>
        <Iframe src={getUrl(entity?.id)}/>
    </Load>
}

const useVisualitzar = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id: any, row: any) => {
        console.log(id, row)
        setEntity(row)
        setOpen(true);
    }

    const handleClose = () => {
        setEntity(undefined);
        setOpen(false);
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={''}
            componentProps={{ fullWidth: true, maxWidth: 'md'}}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close')
                },
            ]}
            buttonCallback={(value: any): void => {
                if (value == 'close') {
                    handleClose();
                }
            }}
        >
            <Visualitzar entity={entity}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useVisualitzar;