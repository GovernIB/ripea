import { Viewer, Worker } from "@react-pdf-viewer/core";
import { defaultLayoutPlugin } from "@react-pdf-viewer/default-layout";

// estilos por defecto
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';

const commonProps = { width: '100%', height: '500px', border: '1px solid lightgray', borderRadius: '4px' }

const Iframe = (props:any) => {
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
                    {...other}
                />
            </Worker>
        </div>
    );
}
export default Iframe;