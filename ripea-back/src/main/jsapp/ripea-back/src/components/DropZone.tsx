import React, {ReactNode} from "react";
import {toBase64} from "reactlib";
import {useDropzone} from "react-dropzone";

type DropZoneProps = {
    onDrop: (file:any) => void,
    children: ReactNode,
    disabled?: boolean,
}

const DropZone = (props:DropZoneProps) => {
    const {onDrop: onDropFile, children, disabled = false} = props;

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
    const { getRootProps, getInputProps, isDragActive } = useDropzone({ noClick: true, onDrop })
    const dragDropStyle = { border: '2px dashed ' + (isDragActive ? 'orange' : 'transparent') };

    if (disabled){
        return children
    }

    return <div
        style={{
            display: 'flex',
            flexDirection: 'column',
            height: '100%',
            ...dragDropStyle
        }}
        {...getRootProps()}>
        <input {...getInputProps()} />
        {children}
    </div>
}
export default DropZone;