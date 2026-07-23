import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';
import {useToProgramaAntic} from "@src/pages/user/UserHeadToolbar.tsx";
import Load from "@src/components/Load.tsx";
import {useTranslation} from "react-i18next";
import {useState} from "react";
import {MuiDialog} from "reactlib";
import { Viewer, Worker } from "@react-pdf-viewer/core";
import { defaultLayoutPlugin } from "@react-pdf-viewer/default-layout";
import { Alert } from "@mui/material";

const commonProps = { width: '100%', height: '500px', border: '1px solid lightgray', borderRadius: '4px' }

const Iframe = (props:any) => {
    const { t } = useTranslation();
    const { src, hidden, style, isPDF = false, ...other } = props

    if(!src || hidden) {
        return <></>
    }

    if (!isPDF) {
        return <iframe src={src} {...other} style={{...commonProps, ...style}}/>
    }

    const defaultLayoutPluginInstance = defaultLayoutPlugin();
    return (
        <div style={{ ...style }}>
            <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                <Viewer
                    fileUrl={src}   // tu PDF (en public/ o desde una URL)
                    plugins={[defaultLayoutPluginInstance]}
                    renderError={() => (
                        <Alert severity="warning" sx={{ m: 2 }}>
                            {t('page.document.action.view.error')}
                        </Alert>
                    )}
                    {...other}
                />
            </Worker>
        </div>
    );
}

export const useIframeDialog = () => {
    const { t } = useTranslation();
    const { getUrl } = useToProgramaAntic();
    const [open, setOpen] = useState(false);
    const [url, setUrl] = useState<string>();

    const handleOpen = (url:string) => {
        setUrl(url)
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setUrl(undefined);
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
            <Load value={url}>
                {url && <Iframe isPDF src={getUrl(url)}/>}
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog,
    }
}
export default Iframe;