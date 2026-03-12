import {useState} from "react";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {DetailCardContent, DetailCard} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";

const PublicacioDetail = (props:any) => {
    const {entity} = props
    const { t } = useTranslation();

    return <DetailCard>
        <DetailCardContent title={t('page.publicacio.detall.document')}>{entity?.document?.description}</DetailCardContent>
        <DetailCardContent title={t('page.publicacio.detall.enviatData')}>{formatDate(entity?.enviatData)}</DetailCardContent>
        <DetailCardContent title={t('page.publicacio.detall.estat')}>{entity?.estat}</DetailCardContent>
        <DetailCardContent title={t('page.publicacio.detall.tipus')}>{entity?.tipus}</DetailCardContent>
        <DetailCardContent title={t('page.publicacio.detall.assumpte')}>{entity?.assumpte}</DetailCardContent>
        <DetailCardContent title={t('page.publicacio.detall.observacions')} hiddenIfEmpty>{entity?.observacions}</DetailCardContent>
    </DetailCard>
}

const usePublicacioDetail = () => {
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const { t } = useTranslation();

    const handleOpen = (_id:any, row:any) => {
        // console.log(id, row)
        setEntity(row);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.publicacio.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'sm'}}
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
            <Load value={entity}>
                <PublicacioDetail entity={entity}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default usePublicacioDetail;