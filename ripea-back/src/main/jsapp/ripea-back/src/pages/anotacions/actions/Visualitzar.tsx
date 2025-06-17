import {useState} from "react";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import Iframe from "../../../components/Iframe.tsx";

const getUrl = (id:any) => {
    return `${import.meta.env.VITE_BASE_URL}expedientPeticio/annex/${id}/content`;
}

const Visualitzar = (props:any) => {
    const {id} = props;

    return <Load value={id}>
        <Iframe src={getUrl(id)}/>
    </Load>
}

const useVisualitzar = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entityId, setEntityId] = useState<any>();

    const handleOpen = (id: any) => {
        setEntityId(id)
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntityId(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.document.action.view.title')}
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
            <Visualitzar id={entityId}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useVisualitzar;