import {useState} from "react";
import {Alert, Collapse, Icon, IconButton} from "@mui/material";
import Box from "@mui/material/Box";

const AlertExpand = (props:any) => {
    const {label, children, ...other} = props
    const [verDetalles, setVerDetalles] = useState(false);

    const toggleDetalles = () => setVerDetalles((prev) => !prev);

    return (<>
        <Alert
            {...other}
            action={
                <IconButton
                    aria-label="ver detalles"
                    color="inherit"
                    size="small"
                    onClick={toggleDetalles}
                >
                    <Icon>{verDetalles ? 'expand_less' : 'expand_more'}</Icon>
                </IconButton>
            }
        >
            {label}
            <Collapse in={verDetalles}>
                <Box mt={1}>
                    {children}
                </Box>
            </Collapse>
        </Alert>
    </>);
}
export default AlertExpand;