import React, {ReactNode} from "react";
import {toBase64} from "reactlib";
import {useDropzone} from "react-dropzone";

type DropZoneProps = {
    onDrop: (file:any) => void,
    children: ReactNode,
    disabled?: boolean,
    'aria-label'?: string,
}

const DropZone = (props:DropZoneProps) => {
    const {onDrop: onDropFile, children, disabled = false, 'aria-label': ariaLabel} = props;

    const onDrop = React.useCallback((acceptedFiles: any) => {
        const droppedFile = acceptedFiles[0];
        toBase64(droppedFile).then(base64 => {
            const file = {
                name: droppedFile.name,
                content: base64,
                contentType: droppedFile.type,
                contentLength: droppedFile.size,
            };
            onDropFile(file)
        });
    }, [])
    const { getRootProps, getInputProps, isDragActive } = useDropzone({ noClick: true, onDrop, disabled })
    const dragDropStyle = { border: '2px dashed ' + (isDragActive ? 'orange' : 'transparent') };

    // El contenidor es renderitza sempre, també deshabilitat: si canviés l'arbre (fills sols ↔ dins el div), React
    // desmuntaria i tornaria a muntar els fills cada vegada que canvia disabled (p.ex. la graella de documents es
    // tornava a carregar sencera quan l'expedient es bloqueja o es desbloqueja pel tancament en segon pla).
    return <div
        style={{
            display: 'flex',
            flexDirection: 'column',
            height: '100%',
            ...dragDropStyle
        }}
        {...(disabled ? {} : getRootProps())}>
        {!disabled && <input {...getInputProps()} aria-label={ariaLabel} />}
        {children}
    </div>
}
export default DropZone;