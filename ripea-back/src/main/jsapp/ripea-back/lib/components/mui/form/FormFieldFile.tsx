import * as React from 'react';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import { FormFieldCustomProps } from '../../form/FormField';
import { toBase64 } from '../../../util/files';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldFileProps = FormFieldCustomProps & {
    accept?: string;
};

export const FormFieldFile: React.FC<FormFieldFileProps> = (props) => {
    const {
        name,
        label,
        value,
        field,
        fieldError,
        inline,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
        accept
    } = props;
    const [fileName, setFileName] = React.useState<string | undefined>(value?.name);
    const fileInputRef = React.useRef<HTMLInputElement>(null);
    const {
        helperText,
        title,
        startAdornment,
    } = useFormFieldCommon(field, fieldError, inline, componentProps);
    const handleOnChange = () => {
        const currentFile = fileInputRef.current?.files?.[0];
        if (currentFile != null) {
            toBase64(currentFile).then(base64 => {
                const fileValue = {
                    name: currentFile.name,
                    content: base64,
                    contentType: currentFile.type,
                    contentLength: currentFile.size,
    
                };
                onChange(fileValue);
                setFileName(currentFile?.name);
            });
        } else {
            onChange(null);
            setFileName(undefined);
        }
    }
    const handleClearButtonClick = (event: any) => {
        event.stopPropagation();
        event.preventDefault();
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
        onChange(null);
        setFileName(undefined);
    }
    const handleDownloadButtonClick = (event: any) => {
        event.stopPropagation();
        event.preventDefault();
        console.log('>>> handleDownloadButtonClick')
    }
    const clearEndAdornment = value != null ? <IconButton size="small" onClick={handleClearButtonClick}>
        <Icon fontSize="inherit">clear</Icon>
    </IconButton> : undefined;
    const downloadEndAdornment = value != null ? <IconButton size="small" onClick={handleDownloadButtonClick}>
        <Icon fontSize="inherit">file_download</Icon>
    </IconButton> : undefined;
    const endAdornment = <>
        {clearEndAdornment}
        {downloadEndAdornment}
        {componentProps?.slotProps?.input?.endAdornment}
        {<Icon>attach_file</Icon>}
    </>;
    const inputProps = {
        readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment,
        endAdornment,
    };
    const htmlInputProps = {
        maxLength: field?.maxLength,
        ...componentProps?.slotProps?.htmlInput,
    };
    return <>
        <input type="file" hidden accept={accept} onChange={handleOnChange} ref={fileInputRef} />
        <TextField
            name={name}
            label={!inline ? label : undefined}
            placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
            value={fileName ?? ''}
            required={required ?? field?.required}
            disabled={disabled}
            error={fieldError != null}
            title={title}
            helperText={helperText}
            onClick={(event) => {
                if (readOnly || disabled) {
                    return;
                }
                event.stopPropagation;
                event.preventDefault();
                (event.target as any).blur();
                fileInputRef.current?.click();
            }}
            fullWidth
            {...componentProps}
            slotProps={{
                input: inputProps,
                htmlInput: htmlInputProps,
            }}
            sx={{
                '& input': !readOnly && !disabled ? { cursor: 'pointer' } : undefined,
            }} />
    </>;
}

/*const FormFieldFile = (props) => {
    const {
        name,
        label,
        required,
        disabled,
        readOnly,
        margin,
        variant,
        size,
        onChange,
        autoFocus, // TODO
        inline,
        hideLabel,
        fullWidth,
        inputRef,
        contextId,
        contextField,
        contextValue,
        contextFileName,
        contextFileType,
        contextResourceName, // TODO
        contextFieldErrors,
        fieldErrorEndAdornment, // TODO
        placeholder,
        image,
        dropZone,
        dropZoneHeight,
        hideDownloadButton,
        ...otherProps
    } = props;
    const { t } = useTranslation('components');
    const { apiInitialized, execHrefDownload: apiExecHrefDownload } = useResourceApi(contextResourceName);
    const [remoteImageString, setRemoteImageString] = React.useState();
    const fieldLabel = hideLabel ? null : contextField?.prompt ?? label ?? name;
    const fieldRequired = required ?? contextField?.required;
    const fieldError = contextFieldErrors?.find((e) => e.field === name);
    const showDownloadLink = contextFileName && contextValue === '';
    const isShowingContent = contextFileName || showDownloadLink;
    const isEditable = !readOnly && !disabled;
    const FileInputProps = {
        startAdornment: isShowingContent ? <InputAdornment position="start">
            <Icon fontSize={size}>attach_file</Icon>
        </InputAdornment> : undefined,
        endAdornment: otherProps?.InputProps?.endAdornment ?? <>
            {contextFileName && isEditable && <IconButton
                title={t("form.field.file.clear")}
                size="small"
                onClick={(event) => {
                    event.stopPropagation();
                    event.preventDefault();
                    onChange();
                }}>
                <Icon fontSize="inherit">clear</Icon>
            </IconButton>}
            {showDownloadLink && <IconButton
                title={t("form.field.file.download")}
                size={size}
                onClick={(event) => {
                    event.stopPropagation();
                    event.preventDefault();
                    apiExecHrefDownload(
                        contextField.fileDownloadLink.href,
                        { resourceId: contextId },
                        { refresh: true }
                    );
                }}>
                <Icon fontSize="inherit">file_download</Icon>
            </IconButton>}
            {!isShowingContent && isEditable && <InputAdornment position='end'>
                <Icon fontSize={size}>attach_file</Icon>
            </InputAdornment>}
        </>,
        ...(otherProps?.InputProps ?? {})
    };

    React.useEffect(() => {
        if (apiInitialized && image && contextId != null) {
            apiExecHrefDownload(contextField.fileDownloadLink.href, { resourceId: contextId }, {
                callbacks: {
                    setResponse: (response) => {
                        const reader = new FileReader();
                        reader.readAsDataURL(response.data);
                        reader.onloadend = function () {
                            const base64data = reader.result;
                            setRemoteImageString(base64data);
                        }
                    },
                },
                refresh: true,
            }, false);
        }
    }, [apiInitialized, contextField.fileDownloadLink.href, contextId]);

    const getImageValue = () => {
        if (contextValue)
            return `data:${contextFileType};base64,${contextValue}`;
        if (showDownloadLink)
            return remoteImageString;
        return undefined;
    };

    const buttonAvatarId = `button-avatar-${name}`;

    if (image) {
        return <Box
            title={contextField?.helperText}
            sx={{
                borderWidth: 1,
                borderStyle: 'solid',
                borderColor: 'grey.400',
                position: 'relative',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                width: '128px',
                height: '128px',
                borderRadius: '9999px',
                overflow: 'hidden',
                ...otherProps.sx
            }}
        >
            <Box sx={{
                position: 'absolute',
                inset: 0,
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: 'rgba(35,35,35,0.5)',
                zIndex: '20',
                opacity: 0,
                transition: "opacity 0.5s",
                '&:hover': {
                    opacity: 1,
                },
            }}>
                {isEditable &&
                    <div title={t("form.field.file.edit")}>
                        <label htmlFor={buttonAvatarId}
                            style={{
                                display: 'flex',
                                padding: '8px',
                                cursor: 'pointer',
                            }}
                        >
                            <input
                                type='file'
                                accept='image/*'
                                ref={inputRef}
                                style={{ display: 'none' }}
                                id={buttonAvatarId}
                                onChange={onChange}
                            />
                            <Icon sx={{ color: '#ffffff' }}>edit</Icon>
                        </label>
                    </div>
                }
                {!hideDownloadButton &&
                    <div title={t("form.field.file.download")}>
                        <IconButton
                            onClick={(event) => {
                                event.stopPropagation();
                                event.preventDefault();
                                apiExecHrefDownload(
                                    contextField.fileDownloadLink.href,
                                    { resourceId: contextId },
                                    { refresh: true }
                                );
                            }}
                            sx={{ color: '#ffffff' }}
                        >
                            <Icon>file_download</Icon>
                        </IconButton>
                    </div>}
                {isEditable &&
                    <div title={t("form.field.file.clear")}>
                        <IconButton
                            onClick={() => {
                                onChange();
                            }}
                            sx={{ color: '#ffffff' }}
                        >
                            <Icon>delete</Icon>
                        </IconButton>
                    </div>
                }
            </Box>
            <Avatar
                src={getImageValue()}
                variant="square"
                {...(otherProps?.AvatarProps ?? {})}
                sx={{
                    backgroundColor: 'background.default',
                    color: 'text.secondary',
                    objectFit: 'cover',
                    width: '100%',
                    height: '100%',
                    ...(otherProps?.AvatarProps?.sx ?? {})
                }}
            >
                {placeholder ? placeholder : <Icon fontSize='large'>image_not_supported</Icon>}
            </Avatar>
        </Box>;
    }
    return <>
        <input type="file" hidden accept={contextField?.fileAccept} ref={inputRef} onChange={onChange} />
        <TextField
            fullWidth={fullWidth}
            label={fieldLabel}
            title={contextField?.helperText}
            required={fieldRequired}
            disabled={disabled}
            readOnly={true}
            value={contextFileName ?? ''}
            error={!!fieldError}
            helperText={fieldError && !inline ? fieldError.message : undefined}
            margin={margin}
            variant={variant}
            size={size}
            onClick={(event) => {
                if (!isEditable) {
                    return;
                }
                event.stopPropagation;
                event.preventDefault();
                event.target.blur();
                inputRef.current?.click();
            }}
            {...otherProps}
            InputProps={FileInputProps}
            sx={{
                '& input': isEditable ? { cursor: 'pointer' } : undefined,
            }} />
    </>;
}*/

export default FormFieldFile;