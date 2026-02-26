import {useState} from "react";
import {Alert, Collapse, Icon, IconButton} from "@mui/material";
import Box from "@mui/material/Box";
import AlertTitle from "@mui/material/AlertTitle";

const AlertExpand = (props:any) => {
    const { title, label, sx, children, onClose, action, ...other} = props
    const [verDetalles, setVerDetalles] = useState(false);

    const toggleDetalles = () => setVerDetalles((prev) => !prev);

    return (<>
        <Alert
            {...other}
            sx={{
                py: 0,
                px: 1.5,
                fontSize: '0.85rem',
                minHeight: 'unset',
                lineHeight: 1.3,
                padding: '6px',
                ...sx, // permite sobrescribir desde fuera
            }}
            action={
                <>
                    {action}
                    <IconButton
                        aria-label="ver detalles"
                        color="inherit"
                        size="small"
                        onClick={toggleDetalles}
                    >
                        <Icon sx={{ m: 0 }}>{verDetalles ? 'expand_less' : 'expand_more'}</Icon>
                    </IconButton>
                    {onClose && <IconButton
                        color="inherit"
                        size="small"
                        onClick={()=>onClose?.()}
                    >
                        <Icon sx={{m: 0}}>close</Icon>
                    </IconButton>}
                </>
            }
        >
            {title && <AlertTitle sx={{m: 0}}>{title}</AlertTitle>}
            {label}
            <Collapse in={verDetalles}>
                <Box mt={1}>{children}</Box>
            </Collapse>
        </Alert>
    </>);
}
export default AlertExpand;