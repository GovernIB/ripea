import {useState} from "react";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import Iframe from "../../../components/Iframe.tsx";
import {useToProgramaAntic} from "../../user/UserHeadToolbar.tsx";

const Visualitzar = (props:any) => {
    const {id} = props;
    const { getUrl } = useToProgramaAntic();

    return <Load value={id}>
        <Iframe isPDF src={getUrl(`expedientPeticio/annex/${id}/content`)}/>
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
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
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
        dialog,
        isValid: (row:any) => ['pdf', 'odt', 'docx'].includes(row?.fitxerExtension)
    }
}
export default useVisualitzar;