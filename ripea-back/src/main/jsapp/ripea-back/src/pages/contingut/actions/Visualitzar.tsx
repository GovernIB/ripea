import {useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import Iframe from "../../../components/Iframe.tsx";
import {Firmes} from "../details/DocumentDetail.tsx";
import {useToProgramaAntic} from "../../user/UserHeadToolbar.tsx";
import {useActions} from "../details/ContingutActions.tsx";

const Visualitzar = (props: any) => {
    const {entity} = props;
    const { getUrl } = useToProgramaAntic();

    return <Load value={entity}>
        <Firmes entity={entity}/>
        <Iframe isPDF={true} src={getUrl(`contingut/document/${entity?.id}/getImprimibleOrOriginal`)}/>
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

    const {apiDownload} = useActions()

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

    let buttons :any[] = [
        {
            value: 'download',
            text: t('page.document.action.original.label'),
            icon: 'download',
            hidden: entity?.documentTipus == 'FISIC'
        },
        {
            value: 'descarregarImprimible',
            text: t('page.document.action.descarregarImprimible.label'),
            icon: 'download',
            hidden: entity?.estat != 'CUSTODIAT'
        },
    ]
        .filter((button:any)=>!button?.hidden)

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={entity?.nom}
            componentProps={{fullWidth: true, maxWidth: 'md'}}
            buttons={buttons}
            buttonCallback={(value :any) :void => {
                switch (value){
                    case 'download':
                        apiDownload(entity?.id, 'adjunt', t('page.expedient.results.actionOk'))
                        break;
                    case 'descarregarImprimible':
                        apiDownload(entity?.id, 'imprimible', t('page.document.action.imprimible.ok'))
                        break;
                }
                handleClose();
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