import {useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import Iframe from "../../../components/Iframe.tsx";
import {Firmes} from "../details/DocumentDetail.tsx";
import {Grid} from "@mui/material";
import {useToProgramaAntic} from "../../user/UserHeadToolbar.tsx";

const Visualitzar = (props: any) => {
    const {entity} = props;
    const { getUrl } = useToProgramaAntic();

    return <Load value={entity}>
        <Firmes entity={entity}/>
        <Grid item sx={{p: 0, mt: 1}}><Iframe src={getUrl(`contingut/document/${entity?.id}/getPDF`)}/></Grid>
    </Load>
}

const perspectives = ['FIRMES']
const useVisualitzar = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('documentResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id: any) => {
        if (apiIsReady && id) {
            apiGetOne(id, {perspectives})
                .then((app) => setEntity(app))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.document.action.view.title')}
            componentProps={{fullWidth: true, maxWidth: 'md'}}
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
            <Visualitzar entity={entity}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
        isValid: (row:any) => ['pdf', 'odt', 'docx'].includes(row?.fitxerExtension)
    }
}
export default useVisualitzar;